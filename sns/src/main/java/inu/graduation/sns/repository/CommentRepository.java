package inu.graduation.sns.repository;

import inu.graduation.sns.domain.Comment;
import inu.graduation.sns.model.comment.response.CommentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select new inu.graduation.sns.model.comment.response.CommentResponse(c) from Comment c where c.post.id =:postId order by c.createdAt desc")
    Slice<CommentResponse> findCommentsByPostId(@Param("postId") Long postId, Pageable pageable);

    @Query("select c from Comment c where c.post.id =:postId order by c.createdAt desc")
    Page<Comment> findCommentsByPost(@Param("postId") Long postId, Pageable pageable);
}
