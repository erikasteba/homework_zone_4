package coursesit.controllers;

import coursesit.services.UserService;
import coursesit.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    private static Logger logger = LoggerFactory.getLogger(AuthController.class);

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String email,
                               @RequestParam String password,
                               Model model) {
        boolean isUsernameTaken = userService.isUsernameTaken(username);
        boolean isEmailTaken = userService.isEmailTaken(email);

        if (isUsernameTaken) {
            model.addAttribute("usernameError", "Username is already taken");
        }

        if (isEmailTaken) {
            model.addAttribute("emailError", "Email is already used");
        }

        if (isUsernameTaken || isEmailTaken) {
            logger.info("username and email are taken");
            return "register";
        }

        userService.registerUser(username, email, password);
        logger.info("User is successfully registered");
        return "redirect:/login";
    }


    @GetMapping("/login")
    public String showLoginForm(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid username or password");
        }
        logger.info("User is successfully logged in");
        return "login";
    }


}