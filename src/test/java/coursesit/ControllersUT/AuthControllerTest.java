package coursesit.ControllersUT;

import coursesit.controllers.AuthController;
import coursesit.services.UserService;
import coursesit.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @InjectMocks
    private AuthController authController; // Автоматически внедряется мок userService

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Инициализация аннотаций Mockito
    }

    @Test
    void testShowRegistrationForm() {
        String viewName = authController.showRegistrationForm(model);
        verify(model).addAttribute(eq("user"), any(User.class));
        assertEquals("register", viewName);
    }

    @Test
    void testRegisterUserSuccess() {
        when(userService.isUsernameTaken("newuser")).thenReturn(false);

        String viewName = authController.registerUser("newuser", "email@example.com", "password", model);
        verify(userService).registerUser("newuser", "email@example.com", "password");
        assertEquals("redirect:/login", viewName);
    }

    @Test
    void testRegisterUserFailure() {
        when(userService.isUsernameTaken("existinguser")).thenReturn(true);

        String viewName = authController.registerUser("existinguser", "email@example.com", "password", model);
        verify(model).addAttribute("error", "Username is already taken");
        assertEquals("register", viewName);
    }

    @Test
    void testShowLoginFormWithError() {
        // Создаем mock для Model
        Model model = mock(Model.class);

        // Вызываем метод с параметром error
        String viewName = authController.showLoginForm("Invalid username or password", model);

        // Проверяем, что viewName соответствует "login"
        assertEquals("login", viewName);

        // Проверяем, что в модель добавляется атрибут error
        verify(model, times(1)).addAttribute("error", "Invalid username or password");
    }

}
