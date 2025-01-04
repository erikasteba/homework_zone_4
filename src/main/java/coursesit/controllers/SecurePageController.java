package coursesit.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SecurePageController {

    @GetMapping("/myPage")
    public String showPage(Model model) {
        // Получаем информацию о текущем пользователе
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();  // Имя пользователя
            model.addAttribute("username", username);
        } else {
            return "redirect:/login";  // Перенаправляем на страницу логина, если пользователь не аутентифицирован
        }

        // Возвращаем защищенную страницу
        return "myPage";
    }
}

