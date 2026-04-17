package com.example.agricommunity.config;

import com.example.agricommunity.interceptor.AuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 注册 JWT 拦截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthInterceptor())
                // 拦截所有的业务接口
                .addPathPatterns("/api/**", "/user/**", "/product/**")
                .excludePathPatterns(
                        "/user/login",
                        "/user/register",
                        "/api/user/login",
                        "/api/user/register",
                        "/product/list",
                        "/api/product/list",
                        "/product/{id}",
                        "/uploads/**"        // 🌟 核心新增：必须给图片访问路径放行，否则前端看不了图片！
                );
    }

    // 配置全局跨域
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    // 🌟 核心新增：开放 uploads 文件夹的静态资源访问权限
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String projectPath = System.getProperty("user.dir");
        // 当访问 http://你的IP:8080/uploads/xxx.jpg 时，直接去电脑项目根目录的 uploads 文件夹里找
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + projectPath + "/uploads/");
    }
}