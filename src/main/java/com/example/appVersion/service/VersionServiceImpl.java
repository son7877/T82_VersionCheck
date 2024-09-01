package com.example.appVersion.service;

import com.example.appVersion.dto.request.VersionRequest;
import com.example.appVersion.global.domain.entity.Version;
import com.example.appVersion.global.repository.VersionRepository;
import com.example.appVersion.util.JwtTokenProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@Service
@Slf4j
@RequiredArgsConstructor
public class VersionServiceImpl implements VersionService {

    @Value("${appstore.minimumSupportedVersion}")
    private String minimumSupportedVersion;

    private final VersionRepository versionRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String getLatestVersionInfo() {
        return versionRepository.findLatestVersion();
    }

    @Override
    public Boolean handleVersionCheck(VersionRequest versionRequest) {
        try {
            Version version = checkAndSaveVersion(versionRequest);
            if (versionRequest.currentVersion().compareTo(version.getLatestVersion()) < -3) {
                return false; // 강제 업데이트 필요
            } else if (versionRequest.currentVersion().compareTo(version.getLatestVersion()) <= -1) {
                return true; // 선택 업데이트 필요
            } else {
                return null; // 업데이트 필요 없음
            }
        } catch (Exception e) {
            throw new RuntimeException("최신 버전을 불러올 수 없습니다.", e);
        }
    }

    private Version checkAndSaveVersion(VersionRequest versionRequest) throws UnsupportedEncodingException {
        try {
            String latestVersion = getLatestTestFlightVersion(versionRequest.appId());
            boolean forceUpdate = versionRequest.currentVersion().compareTo(minimumSupportedVersion) < 0;

            Version version = Version.builder()
                    .latestVersion(latestVersion)
                    .minimumSupportedVersion(minimumSupportedVersion)
                    .forceUpdate(forceUpdate)
                    .build();

            return saveVersionInfo(version);
        } catch (Exception e) {
            log.info("버전 확인 및 저장 중 오류 발생: " + e.getMessage());
            throw e;
        }
    }

    public String getLatestTestFlightVersion(String appId) throws UnsupportedEncodingException {
        String token = jwtTokenProvider.createToken();

        String url = "https://api.appstoreconnect.apple.com/v1/builds?filter[app]=" + appId + "&sort=-uploadedDate";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
            log.info("Response Status Code: " + response.getStatusCode());
            log.info("Response Body: " + response.getBody());
            return extractLatestAppVersion(response.getBody());
        } catch (HttpClientErrorException e) {
            log.info("HTTP Status Code: " + e.getStatusCode());
            log.info("Response Body: " + e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            log.info("예상치 못한 오류 발생: " + e.getMessage());
            throw e;
        }
    }

    private String extractLatestAppVersion(String responseBody) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode dataNode = rootNode.path("data");
            if (dataNode.isArray() && dataNode.size() > 0) {
                JsonNode firstBuild = dataNode.get(0);
                return firstBuild.path("attributes").path("version").asText();
            }
        } catch (IOException e) {
            log.info("오류 발생: " + e.getMessage());
        }
        return null;
    }

    private Version saveVersionInfo(Version version) {
        return versionRepository.save(version);
    }
}
