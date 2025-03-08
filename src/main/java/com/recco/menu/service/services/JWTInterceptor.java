//package com.recco.menu.service.services;
//
//import com.recco.menu.service.util.JWTUtil;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.HandlerInterceptor;
//import org.springframework.web.util.UriUtils;
//
//import java.nio.charset.StandardCharsets;
//
//@Component
//public class JWTInterceptor implements HandlerInterceptor {
//
//    private final JWTUtil jwtUtil;
//
//    public JWTInterceptor(JWTUtil jwtUtil) {
//        this.jwtUtil = jwtUtil;
//    }
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        String token = extractToken(request);
//
//        if (token == null || token.isEmpty()) {
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing token in header or query parameter");
//            return false;
//        }
//
//        try {
//            String tableId = jwtUtil.validateTokenAndGetTableId(token);
//            request.setAttribute("tableId", tableId);
//            return true;
//        } catch (Exception e) {
//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
//            return false;
//        }
//    }
//
//    private String extractToken(HttpServletRequest request) {
//        // 1️⃣ Check Authorization Header (First Priority)
//        String authHeader = request.getHeader("Authorization");
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            return authHeader.substring(7); // Remove "Bearer " prefix
//        }
//
//        // 2️⃣ Check Query Parameter (Fallback)
//        String tokenParam = request.getParameter("qrToken"); // Use "qrToken" to match the QR Code Service
//        if (tokenParam != null && !tokenParam.isEmpty()) {
//            return UriUtils.decode(tokenParam, StandardCharsets.UTF_8);
//        }
//
//        return null; // No token found
//    }
//
//}
