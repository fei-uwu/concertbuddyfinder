package com.concertbuddy.concertbuddyfinder.middleware;

import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDateTime;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

@Component
@Order(1)
public class LoggingFilter implements Filter {
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        // Get request URL
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();

        // Log the request method, URL, and timestamp to file before handling the request
        PrintStream fileStream = new PrintStream("finder-request-log.txt");
        System.setOut(fileStream);
        System.out.println("[" + LocalDateTime.now() + "] " +
                request.getDispatcherType() + " request to " + requestURI +
                " at " + request.getRemoteAddr());

        // Continue with the request-handling process
        chain.doFilter(request, response);

        // Log additional information after the request has been handled
        System.out.println("[" + LocalDateTime.now() + "] RESPONSE " + 
                "Response status: " + response.getContentType());
    }
}
