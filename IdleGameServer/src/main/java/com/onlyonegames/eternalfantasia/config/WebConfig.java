package com.onlyonegames.eternalfantasia.config;

import com.onlyonegames.eternalfantasia.Interceptor.SessionInterceptor;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer, WebServerFactoryCustomizer<TomcatServletWebServerFactory> {
        // ---중략---/
//        @Override
        public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(sessionInterceptor())
                        .addPathPatterns("/*")
                        .addPathPatterns("/**/*")
                        .excludePathPatterns("/test/**/")
                        .excludePathPatterns("/auth/Login"); //로그인 쪽은 예외처리를 한다.
        }

        @Bean
        public SessionInterceptor sessionInterceptor() {
                return new SessionInterceptor();
        }
        // static 리소스 처리
        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry.addResourceHandler("/resources/**").addResourceLocations("/WEB-INF/resources/");
                registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
        }
        @Override
        public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("*");;
        }

        //by rainful 2021-01-19
        //게임팟 쿠폰 웹훅이 json으로 넘어와서 해당 함수 오버라이딩.
        @Override
        public void customize(TomcatServletWebServerFactory factory) {
                factory.addConnectorCustomizers((TomcatConnectorCustomizer)
                        connector -> connector.setAttribute("relaxedQueryChars", "<>[\\]^`{|}"));
        }
}