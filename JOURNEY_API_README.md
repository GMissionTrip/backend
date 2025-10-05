# 둘러보기 (Journey) API 구현 완료 ✅

## 📦 구현된 내용

### 1. **엔티티 (Entity)**
- ✅ `Post`: 게시물
- ✅ `PostLike`: 게시물 좋아요
- ✅ `Comment`: 댓글
- ✅ `CommentLike`: 댓글 좋아요
- ✅ `Bookmark`: 북마크
- ✅ `Report`: 신고

### 2. **Repository**
- ✅ `PostRepository`: 게시물 조회 (피드/인기순)
- ✅ `PostLikeRepository`: 좋아요 관리
- ✅ `CommentRepository`: 댓글 조회
- ✅ `BookmarkRepository`: 북마크 관리
- ✅ `ReportRepository`: 신고 관리

### 3. **Service**
- ✅ `JourneyService`: 모든 비즈니스 로직 구현
  - 게시물 목록 조회 (feed/popular)
  - 게시물 상세 조회 (조회수 자동 증가)
  - 좋아요 토글
  - 북마크 토글
  - 댓글 조회/작성/삭제
  - 게시물 신고 (중복 방지, 5회 이상 시 자동 숨김)

### 4. **Controller**
- ✅ `JourneyController`: 8개 API 엔드포인트
  - `GET /api/journey/posts` - 게시물 목록
  - `GET /api/journey/posts/{postId}` - 게시물 상세
  - `POST /api/journey/posts/{postId}/like` - 좋아요 토글
  - `POST /api/journey/posts/{postId}/bookmark` - 북마크 토글
  - `GET /api/journey/posts/{postId}/comments` - 댓글 목록
  - `POST /api/journey/comments` - 댓글 작성
  - `DELETE /api/journey/comments/{commentId}` - 댓글 삭제
  - `POST /api/journey/reports` - 게시물 신고

- ✅ `TestJourneyController`: 더미 데이터 생성
  - `POST /api/test/journey/init-dummy-data` - 테스트 데이터 생성

### 5. **DTO**
- ✅ `PostResponseDto`: 게시물 응답
- ✅ `CommentResponseDto`: 댓글 응답
- ✅ `CreateCommentRequestDto`: 댓글 작성 요청
- ✅ `ReportRequestDto`: 신고 요청

---

## 🚀 사용 방법

### 1. **서버 실행**
```bash
cd backend
./gradlew bootRun
```

### 2. **더미 데이터 생성**
```bash
curl -X POST http://localhost:8080/api/test/journey/init-dummy-data
```

### 3. **API 호출 예시**

#### 게시물 목록 조회 (피드)
```bash
curl -X GET "http://localhost:8080/api/journey/posts?type=feed" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

#### 게시물 목록 조회 (인기순)
```bash
curl -X GET "http://localhost:8080/api/journey/posts?type=popular" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

#### 좋아요 토글
```bash
curl -X POST "http://localhost:8080/api/journey/posts/1/like" \
  -H "Authorization: Bearer {JWT_TOKEN}"
```

#### 댓글 작성
```bash
curl -X POST "http://localhost:8080/api/journey/comments" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "postId": "1",
    "content": "좋은 정보 감사합니다!"
  }'
```

#### 신고
```bash
curl -X POST "http://localhost:8080/api/journey/reports" \
  -H "Authorization: Bearer {JWT_TOKEN}" \
  -H "Content-Type: application/json" \
  -d '{
    "postId": "1",
    "reason": "spam",
    "description": "광고성 게시물입니다."
  }'
```

---

## 🗄️ 데이터베이스 마이그레이션

```sql
-- posts 테이블
CREATE TABLE posts (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  title VARCHAR(200) NOT NULL,
  content TEXT NOT NULL,
  location VARCHAR(100) NOT NULL,
  tags JSON,
  images JSON,
  likes INT DEFAULT 0,
  comments INT DEFAULT 0,
  views INT DEFAULT 0,
  status ENUM('ACTIVE', 'HIDDEN', 'DELETED') DEFAULT 'ACTIVE',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES member(id)
);

-- post_likes 테이블
CREATE TABLE post_likes (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  post_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY unique_like (post_id, user_id),
  FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
  FOREIGN KEY (user_id) REFERENCES member(id) ON DELETE CASCADE
);

-- comments 테이블
CREATE TABLE comments (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  post_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  content TEXT NOT NULL,
  likes INT DEFAULT 0,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
  FOREIGN KEY (user_id) REFERENCES member(id) ON DELETE CASCADE
);

-- comment_likes 테이블
CREATE TABLE comment_likes (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  comment_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY unique_comment_like (comment_id, user_id),
  FOREIGN KEY (comment_id) REFERENCES comments(id) ON DELETE CASCADE,
  FOREIGN KEY (user_id) REFERENCES member(id) ON DELETE CASCADE
);

-- bookmarks 테이블
CREATE TABLE bookmarks (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  post_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY unique_bookmark (post_id, user_id),
  FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
  FOREIGN KEY (user_id) REFERENCES member(id) ON DELETE CASCADE
);

-- reports 테이블
CREATE TABLE reports (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  post_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  reason ENUM('INAPPROPRIATE', 'SPAM', 'HARASSMENT', 'FAKE', 'OTHER') NOT NULL,
  description TEXT,
  status ENUM('PENDING', 'REVIEWED', 'RESOLVED') DEFAULT 'PENDING',
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (post_id) REFERENCES posts(id) ON DELETE CASCADE,
  FOREIGN KEY (user_id) REFERENCES member(id) ON DELETE CASCADE
);
```

---

## ✨ 구현된 기능

### 1. **낙관적 업데이트 (Optimistic Update)**
- 프론트엔드에서 즉시 UI 업데이트
- 백엔드 오류 시 롤백

### 2. **자동 조회수 증가**
- 게시물 상세 조회 시 `views` 자동 +1

### 3. **신고 기능**
- 중복 신고 방지
- 5회 이상 신고 시 자동 숨김 처리

### 4. **인기 게시물 알고리즘**
```java
score = (likes * 2) + (comments * 3) + (views * 0.1)
```

### 5. **JWT 인증**
- 모든 API는 JWT 토큰 필요
- `@AuthenticationPrincipal`로 현재 사용자 정보 조회

---

## 📝 TODO

- [ ] 댓글 좋아요 기능 구현 (`CommentLike` 사용)
- [ ] 게시물 작성 API 추가
- [ ] 게시물 수정/삭제 API 추가
- [ ] 이미지 업로드 API 추가 (S3)
- [ ] 페이지네이션 구현
- [ ] 아카이브에 게시물 추가 API 구현

---

## 🐛 알려진 이슈

- `Member` 엔티티에 `level`, `gender` 필드가 없음 (현재 더미값 사용)
- 조회수 중복 카운트 방지 미구현 (선택사항)

---

## 🎉 완료!

백엔드 API가 성공적으로 구현되었습니다!  
프론트엔드의 낙관적 업데이트와 함께 사용하면 빠르고 부드러운 UX를 제공할 수 있습니다.

