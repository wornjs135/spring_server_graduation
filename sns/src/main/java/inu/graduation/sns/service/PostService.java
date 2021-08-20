package inu.graduation.sns.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import inu.graduation.sns.domain.*;
import inu.graduation.sns.exception.CategoryException;
import inu.graduation.sns.exception.HashtagException;
import inu.graduation.sns.exception.MemberException;
import inu.graduation.sns.exception.PostException;
import inu.graduation.sns.model.post.request.PostSaveRequest;
import inu.graduation.sns.model.post.request.PostUpdateRequest;
import inu.graduation.sns.model.post.response.PostDetailResponse;
import inu.graduation.sns.model.post.response.PostResponse;
import inu.graduation.sns.model.post.response.PostSimpleResponse;
import inu.graduation.sns.repository.*;
import inu.graduation.sns.repository.query.PostHashtagQueryRepository;
import inu.graduation.sns.repository.query.PostQueryRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PostService {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.s3.thumbnailBucket}")
    private String thumbnailBucket;

    private final AmazonS3Client amazonS3Client;
    private final PostRepository postRepository;
    private final ImageRepository imageRepository;
    private final MemberRepository memberRepository;
    private final CategoryRepository categoryRepository;
    private final HashtagRepository hashtagRepository;
    private final PostHashtagRepository postHashtagRepository;
    private final PostQueryRepository postQueryRepository;
    private final PostHashtagQueryRepository postHashtagQueryRepository;

    //게시글 생성 + 이미지 업로드 + 해쉬태그 생성
    @Transactional
    public PostResponse createPost(Long memberId, Long categoryId, PostSaveRequest request, List<MultipartFile> images) {
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException("존재하지 않는 회원입니다."));
        Category findCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryException("존재하지 않는 카테고리입니다."));
        Post createdPost = Post.createPost(findMember, findCategory, request);
        Post savedPost = postRepository.save(createdPost);

        // 이미지 s3에 업로드
        uploadImageS3(images, savedPost);

        // 해시태그 추출해서 저장
        extractHashTag(savedPost.getContent(), savedPost);

        return new PostResponse(savedPost);
    }

    // 게시글 내용들 수정
    @Transactional
    public PostDetailResponse updatePost(Long memberId, Long postId, Long categoryId, PostUpdateRequest request) {
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException("존재하지 않는 회원입니다."));
        Category findCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryException("존재하지 않는 카테고리입니다."));
        Post findPost = postRepository.findById(postId)
                .orElseThrow(() -> new PostException("존재하지 않는 게시글입니다."));
        if(!findPost.getMember().getId().equals(findMember.getId())){
            throw new PostException("본인의 글이 아닙니다.");
        }
        findPost.update(findCategory, request);

        // 해시태그 삭제
        postHashtagRepository.deleteAllByPost(findPost);
        // 해시태그 추출해서 다시 저장
        extractHashTag(request.getContent(), findPost);

        return new PostDetailResponse(findPost);
    }

    // 게시글 이미지 수정
    @Transactional
    public boolean updatePostImage(Long memberId, Long postId, List<MultipartFile> images) {
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException("존재하지 않는 회원입니다."));
        Post findPost = postRepository.findById(postId)
                .orElseThrow(() -> new PostException("존재하지 않는 게시글입니다."));
        if(!findPost.getMember().getId().equals(findMember.getId())){
            throw new PostException("본인의 글이 아닙니다.");
        }
        List<Image> allImageByPostId = imageRepository.findAllByPostId(findPost.getId());
//        //s3의 이미지 삭제
//        for (Image image : allImageByPostId) {
//            amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, image.getStoreName()));
//            amazonS3Client.deleteObject(new DeleteObjectRequest(thumbnailBucket, image.getStoreName()));
//        }
        //DB에서도 이미지 삭제
        imageRepository.deleteAll(allImageByPostId);

        //새 이미지 다시 업로드
        uploadImageS3(images, findPost);

        return true;
    }

    // 게시글 삭제
    @Transactional
    public boolean deletePost(Long memberId, Long postId) {
        Member findMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException("존재하지 않는 회원입니다."));
        Post findPost = postRepository.findById(postId)
                .orElseThrow(() -> new PostException("존재하지 않는 게시글입니다."));
        if(!findPost.getMember().getId().equals(findMember.getId())){
            throw new PostException("본인의 글이 아닙니다.");
        }

//        List<Image> allImageByPostId = imageRepository.findAllByPostId(findPost.getId());
//        //s3의 이미지 삭제
//        for (Image image : allImageByPostId) {
//            amazonS3Client.deleteObject(new DeleteObjectRequest(bucket, image.getStoreName()));
//            amazonS3Client.deleteObject(new DeleteObjectRequest(thumbnailBucket, image.getStoreName()));
//        }

        postRepository.delete(findPost);

        return true;
    }

    // 관리자가 게시글 삭제
    @Transactional
    public boolean adminDeletePost(Long postId) {
        Post findPost = postRepository.findById(postId)
                .orElseThrow(() -> new PostException("존재하지 않는 게시글입니다."));
        postRepository.delete(findPost);

        return true;
    }

    // 게시글 조회(웹)
    public Page<PostResponse> findPostByAddress(String firstAddress, String secondAddress, Pageable pageable) {
        Page<Post> findPosts = postQueryRepository.findByAddress(firstAddress, secondAddress, pageable);

        return findPosts.map(post -> new PostResponse(post));

    }

    // 게시글 간단 조회(앱)
    public Slice<PostSimpleResponse> findSimplePostList(String firstAddress, String secondAddress, Pageable pageable) {
        Slice<Post> findSimplePosts = postRepository.findSimplePostList(firstAddress, secondAddress, pageable);

        return findSimplePosts.map(post -> new PostSimpleResponse(post));
    }

    // 내가 쓴 게시글 조회(웹)
    public Page<PostResponse> findMyPostListWeb(Long memberId, Pageable pageable) {
        Page<Post> myPostWeb = postQueryRepository.findMyPostWeb(memberId, pageable);

        return myPostWeb.map(post -> new PostResponse(post));
    }

    // 내가 쓴 게시글 간단 조회(앱)
    public Slice<PostSimpleResponse> findMyPostList(Long memberId, Pageable pageable) {
        Slice<Post> myPostList = postRepository.findMyPostList(memberId, pageable);

        return myPostList.map(post -> new PostSimpleResponse(post));
    }

    // 게시글 상세 조회
    public PostDetailResponse findPost(Long postId) {
        return postQueryRepository.findPost(postId);
    }

    // 해시태그로 게시글 조회(웹)
    public Page<PostResponse> findPostsByHashtag(String hashtag, Pageable pageable) {
        Hashtag findHashtag = hashtagRepository.findByName(hashtag)
                .orElseThrow(() -> new HashtagException("존재하지 않는 해시태그입니다."));

        return postHashtagQueryRepository.findPostByHashtagId(findHashtag.getId(), pageable);
    }

    // 해시태그로 게시글 조회(앱)
    public Slice<PostSimpleResponse> findPostsByhashtagApp(String hashtag, Pageable pageable) {
        Hashtag findHashtag = hashtagRepository.findByName(hashtag)
                .orElseThrow(() -> new HashtagException("존재하지 않는 해시태그입니다."));

        return postHashtagRepository.findPostsByHashtagId(findHashtag.getId(), pageable);
    }

    // s3 이미지 업로드 함수 + db저장
    public boolean uploadImageS3(List<MultipartFile> images, Post createdPost) {
        if (images != null){
            if (!images.isEmpty()) {
                Image imageObject;
                ObjectMetadata objectMetadata = new ObjectMetadata();

                for (MultipartFile img: images) {
                    if(!img.isEmpty()){
                        if(!img.getContentType().startsWith("image")){
                            throw new IllegalStateException();
                        }

                        objectMetadata.setContentLength(img.getSize());
                        objectMetadata.setContentType(img.getContentType());
                        String storeName = UUID.randomUUID().toString() + "_" + img.getOriginalFilename();

                        try {
                            amazonS3Client.putObject(new PutObjectRequest(bucket,storeName, img.getInputStream(), objectMetadata)
                                    .withCannedAcl(CannedAccessControlList.PublicRead));

                            //이미지 url 가져오기
                            String imageUrl = amazonS3Client.getUrl(bucket, storeName).toString();
                            String thumbnailImageUrl = amazonS3Client.getUrl(thumbnailBucket, storeName).toString();

                            imageObject = Image.createImage(imageUrl, thumbnailImageUrl, storeName, createdPost);
                            imageRepository.save(imageObject);
                        } catch (Exception ex){
                            throw new MemberException(ex.getMessage());
                        }
                    }
                }
            }
        }
        return true;
    }

    // 해시태그 추출
    public boolean extractHashTag(String content, Post savedPost) {
        Pattern p = Pattern.compile("\\#([0-9a-zA-Z가-힣]*)");
        Matcher m = p.matcher(content);
        String extractHashTag = null;

        while(m.find()) {
            extractHashTag = sepcialCharacter_replace(m.group());
            if(extractHashTag != null) {
                // 해시태그가 존재하지 않으면 새로 만들어서 저장
                Optional<Hashtag> findHashtag = hashtagRepository.findByName(extractHashTag);
                if(!findHashtag.isPresent()){
                    Hashtag hashtag = Hashtag.createHashtag(extractHashTag);
                    Hashtag savedHashtag = hashtagRepository.save(hashtag);
                    PostHashtag postHashtag = PostHashtag.createPostHashtag(savedHashtag, savedPost);
                    postHashtagRepository.save(postHashtag);
                } else { // 해시태그가 존재하면 바로 저장
                    PostHashtag postHashtag = PostHashtag.createPostHashtag(findHashtag.orElseThrow(() -> new HashtagException("존재하지 않는 해시태그 입니다.")),
                                                                            savedPost);
                    postHashtagRepository.save(postHashtag);
                }
            }
        }
        return true;
    }

    // 특수문자 공백으로 대체
    public String sepcialCharacter_replace(String str) {
        str = StringUtils.replaceChars(str, "-_+=!@#$%^&*()[]{}|\\;:'\"<>,.?/~`） ","");

        if(str.length() < 1) {
            return null;
        }
        return str;
    }

}
