package com.gangchu.gangchutrip.journey.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/journey")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001", "http://localhost:3002"})
public class JourneyController {

    @GetMapping("/posts")
    public ResponseEntity<Map<String, Object>> getPosts(
            @RequestParam(defaultValue = "feed") String type,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit) {
        
        // DB에서 로드된 더미 데이터 생성
        List<Map<String, Object>> posts = new ArrayList<>();
        
        // 실제 DB 데이터 시뮬레이션
        String[] titles = {
            "제주도 3박 4일 완벽 가이드 ✈️",
            "강원도 겨울 여행 추천 코스 ❄️", 
            "부산 맛집 투어 완전 정복 🍜",
            "경주 역사 여행기 🏛️",
            "전주 한옥마을에서의 하루 🏮",
            "여수 밤바다의 로맨스 🌙",
            "안동 하회마을에서의 전통체험 🎭",
            "태안반도에서의 힐링여행 🌊"
        };
        
        String[] contents = {
            "제주도에서 정말 즐거운 시간을 보냈습니다! 한라산 등반부터 성산일출봉까지, 그리고 현지 맛집들까지 모두 공유합니다. 특히 제주 흑돼지는 꼭 드셔보세요! 🐷",
            "눈 내리는 강원도의 겨울은 정말 아름다워요! 속초와 강릉을 다녀왔는데, 설악산의 설경이 장관이었습니다. 스키장도 추천해요! 🎿",
            "부산의 숨은 맛집들을 찾아다니며 먹방 투어를 했습니다! 해운대는 역시 최고! 회, 돼지국밥, 밀면까지 모든 걸 다 먹었어요. 부산 사람들 정말 친절해요! 😋",
            "경주에서 신라의 역사를 만끽했습니다! 불국사, 석굴암, 첨성대까지 모두 둘러봤어요. 특히 석굴암의 석가여래좌상은 정말 감동적이었습니다. 역사 공부도 되고 좋았어요! 📚",
            "전주 한옥마을에서 전통문화를 체험했습니다! 한복 입고 사진도 찍고, 전주비빔밥도 맛있게 먹었어요. 특히 한옥에서 자는 경험은 정말 특별했습니다. 추천해요! 👘",
            "여수에서 연인과 함께한 로맨틱한 여행! 여수밤바다에서 바다전망대도 보고, 오동도도 걸었어요. 특히 여수 갈치조림은 정말 맛있었습니다. 커플여행 추천! 💕",
            "안동 하회마을에서 전통문화를 깊이 체험했습니다! 하회탈춤도 보고, 안동소주도 마셔봤어요. 특히 하회별신굿탈놀이는 정말 재미있었습니다. 전통문화 애호가들에게 추천! 🎪",
            "태안반도에서 바다와 함께한 힐링여행! 신두리해수욕장에서 조개잡기도 하고, 꽃지해수욕장에서 일몰도 봤어요. 특히 태안의 신선한 해산물은 정말 맛있었습니다! 🦀"
        };
        
        String[] locations = {
            "제주도 제주시",
            "강원도 속초시", 
            "부산 해운대구",
            "경상북도 경주시",
            "전라북도 전주시",
            "전라남도 여수시",
            "경상북도 안동시",
            "충청남도 태안군"
        };
        
        String[] nicknames = {
            "제주러버", "겨울여행러", "부산맛집왕", "경주탐방러",
            "전주한옥마을", "여수밤바다", "안동하회마을", "태안반도"
        };
        
        int[] likes = {127, 89, 256, 178, 94, 203, 145, 167};
        int[] comments = {23, 15, 48, 31, 18, 42, 27, 35};
        int[] views = {1542, 892, 3241, 2156, 1234, 1876, 1654, 1987};
        
        for (int i = 0; i < Math.min(limit, titles.length); i++) {
            Map<String, Object> post = new HashMap<>();
            post.put("id", (long)(i + 1));
            post.put("title", titles[i]);
            post.put("content", contents[i]);
            post.put("location", locations[i]);
            post.put("likes", likes[i]);
            post.put("comments", comments[i]);
            post.put("views", views[i]);
            post.put("isLiked", i % 3 == 0);
            post.put("isBookmarked", i % 4 == 0);
            post.put("createdAt", java.time.LocalDateTime.now().minusDays(i + 1).toString());
            post.put("updatedAt", java.time.LocalDateTime.now().minusDays(i + 1).toString());
            
            // 사용자 정보
            Map<String, Object> user = new HashMap<>();
            user.put("id", "user" + (i + 1));
            user.put("nickname", nicknames[i]);
            user.put("profileImage", "https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?fit=crop&w=100&q=80");
            user.put("level", 15 + i * 3);
            user.put("gender", i % 2 == 0 ? "male" : "female");
            post.put("user", user);
            
            // 태그
            String[][] tags = {
                {"제주도", "한라산", "맛집", "성산일출봉", "흑돼지"},
                {"강원도", "속초", "겨울여행", "설악산", "스키"},
                {"부산", "맛집투어", "해운대", "회", "돼지국밥"},
                {"경주", "불국사", "석굴암", "첨성대", "신라"},
                {"전주", "한옥마을", "한복", "비빔밥", "전통문화"},
                {"여수", "밤바다", "오동도", "갈치조림", "커플여행"},
                {"안동", "하회마을", "하회탈춤", "안동소주", "전통문화"},
                {"태안반도", "신두리해수욕장", "꽃지해수욕장", "해산물", "일몰"}
            };
            post.put("tags", tags[i]);
            
            // 이미지
            String[] images = {
                "https://images.unsplash.com/photo-1579548122080-c35fd6820ecb?fit=crop&w=600&q=80",
                "https://images.unsplash.com/photo-1506905925346-21bda4d32df4?fit=crop&w=600&q=80"
            };
            post.put("images", images);
            
            posts.add(post);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", posts);
        response.put("pagination", Map.of(
            "page", page,
            "limit", limit,
            "total", titles.length,
            "totalPages", (int) Math.ceil((double) titles.length / limit)
        ));
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<Map<String, Object>> toggleLike(@PathVariable Long postId) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", Map.of(
            "isLiked", true,
            "likes", 15
        ));
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<Map<String, Object>> getComments(@PathVariable Long postId) {
        List<Map<String, Object>> comments = new ArrayList<>();
        
        for (int i = 1; i <= 3; i++) {
            Map<String, Object> comment = new HashMap<>();
            comment.put("id", "comment_" + i);
            comment.put("content", "댓글 " + i);
            comment.put("author", "사용자" + i);
            comment.put("createdAt", "2024-01-01T00:00:00Z");
            comments.add(comment);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", comments);
        
        return ResponseEntity.ok(response);
    }
}
