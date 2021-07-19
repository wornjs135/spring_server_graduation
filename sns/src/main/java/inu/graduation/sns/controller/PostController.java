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
                                             @RequestPart(required = false) List<MultipartFile> image){
        postService.createPost(memberId, categoryId, request, image);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    //게시글 수정(내용,주소,별점,공개비공개,카테고리)
    @PatchMapping("/posts/{postId}/categories/{categoryId}")
    public ResponseEntity<PostDetailResponse> updatePost(@LoginMember Long memberId,
                                     @PathVariable Long postId,
                                     @PathVariable Long categoryId,
                                     @RequestBody @Valid PostUpdateRequest request){
        return ResponseEntity.ok(postService.updatePost(memberId, postId, categoryId, request));
    }

    //게시글 이미지 수정(기존 이미지들 삭제되고 새로 업로드)
    @PatchMapping("/posts/{postId}/images")
    public ResponseEntity updatePostImage(@LoginMember Long memberId,
                                     @PathVariable Long postId,
                                     @RequestPart List<MultipartFile> image){
        postService.updatePostImage(memberId, postId, image);

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
    @GetMapping("/posts")
    public ResponseEntity<Page<PostResponse>> findPostByAddress(@RequestParam String firstAddress, @RequestParam String secondAddress,
                                            Pageable pageable){
        return ResponseEntity.ok(postService.findPostByAddress(firstAddress, secondAddress, pageable));
    }

    // 게시글 간단 조회(앱)
    @GetMapping("/m/posts")
    public ResponseEntity<Slice<PostSimpleResponse>> findSimplePosts(@RequestParam String firstAddress, @RequestParam String secondAddress, Pageable pageable){
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
    @GetMapping("/posts/hashtag")
    public ResponseEntity<Page<PostResponse>> findPostsByHashtag(@RequestParam String hashtag, Pageable pageable){
        Page<PostResponse> findPostListByHashtag = postService.findPostsByHashtag(hashtag, pageable);

        return ResponseEntity.ok(findPostListByHashtag);
    }

    // 해시태그로 게시글검색(앱)
    @GetMapping("m/posts/hashtag")
    public ResponseEntity<Slice<PostSimpleResponse>> findPostByHashtagApp(@RequestParam String hashtag, Pageable pageable){
        Slice<PostSimpleResponse> findPostListByHashtag = postService.findPostsByhashtagApp(hashtag, pageable);

        return ResponseEntity.ok(findPostListByHashtag);
    }
}
