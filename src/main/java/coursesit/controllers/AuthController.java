package coursesit.controllers;

import coursesit.services.UserService;
import coursesit.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username, @RequestParam String email, @RequestParam String password, Model model) {
        if (userService.isUsernameTaken(username)) {
            model.addAttribute("error", "Username is already taken");
            return "register";
        }

        userService.registerUser(username, email, password);
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    //@GetMapping("/homepage")
    //public String showHomepage() {
    //    return "homepage"; // Убедитесь, что существует шаблон homepage.html
    //}
}