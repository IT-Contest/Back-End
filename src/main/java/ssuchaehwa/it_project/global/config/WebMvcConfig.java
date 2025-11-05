package ssuchaehwa.it_project.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // .well-known 디렉토리의 리소스를 명시적으로 처리
        registry.addResourceHandler("/.well-known/**")
                .addResourceLocations("classpath:/static/.well-known/");
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        // apple-app-site-association 파일에 대해 application/json Content-Type 설정
        configurer.mediaType("apple-app-site-association", MediaType.APPLICATION_JSON);
    }
}
