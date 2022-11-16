package net.dg.paiproiect.controller;

import lombok.AllArgsConstructor;
import net.dg.paiproiect.entity.ConfirmationToken;
import net.dg.paiproiect.entity.User;
import net.dg.paiproiect.repository.ConfirmationTokenRepository;
import net.dg.paiproiect.repository.UserRepository;
import net.dg.paiproiect.service.EmailService;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

import static net.dg.paiproiect.constants.ServiceConstants.*;

@Controller
@AllArgsConstructor
public class LoginController {

    private final UserRepository userRepository;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final EmailService emailService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/403")
    public String error403() {
        return "error403";
    }

    @RequestMapping(value = "/confirm-account", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView confirmUserAccount(ModelAndView modelAndView, @RequestParam("token") String confirmationToken) {

        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);

        if (token != null) {
            Optional<User> optional = userRepository.findByEmail(token.getUser().getEmail());
            if (optional.isPresent()) {

                User user = optional.get();
                user.setAccountUnLocked();
                userRepository.save(user);
                modelAndView.setViewName("accountVerified");
            }
        } else {
            modelAndView.addObject("message", "true");
            modelAndView.setViewName("accountVerified");
        }

        return modelAndView;
    }

    @GetMapping("/forgot-password")
    public ModelAndView displayResetPassword(ModelAndView modelAndView, User user) {
        modelAndView.addObject("user", user);
        modelAndView.setViewName(FORGOT_PASSWORD);
        return modelAndView;
    }

    @PostMapping("/forgot-password")
    public ModelAndView forgotUserPassword(ModelAndView modelAndView, User user) {
        Optional<User> optional = userRepository.findByEmail(user.getEmail());

        if (optional.isPresent()) {

            User existingUser = optional.get();
            ConfirmationToken confirmationToken = new ConfirmationToken(existingUser);
            confirmationTokenRepository.save(confirmationToken);

            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(existingUser.getEmail());
            mailMessage.setSubject("Complete Password Reset");
            mailMessage.setFrom("javaprojects1999@gmail.com");
            mailMessage.setText("To complete the password reset, please click here: "
                    + "http://localhost:8888/confirm-reset?token=" + confirmationToken.getConfirmationToken());

            emailService.sendEmail(mailMessage);

            modelAndView.addObject("succes", "Request to reset password received" +
                    ", check your inbox for the reset link.");
            modelAndView.setViewName(FORGOT_PASSWORD);
        } else {
            modelAndView.addObject(ERROR, "This email does not exist!");
            modelAndView.setViewName(FORGOT_PASSWORD);
        }

        return modelAndView;
    }

    @RequestMapping(value = "/confirm-reset", method = {RequestMethod.GET, RequestMethod.POST})
    public ModelAndView validateResetToken(ModelAndView modelAndView,
                                           @RequestParam("token") String confirmationToken) {
        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);

        if (token != null) {
            Optional<User> optional = userRepository.findByEmail(token.getUser().getEmail());

            User user = optional.orElseThrow(() -> new IllegalArgumentException("User cannot be null"));

            userRepository.save(user);
            modelAndView.addObject("user", user);
            modelAndView.addObject("email", user.getEmail());
            modelAndView.setViewName(RESET_PASSWORD);
        } else {
            modelAndView.addObject(ERROR, "The link is invalid or broken!");
            modelAndView.setViewName(RESET_PASSWORD);
        }

        return modelAndView;
    }

    @PostMapping(value = "/reset-password")
    public ModelAndView resetUserPassword(ModelAndView modelAndView, User user) {


        if (user.getEmail() != null) {
            Optional<User> optional = userRepository.findByEmail(user.getEmail());
            User tokenUser = optional.orElseThrow(() -> new IllegalArgumentException("User cannot be null"));

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            tokenUser.setPassword(encoder.encode(user.getPassword()));

            userRepository.save(tokenUser);
            modelAndView.addObject("succes", "Password succesfully reseted." +
                    "You can now log in with the new credentials.");
            modelAndView.setViewName(RESET_PASSWORD);
        } else {
            modelAndView.addObject(ERROR, "The link is invalid or broken!");
            modelAndView.setViewName(RESET_PASSWORD);
        }
        return modelAndView;
    }

}