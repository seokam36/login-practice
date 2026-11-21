package com.example.demo.user.controller;

import com.example.demo.user.dto.SignupRequest;
import com.example.demo.user.dto.UserResponse;
import com.example.demo.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    @PostMapping("/signup")
    public UserResponse signup(@Valid @RequestBody SignupRequest request){
        return userService.signup(request);
    }
}
