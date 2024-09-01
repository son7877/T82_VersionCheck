package com.example.appVersion.service;

import com.example.appVersion.dto.request.VersionRequest;
import com.example.appVersion.global.domain.entity.Version;

import java.io.UnsupportedEncodingException;

public interface VersionService {
    String getLatestVersionInfo();
    Boolean handleVersionCheck(VersionRequest versionRequest);
}
