package inu.graduation.sns.repository;


import inu.graduation.sns.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("select p from Post p where p.address like %:firstAddress% and p.address like %:secondAddress% and p.isOpen = true order by p.createdAt desc")
    Slice<Post> findSimplePostList(@Param("firstAddress") String firstAddress, @Param("secondAddress") String secondAddress, Pageable pageable);

    @Query("select p from Post p where p.member.id =:memberId order by p.createdAt desc")
    Slice<Post> findMyPostList(@Param("memberId") Long memberId, Pageable pageable);

}
