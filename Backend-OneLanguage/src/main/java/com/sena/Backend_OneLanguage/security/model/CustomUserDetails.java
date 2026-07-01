package com.sena.Backend_OneLanguage.security.model;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Collections;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import com.sena.Backend_OneLanguage.users.entity.User;

public class CustomUserDetails implements UserDetails{

    private final User user;

    public CustomUserDetails(User user){
        this.user = user;
    }

    public User getUser(){
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        //Roles y permisos.

        return Collections.emptyList();
    }

    @Override
    public String getPassword() {

        return user.getPasswordHash();

    }

    @Override
    public String getUsername() {

        return user.getEmail();

    }

    @Override
    public boolean isAccountNonExpired() {

        return true;

    }

    @Override
    public boolean isAccountNonLocked() {

        OffsetDateTime lockedUntil = user.getLockedUntil();

        return lockedUntil == null || lockedUntil.isBefore(OffsetDateTime.now());

    }

    @Override
    public boolean isCredentialsNonExpired() {

        return true;

    }

    @Override
    public boolean isEnabled() {

        return Boolean.TRUE.equals(user.getUserStatus());

    }

}