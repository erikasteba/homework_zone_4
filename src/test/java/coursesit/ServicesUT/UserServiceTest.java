package coursesit.ServicesUT;

import coursesit.Repositories.UserProfileRepository;
import coursesit.Repositories.UserRepository;
import coursesit.entities.User;
import coursesit.entities.UserProfile;
import coursesit.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private UserDetails userDetails;

    private User mockUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Создаем мокированные данные
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");
        mockUser.setEmail("testuser@example.com");
        mockUser.setPassword("encodedPassword");
    }

    @Test
    void testIsUsernameTaken() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertTrue(userService.isUsernameTaken("testuser"));
        verify(userRepository, times(1)).existsByUsername("testuser");
    }

    @Test
    void testRegisterUser() {
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L); // Имитируем сохранение
            return savedUser;
        });

        userService.registerUser("testuser", "testuser@example.com", "password");

        verify(userRepository, times(1)).save(any(User.class));
        verify(userProfileRepository, times(1)).save(any(UserProfile.class));
    }

    @Test
    void testGetCurrentUserSuccess() {
        // Мокаем Authentication
        Authentication mockAuthentication = mock(Authentication.class);
        UserDetails mockUserDetails = mock(UserDetails.class);

        // Настраиваем поведение mockUserDetails
        when(mockUserDetails.getUsername()).thenReturn("testuser");

        // Настраиваем поведение mockAuthentication
        when(mockAuthentication.getPrincipal()).thenReturn(mockUserDetails);

        // Настраиваем SecurityContext
        when(securityContext.getAuthentication()).thenReturn(mockAuthentication);
        SecurityContextHolder.setContext(securityContext);

        // Настраиваем userRepository
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));

        // Проверяем метод
        User currentUser = userService.getCurrentUser();

        // Проверки
        assertNotNull(currentUser);
        assertEquals("testuser", currentUser.getUsername());
        verify(userRepository, times(1)).findByUsername("testuser");
    }



    @Test
    void testGetCurrentUserNotFound() {
        // Мокаем Authentication
        Authentication mockAuthentication = mock(Authentication.class);
        UserDetails mockUserDetails = mock(UserDetails.class);

        // Настраиваем поведение mockUserDetails
        when(mockUserDetails.getUsername()).thenReturn("nonexistentuser");

        // Настраиваем поведение mockAuthentication
        when(mockAuthentication.getPrincipal()).thenReturn(mockUserDetails);

        // Настраиваем SecurityContext
        when(securityContext.getAuthentication()).thenReturn(mockAuthentication);
        SecurityContextHolder.setContext(securityContext);

        // Настраиваем userRepository для возврата пустого результата
        when(userRepository.findByUsername("nonexistentuser")).thenReturn(Optional.empty());

        // Проверяем, что выбрасывается исключение
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.getCurrentUser());

        assertEquals("User not found: nonexistentuser", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("nonexistentuser");
    }



    @Test
    void testGetCurrentUserInvalidPrincipal() {
        Authentication mockAuthentication = mock(Authentication.class);
        when(mockAuthentication.getPrincipal()).thenReturn(new Object()); // Некорректный principal
        when(securityContext.getAuthentication()).thenReturn(mockAuthentication);
        SecurityContextHolder.setContext(securityContext);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.getCurrentUser());

        assertEquals("Authentication principal is not of type UserDetails", exception.getMessage());
    }

}
