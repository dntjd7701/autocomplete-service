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


---

# 2025-05-14 Transactional Outbox + Debezium + Kafka 패턴 테스트 추가 

### 흐름도 

[Spring JPA] → [Outbox 테이블에 INSERT] → [Debezium (Kafka Connect)] → [Kafka Topic] → [Consumer] → [Elasticsearch 저장]

---

# Jmeter test

result.jtl 분석

```bash
jmeter -n -t autocomplete-load-test.jmx -l result.jtl
```

GUI 생성 

```bash
jmeter -n -t autocomplete-load-test.jmx -l result.jtl -e -o report
open report/index.html  
```

ThreadGroup.num_threads: 1000

ThreadGroup.ramp_time: 10

LoopController.loops: 10

## Result  

wildcard 는 기본적으로 캐싱처리가 되지 않기에 매번 새로운 조회를 시도

keyword: *펜타*

## (Before Redis Caching)

>summary +   5657 in 00:00:08 =  749.5/s Avg:   317 Min:    35 Max:   453 Err:     0 (0.00%) Active: 340 Started: 754 Finished: 414

> summary +   4343 in 00:00:05 =  801.7/s Avg:   380 Min:    88 Max:   578 Err:     0 (0.00%) Active: 0 Started: 1000 Finished: 1000

> summary =  10000 in 00:00:13 =  771.2/s Avg:   345 Min:    35 Max:   578 Err:     0 (0.00%)

| 항목               | 값               | 설명                    |
| ---------------- | --------------- | --------------------- |
| **총 요청 수**       | `10,000`        | 아주 큰 볼륨의 테스트          |
| **총 소요 시간**      | `13초`           | 고속 처리 환경              |
| **평균 처리량**       | `771.2 req/sec` | 1초에 약 770건 처리 (우수)    |
| **평균 응답시간**      | `345 ms`        | 실무 기준으로 괜찮은 수준        |
| **최소 / 최대 응답시간** | `35 / 578 ms`   | 응답 편차는 약간 있지만 허용 범위 내 |
| **에러율**          | `0%`            | 전 요청 성공, 안정성 최고 👍    |

8코어 기준 50% 순간 사용량 발견
최대 응답시간 412ms

# (After Redis Caching)

.. 다만 정해진 키워드라 너무 긍정적인 테스트 기준이라 생각함


