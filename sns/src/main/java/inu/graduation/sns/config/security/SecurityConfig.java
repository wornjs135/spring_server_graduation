package inu.graduation.sns.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                // cors 적용 1
                .cors().configurationSource(corsConfigurationSource())
                .and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/exception/**").permitAll()
                .antMatchers(HttpMethod.POST, "/members", "/members/login", "/members/refresh").permitAll()
                .antMatchers(HttpMethod.GET, "/docs/member.html", "/docs/post.html", "/docs/good.html", "/docs/comment.html", "/docs/category.html", "/docs/admin.html",
                                            "/posts", "posts/hashtag").permitAll()
                .antMatchers(HttpMethod.POST, "/admin/categories").hasRole("ADMIN")
                .antMatchers(HttpMethod.PATCH, "/admin/categories/{categoryId}").hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/admin/categories/{categoryId}",
                        "/admin/members/{memberId}","/admin/posts/{postId}", "/admin/comments/{commentId}").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/admin/members").hasRole("ADMIN")
                .anyRequest().hasAnyRole("MEMBER", "ADMIN")
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                .accessDeniedHandler(new CustomAccessDeniedHandler())
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
    }

// CORS 허용 적용 2
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOrigin("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.addExposedHeader("*");
//        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
