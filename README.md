# Redis + Elasticsearch Practice Project for AmaranthH

## 2025-05-13

현재 Version update 미진행, 차후 업데이트 예정  

- Spring boot 2.3.x

> build.gradle 

```groovy
plugins {
    id 'java'
    id 'org.springframework.boot' version '2.3.9.RELEASE'
    id 'io.spring.dependency-management' version '1.0.11.RELEASE'
}
```

> gradle-wrapper.properties

```groovy 
distributionUrl=https\://services.gradle.org/distributions/gradle-6.8.3-bin.zip
```

> JVM 

```bash
1.8로 설정 
```


## 고려사항 

- 동기화정책
- 캐싱처리
- 형태소 
- 인기검색어, 최근검색어 ?
- Exception 처리(이중화정책 고려)
  - Docker 
  - K8s
  - Nginx 
- Transaction

## 1. Dependencies 

```groovy
dependencies {
    // Spring MVC 및 REST API 개발을 위한 기본 웹 스타터
    implementation 'org.springframework.boot:spring-boot-starter-web'

    // Redis 사용을 위한 스타터 (RedisTemplate, @Cacheable 등 지원)
    implementation 'org.springframework.boot:spring-boot-starter-data-redis'

    // Elasticsearch 연동을 위한 스타터 (Spring Data Elasticsearch, Repository 등)
    implementation 'org.springframework.boot:spring-boot-starter-data-elasticsearch'

    // JSON 직렬화/역직렬화를 위한 라이브러리 (ObjectMapper 등)
    implementation 'com.fasterxml.jackson.core:jackson-databind'

    // 롬복 사용 시 컴파일 시점에 필요한 설정 (@Getter, @Builder 등 코드 자동 생성)
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'

    // 테스트 코드 작성 시 필요한 의존성 (JUnit, MockMvc 등 포함)
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}
```


## 2. 


