package inu.graduation.sns.controller;

import inu.graduation.sns.config.security.LoginMember;
import inu.graduation.sns.service.GoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GoodController {

    private final GoodService goodService;

    // 좋아요 하기
    @PostMapping("/posts/{postId}/goods")
    public ResponseEntity saveGood(@LoginMember Long memberId,
                                   @PathVariable Long postId){
        goodService.saveGood(memberId, postId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 좋아요 취소
    @DeleteMapping("/posts/goods/{goodId}")
    public ResponseEntity cancleGood(@LoginMember Long memberId,
                                     @PathVariable Long goodId){
        goodService.cancleGood(memberId, goodId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
