package com.k9b9.springjwtsimple;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiTest {

    @GetMapping("/api/test/user")
    @PreAuthorize("hasRole('USER') or hasRole('OWNER') or hasRole('ADMIN')")
    public String userAccess() {
        return ">>> Hello from User Contents";
    }

    @GetMapping("/api/test/customer")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('OWNER') or hasRole('ADMIN')")
    public String projectManagementAccess() {
        return ">>> Hello from Customer Contents";
    }

    @GetMapping("/api/test/admin")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    public String adminAccess() {
        return ">>> Hello from Admin Contents";
    }
}