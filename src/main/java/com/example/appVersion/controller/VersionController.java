package com.example.appVersion.controller;

import com.example.appVersion.dto.request.VersionRequest;
import com.example.appVersion.service.VersionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/version")
public class VersionController {

    private final VersionService versionService;

    // 버전 확인
    @PostMapping("/check")
    public Boolean checkVersion(@RequestBody VersionRequest versionRequest) {
        return versionService.handleVersionCheck(versionRequest);
    }

    // 최신 버전 불러오기
    @GetMapping("/latest")
    public String getLatestVersion() {
        return versionService.getLatestVersionInfo();
    }
}
