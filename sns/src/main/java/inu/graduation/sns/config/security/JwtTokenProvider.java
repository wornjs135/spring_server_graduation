package inu.graduation.sns.config.security;

import inu.graduation.sns.domain.Member;
import inu.graduation.sns.exception.AuthenticationException;
import inu.graduation.sns.exception.MemberException;
import inu.graduation.sns.model.common.CreateToken;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    @Value("${spring.jwt.secretKey}")
    private String secretKey;

    @Value("${spring.jwt.refreshSecretKey}")
    private String refreshSecretKey;

    private Long accessTokenValidTime = 1000L * 60 * 60 * 24 * 30;
    private Long refreshTokenValidTime = 1000L * 60 * 60 * 24 * 30 * 6;

    private final UserDetailsService userDetailsService;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
        refreshSecretKey = Base64.getEncoder().encodeToString(refreshSecretKey.getBytes());
    }

    public Authentication getAuthentication(String jwtToken){
        UserDetails userDetails = userDetailsService.loadUserByUsername(getMemberPk(jwtToken));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public CreateToken createToken(String memberPk){
        return CreateToken.from(createAccessToken(memberPk), createRefreshToken(memberPk));
    }

    public String createAccessToken(String memberPk){
        Claims claims = Jwts.claims().setSubject(memberPk);
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessTokenValidTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String createRefreshToken(String memberPk){
        Claims claims = Jwts.claims().setSubject(memberPk);
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshTokenValidTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String refresh(String existRefreshToken, Member member){
        if(!existRefreshToken.equals(member.getRefreshToken())){
            throw new AuthenticationException("refreshToken이 일치하지 않습니다.");
        }
        return createAccessToken(Long.toString(member.getId()));
    }

    public String getMemberPk(String jwtToken){
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken)
                .getBody().getSubject();
    }

//    public String getMemberPk(String token) {
//        String jwt = token.substring(7);
//        String userId = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwt)
//                .getBody().getSubject();
//        return userId;
//    }

    public boolean validateToken(String jwtToken) {
        try{
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e){
            return false;
        }
    }
}
