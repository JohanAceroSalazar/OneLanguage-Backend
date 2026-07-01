package com.sena.Backend_OneLanguage.auth.service.imple;

import com.sena.Backend_OneLanguage.auth.dto.AuthResponseDto;
import com.sena.Backend_OneLanguage.auth.dto.LoginRequestDto;
import com.sena.Backend_OneLanguage.auth.mapper.AuthMapper;
import com.sena.Backend_OneLanguage.auth.service.AuthService;
import com.sena.Backend_OneLanguage.security.jwt.JwtService;
import com.sena.Backend_OneLanguage.security.model.CustomUserDetails;
import com.sena.Backend_OneLanguage.users.dto.UserResponseDto;
import com.sena.Backend_OneLanguage.users.entity.User;
import com.sena.Backend_OneLanguage.users.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImple implements AuthService {

        private final AuthenticationManager authenticationManager;
        private final JwtService jwtService;
        private final UserMapper userMapper;
        private final AuthMapper authMapper;

        @Override
        public AuthResponseDto login(LoginRequestDto request) {

        Authentication authentication =
                authenticationManager.authenticate(

                        new UsernamePasswordAuthenticationToken(

                                request.getEmail(),
                                request.getPassword()

                        )

                );

        CustomUserDetails userDetails =
                (CustomUserDetails) authentication.getPrincipal();

        User user = userDetails.getUser();

        String token = jwtService.generateToken(userDetails);

        UserResponseDto responseDto =
                userMapper.toResponse(user);

        return authMapper.toResponse(
                token,
                responseDto);
        }
}