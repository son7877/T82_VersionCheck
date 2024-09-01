package com.example.appVersion.global.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "version")
public class Version {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "VERSION_ID")
    private long id;

    // 최신 버전
    @Column(name = "LATEST_VERSION", length = 255) @Getter @Setter
    private String latestVersion;

    // 최소 지원 버전
    @Column(name = "MINIMUM_SUPPORTED_VERSION", length = 255) @Getter @Setter
    private String minimumSupportedVersion;

    // 강제 업데이트 여부
    @Column(name = "forceUpdate")
    private boolean forceUpdate;

}
