# T82\_IOS\_VersionCheck

### A. 프로젝트 목표

  - T82 어플리케이션의 안정적인 버전 관리를 통해 사용자에게 항상 최신 버전의 어플을 사용하도록 유도했습니다.
  - JWT(JSON Web Token)를 이용한 인증 과정을 구현하여, 허가된 관리자만이 버전 정보를 안전하게 관리할 수 있도록 했습니다.
  - Spring Boot와 Docker를 활용하여 서버를 구축하고, 유지 보수가 용이한 환경을 구성했습니다.

### B. 프로젝트 구성

#### b-1 사용한 프레임워크 및 라이브러리

  - 프로그래밍 언어 및 프레임워크 : Java, Springboot

  - 데이터베이스 및 컨테이너 : Mysql, Docker

  - 보안 : JWT

#### b-2 Project File Tree

```
.
├── Dockerfile
├── README.md
├── build.gradle
├── config
│   └── AuthKey.p8
├── gradle
│   └── wrapper
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── gradlew
├── gradlew.bat
├── settings.gradle
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── example
    │   │           └── appVersion
    │   │               ├── AppVersionApplication.java
    │   │               ├── controller
    │   │               │   └── VersionController.java
    │   │               ├── dto
    │   │               │   └── request
    │   │               │       └── VersionRequest.java
    │   │               ├── global
    │   │               │   ├── domain
    │   │               │   │   └── entity
    │   │               │   │       └── Version.java
    │   │               │   └── repository
    │   │               │       └── VersionRepository.java
    │   │               ├── service
    │   │               │   ├── VersionService.java
    │   │               │   └── VersionServiceImpl.java
    │   │               └── util
    │   │                   └── JwtTokenProvider.java
    │   └── resources
    │       └── application.properties
    └── test
        └── java
            └── com
                └── example
                    └── appVersion
                        └── AppVersionApplicationTests.java
```

### C. 동작 과정

이 서버는 **일반 사용자**와 **관리자**의 역할이 명확히 구분되어 동작합니다.

#### 👨‍💻 **관리자: 버전 등록 (JWT 인증 필요)**

1.  **(관리자)** 서버에 로그인을 요청하여 \*\*JWT(인증 토큰)\*\*를 발급받습니다.
2.  **(관리자 → 서버)** 새로운 앱 버전 정보를 등록할 때, 발급받은 JWT를 HTTP 요청 헤더에 포함하여 서버로 전송합니다.
3.  **(서버)** JWT가 유효한지 검증하고, **유효한 경우에만** 새로운 버전 정보를 데이터베이스에 저장합니다. 이를 통해 허가된 관리자만 버전 정보를 변경할 수 있습니다.

#### 📱 **일반 사용자: 버전 확인 (JWT 인증 불필요)**

1.  **(앱)** 사용자가 T82 앱을 실행합니다.
2.  **(앱 → 서버)** 앱이 '버전 체크 서버'에게 현재 설치된 버전 정보를 보냅니다. (이 과정에서는 JWT가 필요 없습니다.)
3.  **(서버)** 데이터베이스에서 최신 버전 정보를 확인합니다.
4.  **(서버 → 앱)** 앱에게 최신 버전 정보를 알려줍니다.
5.  **(앱)** 서버가 알려준 최신 버전 정보에 따라, 업데이트가 필요하면 사용자에게 알림 창을 보여줍니다.
