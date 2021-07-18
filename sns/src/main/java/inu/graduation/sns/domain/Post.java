package inu.graduation.sns.domain;

import inu.graduation.sns.model.post.request.PostSaveRequest;
import inu.graduation.sns.model.post.request.PostUpdateRequest;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    private String content;

    private String firstAddress;

    private String secondAddress;

    private String restAddress;

    private Integer score;

    private Boolean isOpen;

    private Integer countGood;

    private Integer countComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> commentList = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Image> imageList = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Good> goodList = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<PostHashtag> postHashtagList = new ArrayList<>();

    public static Post createPost(Member findMember, Category findCategory, PostSaveRequest request) {
        Post post = new Post();
        post.member = findMember;
        post.category = findCategory;
        post.content = request.getContent();
        post.firstAddress = request.getFirstAddress();
        post.secondAddress = request.getSecondAddress();
        post.restAddress = request.getRestAddress();
        post.score = request.getScore();
        post.isOpen = request.getIsOpen();
        post.countGood = 0;
        post.countComment = 0;
        return post;
    }

    public boolean update(Category findCategory, PostUpdateRequest request) {
        this.category = findCategory;
        this.content = request.getContent();
        this.firstAddress = request.getFirstAddress();
        this.secondAddress = request.getSecondAddress();
        this.restAddress = request.getRestAddress();
        this.score = request.getScore();
        this.isOpen = request.getIsOpen();
        return true;
    }

    public boolean addCommentCount() {
        this.countComment += 1;
        return true;
    }

    public boolean removeCommentCount() {
        this.countComment -= 1;
        return true;
    }

    public boolean addGoodCount() {
        this.countGood += 1;
        return true;
    }

    public boolean removeGoodCount(){
        this.countGood -= 1;
        return true;
    }
}
