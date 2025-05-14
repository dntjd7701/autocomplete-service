# Elasticsearch

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

### index

code, 한글, 영문 3가지로 조회할수있도록 구성 

curl -X PUT http://localhost:9200/autocomplete-index \
-H "Content-Type: application/json" \
-d @autocomplete-index.json


```bash
curl -u elastic:1234 -X PUT http://localhost:9200/autocomplete-index \
  -H "Content-Type: application/json" \
  -d '{
    "settings": {
      "analysis": {
        "tokenizer": {
          "nori_edge": {
            "type": "nori_tokenizer",
            "decompound_mode": "mixed"
          },
          "edge_ngram_tokenizer_en": {
            "type": "edge_ngram",
            "min_gram": 1,
            "max_gram": 20,
            "token_chars": [ "letter", "digit" ]
          }
        },
        "analyzer": {
          "autocomplete_ko": {
            "type": "custom",
            "tokenizer": "nori_edge",
            "filter": ["lowercase"]
          },
          "autocomplete_en": {
            "type": "custom",
            "tokenizer": "edge_ngram_tokenizer_en",
            "filter": ["lowercase"]
          }
        }
      }
    },
    "mappings": {
      "properties": {
        "companyId":   { "type": "keyword" },
        "code":        { "type": "text", "analyzer": "autocomplete_en" },
        "name_ko":     { "type": "text", "analyzer": "autocomplete_ko" },
        "name_en":     { "type": "text", "analyzer": "autocomplete_en" },
        "category":    { "type": "keyword" }
      }
    }
  }'

```

```bash 
# 1. 기존 인덱스 삭제
curl -X DELETE http://localhost:9200/autocomplete-index

# 2. 새 인덱스 생성
curl -u elastic:1234 -X PUT http://localhost:9200/autocomplete-index \
  -H "Content-Type: application/json" \
  -d @autocomplete-index.json
```

```bash
curl -X POST http://localhost:9200/_bulk \
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