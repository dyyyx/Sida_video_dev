package com.imooc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.imooc.controller.interceptor.MiniInterceptor;

@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {

		registry.addResourceHandler("/**").addResourceLocations("classpath:/META-INF/resources/")
				.addResourceLocations("file:D:/video_space_dev/");
	}

	@Bean
	public MiniInterceptor getMiniInterceptor() {
		return new MiniInterceptor();
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(getMiniInterceptor())
				.addPathPatterns("/user/**")
				.addPathPatterns("/video/upload", "/video/uploadCover", "/video/userLike",
								"/video/userUnLike", "/video/saveComment")
				.addPathPatterns("/bgm/**").excludePathPatterns("/user/queryPublisher");

		super.addInterceptors(registry);
	}

}
