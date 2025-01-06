package coursesit.controllers;

import coursesit.Repositories.UserProfileRepository;
import coursesit.Repositories.UserRepository;
import coursesit.entities.User;
import coursesit.entities.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

@Autowired
    private UserRepository userRepository;
    @Autowired
    private UserProfileRepository userProfileRepository;
    @GetMapping("/profile")
    public String showProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            System.out.println("Authenticated username: " + username);

            User currentUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found: " + username));
            System.out.println("Current user: " + currentUser);
            System.out.println("Current user ID: " + currentUser.getId());

            // checking if method finds user profile
            UserProfile userProfile = userProfileRepository.findByUser_Id(currentUser.getId())
                    .orElseThrow(() -> new RuntimeException("Profile not found for user: " + username));
            System.out.println("UserProfile: " + userProfile);

            model.addAttribute("userProfile", userProfile);
            model.addAttribute("username", username);
        } else {
            System.out.println("Profile couldn`t be loaded, try again.");
            return "redirect:/login";
        }

        return "profile";
    }


}
