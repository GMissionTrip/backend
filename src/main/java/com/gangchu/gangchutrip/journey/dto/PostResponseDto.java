package com.gangchu.gangchutrip.journey.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gangchu.gangchutrip.journey.entity.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class PostResponseDto {
    private String id;
    private String userId;
    private UserDto user;
    private String title;
    private String content;
    private String location;
    private List<String> tags;
    private List<String> images;
    private Integer likes;
    private Integer comments;
    private Integer views;
    private Boolean isLiked;
    private Boolean isBookmarked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Getter
    @Builder
    public static class UserDto {
        private String id;
        private String nickname;
        private String profileImage;
        private Integer level;
        private String gender;
    }

    public static PostResponseDto from(Post post, boolean isLiked, boolean isBookmarked) {
        ObjectMapper mapper = new ObjectMapper();
        List<String> tags = new ArrayList<>();
        List<String> images = new ArrayList<>();

        try {
            if (post.getTags() != null) {
                tags = mapper.readValue(post.getTags(), List.class);
            }
            if (post.getImages() != null) {
                images = mapper.readValue(post.getImages(), List.class);
            }
        } catch (Exception e) {
            // JSON 파싱 실패 시 빈 리스트 유지
        }

        return PostResponseDto.builder()
                .id(String.valueOf(post.getId()))
                .userId(String.valueOf(post.getMember().getId()))
                .user(UserDto.builder()
                        .id(String.valueOf(post.getMember().getId()))
                        .nickname(post.getMember().getNickname())
                        .profileImage(post.getMember().getProfile_image_url())
                        .level(1) // TODO: Member에 level 필드 추가 필요
                        .gender(null) // TODO: Member에 gender 필드 추가 필요
                        .build())
                .title(post.getTitle())
                .content(post.getContent())
                .location(post.getLocation())
                .tags(tags)
                .images(images)
                .likes(post.getLikes())
                .comments(post.getComments())
                .views(post.getViews())
                .isLiked(isLiked)
                .isBookmarked(isBookmarked)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}

