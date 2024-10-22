package com.ip.api.controller;

import com.ip.api.domain.User;
import com.ip.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HelloController {
    private final UserRepository userRepository;
    @GetMapping("/")
    public String healthCheck() {
        return "hello Health Check!";
    }

    @GetMapping("/health")
    public String hello() {
        User user = new User("juhee");
        User newUser = userRepository.save(user);
        return "Id: " + newUser + " Name: " + newUser.getName();
    }
}
