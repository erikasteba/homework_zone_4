package coursesit.ServicesUT;

import coursesit.Repositories.UserRepository;
import coursesit.entities.User;
import coursesit.services.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoadUserByUsernameSuccess() {
        // Подготовка данных
        User mockUser = new User();
        mockUser.setUsername("admin");
        mockUser.setPassword("password123");
        mockUser.setRole("ROLE_ADMIN");

        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(mockUser));

        // Вызов тестируемого метода
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("admin");

        // Проверка
        assertNotNull(userDetails);
        assertEquals("admin", userDetails.getUsername());
        assertEquals("password123", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN")));

        verify(userRepository, times(1)).findByUsername("admin");
    }

    @Test
    void testLoadUserByUsernameNotFound() {
        // Подготовка данных
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        // Вызов тестируемого метода и проверка исключения
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () ->
                customUserDetailsService.loadUserByUsername("unknown"));

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("unknown");
    }
}
