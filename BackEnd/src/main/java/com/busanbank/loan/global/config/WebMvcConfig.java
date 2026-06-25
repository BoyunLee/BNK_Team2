package com.busanbank.loan.global.config;

import com.busanbank.loan.global.interceptor.AdminAuthInterceptor;
import com.busanbank.loan.global.interceptor.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final AdminAuthInterceptor adminAuthInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 고객 세션 인증 — 관리자 영역(/api/v1/admin/**)은 별도 인터셉터가 담당하므로 제외
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/v1/**")
                .excludePathPatterns(
                        "/api/v1/auth/email/**",
                        "/api/v1/auth/register",
                        "/api/v1/auth/login",
                        // 상품 목록/상세는 비로그인 열람 허용
                        "/api/v1/products",
                        "/api/v1/products/**",
                        // 관리자 영역은 관리자 세션으로 보호(아래 adminAuthInterceptor)
                        "/api/v1/admin/**",
                        "/swagger-ui/**",
                        "/v3/api-docs/**"
                );

        // 관리자 세션 인증 — 로그인 제외 전체 관리자 API
        registry.addInterceptor(adminAuthInterceptor)
                .addPathPatterns("/api/v1/admin/**")
                .excludePathPatterns("/api/v1/admin/auth/login");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("http://localhost:*", "https://*.busanbank.com")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
