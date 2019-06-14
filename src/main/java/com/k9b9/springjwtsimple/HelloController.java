package com.k9b9.springjwtsimple;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @RequestMapping("/public")
    public String sayHelloPublic() {
        return "Hello from Public!";
    }

    @RequestMapping("/admin")
    public String sayHelloAdmin() {
        return "Hello from Admin!";
    }
}