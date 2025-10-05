-- 더미 사용자 데이터 (이미 존재하는 경우 스킵)
INSERT IGNORE INTO member (id, email, nickname, profile_image_url, point, created_at)
VALUES
(1, 'liam@example.com', 'Liam', 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150&h=150&fit=crop&crop=face', 1500, NOW()),
(2, 'emma@example.com', 'Emma', 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150&h=150&fit=crop&crop=face', 2300, NOW()),
(3, 'noah@example.com', 'Noah', 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150&h=150&fit=crop&crop=face', 1800, NOW()),
(4, 'olivia@example.com', 'Olivia', 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=150&h=150&fit=crop&crop=face', 3200, NOW()),
(5, 'william@example.com', 'William', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&h=150&fit=crop&crop=face', 2100, NOW());

-- Journey 게시물 더미 데이터
INSERT IGNORE INTO posts (id, user_id, title, content, location, tags, images, likes, comments, views, status, created_at, updated_at)
VALUES
(1, 1, '제주도 3박 4일 완벽 가이드', '제주도에서 정말 즐거운 시간을 보냈습니다. 추천 코스와 맛집을 공유합니다!', '제주도 완산읍', '["제주도","섬산입을봄","맛집"]', '["https://images.unsplash.com/photo-1579548122080-c35fd6820ecb?fit=crop&w=600&q=80"]', 127, 23, 1542, 'ACTIVE', DATE_SUB(NOW(), INTERVAL 2 HOUR), DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(2, 2, '강원도 겨울 여행 추천 코스', '눈 내리는 강원도의 겨울은 정말 아름다워요. 속초와 강릉을 다녀왔습니다.', '강원도 속초시', '["강원도","속초","겨울여행"]', '["https://images.unsplash.com/photo-1506905925346-21bda4d32df4?fit=crop&w=600&q=80"]', 89, 15, 892, 'ACTIVE', DATE_SUB(NOW(), INTERVAL 5 HOUR), DATE_SUB(NOW(), INTERVAL 5 HOUR)),
(3, 3, '부산 맛집 투어 완전 정복', '부산의 숨겨진 맛집들을 찾아다닌 1박 2일 여행기입니다.', '부산광역시 해운대구', '["부산","맛집","해운대"]', '["https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?fit=crop&w=600&q=80"]', 256, 34, 3241, 'ACTIVE', DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY));

-- 여행 장소 더미 데이터 (강원도)
INSERT IGNORE INTO places (id, name, description, category, address, latitude, longitude, rating, review_count, image_url, created_at)
VALUES
-- 춘천
(1, '남이섬', '아름다운 자연과 메타세쿼이아 길로 유명한 섬', '관광지', '강원도 춘천시 남산면 남이섬길 1', 37.7914, 127.5258, 4.5, 1250, 'https://images.unsplash.com/photo-1570214476726-ee84f0aa6b3d?w=600', NOW()),
(2, '춘천 닭갈비 골목', '춘천의 대표 음식 닭갈비를 맛볼 수 있는 명동', '맛집', '강원도 춘천시 명동길 일대', 37.8813, 127.7300, 4.3, 890, 'https://images.unsplash.com/photo-1604908176997-125f25cc6f3d?w=600', NOW()),
(3, '김유정역', '드라마 촬영지로 유명한 간이역', '관광지', '강원도 춘천시 신동면 김유정로 1447', 37.8521, 127.5645, 4.2, 567, 'https://images.unsplash.com/photo-1474487548417-781cb71495f3?w=600', NOW()),

-- 강릉
(4, '경포대', '강릉의 대표적인 해변과 호수', '관광지', '강원도 강릉시 경포로 365', 37.8051, 128.9088, 4.6, 2340, 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=600', NOW()),
(5, '정동진 해변', '일출이 아름다운 동해안 해변', '관광지', '강원도 강릉시 강동면 정동역길 17', 37.6897, 129.0335, 4.7, 1890, 'https://images.unsplash.com/photo-1505142468610-359e7d316be0?w=600', NOW()),
(6, '초당순두부마을', '강릉의 유명한 순두부 맛집 골목', '맛집', '강원도 강릉시 초당동 일대', 37.7865, 128.9304, 4.4, 1123, 'https://images.unsplash.com/photo-1546069901-ba9599a7e63c?w=600', NOW()),

-- 속초
(7, '속초해수욕장', '속초의 대표 해변', '관광지', '강원도 속초시 해오름로 190', 38.2070, 128.5919, 4.5, 1567, 'https://images.unsplash.com/photo-1559827260-dc66d52bef19?w=600', NOW()),
(8, '속초 중앙시장', '신선한 해산물과 다양한 먹거리', '맛집', '강원도 속초시 중앙로 147', 38.2071, 128.5912, 4.6, 2109, 'https://images.unsplash.com/photo-1555939594-58d7cb561ad1?w=600', NOW()),
(9, '아바이마을', '북한 실향민들이 정착한 독특한 문화마을', '관광지', '강원도 속초시 청호동 아바이마을', 38.2156, 128.5967, 4.3, 789, 'https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=600', NOW()),

-- 양양
(10, '낙산사', '아름다운 해변 절경의 천년 고찰', '관광지', '강원도 양양군 강현면 낙산사로 100', 38.1246, 128.6277, 4.7, 1456, 'https://images.unsplash.com/photo-1478436127897-769e1b3f0f36?w=600', NOW()),
(11, '서피비치', '서핑으로 유명한 양양 해변', '관광지', '강원도 양양군 현남면 인구중앙길 119', 38.0764, 128.7191, 4.5, 934, 'https://images.unsplash.com/photo-1502680390469-be75c86b636f?w=600', NOW()),

-- 평창
(12, '오대산 월정사', '고즈넉한 산사와 전나무 숲길', '관광지', '강원도 평창군 진부면 오대산로 374-8', 37.7314, 128.5669, 4.8, 2234, 'https://images.unsplash.com/photo-1478436127897-769e1b3f0f36?w=600', NOW()),
(13, '알펜시아 리조트', '스키와 골프를 즐길 수 있는 리조트', '관광지', '강원도 평창군 대관령면 솔봉로 325', 37.6558, 128.6718, 4.4, 1678, 'https://images.unsplash.com/photo-1551698618-1dfe5d97d256?w=600', NOW()),

-- 동해
(14, '추암해변', '기암괴석이 아름다운 해변', '관광지', '강원도 동해시 추암동', 37.4697, 129.1616, 4.6, 1234, 'https://images.unsplash.com/photo-1519046904884-53103b34b206?w=600', NOW()),
(15, '망상해변', '넓고 깨끗한 백사장', '관광지', '강원도 동해시 망상동', 37.6372, 129.1253, 4.5, 987, 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?w=600', NOW()),

-- 삼척
(16, '환선굴', '신비로운 석회동굴', '관광지', '강원도 삼척시 신기면 환선로 800', 37.3561, 129.0086, 4.7, 1567, 'https://images.unsplash.com/photo-1514905552197-0610a4d8fd73?w=600', NOW()),
(17, '죽서루', '삼척의 대표 누각', '관광지', '강원도 삼척시 죽서루길 37', 37.4453, 129.1656, 4.4, 678, 'https://images.unsplash.com/photo-1548013146-72479768bada?w=600', NOW()),

-- 태백
(18, '태백산 천제단', '하늘에 제사를 지내는 제단', '관광지', '강원도 태백시 소도동 산1-1', 37.1000, 128.9158, 4.6, 890, 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?w=600', NOW()),
(19, '구문소', '황지천이 만든 자연 동굴', '관광지', '강원도 태백시 동점동 산185-5', 37.1634, 128.9892, 4.5, 567, 'https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?w=600', NOW()),

-- 홍천
(20, '홍천 은행나무숲', '가을에 아름다운 은행나무 숲', '관광지', '강원도 홍천군 내면 창촌리 1035', 37.8912, 128.1567, 4.7, 1123, 'https://images.unsplash.com/photo-1511497584788-876760111969?w=600', NOW());

-- 여행 테마 데이터
INSERT IGNORE INTO trip_themes (id, name, description, category, icon, created_at)
VALUES
(1, '자연과 힐링', '자연 속에서 휴식을 취하며 힐링하는 여행', 'nature', '🌳', NOW()),
(2, '맛집 탐방', '지역의 맛집을 찾아다니는 미식 여행', 'food', '🍽️', NOW()),
(3, '문화와 역사', '문화재와 역사 유적지를 둘러보는 여행', 'culture', '🏛️', NOW()),
(4, '액티비티', '스포츠와 레저 활동을 즐기는 여행', 'activity', '🏄', NOW()),
(5, '포토 스팟', '인스타그램 감성 사진 찍기 좋은 여행', 'photo', '📸', NOW()),
(6, '로맨틱', '연인과 함께하는 로맨틱한 여행', 'romantic', '💕', NOW()),
(7, '가족 여행', '온 가족이 즐길 수 있는 여행', 'family', '👨‍👩‍👧‍👦', NOW()),
(8, '힙한 여행', '트렌디하고 핫한 장소를 찾아가는 여행', 'trendy', '✨', NOW());

-- 동행 타입 데이터
INSERT IGNORE INTO companion_types (id, name, description, icon, created_at)
VALUES
(1, '나홀로', '혼자서 자유롭게 떠나는 여행', '🧍', NOW()),
(2, '연인과', '소중한 사람과 함께하는 여행', '💑', NOW()),
(3, '친구와', '친구들과 함께하는 즐거운 여행', '👥', NOW()),
(4, '가족과', '가족 모두가 함께하는 여행', '👨‍👩‍👧‍👦', NOW());

-- 활동 타입 데이터  
INSERT IGNORE INTO activity_types (id, name, description, icon, created_at)
VALUES
(1, '맛집 탐방', '지역 맛집과 특산물 즐기기', '🍽️', NOW()),
(2, '자연 감상', '아름다운 자연 풍경 감상하기', '🌄', NOW()),
(3, '액티비티', '다양한 야외 활동과 스포츠', '🏃', NOW()),
(4, '문화 체험', '지역 문화와 역사 체험하기', '🎭', NOW()),
(5, '휴식', '편안한 휴식과 힐링', '😌', NOW()),
(6, '쇼핑', '쇼핑과 로컬 마켓 탐방', '🛍️', NOW()),
(7, '사진 촬영', '인생샷 명소에서 사진 찍기', '📸', NOW()),
(8, '캠핑', '자연 속에서 캠핑과 바베큐', '⛺', NOW());

-- 포인트 내역 더미 데이터 (기존 엔티티 구조: member_id, amount, reason, history_date)
INSERT IGNORE INTO point_history (id, member_id, amount, reason, history_date)
VALUES
(1, 1, 500, '여행 완료 보상 - 제주도 여행', DATE_SUB(NOW(), INTERVAL 7 DAY)),
(2, 1, 300, '미션 완료 보상 - 사진 3장 업로드', DATE_SUB(NOW(), INTERVAL 5 DAY)),
(3, 1, -200, '여행 계획 프리미엄 기능 - 경로 최적화', DATE_SUB(NOW(), INTERVAL 3 DAY)),
(4, 1, 100, '리뷰 작성 보상 - 강릉 맛집 리뷰', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(5, 1, 800, '여행 완료 보상 - 강원도 여행', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(6, 2, 500, '여행 완료 보상 - 부산 여행', DATE_SUB(NOW(), INTERVAL 10 DAY)),
(7, 2, 400, '미션 완료 보상 - 5곳 방문 완료', DATE_SUB(NOW(), INTERVAL 8 DAY)),
(8, 2, -300, '여행 가이드 구매 - 프리미엄 가이드북', DATE_SUB(NOW(), INTERVAL 6 DAY)),
(9, 2, 150, '친구 초대 보상 - 친구 추천', DATE_SUB(NOW(), INTERVAL 4 DAY)),
(10, 2, 1000, '여행 완료 보상 - 전주 여행', DATE_SUB(NOW(), INTERVAL 1 DAY));

-- 배지 데이터 (기존 엔티티 구조: content, badge_condition)
INSERT IGNORE INTO badge (id, content, badge_condition)
VALUES
(1, '🌱 여행 초보자 - 첫 여행을 완료하셨습니다', '여행 1회 완료'),
(2, '🗺️ 여행 마니아 - 10회 이상의 여행을 다녀오셨습니다', '여행 10회 완료'),
(3, '📸 사진 작가 - 여행 사진을 50장 이상 업로드하셨습니다', '사진 50장 업로드'),
(4, '🍜 맛집 헌터 - 맛집 리뷰를 20개 이상 작성하셨습니다', '리뷰 20개 작성'),
(5, '🦋 소셜 나비 - 다른 사용자와 10회 이상 소통하셨습니다', '댓글 및 좋아요 100회'),
(6, '🏔️ 강원도 마스터 - 강원도 주요 명소 20곳을 방문하셨습니다', '강원도 20곳 방문'),
(7, '🎯 미션 달인 - 여행 미션을 30개 이상 완료하셨습니다', '미션 30개 완료'),
(8, '🌅 아침형 인간 - 일출 명소를 5곳 이상 방문하셨습니다', '일출 명소 5곳 방문');
