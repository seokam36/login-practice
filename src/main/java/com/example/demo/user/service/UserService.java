package com.example.demo.user.service;

import com.example.demo.user.dto.LoginRequest;
import com.example.demo.user.dto.SignupRequest;
import com.example.demo.user.dto.UserResponse;
import com.example.demo.user.entity.UserEntity;
import com.example.demo.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public UserResponse signup(SignupRequest request){
        // 아이디 중복 체크
        if (userRepository.existsByUserName(request.userName())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 사용 중인 아이디입니다.");
        }

        // 닉네임 중복체크
        if (userRepository.existsByNickName(request.nickName())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 사용 중인 닉네임입니다.");
        }

        // 비밀번호 암호화 해서 회원 만들기
        UserEntity user = UserEntity.builder()
                .userName(request.userName())
                .nickName(request.nickName())
                .password(passwordEncoder.encode(request.password()))
                .build();

        // 저장 (동시 가입시 DB가 중복을 막으면 409로 바꿈)
        try {
            UserEntity saved = userRepository.saveAndFlush(user);
            return UserResponse.from(saved);
        } catch (DataIntegrityViolationException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 사용 중인 아이디 또는 닉네임입니다.");
        }
    }

    public String login(LoginRequest request) {
        // 1. 아이디 검사, 없으면 401
        UserEntity user = userRepository.findByUserName(request.userName())
                .orElseThrow(()-> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 틀렸습니다."));
        // 2. 비밀번호 검사, 없으면 401
        if (!passwordEncoder.matches(request.password(), user.getPassword())){
            throw (new ResponseStatusException(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 틀렸습니다."));
        }

        // 3. accessToken에 id랑 userName넣어서 리턴
        return tokenService.createAccessToken(user.getId(), user.getUserName());
    }
}
