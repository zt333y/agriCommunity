package com.example.agricommunity.config;

import com.example.agricommunity.interceptor.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 注册 JWT 拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor())
                // 拦截所有的业务接口
                .addPathPatterns("/api/**", "/user/**", "/product/**")
                // 放行登录接口、无需登录就能浏览的商品列表接口
                .excludePathPatterns(
                        "/user/login",
                        "/product/list",
                        "/product/{id}"
                );
    }

    // 配置全局跨域 (配置了这个，Controller 里的 @CrossOrigin 就可以删掉了)
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}