package com.gangchu.gangchutrip.journey.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateCommentRequestDto {
    private String postId;
    private String content;
}

