package inu.graduation.sns.repository;

import inu.graduation.sns.domain.Post;
import inu.graduation.sns.domain.PostHashtag;
import inu.graduation.sns.model.post.response.PostSimpleResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostHashtagRepository extends JpaRepository<PostHashtag, Long> {
    void deleteAllByPost(Post post);

    @Query("select new inu.graduation.sns.model.post.response.PostSimpleResponse(ph.post) from PostHashtag ph where ph.hashtag.id =:hashtagId and ph.post.isOpen = true order by ph.post.createdAt desc")
    Slice<PostSimpleResponse> findPostsByHashtagId(@Param("hashtagId") Long hashtagId, Pageable pageable);
}
