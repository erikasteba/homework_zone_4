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
    private AuthController authController;

    @Mock
    private UserService userService;

    @Mock
    private Model model;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
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

        String viewName = authController.registerUser("user123", "user123@gmail.com", "password", model);
        verify(userService).registerUser("user123", "user123@gmail.com", "password");
        assertEquals("redirect:/login", viewName);
    }

    @Test
    void testRegisterUserFailure() {
        when(userService.isUsernameTaken("user123")).thenReturn(true);

        String viewName = authController.registerUser("user123", "user123@gmail.com", "password", model);
        verify(model).addAttribute("usernameError", "Username is already taken");
        assertEquals("register", viewName);
    }

    @Test
    void testShowLoginFormWithError() {

        Model modelMock = mock(Model.class);

        String viewName = authController.showLoginForm("Invalid username or password", modelMock);

        // checking that viewName is same as "login"
        assertEquals("login", viewName);

        // checking that atribute error is added to the modelMock
        verify(modelMock, times(1)).addAttribute("error", "Invalid username or password");
    }

}
