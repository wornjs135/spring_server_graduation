package inu.graduation.sns.config.security;

import inu.graduation.sns.exception.AuthenticationException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean isLoginMemberAnnotation = parameter.getParameterAnnotation(LoginMember.class) != null;
        boolean isLongClass = Long.class.equals(parameter.getParameterType());

        return isLoginMemberAnnotation && isLongClass;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

//        User user = (User) authentication.getPrincipal();
        try {
            User user = (User) authentication.getPrincipal();
            return Long.valueOf(user.getUsername());
        } catch (ClassCastException e) {
            throw new AuthenticationException("토큰이 잘못되었습니다.");
        }
//        String bearerToken = webRequest.getHeader(HttpHeaders.AUTHORIZATION);
//        String memberPk = jwtTokenProvider.getMemberPk(bearerToken);
//        return memberPk;
    }
}
