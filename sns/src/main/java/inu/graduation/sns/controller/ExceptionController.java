package inu.graduation.sns.controller;

import inu.graduation.sns.exception.AccessDeniedException;
import inu.graduation.sns.exception.AuthenticationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExceptionController {

    @GetMapping("/exception/entrypoint")
    public ResponseEntity authenticationException(){
        throw new AuthenticationException("인증 오류가 발생하였습니다.");
    }

    @GetMapping("/exception/accessdenied")
    public ResponseEntity accessDeniedException(){
        throw new AccessDeniedException("권한이 없습니다.");
    }

    @GetMapping("/exception/token")
    public ResponseEntity tokenException() { throw new AuthenticationException("유효하지 않는 토큰입니다."); }
}