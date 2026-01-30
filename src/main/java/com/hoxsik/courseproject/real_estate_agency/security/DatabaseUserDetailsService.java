package com.hoxsik.courseproject.real_estate_agency.security;

import com.hoxsik.courseproject.real_estate_agency.jpa.entities.User;
import com.hoxsik.courseproject.real_estate_agency.jpa.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DatabaseUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public DatabaseUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<User> user = userRepository.findByUsername(username);

        if (user.isEmpty())
            throw new UsernameNotFoundException("Пользователь с указанным именем не найден");

        return new DatabaseUserDetails(user.get());
    }
}
