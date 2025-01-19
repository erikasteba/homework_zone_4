package coursesit.controllers;

import coursesit.repositories.UserProfileRepository;
import coursesit.repositories.UserRepository;
import coursesit.entities.User;
import coursesit.entities.UserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;

    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserRepository userRepository, UserProfileRepository userProfileRepository) {
        this.userRepository = userRepository;
        this.userProfileRepository = userProfileRepository;
    }

    @GetMapping("/profile")
    public String showProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            logger.info("Authenticated username: {}", username);

            User currentUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found: " + username));
            logger.info("Current user: {}", currentUser);
            logger.info("Current user ID: {}", currentUser.getId());

            // checking if method finds user profile
            UserProfile userProfile = userProfileRepository.findByUser_Id(currentUser.getId())
                    .orElseThrow(() -> new RuntimeException("Profile not found for user: " + username));
            logger.info("UserProfile: {}", userProfile);

            model.addAttribute("userProfile", userProfile);
            model.addAttribute("username", username);
        } else {
            logger.info("Profile couldn`t be loaded, try again.");
            return "redirect:/login";
        }

        return "profile";
    }


}
