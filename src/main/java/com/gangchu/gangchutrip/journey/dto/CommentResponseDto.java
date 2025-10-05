package com.gangchu.gangchutrip.journey.dto;

import com.gangchu.gangchutrip.journey.entity.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentResponseDto {
    private String id;
    private String postId;
    private String userId;
    private UserDto user;
    private String content;
    private Integer likes;
    private Boolean isLiked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Getter
    @Builder
    public static class UserDto {
        private String id;
        private String nickname;
        private String profileImage;
        private Integer level;
    }

    public static CommentResponseDto from(Comment comment, boolean isLiked) {
        return CommentResponseDto.builder()
                .id(String.valueOf(comment.getId()))
                .postId(String.valueOf(comment.getPost().getId()))
                .userId(String.valueOf(comment.getMember().getId()))
                .user(UserDto.builder()
                        .id(String.valueOf(comment.getMember().getId()))
                        .nickname(comment.getMember().getNickname())
                        .profileImage(comment.getMember().getProfile_image_url())
                        .level(1) // TODO: Member에 level 필드 추가 필요
                        .build())
                .content(comment.getContent())
                .likes(comment.getLikes())
                .isLiked(isLiked)
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}

