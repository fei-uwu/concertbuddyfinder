package com.concertbuddy.concertbuddyfinder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

import com.concertbuddy.concertbuddyfinder.middleware.LoggingFilter;

@SpringBootApplication
public class ConcertbuddyfinderApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConcertbuddyfinderApplication.class, args);
	}

	@Bean
    public FilterRegistrationBean<LoggingFilter> loggingFilter() {
        FilterRegistrationBean<LoggingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new LoggingFilter());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
}
