package com.pipelinex.shared.config;

import com.pipelinex.auth.ForcePasswordChangeInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final ForcePasswordChangeInterceptor forcePasswordChangeInterceptor;

    public WebConfig(ForcePasswordChangeInterceptor forcePasswordChangeInterceptor) {
        this.forcePasswordChangeInterceptor = forcePasswordChangeInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(forcePasswordChangeInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/login", "/error");
    }
}
