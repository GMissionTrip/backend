# 🧪 GMTrip 유저 플로우 테스트 가이드

## 📋 전체 플로우 개요

```
1. 회원가입/로그인 (카카오)
   ↓
2. 여행 생성 (지역, 날짜, 테마 선택)
   ↓
3. 미션 자동 생성 (6개 기본 미션)
   ↓
4. 여행 시작
   ↓
5. 미션 수행 (사진 업로드, 텍스트 입력 등)
   ↓
6. 리워드 획득 (포인트 지급)
   ↓
7. 여행 완료
   ↓
8. 아카이브 저장
```

## 🔧 API 엔드포인트

### 1. 회원가입/로그인
```http
POST /api/auth/kakao/callback
Content-Type: application/json

{
  "code": "카카오_인증_코드"
}
```

### 2. 여행 생성
```http
POST /api/travels
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json

{
  "title": "강원도 가족 여행",
  "region": "강원도",
  "startDate": "2025-01-15T00:00:00",
  "endDate": "2025-01-20T00:00:00",
  "travelWith": "FAMILY",
  "accompanyNum": 4,
  "mainTheme": "자연과 힐링",
  "subTheme": "맛집 탐방"
}
```

**응답**: Travel 객체 + 자동 생성된 6개 미션

### 3. 미션 목록 조회
```http
GET /api/missions/travel/{travelId}
Authorization: Bearer {JWT_TOKEN}
```

### 4. 여행 시작
```http
POST /api/travels/{travelId}/start
Authorization: Bearer {JWT_TOKEN}
```

### 5. 미션 수행
```http
POST /api/missions/{missionId}/submit
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json

{
  "photos": [
    "https://example.com/photo1.jpg",
    "https://example.com/photo2.jpg",
    "https://example.com/photo3.jpg"
  ],
  "text": "오늘 하루 요약",
  "temperature": 80,
  "selectedOption": "option1"
}
```

**자동 처리**:
- 미션 상태 → COMPLETED
- 포인트 지급 (Member.totalPoints 증가)
- PointHistory 레코드 생성

### 6. 포인트 히스토리 조회
```http
GET /api/me/points
Authorization: Bearer {JWT_TOKEN}
```

### 7. 여행 완료
```http
POST /api/travels/{travelId}/complete
Authorization: Bearer {JWT_TOKEN}
```

**자동 처리**:
- 여행 상태 → COMPLETED
- Archive 레코드 생성 (자동)

### 8. 아카이브 조회
```http
GET /api/archives
Authorization: Bearer {JWT_TOKEN}
```

## 🧪 통합 테스트 API

### 전체 플로우 한번에 테스트
```http
POST /api/test/user-flow/complete-flow
Authorization: Bearer {JWT_TOKEN}
```

**이 API는 자동으로 다음을 수행합니다**:
1. ✅ 회원 정보 확인
2. ✅ 여행 생성
3. ✅ 미션 자동 생성 확인
4. ✅ 여행 시작
5. ✅ 첫 번째 미션 수행
6. ✅ 포인트 지급 확인
7. ✅ 여행 완료
8. ✅ 최종 통계 출력

**응답 예시**:
```json
{
  "code": "SUCCESS",
  "data": {
    "step1_member": {
      "id": 1,
      "nickname": "테스터",
      "email": "test@example.com",
      "totalPoints": 0
    },
    "step2_travel": {
      "id": 1,
      "title": "강원도 가족 여행",
      "status": "PLANNING"
    },
    "step3_missions": [
      {
        "id": 1,
        "title": "3컷 여행 요약",
        "points": 250,
        "status": "UNLOCKED"
      }
      // ... 5개 더
    ],
    "step4_started_travel": {
      "status": "IN_PROGRESS"
    },
    "step5_completed_mission": {
      "id": 1,
      "status": "COMPLETED"
    },
    "step5_total_points": 250,
    "step7_completed_travel": {
      "status": "COMPLETED"
    },
    "step8_final_stats": {
      "completedMissions": 1,
      "totalMissions": 6,
      "completionRate": 16.67,
      "totalPoints": 250,
      "level": 1
    },
    "success": true,
    "message": "전체 유저 플로우 테스트 완료!"
  }
}
```

### 내 데이터 전체 조회
```http
GET /api/test/user-flow/my-data
Authorization: Bearer {JWT_TOKEN}
```

**응답**: 회원 정보, 여행 목록, 미션 목록, 포인트 히스토리 전체

## 📊 데이터베이스 변화 확인

### 테스트 전
```sql
-- 회원
SELECT * FROM member WHERE id = 1;
-- totalPoints: 0

-- 여행
SELECT COUNT(*) FROM travel WHERE member_id = 1;
-- 0

-- 미션
SELECT COUNT(*) FROM missions;
-- 0

-- 포인트 히스토리
SELECT COUNT(*) FROM point_history WHERE member_id = 1;
-- 0
```

### 테스트 후
```sql
-- 회원
SELECT * FROM member WHERE id = 1;
-- totalPoints: 250 (첫 미션 완료)

-- 여행
SELECT id, title, status FROM travel WHERE member_id = 1;
-- 1, "강원도 가족 여행", "COMPLETED"

-- 미션
SELECT id, title, status, points FROM missions WHERE travel_id = 1;
-- 6개 미션, 1개 COMPLETED, 5개 UNLOCKED

-- 포인트 히스토리
SELECT * FROM point_history WHERE member_id = 1 ORDER BY history_date DESC;
-- "미션 완료: 3컷 여행 요약", 250P
```

## 🚀 테스트 실행 방법

### 1. cURL로 테스트
```bash
# 1. 카카오 로그인하여 JWT 토큰 획득
TOKEN="your_jwt_token_here"

# 2. 전체 플로우 테스트 실행
curl -X POST http://localhost:8080/api/test/user-flow/complete-flow \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json"

# 3. 결과 확인
curl -X GET http://localhost:8080/api/test/user-flow/my-data \
  -H "Authorization: Bearer $TOKEN"
```

### 2. Swagger UI로 테스트
1. http://localhost:8080/swagger-ui/index.html 접속
2. Authorize 버튼 클릭 → JWT 토큰 입력
3. `user-flow-test-controller` 섹션 찾기
4. `POST /api/test/user-flow/complete-flow` 실행
5. 응답 확인

### 3. Postman으로 테스트
1. Collection 생성
2. Authorization → Bearer Token 설정
3. 각 API 엔드포인트 테스트
4. 또는 `/api/test/user-flow/complete-flow` 한번에 테스트

## 📈 예상 결과

✅ **성공 시나리오**:
- 여행 1개 생성
- 미션 6개 자동 생성
- 미션 1개 완료
- 포인트 250P 획득
- PointHistory 1건 생성
- 여행 상태 PLANNING → IN_PROGRESS → COMPLETED
- 최종 통계: 완료율 16.67%

❌ **실패 가능한 경우**:
- JWT 토큰 없음 → 401 Unauthorized
- 잘못된 요청 데이터 → 400 Bad Request
- 권한 없음 → 403 Forbidden
- 리소스 없음 → 404 Not Found

## 🔍 로그 확인

백엔드 로그에서 다음 내용 확인:
```
=== 1단계: 회원 정보 확인 ===
회원 ID: 1, 닉네임: 테스터, 이메일: test@example.com

=== 2단계: 여행 생성 ===
여행 생성 완료 - ID: 1, 제목: 강원도 가족 여행

=== 3단계: 자동 생성된 미션 확인 ===
생성된 미션 수: 6
- 미션: 3컷 여행 요약 (250P)
- 미션: 감정 온도계 (120P)
...

=== 8단계: 최종 통계 ===
완료 미션: 1/6
완료율: 16.67%
총 포인트: 250P
```

## 🎯 다음 단계

1. ✅ 기본 플로우 테스트 완료
2. ✅ DB 저장 확인
3. ✅ 포인트 시스템 작동 확인
4. ⏭️ 프론트엔드 연동
5. ⏭️ 실제 사용자 시나리오 테스트
6. ⏭️ 에러 처리 시나리오 테스트

