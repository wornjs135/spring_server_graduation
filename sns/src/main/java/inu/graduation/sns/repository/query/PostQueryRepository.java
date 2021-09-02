package inu.graduation.sns.repository.query;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import inu.graduation.sns.domain.*;
import inu.graduation.sns.model.member.response.MemberResponse;
import inu.graduation.sns.model.post.response.PostDetailResponse;
import inu.graduation.sns.model.post.response.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static inu.graduation.sns.domain.QCategory.*;
import static inu.graduation.sns.domain.QGood.*;
import static inu.graduation.sns.domain.QHashtag.*;
import static inu.graduation.sns.domain.QImage.*;
import static inu.graduation.sns.domain.QMember.*;
import static inu.graduation.sns.domain.QPost.*;

@Repository
@RequiredArgsConstructor
public class PostQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Page<Post> findByAddress(String firstAddress, String secondAddress, Pageable pageable){
        List<Post> findPosts = queryFactory
                .selectFrom(post)
                .where(post.address.contains(firstAddress), post.address.contains(secondAddress),
                        post.isOpen.eq(true))
                .leftJoin(post.member, member).fetchJoin()
                .leftJoin(post.category, category).fetchJoin()
//                .leftJoin(post.goodList, good).on(good.member.id.eq(memberId))
                .orderBy(post.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

//        List<PostResponse> findPosts = queryFactory
//                .select(Projections.constructor(PostResponse.class, post))
//                .from(post)
//                .where(post.address.contains(firstAddress), post.address.contains(secondAddress),
//                        post.isOpen.eq(true))
//                .leftJoin(post.member, member).fetchJoin()
//                .leftJoin(post.category, category).fetchJoin()
//                .leftJoin(post.goodList, good).on(good.member.id.eq(memberId))
//                .orderBy(post.createdAt.desc())
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .fetch();

        long totalCount = queryFactory
                .selectFrom(post)
                .where(post.address.contains(firstAddress), post.address.contains(secondAddress),
                        post.isOpen.eq(true))
                .orderBy(post.createdAt.desc())
                .fetchCount();

        return new PageImpl<>(findPosts, pageable, totalCount);
    }

    public Post findPost(Long postId){
//        PostDetailResponse findPost = queryFactory
//                .select(Projections.constructor(PostDetailResponse.class, post))
//                .from(post)
//                .where(post.id.eq(postId))
//                .fetchOne();
        Post findPost = queryFactory
                .selectFrom(QPost.post)
                .where(QPost.post.id.eq(postId))
                .fetchOne();

        return findPost;
    }

    public Page<Post> findMyPostWeb(Long memberId, Pageable pageable){
        List<Post> findPosts = queryFactory
                .selectFrom(post)
                .where(post.member.id.eq(memberId))
                .leftJoin(post.member, member).fetchJoin()
                .leftJoin(post.category, category).fetchJoin()
                .orderBy(post.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long totalCount = queryFactory
                .selectFrom(post)
                .where(post.member.id.eq(memberId))
                .orderBy(post.createdAt.desc())
                .fetchCount();

        return new PageImpl<>(findPosts, pageable, totalCount);
    }

    public Page<Post> findMyPostByAddressWeb(String firstAddress, String secondAddress, Long memberId, Pageable pageable) {
        List<Post> findPosts = queryFactory
                .selectFrom(post)
                .where(post.member.id.eq(memberId), post.address.contains(firstAddress), post.address.contains(secondAddress))
                .leftJoin(post.member, member).fetchJoin()
                .leftJoin(post.category, category).fetchJoin()
                .orderBy(post.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long totalCount = queryFactory
                .selectFrom(post)
                .where(post.member.id.eq(memberId), post.address.contains(firstAddress), post.address.contains(secondAddress))
                .orderBy(post.createdAt.desc())
                .fetchCount();

        return new PageImpl<>(findPosts, pageable, totalCount);
    }
}
