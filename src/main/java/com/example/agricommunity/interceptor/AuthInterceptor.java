package com.example.agricommunity.interceptor;

import com.example.agricommunity.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1. 放行浏览器的预检请求 (解决跨域附带的 OPTIONS 请求问题)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 2. 从 HTTP 请求头中获取前端传来的 Token（约定字段名为 Authorization）
        String token = request.getHeader("Authorization");

        // 3. 校验 Token 是否存在
        if (token == null || token.trim().isEmpty()) {
            returnError(response, "请求被拦截：未登录或缺少Token");
            return false;
        }

        try {
            // 业界规范：前端传来的 Token 通常以 "Bearer " 开头，我们需要截取后面的真实加密串
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            // 4. 校验并解析 Token
            Claims claims = JwtUtils.parseToken(token);

            // 👉 5. 核心操作：把解析出来的真实 userId 悄悄塞进 request 里，传给后面的 Controller！
            request.setAttribute("currentUserId", claims.get("userId"));
            return true;

        } catch (Exception e) {
            // Token 过期或被篡改时，会走入这个异常
            returnError(response, "请求被拦截：Token 无效或已过期，请重新登录");
            return false;
        }
    }

    // 辅助方法：拦截失败时，给前端返回标准的 JSON 格式报错
    private void returnError(HttpServletResponse response, String msg) throws Exception {
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write("{\"code\":401, \"msg\":\"" + msg + "\"}");
    }
}