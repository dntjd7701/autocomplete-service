# Redis + Elasticsearch Practice Project for AmaranthH

## 프로젝트 구조 

```bash
autocomplete-service/
│
├── build.gradle
├── docker-compose.yml
├── settings.gradle
│
├── src/
│   ├── main/
│   │   ├── java/com/example/autocomplete/
│   │   │   ├── config/           # 설정 (Redis, Elasticsearch, DB, Swagger)
│   │   │   ├── controller/       # REST API
│   │   │   ├── domain/           # Entity 및 JPA 매핑
│   │   │   ├── dto/              # Request/Response DTO
│   │   │   ├── repository/       # JPA/Redis/ES Repository
│   │   │   ├── service/          # 핵심 비즈니스 로직
│   │   │   └── util/             # 유틸 클래스 (ES query builder 등)
│   │   └── resources/
│   │       ├── application.yml
│   │       └── logback-spring.xml
│   │
│   └── test/java/com/example/autocomplete/
│       ├── controller/
│       ├── service/
│       └── repository/

```

## 흐름도 구성 

```scss
사용자 입력
   ↓
Spring REST API (검색 요청)
   ↓
Redis Cache 조회 → Cache hit → 결과 반환
                     ↓ miss
              Elasticsearch 검색
                     ↓
              DB fallback (옵션)
                     ↓
        결과 Redis 저장 후 클라이언트 응답
```


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

> Java 

```bash
11 
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

---

## Tips 

#### 실행 시 외부 config 적용하는법 

```bash 
java -jar app.jar --spring.config.location=classpath:/config/
```

#### properties 기본 로딩 경로(우선순위 높음 -> 낮음 )

| 위치                   | 설명                               | 우선순위 |
| -------------------- | -------------------------------- | ---- |
| `classpath:/config/` | ✅ `src/main/resources/config/` 안 | ★ 1등 |
| `classpath:/`        | ✅ `src/main/resources/` 안        | 2등   |
| `file:./config/`     | 외부 경로 (루트 기준 `./config/`)        | 3등   |
| `file:./`            | 외부 루트 디렉토리                       | 4등   |


