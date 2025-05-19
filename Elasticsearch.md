# Elasticsearch

## Linux server 

[//]: # (no docker DB server)

#### plugin 설치 

```bash
./bin/elasticsearch-plugin install analysis-nori
```

#### index 적용 

```bash
curl -u id:pw-X PUT https://10.82.6.163:19204/autocomplete-index \
-H "Content-Type: application/json" \
-d @autocomplete-index.json
```




---

### 한글 형태소 nori plugin 설치 

```bash
docker exec -it <container_id> bin/elasticsearch-plugin install analysis-nori
```

```bash
docker restart container_ID
```

✅ 확인 방법
nori_tokenizer가 설치되었는지 확인:

```bash
curl -u elastic:1234 -X GET http://localhost:9200/_analyze -H "Content-Type: application/json" -d '{          ─╯
  "tokenizer": "nori_tokenizer",
  "text": "자동완성"
}'
```
→ 토큰이 나오면 설치 완료

### 삭제 

```bash 
# 1. 기존 인덱스 삭제
curl -u elastic:1234 -X DELETE http://localhost:9200/autocomplete-index
```

### 생성 
```bash 
# 2. 새 인덱스 생성
curl -u elastic:1234 -X PUT http://localhost:9200/autocomplete-index \
  -H "Content-Type: application/json" \
  -d @autocomplete-index.json
```

### bulk 입력 
```bash
curl -u elastic:1234 -X POST http://localhost:9200/_bulk \
  -H "Content-Type: application/json" \
  --data-binary @bulk_data.json
```
✅ 정리

| 확인 작업     | 명령어                                       |
| --------- | ----------------------------------------- |
| 인덱스 존재 확인 | `GET /_cat/indices?v`                     |
| 매핑 확인     | `GET /autocomplete-index/_mapping?pretty` |
| 분석기 동작 확인 | `POST /autocomplete-index/_analyze`       |
| 테스트 문서 등록 | `POST /autocomplete-index/_doc/{id}`      |
| 검색 확인     | `GET /autocomplete-index/_search?q=...`   |

## 확인 

```bash
curl -u elastic:1234 -X GET http://localhost:9200/_cat/indices\?v
```