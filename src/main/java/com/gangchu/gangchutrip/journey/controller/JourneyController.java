package com.gangchu.gangchutrip.journey.controller;

import com.gangchu.gangchutrip.global.exception.BaseException;
import com.gangchu.gangchutrip.global.response.ApiResponse;
import com.gangchu.gangchutrip.global.response.ApiResponseFactory;
import com.gangchu.gangchutrip.global.response.ResponseCode;
import com.gangchu.gangchutrip.global.security.jwt.MemberPrincipal;
import com.gangchu.gangchutrip.journey.dto.CommentResponseDto;
import com.gangchu.gangchutrip.journey.dto.CreateCommentRequestDto;
import com.gangchu.gangchutrip.journey.dto.PostResponseDto;
import com.gangchu.gangchutrip.journey.dto.ReportRequestDto;
import com.gangchu.gangchutrip.journey.service.JourneyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/journey")
@RequiredArgsConstructor
public class JourneyController {

    private final JourneyService journeyService;

    /**
     * 게시물 목록 조회
     */
    @GetMapping("/posts")
    public ResponseEntity<ApiResponse<List<PostResponseDto>>> getPosts(
            @RequestParam(defaultValue = "feed") String type,
            @AuthenticationPrincipal MemberPrincipal principal) {
        
        Long currentUserId = principal != null ? Long.parseLong(principal.getUsername()) : null;
        List<PostResponseDto> posts = journeyService.getPosts(type, currentUserId);
        
        return ApiResponseFactory.success(ResponseCode.SUCCESS, posts);
    }

    /**
     * 게시물 상세 조회 (조회수 증가)
     */
    @GetMapping("/posts/{postId}")
    public ResponseEntity<ApiResponse<PostResponseDto>> getPost(
            @PathVariable Long postId,
            @AuthenticationPrincipal MemberPrincipal principal) {
        
        Long currentUserId = principal != null ? Long.parseLong(principal.getUsername()) : null;
        PostResponseDto post = journeyService.getPost(postId, currentUserId);
        
        return ApiResponseFactory.success(ResponseCode.SUCCESS, post);
    }

    /**
     * 좋아요 토글 (인증 필요)
     */
    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<ApiResponse<Map<String, Object>>> toggleLike(
            @PathVariable Long postId,
            @AuthenticationPrincipal MemberPrincipal principal) {
        
        if (principal == null) {
            throw new BaseException(ResponseCode.UNAUTHORIZED);
        }
        
        Long currentUserId = Long.parseLong(principal.getUsername());
        Map<String, Object> result = journeyService.toggleLike(postId, currentUserId);
        
        return ApiResponseFactory.success(ResponseCode.SUCCESS, result);
    }

    /**
     * 북마크 토글 (인증 필요)
     */
    @PostMapping("/posts/{postId}/bookmark")
    public ResponseEntity<ApiResponse<Map<String, Object>>> toggleBookmark(
            @PathVariable Long postId,
            @AuthenticationPrincipal MemberPrincipal principal) {
        
        if (principal == null) {
            throw new BaseException(ResponseCode.UNAUTHORIZED);
        }
        
        Long currentUserId = Long.parseLong(principal.getUsername());
        Map<String, Object> result = journeyService.toggleBookmark(postId, currentUserId);
        
        return ApiResponseFactory.success(ResponseCode.SUCCESS, result);
    }

    /**
     * 댓글 목록 조회
     */
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse<List<CommentResponseDto>>> getComments(
            @PathVariable Long postId,
            @AuthenticationPrincipal MemberPrincipal principal) {
        
        Long currentUserId = principal != null ? Long.parseLong(principal.getUsername()) : null;
        List<CommentResponseDto> comments = journeyService.getComments(postId, currentUserId);
        
        return ApiResponseFactory.success(ResponseCode.SUCCESS, comments);
    }

    /**
     * 댓글 작성 (인증 필요)
     */
    @PostMapping("/comments")
    public ResponseEntity<ApiResponse<CommentResponseDto>> createComment(
            @RequestBody CreateCommentRequestDto requestDto,
            @AuthenticationPrincipal MemberPrincipal principal) {
        
        if (principal == null) {
            throw new BaseException(ResponseCode.UNAUTHORIZED);
        }
        
        Long currentUserId = Long.parseLong(principal.getUsername());
        CommentResponseDto comment = journeyService.createComment(requestDto, currentUserId);
        
        return ApiResponseFactory.success(ResponseCode.SUCCESS, comment);
    }

    /**
     * 댓글 삭제 (인증 필요)
     */
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse<Void>> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal MemberPrincipal principal) {
        
        if (principal == null) {
            throw new BaseException(ResponseCode.UNAUTHORIZED);
        }
        
        Long currentUserId = Long.parseLong(principal.getUsername());
        journeyService.deleteComment(commentId, currentUserId);
        
        return ApiResponseFactory.success(ResponseCode.SUCCESS);
    }

    /**
     * 게시물 신고 (인증 필요)
     */
    @PostMapping("/reports")
    public ResponseEntity<ApiResponse<Void>> reportPost(
            @RequestBody ReportRequestDto requestDto,
            @AuthenticationPrincipal MemberPrincipal principal) {
        
        if (principal == null) {
            throw new BaseException(ResponseCode.UNAUTHORIZED);
        }
        
        Long currentUserId = Long.parseLong(principal.getUsername());
        journeyService.reportPost(requestDto, currentUserId);
        
        return ApiResponseFactory.success(ResponseCode.SUCCESS);
    }
}

