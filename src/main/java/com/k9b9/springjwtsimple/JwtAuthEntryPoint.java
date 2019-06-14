package com.k9b9.springjwtsimple;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javassist.bytecode.stackmap.TypeData.ClassName;

@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    private static final Logger LOGGER = Logger.getLogger(ClassName.class.getName());

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e)
            throws IOException, ServletException {

        LOGGER.log(Level.WARNING, "Unauthorized error. Message - {}", e.getMessage());
        System.out.println(">>>>>>>> Unauthorized");
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error -> Unauthorized");
    }
}