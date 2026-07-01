package com.sena.Backend_OneLanguage.security.service;

import com.sena.Backend_OneLanguage.security.model.CustomUserDetails;
import com.sena.Backend_OneLanguage.users.entity.User;
import com.sena.Backend_OneLanguage.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userRepository
                .findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Usuario no encontrado"));

        return new CustomUserDetails(user);
    }
}