package inu.graduation.sns.controller;

import inu.graduation.sns.config.security.LoginMember;
import inu.graduation.sns.domain.Post;
import inu.graduation.sns.model.post.request.PostSaveRequest;
import inu.graduation.sns.model.post.request.PostUpdateRequest;
import inu.graduation.sns.model.post.response.PostDetailResponse;
import inu.graduation.sns.model.post.response.PostResponse;
import inu.graduation.sns.model.post.response.PostSimpleResponse;
import inu.graduation.sns.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    //게시글 생성 + 이미지 같이 업로드
    @PostMapping("/posts/categories/{categoryId}")
    public ResponseEntity createPostWithImages(@LoginMember Long memberId,
                                             @PathVariable Long categoryId,
                                             @RequestPart @Valid PostSaveRequest request,
                                             @RequestPart(required = false) List<MultipartFile> images){
        postService.createPost(memberId, categoryId, request);
//                , images);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    //게시글 수정(내용,주소,별점,공개비공개,카테고리)
    @PatchMapping("/posts/{postId}/categories/{categoryId}")
    public ResponseEntity updatePost(@LoginMember Long memberId,
                                     @PathVariable Long postId,
                                     @PathVariable Long categoryId,
                                     @RequestBody @Valid PostUpdateRequest request){
        postService.updatePost(memberId, postId, categoryId, request);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    //게시글 이미지 수정(기존 이미지들 삭제되고 새로 업로드)
    @PatchMapping("/posts/{postId}/images")
    public ResponseEntity updatePost(@LoginMember Long memberId,
                                     @PathVariable Long postId,
                                     @RequestPart List<MultipartFile> images){
        postService.updatePostImage(memberId, postId, images);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 게시글 삭제
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity deletePost(@LoginMember Long memberId,
                                     @PathVariable Long postId){
        postService.deletePost(memberId, postId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 게시글 조회(웹)
    @GetMapping("/posts/{firstAddress}/{secondAddress}")
    public ResponseEntity<Page<PostResponse>> findPostByAddress(@PathVariable String firstAddress, @PathVariable String secondAddress,
                                            Pageable pageable){
        Page<PostResponse> findPostList = postService.findPostByAddress(firstAddress, secondAddress, pageable);
        return ResponseEntity.ok(findPostList);
    }

    // 게시글 간단 조회(앱)
    @GetMapping("/m/posts/{firstAddress}/{secondAddress}")
    public ResponseEntity<Slice<PostSimpleResponse>> findSimplePosts(@PathVariable String firstAddress, @PathVariable String secondAddress, Pageable pageable){
        Slice<PostSimpleResponse> findSimplePostList = postService.findSimplePostList(firstAddress, secondAddress, pageable);

        return ResponseEntity.ok(findSimplePostList);
    }

    // 내가 쓴 글 간단조회(앱)
    @GetMapping("/m/members/posts")
    public ResponseEntity<Slice<PostSimpleResponse>> findMyPostList(@LoginMember Long memberId, Pageable pageable){
        Slice<PostSimpleResponse> myPostList = postService.findMyPostList(memberId, pageable);

        return ResponseEntity.ok(myPostList);
    }

    // 게시글 상세 조회(앱)
    @GetMapping("/m/posts/{postId}")
    public ResponseEntity<PostDetailResponse> findPost(@PathVariable Long postId){
        PostDetailResponse findPost = postService.findPost(postId);

        return ResponseEntity.ok(findPost);
    }

    // 해시태그로 게시글검색(웹)
    @GetMapping("/posts/hashtag/{hashtag}")
    public ResponseEntity<Page<PostResponse>> findPostsByHashtag(@PathVariable String hashtag, Pageable pageable){
        Page<PostResponse> findPostListByHashtag = postService.findPostsByHashtag(hashtag, pageable);

        return ResponseEntity.ok(findPostListByHashtag);
    }

    // 해시태그로 게시글검색(앱)
    @GetMapping("m/posts/hashtag/{hashtag}")
    public ResponseEntity<Slice<PostSimpleResponse>> findPostByHashtagApp(@PathVariable String hashtag, Pageable pageable){
        Slice<PostSimpleResponse> findPostListByHashtag = postService.findPostsByhashtagApp(hashtag, pageable);

        return ResponseEntity.ok(findPostListByHashtag);
    }
}
