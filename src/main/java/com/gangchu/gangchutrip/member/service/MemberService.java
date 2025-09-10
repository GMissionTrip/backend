package com.gangchu.gangchutrip.member.service;


import com.gangchu.gangchutrip.auth.entity.Member;
import com.gangchu.gangchutrip.auth.repository.MemberRepository;
import com.gangchu.gangchutrip.global.entity.BadgeOwner;
import com.gangchu.gangchutrip.global.entity.PointHistory;
import com.gangchu.gangchutrip.global.response.ApiResponseFactory;
import com.gangchu.gangchutrip.global.response.ResponseCode;
import com.gangchu.gangchutrip.member.dto.MemberResponseDTO;
import com.gangchu.gangchutrip.member.dto.PointDetailResponseDTO;
import com.gangchu.gangchutrip.member.repository.BadgeOwnerRepository;
import com.gangchu.gangchutrip.member.repository.PointHistoryRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BadgeOwnerRepository badgeOwnerRepository;
    private final PointHistoryRepository pointHistoryRepository;

    public ResponseEntity<?> getMemberInfo(String username) {
        Member m = memberRepository.findByEmail(username);
        if (m == null) {
            return ApiResponseFactory.error(ResponseCode.USER_NOT_FOUND, "Member not found");
        }
        List<BadgeOwner> badgeOwns = badgeOwnerRepository.findAllByMemberId(m.getId());
        List<Long> badgeIds = badgeOwns.stream().map(BadgeOwner::getBadgeId).toList();
        return ApiResponseFactory.success(ResponseCode.OK,
                new MemberResponseDTO(m.getId(), m.getEmail(), m.getNickname(), m.getProfile_image_url(), m.getPoint(), m.getCreated_at(), badgeIds));
    }

    public ResponseEntity<?> updateMemberInfo(String username, String nickname, String profileImageUrl) {
        Member m = memberRepository.findByEmail(username);

        if (m == null) {
            return ApiResponseFactory.error(ResponseCode.USER_NOT_FOUND, "Member not found");
        }
        if (nickname != null && !nickname.isBlank()) {
            m.setNickname(nickname);
        }
        if (profileImageUrl != null && !profileImageUrl.isBlank()) {
            m.setProfile_image_url(profileImageUrl);
        }
        memberRepository.save(m);
        return ApiResponseFactory.success(ResponseCode.OK, "Member info updated successfully");
    }

    public ResponseEntity<?> getMemberPoint(String username) {
        Member m = memberRepository.findByEmail(username);
        if (m == null) {
            return ApiResponseFactory.error(ResponseCode.USER_NOT_FOUND, "Member not found");
        }
        return ApiResponseFactory.success(ResponseCode.OK, m.getPoint());
    }

    public ResponseEntity<?> getMemberPointDetail(String username) {
        Member m = memberRepository.findByEmail(username);
        if (m == null) {
            return ApiResponseFactory.error(ResponseCode.USER_NOT_FOUND, "Member not found");
        }
        List<PointHistory> pointHistories = pointHistoryRepository.findAllByMemberIdOrderByHistoryDateDesc(m.getId());
        List<PointDetailResponseDTO> pointDetails = pointHistories.stream()
            .map(PointDetailResponseDTO::from)
            .toList();
        return ApiResponseFactory.success(ResponseCode.OK, pointDetails);
    }
}
