-- 사용자 테이블 더미 데이터
INSERT INTO member (id, email, nickname, profile_image_url, point, created_at, updated_at) VALUES
(1, 'jeju_lover@example.com', '제주러버', 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?fit=crop&w=100&q=80', 1250, NOW(), NOW()),
(2, 'winter_traveler@example.com', '겨울여행러', 'https://images.unsplash.com/photo-1494790108755-2616b612b786?fit=crop&w=100&q=80', 890, NOW(), NOW()),
(3, 'busan_food_king@example.com', '부산맛집왕', 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?fit=crop&w=100&q=80', 2100, NOW(), NOW()),
(4, 'gyeongju_explorer@example.com', '경주탐방러', 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?fit=crop&w=100&q=80', 1780, NOW(), NOW()),
(5, 'jeonju_hanok@example.com', '전주한옥마을', 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?fit=crop&w=100&q=80', 940, NOW(), NOW()),
(6, 'yeosu_night_sea@example.com', '여수밤바다', 'https://images.unsplash.com/photo-1544005313-94ddf0286df2?fit=crop&w=100&q=80', 2030, NOW(), NOW()),
(7, 'andong_hahoe@example.com', '안동하회마을', 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?fit=crop&w=100&q=80', 1450, NOW(), NOW()),
(8, 'taean_peninsula@example.com', '태안반도', 'https://images.unsplash.com/photo-1494790108755-2616b612b786?fit=crop&w=100&q=80', 1670, NOW(), NOW());

-- 여행 게시물 테이블 더미 데이터
INSERT INTO journey_post (id, user_id, title, content, location, tags, images, likes, comments, views, is_liked, is_bookmarked, created_at, updated_at) VALUES
(1, 1, '제주도 3박 4일 완벽 가이드 ✈️', '제주도에서 정말 즐거운 시간을 보냈습니다! 한라산 등반부터 성산일출봉까지, 그리고 현지 맛집들까지 모두 공유합니다. 특히 제주 흑돼지는 꼭 드셔보세요! 🐷', '제주도 제주시', '제주도,한라산,맛집,성산일출봉,흑돼지', 'https://images.unsplash.com/photo-1579548122080-c35fd6820ecb?fit=crop&w=600&q=80,https://images.unsplash.com/photo-1506905925346-21bda4d32df4?fit=crop&w=600&q=80', 127, 23, 1542, false, false, DATE_SUB(NOW(), INTERVAL 2 HOUR), DATE_SUB(NOW(), INTERVAL 2 HOUR)),
(2, 2, '강원도 겨울 여행 추천 코스 ❄️', '눈 내리는 강원도의 겨울은 정말 아름다워요! 속초와 강릉을 다녀왔는데, 설악산의 설경이 장관이었습니다. 스키장도 추천해요! 🎿', '강원도 속초시', '강원도,속초,겨울여행,설악산,스키', 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?fit=crop&w=600&q=80,https://images.unsplash.com/photo-1528127269322-539801943592?fit=crop&w=600&q=80', 89, 15, 892, false, false, DATE_SUB(NOW(), INTERVAL 5 HOUR), DATE_SUB(NOW(), INTERVAL 5 HOUR)),
(3, 3, '부산 맛집 투어 완전 정복 🍜', '부산의 숨은 맛집들을 찾아다니며 먹방 투어를 했습니다! 해운대는 역시 최고! 회, 돼지국밥, 밀면까지 모든 걸 다 먹었어요. 부산 사람들 정말 친절해요! 😋', '부산 해운대구', '부산,맛집투어,해운대,회,돼지국밥', 'https://images.unsplash.com/photo-1528127269322-539801943592?fit=crop&w=600&q=80,https://images.unsplash.com/photo-1579548122080-c35fd6820ecb?fit=crop&w=600&q=80', 256, 48, 3241, true, true, DATE_SUB(NOW(), INTERVAL 1 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY)),
(4, 4, '경주 역사 여행기 🏛️', '경주에서 신라의 역사를 만끽했습니다! 불국사, 석굴암, 첨성대까지 모두 둘러봤어요. 특히 석굴암의 석가여래좌상은 정말 감동적이었습니다. 역사 공부도 되고 좋았어요! 📚', '경상북도 경주시', '경주,불국사,석굴암,첨성대,신라', 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?fit=crop&w=600&q=80,https://images.unsplash.com/photo-1528127269322-539801943592?fit=crop&w=600&q=80', 178, 31, 2156, false, false, DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY)),
(5, 5, '전주 한옥마을에서의 하루 🏮', '전주 한옥마을에서 전통문화를 체험했습니다! 한복 입고 사진도 찍고, 전주비빔밥도 맛있게 먹었어요. 특히 한옥에서 자는 경험은 정말 특별했습니다. 추천해요! 👘', '전라북도 전주시', '전주,한옥마을,한복,비빔밥,전통문화', 'https://images.unsplash.com/photo-1579548122080-c35fd6820ecb?fit=crop&w=600&q=80,https://images.unsplash.com/photo-1506905925346-21bda4d32df4?fit=crop&w=600&q=80', 94, 18, 1234, true, false, DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
(6, 6, '여수 밤바다의 로맨스 🌙', '여수에서 연인과 함께한 로맨틱한 여행! 여수밤바다에서 바다전망대도 보고, 오동도도 걸었어요. 특히 여수 갈치조림은 정말 맛있었습니다. 커플여행 추천! 💕', '전라남도 여수시', '여수,밤바다,오동도,갈치조림,커플여행', 'https://images.unsplash.com/photo-1528127269322-539801943592?fit=crop&w=600&q=80,https://images.unsplash.com/photo-1579548122080-c35fd6820ecb?fit=crop&w=600&q=80', 203, 42, 1876, false, true, DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY)),
(7, 7, '안동 하회마을에서의 전통체험 🎭', '안동 하회마을에서 전통문화를 깊이 체험했습니다! 하회탈춤도 보고, 안동소주도 마셔봤어요. 특히 하회별신굿탈놀이는 정말 재미있었습니다. 전통문화 애호가들에게 추천! 🎪', '경상북도 안동시', '안동,하회마을,하회탈춤,안동소주,전통문화', 'https://images.unsplash.com/photo-1506905925346-21bda4d32df4?fit=crop&w=600&q=80,https://images.unsplash.com/photo-1528127269322-539801943592?fit=crop&w=600&q=80', 145, 27, 1654, true, true, DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY)),
(8, 8, '태안반도에서의 힐링여행 🌊', '태안반도에서 바다와 함께한 힐링여행! 신두리해수욕장에서 조개잡기도 하고, 꽃지해수욕장에서 일몰도 봤어요. 특히 태안의 신선한 해산물은 정말 맛있었습니다! 🦀', '충청남도 태안군', '태안반도,신두리해수욕장,꽃지해수욕장,해산물,일몰', 'https://images.unsplash.com/photo-1579548122080-c35fd6820ecb?fit=crop&w=600&q=80,https://images.unsplash.com/photo-1506905925346-21bda4d32df4?fit=crop&w=600&q=80', 167, 35, 1987, false, false, DATE_SUB(NOW(), INTERVAL 14 DAY), DATE_SUB(NOW(), INTERVAL 14 DAY));

-- 여행 옵션 더미 데이터
INSERT INTO trip_companion (id, label, icon, order_index) VALUES
(1, '혼자', '👤', 1),
(2, '연인', '💕', 2),
(3, '가족', '👨‍👩‍👧‍👦', 3),
(4, '친구', '👥', 4),
(5, '동료', '👔', 5);

INSERT INTO trip_activity (id, label, icon, order_index) VALUES
(1, '맛집투어', '🍽️', 1),
(2, '관광지', '🏛️', 2),
(3, '자연경관', '🌲', 3),
(4, '쇼핑', '🛍️', 4),
(5, '액티비티', '🎯', 5);

INSERT INTO trip_theme (id, label, icon, order_index) VALUES
(1, '힐링', '🧘', 1),
(2, '모험', '🏔️', 2),
(3, '문화', '🎭', 3),
(4, '로맨틱', '💕', 4),
(5, '가족여행', '👨‍👩‍👧‍👦', 5);

-- 미션 더미 데이터
INSERT INTO mission (id, title, description, type, points, is_active, created_at) VALUES
(1, '사진 찍기', '여행지에서 멋진 사진을 찍어보세요!', 'PHOTO', 100, true, NOW()),
(2, '포즈 챌린지', '재미있는 포즈로 사진을 찍어보세요!', 'POSE', 150, true, NOW()),
(3, '랜덤 챌린지', '랜덤으로 제시되는 미션을 완료하세요!', 'RANDOM', 200, true, NOW()),
(4, '룰렛 미션', '룰렛을 돌려서 나온 미션을 완료하세요!', 'ROULETTE', 120, true, NOW()),
(5, '온도 체크', '현재 온도를 측정해보세요!', 'TEMPERATURE', 80, true, NOW()),
(6, '텍스트 입력', '여행에 대한 생각을 적어보세요!', 'TEXT', 90, true, NOW());

-- 장소 더미 데이터
INSERT INTO place (id, name, address, latitude, longitude, category, description, created_at) VALUES
(1, '한라산', '제주특별자치도 제주시', 33.3617, 126.5292, 'MOUNTAIN', '제주도의 최고봉', NOW()),
(2, '성산일출봉', '제주특별자치도 서귀포시', 33.4584, 126.9422, 'VOLCANO', '일출 명소', NOW()),
(3, '설악산', '강원도 속초시', 38.1466, 128.4236, 'MOUNTAIN', '겨울 설경이 아름다운 산', NOW()),
(4, '해운대해수욕장', '부산광역시 해운대구', 35.1587, 129.1603, 'BEACH', '부산의 대표 해수욕장', NOW()),
(5, '불국사', '경상북도 경주시', 35.7894, 129.3319, 'TEMPLE', '신라의 대표 사찰', NOW()),
(6, '석굴암', '경상북도 경주시', 35.7894, 129.3319, 'TEMPLE', '유네스코 세계문화유산', NOW()),
(7, '전주한옥마을', '전라북도 전주시', 35.8242, 127.1480, 'VILLAGE', '전통 한옥 마을', NOW()),
(8, '여수밤바다', '전라남도 여수시', 34.7604, 127.6622, 'BAY', '아름다운 밤바다 전망', NOW()),
(9, '하회마을', '경상북도 안동시', 36.5392, 128.5186, 'VILLAGE', '전통 민속마을', NOW()),
(10, '신두리해수욕장', '충청남도 태안군', 36.8167, 126.2000, 'BEACH', '조개잡기 명소', NOW());
