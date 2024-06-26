package com.raczkowski.app.user;

import com.raczkowski.app.dto.UserDto;
import com.raczkowski.app.dtoMappers.UserDtoMapper;
import com.raczkowski.app.exceptions.ResponseException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email);
    }

    public String signUpUser(AppUser appUser) {
        if (userRepository.findByEmail(appUser.getEmail()) != null) {
            throw new ResponseException("User already exists");
        }

        if (appUser.getPassword().length() < 8) {
            throw new ResponseException("Shorter than minimum length 8");
        }

        String encodedPassword = bCryptPasswordEncoder
                .encode(appUser.getPassword());

        appUser.setPassword(encodedPassword);

        userRepository.save(appUser);

        return null;
    }

    public List<AppUser> loadAllUser() {
        return userRepository.findAll();
    }

    public AppUser getLoggedUser() {
        return userRepository.findByEmail(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getName());
    }

    public UserDto getUserById(Long id) {
        return UserDtoMapper.userDto(userRepository.getAppUserById(id));
    }

    public AppUser getUserByEmail(String email){
        return userRepository.findByEmail(email);
    }
}
