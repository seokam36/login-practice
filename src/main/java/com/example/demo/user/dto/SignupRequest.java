package com.example.demo.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignupRequest(
        // @NotBlank -> 비어있거나 띄어쓰기만 있으면 안됨
        // @Size(min, max) -> 글자 수 범위
        @NotBlank @Size(min = 4, max = 20) String userName,
        @NotBlank @Size(min = 8, max = 64) String password,
        @NotBlank @Size(min = 2, max = 20) String nickName
) {
}
