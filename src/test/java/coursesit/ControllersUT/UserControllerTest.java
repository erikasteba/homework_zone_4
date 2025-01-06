package coursesit.ControllersUT;

import coursesit.Repositories.UserProfileRepository;
import coursesit.Repositories.UserRepository;
import coursesit.controllers.UserController;
import coursesit.entities.User;
import coursesit.entities.UserProfile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private Model model;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testShowProfileWithAuthenticatedUser() {
        // test data
        String username = "admin";
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setUsername(username);

        UserProfile userProfile = new UserProfile();
        userProfile.setId(1L);
        userProfile.setUser(user);


        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(username);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userProfileRepository.findByUser_Id(userId)).thenReturn(Optional.of(userProfile));

        // calling method
        String viewName = userController.showProfile(model);

        // checks
        assertEquals("profile", viewName);
        verify(model).addAttribute("userProfile", userProfile);
        verify(model).addAttribute("username", username);
    }

    @Test
    void testShowProfileWhenUserNotFound() {

        String username = "user5";

        // Настройка моков
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(username);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userController.showProfile(model);
        });

        assertEquals("User not found: " + username, exception.getMessage());
    }

    @Test
    void testShowProfileWhenProfileNotFound() {

        String username = "admin";
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setUsername(username);


        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn(username);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userProfileRepository.findByUser_Id(userId)).thenReturn(Optional.empty());


        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userController.showProfile(model);
        });

        assertEquals("Profile not found for user: " + username, exception.getMessage());
    }

    @Test
    void testShowProfileWhenNotAuthenticated() {

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);
        SecurityContextHolder.setContext(securityContext);

        String viewName = userController.showProfile(model);

        assertEquals("redirect:/login", viewName);
    }
}
