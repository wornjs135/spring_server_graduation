package inu.graduation.sns.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Good extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "good_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    public static Good createGood(Member findMember, Post findPost) {
        Good good = new Good();
        good.member = findMember;
        good.post = findPost;
        return good;
    }

    public static Good isGoodFalse() {
        Good good = new Good();
        good.id = 0L;
        good.member = null;
        good.post = null;
        return good;
    }
}
