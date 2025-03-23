package com.example.movieticketWeb.service;

import com.example.movieticketWeb.dto.request.*;
import com.example.movieticketWeb.entity.Person;
import com.example.movieticketWeb.exception.EmailServiceException;
import com.example.movieticketWeb.exception.UserNotFoundException;
import com.example.movieticketWeb.repository.IUserRepository;
import jakarta.mail.MessagingException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.Random;

@Service
public class AuthenticationService {
    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;

    public AuthenticationService(
            IUserRepository IUserRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            EmailService emailService
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = IUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    public Person signup(RegisterUserRequest input) {
        try {
            // Tạo đối tượng người dùng từ dữ liệu đầu vào
            Person person = new Person(input.getUsername(), input.getEmail(), passwordEncoder.encode(input.getPassword()), input.getPhone(),"ROLE_USER", input.getGender(),  input.getDob(), input.getRegion());

            // Tạo mã xác nhận và thiết lập thời gian hết hạn
            person.setVerificationCode(generateVerificationCode());
            person.setVerificationCodeExpiresAt(LocalDateTime.now().plusMinutes(15));
            person.setEnabled(false);

            // Lưu người dùng vào cơ sở dữ liệu
            Person savedUser = userRepository.save(person);

            // Gửi email xác nhận nếu lưu thành công
            sendVerificationEmail(savedUser);

            // Trả về đối tượng người dùng đã lưu
            return savedUser;
        } catch (Exception e) {
            // Nếu có lỗi xảy ra (ví dụ: lỗi trong việc lưu người dùng)
            // Log lỗi và trả về thông báo lỗi
            // Bạn có thể sử dụng logging hoặc xử lý thêm tùy vào nhu cầu của bạn
            throw new RuntimeException("Error during user registration: " , e);
        }
    }

    public Person authenticate(LoginUserRequest input) {
        Person user = userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!user.isEnabled()) {
            throw new RuntimeException("Account not verified. Please verify your account.");
        }
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.getEmail(),
                        input.getPassword()
                )
        );

        return user;
    }

    public void verifyUser(VerifyUserRequest input) {

        Optional<Person> optionalUser = userRepository.findByEmail(input.getEmail());

        optionalUser.ifPresentOrElse(user -> {
            // Kiểm tra mã OTP có hết hạn không
            if (user.getVerificationCodeExpiresAt() == null || user.getVerificationCodeExpiresAt().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Verification code has expired");
            }


            // Kiểm tra mã xác thực
            if (!user.getVerificationCode().equals(input.getVerificationCode())) {
                userRepository.save(user);
                throw new RuntimeException("Invalid verification code");
            }

            // Xác thực thành công → Cập nhật trạng thái tài khoản
            user.setEnabled(true);
            user.setVerificationCode(null);
            user.setVerificationCodeExpiresAt(null);

            userRepository.save(user);

        }, () -> {
            throw new UserNotFoundException("User not found");
        });
    }

    public void resendVerificationCode(String email) {
        Optional<Person> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isPresent()) {
            Person user = optionalUser.get();
            if (user.isEnabled()) {
                throw new RuntimeException("Account is already verified");
            }
            user.setVerificationCode(generateVerificationCode());
            user.setVerificationCodeExpiresAt(LocalDateTime.now().plusHours(1));
            sendVerificationEmail(user);
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    private void sendVerificationEmail(Person user) { //TODO: Update with company logo
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
        Person user = userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!user.isEnabled()) {
            throw new RuntimeException("Account not verified. Please verify your account.");
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
        Person user = userRepository.findByEmail(input.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!user.isEnabled()) {
            throw new RuntimeException("Account not verified. Please verify your account.");
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
    public Person loadUserByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));
    }

}
