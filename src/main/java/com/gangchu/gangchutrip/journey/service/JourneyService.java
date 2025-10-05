package com.gangchu.gangchutrip.journey.service;

import com.gangchu.gangchutrip.auth.entity.Member;
import com.gangchu.gangchutrip.auth.repository.MemberRepository;
import com.gangchu.gangchutrip.global.exception.BaseException;
import com.gangchu.gangchutrip.global.response.ResponseCode;
import com.gangchu.gangchutrip.journey.dto.*;
import com.gangchu.gangchutrip.journey.entity.*;
import com.gangchu.gangchutrip.journey.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class JourneyService {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentRepository commentRepository;
    private final BookmarkRepository bookmarkRepository;
    private final ReportRepository reportRepository;
    private final MemberRepository memberRepository;

    // 게시물 목록 조회
    @Transactional(readOnly = true)
    public List<PostResponseDto> getPosts(String type, Long currentUserId) {
        List<Post> posts;
        
        if ("popular".equals(type)) {
            posts = postRepository.findPopularPosts();
        } else {
            posts = postRepository.findByStatusOrderByCreatedAtDesc(Post.PostStatus.ACTIVE);
        }

        Member currentMember = null;
        if (currentUserId != null) {
            currentMember = memberRepository.findById(currentUserId).orElse(null);
        }

        final Member finalMember = currentMember;
        return posts.stream()
                .map(post -> {
                    boolean isLiked = finalMember != null && 
                            postLikeRepository.existsByPostAndMember(post, finalMember);
                    boolean isBookmarked = finalMember != null && 
                            bookmarkRepository.existsByPostAndMember(post, finalMember);
                    return PostResponseDto.from(post, isLiked, isBookmarked);
                })
                .collect(Collectors.toList());
    }

    // 게시물 상세 조회 (조회수 증가)
    @Transactional
    public PostResponseDto getPost(Long postId, Long currentUserId) {
        Post post = postRepository.findByIdAndStatus(postId, Post.PostStatus.ACTIVE)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND));

        // 조회수 증가
        post.setViews(post.getViews() + 1);

        Member currentMember = null;
        if (currentUserId != null) {
            currentMember = memberRepository.findById(currentUserId).orElse(null);
        }

        boolean isLiked = currentMember != null && 
                postLikeRepository.existsByPostAndMember(post, currentMember);
        boolean isBookmarked = currentMember != null && 
                bookmarkRepository.existsByPostAndMember(post, currentMember);

        return PostResponseDto.from(post, isLiked, isBookmarked);
    }

    // 좋아요 토글
    @Transactional
    public Map<String, Object> toggleLike(Long postId, Long currentUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND));
        
        Member member = memberRepository.findById(currentUserId)
                .orElseThrow(() -> new BaseException(ResponseCode.UNAUTHORIZED));

        boolean isLiked;
        
        if (postLikeRepository.existsByPostAndMember(post, member)) {
            // 좋아요 취소
            postLikeRepository.deleteByPostAndMember(post, member);
            post.setLikes(Math.max(0, post.getLikes() - 1));
            isLiked = false;
        } else {
            // 좋아요 추가
            PostLike postLike = new PostLike();
            postLike.setPost(post);
            postLike.setMember(member);
            postLikeRepository.save(postLike);
            post.setLikes(post.getLikes() + 1);
            isLiked = true;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("isLiked", isLiked);
        result.put("likes", post.getLikes());
        return result;
    }

    // 북마크 토글
    @Transactional
    public Map<String, Object> toggleBookmark(Long postId, Long currentUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND));
        
        Member member = memberRepository.findById(currentUserId)
                .orElseThrow(() -> new BaseException(ResponseCode.UNAUTHORIZED));

        boolean isBookmarked;
        
        if (bookmarkRepository.existsByPostAndMember(post, member)) {
            // 북마크 취소
            bookmarkRepository.deleteByPostAndMember(post, member);
            isBookmarked = false;
        } else {
            // 북마크 추가
            Bookmark bookmark = new Bookmark();
            bookmark.setPost(post);
            bookmark.setMember(member);
            bookmarkRepository.save(bookmark);
            isBookmarked = true;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("isBookmarked", isBookmarked);
        return result;
    }

    // 댓글 목록 조회
    @Transactional(readOnly = true)
    public List<CommentResponseDto> getComments(Long postId, Long currentUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND));

        List<Comment> comments = commentRepository.findByPostOrderByCreatedAtAsc(post);

        Member currentMember = null;
        if (currentUserId != null) {
            currentMember = memberRepository.findById(currentUserId).orElse(null);
        }

        return comments.stream()
                .map(comment -> {
                    boolean isLiked = false; // TODO: CommentLike 구현
                    return CommentResponseDto.from(comment, isLiked);
                })
                .collect(Collectors.toList());
    }

    // 댓글 작성
    @Transactional
    public CommentResponseDto createComment(CreateCommentRequestDto requestDto, Long currentUserId) {
        Post post = postRepository.findById(Long.parseLong(requestDto.getPostId()))
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND));
        
        Member member = memberRepository.findById(currentUserId)
                .orElseThrow(() -> new BaseException(ResponseCode.UNAUTHORIZED));

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setMember(member);
        comment.setContent(requestDto.getContent());
        
        commentRepository.save(comment);

        // 게시물 댓글 수 증가
        post.setComments(post.getComments() + 1);

        return CommentResponseDto.from(comment, false);
    }

    // 댓글 삭제
    @Transactional
    public void deleteComment(Long commentId, Long currentUserId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND));

        // 작성자 본인 확인
        if (!comment.getMember().getId().equals(currentUserId)) {
            throw new BaseException(ResponseCode.FORBIDDEN);
        }

        Post post = comment.getPost();
        commentRepository.delete(comment);

        // 게시물 댓글 수 감소
        post.setComments(Math.max(0, post.getComments() - 1));
    }

    // 게시물 신고
    @Transactional
    public void reportPost(ReportRequestDto requestDto, Long currentUserId) {
        Post post = postRepository.findById(Long.parseLong(requestDto.getPostId()))
                .orElseThrow(() -> new BaseException(ResponseCode.NOT_FOUND));
        
        Member member = memberRepository.findById(currentUserId)
                .orElseThrow(() -> new BaseException(ResponseCode.UNAUTHORIZED));

        // 중복 신고 확인
        if (reportRepository.existsByPostAndMember(post, member)) {
            throw new BaseException(ResponseCode.BAD_REQUEST);
        }

        Report report = new Report();
        report.setPost(post);
        report.setMember(member);
        report.setReason(requestDto.getReasonEnum());
        report.setDescription(requestDto.getDescription());
        
        reportRepository.save(report);

        // 신고가 5회 이상이면 게시물 숨김 처리
        long reportCount = reportRepository.countByPost(post);
        if (reportCount >= 5) {
            post.setStatus(Post.PostStatus.HIDDEN);
        }
    }
}

