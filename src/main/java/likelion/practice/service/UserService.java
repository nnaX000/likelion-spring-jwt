//UserService
package likelion.practice.service;

import likelion.practice.dto.UserDTO;
import likelion.practice.entity.Post;
import likelion.practice.entity.User;
import likelion.practice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;

    }
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUserId())
                .password(user.getPassword())
                .authorities("USER")
                .build();
    }
    // 회원가입 기능
    public User registerUser(UserDTO userDTO) {
        if (userRepository.existsByUserId(userDTO.getUserId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID already exists.");
        }

        User user = new User();
        user.setUserId(userDTO.getUserId());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setName(userDTO.getName());
        user.setProfileImage(userDTO.getProfileImage());

        return userRepository.save(user);
    }

    // 로그인 기능
    public User loginUser(String userId, String password) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid password.");
        }

        return user;
    }

    // 아이디 중복 조회
    public boolean existsByUserId(String userId) {
        return userRepository.existsByUserId(userId);
    }

    // 사람 찾기
    public Optional<User> searchUsers(String id) {
        return userRepository.findByUserId(id);
    }

    public User saveUser(User user) {
        return userRepository.save(user); // UserRepository의 save 메서드를 호출하여 사용자 정보 저장
    }

}
