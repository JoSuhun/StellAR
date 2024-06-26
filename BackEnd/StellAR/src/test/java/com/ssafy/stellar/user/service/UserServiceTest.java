package com.ssafy.stellar.user.service;

import com.ssafy.stellar.fcm.entity.DeviceTokenEntity;
import com.ssafy.stellar.fcm.repository.DeviceTokenRepository;
import com.ssafy.stellar.user.dto.request.SignUpDto;
import com.ssafy.stellar.user.dto.response.UserDto;
import com.ssafy.stellar.user.entity.UserEntity;
import com.ssafy.stellar.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Service Unit-Test")
class UserServiceTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private DeviceTokenRepository deviceTokenRepository;

    @Mock
    private UserRepository userRepository;

    UserEntity user;

    @BeforeEach
    void setUp() {
        user = new UserEntity(); // 객체 생성
        String userId = "wncks";
        String password = "wncks1234";
        when(bCryptPasswordEncoder.encode(password)).thenReturn("encodedPassword");

        String encodedPassword = bCryptPasswordEncoder.encode(password);
        user.setUserId(userId);
        user.setPassword(encodedPassword);
    }


    @Test
    @DisplayName("회원가입 성공 테스트")
    void signUpSuccess() {

        // given
        SignUpDto request = new SignUpDto();
        request.setUserId("wncks");
        request.setPassword("wncks1234");
        when(userRepository.findByUserId("wncks")).thenReturn(null);
        when(bCryptPasswordEncoder.encode("wncks1234")).thenReturn("encodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenReturn(user);

        // when
        userService.signUp(request);

        // then
        verify(userRepository).save(any(UserEntity.class));

    }

    @Test
    @DisplayName("회원가입 실패 테스트 - 이미 있는 아이디")
    void signUpFailure() {

        // given
        SignUpDto request = new SignUpDto();
        request.setUserId("wncks");
        request.setPassword("wncks1234");
        when(userRepository.findByUserId("wncks")).thenReturn(user);
        when(bCryptPasswordEncoder.encode("wncks1234")).thenReturn("encodedPassword");

        // when & then
        assertThatThrownBy(() -> {
            userService.signUp(request);
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User already exists with id: " + user.getUserId());

    }

    @Test
    @DisplayName("로그인 성공 테스트")
    void logInSuccess() {

        // given
        DeviceTokenEntity deviceToken = new DeviceTokenEntity();
        deviceToken.setUser(user);
        deviceToken.setDeviceToken("aa");
        when(userRepository.findByUserId("wncks")).thenReturn(user);
        when(bCryptPasswordEncoder.matches("wncks1234", "encodedPassword")).thenReturn(true);
        when(deviceTokenRepository.findByDeviceTokenAndUser(anyString(), any(UserEntity.class))).thenReturn(deviceToken);

        // when
        UserDto userDto = userService.logIn("wncks", "wncks1234", "aa");

        // then
        assertThat(userDto).isNotNull()
                .extracting(UserDto::getUserId)
                .isEqualTo("wncks");
    }

    @Test
    @DisplayName("로그인 실패 테스트")
    void logInFailTest() {

        // given
        DeviceTokenEntity deviceToken = new DeviceTokenEntity();
        deviceToken.setUser(user);
        deviceToken.setDeviceToken("aa");
        when(userRepository.findByUserId("wncks")).thenReturn(user);
        when(bCryptPasswordEncoder.matches("wncks1234", "encodedPassword")).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> {
            userService.logIn("wncks", "wncks1234", null);
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid password");
    }

    @Test
    @DisplayName("디바이스 토큰 저장 테스트")
    void saveDeviceTokenTest() {
        // Given
        when(userRepository.findByUserId("wncks")).thenReturn(user);
        when(bCryptPasswordEncoder.matches("wncks1234", "encodedPassword")).thenReturn(true);
        when(deviceTokenRepository.findByDeviceTokenAndUser(anyString(), any(UserEntity.class))).thenReturn(null);

        // When
        userService.logIn("wncks", "wncks1234", "aa");

        // Then
        verify(deviceTokenRepository).save(any(DeviceTokenEntity.class));
    }

    @Test
    @DisplayName("디바이스 토큰 없이 로그인 테스트")
    void loginWithoutDeviceTokenTest() {
        // Given
        when(userRepository.findByUserId("wncks")).thenReturn(user);
        when(bCryptPasswordEncoder.matches("wncks1234", "encodedPassword")).thenReturn(true);

        // When
        UserDto userDto = userService.logIn("wncks", "wncks1234", null);

        // Then
        assertThat(userDto).isNotNull()
                .extracting(UserDto::getUserId)
                .isEqualTo("wncks");
    }

    @Test
    @DisplayName("회원 탈퇴 성공 테스트")
    void deleteUserSuccessTest() {
        // Given
        when(userRepository.findByUserId("wncks")).thenReturn(user);
        when(bCryptPasswordEncoder.matches("wncks1234", "encodedPassword")).thenReturn(true);

        // When
        userService.deleteUser("wncks", "wncks1234");

        // Then
        verify(userRepository).deleteById("wncks");
    }

    @Test
    @DisplayName("회원 탈퇴 실패 테스트")
    void deleteUserFailTest() {
        // Given
        when(userRepository.findByUserId("wncks")).thenReturn(user);
        when(bCryptPasswordEncoder.matches("wncks1234", "encodedPassword")).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> {
            userService.deleteUser("wncks", "wncks1234");
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("아이디 비밀번호를 잘못 입력하였습니다.");
    }

    @Test
    @DisplayName("유저 없음 테스트")
    void NotFoundUserTest() {
        // Given
        when(userRepository.findByUserId("wncks")).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> {
            userService.deleteUser("wncks", "wncks1234");
        }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User not found with id: " + "wncks");

    }
}