package com.pheonix.authenticationsystem.service;

import com.pheonix.authenticationsystem.repository.UserEntity;
import com.pheonix.authenticationsystem.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> userDetailsEntity = userRepository.findByUserName(username);

        if (userDetailsEntity.isPresent()) {
            Collection<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(userDetailsEntity.get().getRole().toString()));
            return new User(userDetailsEntity.get().getUserName(), userDetailsEntity.get().getPassword(), authorities);
        } else {
            throw new UsernameNotFoundException("Username not found");
        }
    }
}