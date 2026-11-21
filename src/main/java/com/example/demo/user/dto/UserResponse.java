package com.example.demo.user.dto;

import com.example.demo.user.entity.UserEntity;

public record UserResponse(Long id, String userName, String nickName) {

    // 엔티티 응답용 상자 옮겨 담기
    public static UserResponse from(UserEntity user){
        return new UserResponse(user.getId(), user.getUserName(), user.getNickName());
    }
}
