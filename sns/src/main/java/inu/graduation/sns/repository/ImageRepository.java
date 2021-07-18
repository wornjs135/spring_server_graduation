package inu.graduation.sns.repository;

import inu.graduation.sns.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {

    List<Image> findAllByPostId(Long postId);

}
