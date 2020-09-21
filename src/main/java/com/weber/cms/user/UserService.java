package com.weber.cms.user;

import java.util.UUID;

import com.weber.cms.user.model.User;
import com.weber.cms.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User recordUser(User user, boolean encodePassword) {

        if (user.getId() == null) {
            user.setId(UUID.randomUUID());
        }

        if(user.getPassword() != null && encodePassword) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        return userRepository.record(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.getUserByUsername(username);

        if (user == null) {
            throw  new UsernameNotFoundException("User not found using username " + username);
        }

        return user;
    }
}
