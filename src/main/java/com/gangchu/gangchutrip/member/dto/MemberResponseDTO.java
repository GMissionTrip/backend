package com.gangchu.gangchutrip.member.dto;

import com.gangchu.gangchutrip.auth.entity.Member;
import java.util.Date;
import java.util.List;

public record MemberResponseDTO(Long id,
    String email,
    String nickname,
    String profileImageUrl,
    Integer point,
    Date createdAt, List<Long> badges) {

    public static MemberResponseDTO from(Member m, List<Long> badges) {
        return new MemberResponseDTO(
            m.getId(),
            m.getEmail(),
            m.getNickname(),
            m.getProfile_image_url(),
            m.getPoint(),
            m.getCreated_at(),
            badges
        );
    }
}
