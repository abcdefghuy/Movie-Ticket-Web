package com.example.movieticketWeb.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import jakarta.mail.MessagingException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.movieticketWeb.aop.ApiException;
import com.example.movieticketWeb.dto.request.*;
import com.example.movieticketWeb.dto.response.LoginResponse;
import com.example.movieticketWeb.entity.Person;
import com.example.movieticketWeb.enumeration.ErrorCode;
import com.example.movieticketWeb.exception.EmailServiceException;
import com.example.movieticketWeb.exception.UserNotFoundException;
import com.example.movieticketWeb.repository.IUserRepository;

@Service
public class AuthenticationService {
    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final IRefreshTokenService refreshTokenService;
    private final IAESService AESService;
    private final JwtService jwtService;

    public AuthenticationService(
            IUserRepository IUserRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            EmailService emailService,
            com.example.movieticketWeb.service.IRefreshTokenService iRefreshTokenService,
            com.example.movieticketWeb.service.IAESService iaesService,
            JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = IUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        refreshTokenService = iRefreshTokenService;
        AESService = iaesService;
        this.jwtService = jwtService;
    }

    public void signup(RegisterUserRequest input) {
        if (userRepository.existsByEmail(input.getEmail())) {
            throw new ApiException(ErrorCode.EMAIL_ALREADY_REGISTERED);
        }
        try {
            // Tạo đối tượng người dùng từ dữ liệu đầu vào
            Person person = new Person(
                    input.getUsername(),
                    input.getEmail(),
                    passwordEncoder.encode(input.getPassword()),
                    input.getPhone(),
                    "ROLE_USER",
                    input.getGender(),
                    input.getDob(),
                    input.getRegion());

            // Tạo mã xác nhận và thiết lập thời gian hết hạn
            person.setVerificationCode(generateVerificationCode());
            person.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
            person.setEnabled(false);

            // Lưu người dùng vào cơ sở dữ liệu
            Person savedUser = userRepository.save(person);

            // Gửi email xác nhận nếu lưu thành công
            sendVerificationEmail(savedUser);

            // Trả về đối tượng
        } catch (Exception e) {
            // Nếu có lỗi xảy ra (ví dụ: lỗi trong việc lưu người dùng)
            // Log lỗi và trả về thông báo lỗi
            // Bạn có thể sử dụng logging hoặc xử lý thêm tùy vào nhu cầu của bạn
            throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR, "Error during user registration: ");
        }
    }

    public LoginResponse authenticate(LoginUserRequest input) throws Exception {
        Person user = userRepository
                .findByEmail(input.getEmail())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        if (!user.isEnabled()) {
            throw new ApiException(ErrorCode.ACCOUNT_NOT_VERIFIED);
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(input.getEmail(), input.getPassword()));
        } catch (BadCredentialsException e) {
            throw new ApiException(ErrorCode.INVALID_CREDENTIALS);
        }
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        String encryptedAccessToken = AESService.encrypt(accessToken);
        // Lưu refresh token vào Redis
        refreshTokenService.storeRefreshToken(user.getEmail(), refreshToken);

        return new LoginResponse(encryptedAccessToken);
    }

    public void verifyUser(VerifyUserRequest input) {

        Optional<Person> optionalUser = userRepository.findByEmail(input.getEmail());

        optionalUser.ifPresentOrElse(
                user -> {
                    // Kiểm tra mã OTP có hết hạn không
                    if (user.getVerificationCodeExpiresAt() == null
                            || user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
                        throw new ApiException(ErrorCode.OTP_EXPIRED, "Verification code has expired");
                    }
                    // Kiểm tra mã xác thực
                    if (!user.getVerificationCode().equals(input.getVerificationCode())) {
                        userRepository.save(user);
                        throw new ApiException(ErrorCode.INVALID_OTP, "Invalid verification code");
                    }

                    // Xác thực thành công → Cập nhật trạng thái tài khoản
                    user.setEnabled(true);
                    user.setVerificationCode(null);
                    user.setVerificationCodeExpiresAt(null);

                    userRepository.save(user);
                },
                () -> {
                    throw new UserNotFoundException("User not found");
                });
    }

    public void resendVerificationCode(String email) {
        Optional<Person> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            Person user = optionalUser.get();
            if (user.isEnabled()) {
                throw new ApiException(ErrorCode.ACCOUNT_ALREADY_VERIFIED, "Account is already verified");
            }
            user.setVerificationCode(generateVerificationCode());
            user.setVerificationCodeExpiresAt(LocalDateTime.now().plusHours(1));
            sendVerificationEmail(user);
            userRepository.save(user);
        } else {
            throw new ApiException(ErrorCode.USER_NOT_FOUND, "User not found");
        }
    }

    private void sendVerificationEmail(Person user) { // TODO: Update with company logo
        String subject = "Account Verification";
        String verificationCode = "VERIFICATION CODE " + user.getVerificationCode();
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome to our app!</h2>"
                + "<p style=\"font-size: 16px;\">Please enter the verification code below to continue:</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: 0 0 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Verification Code:</h3>"
                + "<p style=\"font-size: 18px; font-weight: bold; color: #007bff;\">" + verificationCode + "</p>"
                + "</div>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendVerificationEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            // Handle email sending exception
            throw new EmailServiceException("Lỗi gửi email: " + e.getMessage());
        }
    }

    public void forgotPassword(ForgotPasswordRequest input) {
        Person user = userRepository
                .findByEmail(input.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!user.isEnabled()) {
            throw new ApiException(ErrorCode.ACCOUNT_NOT_VERIFIED);
        }

        try {
            user.setVerificationCode(generateVerificationCode());
            user.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
            Person savedUser = userRepository.save(user);
            sendVerificationEmail(savedUser);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public void resetPassword(ResetPasswordRequest input) {
        Person user = userRepository
                .findByEmail(input.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!user.isEnabled()) {
            throw new ApiException(ErrorCode.ACCOUNT_NOT_VERIFIED);
        }
        try {
            user.setPassword(passwordEncoder.encode(input.getNewPassword()));
            userRepository.save(user);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }

    public Person loadUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}
