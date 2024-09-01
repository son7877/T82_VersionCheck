package com.example.appVersion.global.repository;

import com.example.appVersion.global.domain.entity.Version;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VersionRepository extends JpaRepository<Version, Long> {
    // latest_version만 반환
    @Query(value = "SELECT v.latestVersion FROM Version v ORDER BY v.id DESC LIMIT 1")
    String findLatestVersion();
}
