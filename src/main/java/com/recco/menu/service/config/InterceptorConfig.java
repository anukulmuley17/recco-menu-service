//package com.recco.menu.service.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//import com.recco.menu.service.services.JWTInterceptor;
//
//@Configuration
//public class InterceptorConfig implements WebMvcConfigurer {
//
//    private final JWTInterceptor jwtInterceptor;
//
//    public InterceptorConfig(JWTInterceptor jwtInterceptor) {
//        this.jwtInterceptor = jwtInterceptor;
//    }
//
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(jwtInterceptor)
//                .addPathPatterns("/api/menu/**"); // Apply to all menu APIs
//    }
//}
