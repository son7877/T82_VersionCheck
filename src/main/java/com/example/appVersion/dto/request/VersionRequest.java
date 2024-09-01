package com.example.appVersion.dto.request;

public record VersionRequest(
        String appId,
        String currentVersion
) {

}
