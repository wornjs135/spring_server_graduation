package inu.graduation.sns.repository.query;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import inu.graduation.sns.domain.Post;
import inu.graduation.sns.domain.QPostHashtag;
import inu.graduation.sns.model.post.response.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Repository;

import java.util.List;

import static inu.graduation.sns.domain.QPostHashtag.*;

@Repository
@RequiredArgsConstructor
public class PostHashtagQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Page<Post> findPostByHashtagId(Long hashtagId, Pageable pageable){
//        QueryResults<PostResponse> result = queryFactory
//                .select(Projections.constructor(PostResponse.class, postHashtag.post)).from(postHashtag)
//                .where(postHashtag.hashtag.id.eq(hashtagId), postHashtag.post.isOpen.eq(true))
//                .orderBy(postHashtag.post.createdAt.desc())
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .fetchResults();
//
//        List<PostResponse> findPosts = result.getResults();
//        long totalCount = result.getTotal();
//
//        return new PageImpl<>(findPosts, pageable, totalCount);

        QueryResults<Post> result = queryFactory
                .select(postHashtag.post).from(postHashtag)
                .where(postHashtag.hashtag.id.eq(hashtagId), postHashtag.post.isOpen.eq(true))
                .orderBy(postHashtag.post.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<Post> findPosts = result.getResults();
        long totalCount = result.getTotal();

        return new PageImpl<>(findPosts, pageable, totalCount);
    }
}
