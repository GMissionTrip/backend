package com.gangchu.gangchutrip.journey.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gangchu.gangchutrip.auth.entity.Member;
import com.gangchu.gangchutrip.auth.repository.MemberRepository;
import com.gangchu.gangchutrip.global.response.ApiResponse;
import com.gangchu.gangchutrip.global.response.ApiResponseFactory;
import com.gangchu.gangchutrip.global.response.ResponseCode;
import com.gangchu.gangchutrip.journey.entity.Post;
import com.gangchu.gangchutrip.journey.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * 테스트용 더미 데이터 생성 컨트롤러
 */
@RestController
@RequestMapping("/api/test/journey")
@RequiredArgsConstructor
public class TestJourneyController {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final ObjectMapper objectMapper;

    @PostMapping("/init-dummy-data")
    public ResponseEntity<ApiResponse<String>> initDummyData() {
        try {
            // 첫 번째 멤버 가져오기 (없으면 생성)
            Member member = memberRepository.findAll().stream()
                    .findFirst()
                    .orElseGet(() -> {
                        Member newMember = new Member();
                        newMember.setEmail("test@example.com");
                        newMember.setNickname("테스트유저");
                        newMember.setPoint(0);
                        return memberRepository.save(newMember);
                    });

            // 더미 게시물 생성
            createDummyPost(member, 
                "제주도 3박 4일 완벽 가이드",
                "제주도에서 정말 즐거운 시간을 보냈습니다. 추천 코스와 맛집을 공유합니다!",
                "제주도 완산읍",
                Arrays.asList("제주도", "섬산입을봄", "맛집"),
                Arrays.asList("https://images.unsplash.com/photo-1579548122080-c35fd6820ecb?fit=crop&w=600&q=80"),
                127, 23, 1542
            );

            createDummyPost(member,
                "강원도 겨울 여행 추천 코스",
                "눈 내리는 강원도의 겨울은 정말 아름다워요. 속초와 강릉을 다녀왔습니다.",
                "강원도 속초시",
                Arrays.asList("강원도", "속초", "겨울여행"),
                Arrays.asList("https://images.unsplash.com/photo-1506905925346-21bda4d32df4?fit=crop&w=600&q=80"),
                89, 15, 892
            );

            createDummyPost(member,
                "부산 맛집 투어 완전 정복",
                "부산의 숨은 맛집들을 찾아다니며 먹방 투어를 했습니다. 해운대는 역시 최고!",
                "부산 해운대구",
                Arrays.asList("부산", "맛집투어", "해운대"),
                Arrays.asList("https://images.unsplash.com/photo-1528127269322-539801943592?fit=crop&w=600&q=80"),
                256, 48, 3241
            );

            return ResponseEntity.ok(ApiResponse.success(ResponseCode.SUCCESS, "더미 데이터 생성 완료"));
        } catch (Exception e) {
            return ResponseEntity.status(ResponseCode.INTERNAL_SERVER_ERROR.getStatus())
                    .body(ApiResponse.error(ResponseCode.INTERNAL_SERVER_ERROR, "더미 데이터 생성 실패: " + e.getMessage()));
        }
    }

    private void createDummyPost(Member member, String title, String content, String location,
                                  List<String> tags, List<String> images,
                                  int likes, int comments, int views) {
        try {
            Post post = new Post();
            post.setMember(member);
            post.setTitle(title);
            post.setContent(content);
            post.setLocation(location);
            post.setTags(objectMapper.writeValueAsString(tags));
            post.setImages(objectMapper.writeValueAsString(images));
            post.setLikes(likes);
            post.setComments(comments);
            post.setViews(views);
            post.setStatus(Post.PostStatus.ACTIVE);
            
            postRepository.save(post);
        } catch (Exception e) {
            System.err.println("게시물 생성 실패: " + e.getMessage());
        }
    }
}

