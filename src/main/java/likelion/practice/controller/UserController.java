//UserController
package likelion.practice.controller;

import likelion.practice.dto.PostDTO;
import likelion.practice.dto.UserDTO;
import likelion.practice.entity.Post;
import likelion.practice.entity.User;
import likelion.practice.security.JwtTokenProvider;
import likelion.practice.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    public UserController(UserService userService, JwtTokenProvider jwtTokenProvider, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
    }

    // 회원가입 API
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody UserDTO userDTO) {
        User registeredUser = userService.registerUser(userDTO);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    // 로그인 API
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody UserDTO userDTO) {
        try {
            // 사용자 인증 시도
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userDTO.getUserId(), userDTO.getPassword())
            );

            // 인증 성공 시 JWT 토큰 생성
            String jwtToken = jwtTokenProvider.createToken(authentication.getName());

            // 응답으로 토큰을 Map 형태로 반환
            Map<String, String> response = new HashMap<>();
            response.put("token", jwtToken);

            return ResponseEntity.ok(response);

        } catch (AuthenticationException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password.");
        }
    }

    // 아이디 중복 조회 API
    @GetMapping("/check-username")
    public ResponseEntity<Map<String, Boolean>> checkUsername(@RequestParam String userId) {
        boolean exists = userService.existsByUserId(userId);

        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/user-info")
    public ResponseEntity<List<UserDTO>> searchPosts(@RequestParam String id) {
        Optional<User> userOptional = userService.searchUsers(id);

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }

        User user = userOptional.get(); // Optional에서 User 객체 추출

        // 엔티티를 DTO로 변환
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(user.getUserId());
        userDTO.setName(user.getName());
        userDTO.setProfileImage(user.getProfileImage());

        // 리스트 형태로 반환
        return ResponseEntity.ok(Collections.singletonList(userDTO));
    }

    @PatchMapping("/alter-id")
    public ResponseEntity<UserDTO> updateUser(@RequestParam String id, @RequestParam String newId) {
        Optional<User> userOptional = userService.searchUsers(id);

        // 사용자 존재 여부 확인
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        User user = userOptional.get(); // Optional에서 User 객체 가져오기
        user.setUserId(newId); // 기존 id를 newId로 변경

        // 변경된 사용자 정보 저장
        User updatedUser = userService.saveUser(user);

        // 업데이트된 엔티티를 DTO로 변환
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(updatedUser.getUserId());
        userDTO.setName(updatedUser.getName());
        userDTO.setProfileImage(updatedUser.getProfileImage());

        return ResponseEntity.ok(userDTO);
    }
}
