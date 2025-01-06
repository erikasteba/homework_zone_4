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
            savedUser.setId(1L);
            return savedUser;
        });

        userService.registerUser("testuser", "testuser@gmail.com", "password");

        verify(userRepository, times(1)).save(any(User.class));
        verify(userProfileRepository, times(1)).save(any(UserProfile.class));
    }

    @Test
    void testGetCurrentUserSuccess() {

        Authentication mockAuthentication = mock(Authentication.class);
        UserDetails mockUserDetails = mock(UserDetails.class);

        when(mockUserDetails.getUsername()).thenReturn("testuser");

        when(mockAuthentication.getPrincipal()).thenReturn(mockUserDetails);

        when(securityContext.getAuthentication()).thenReturn(mockAuthentication);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(mockUser));

        User currentUser = userService.getCurrentUser();

        assertNotNull(currentUser);
        assertEquals("testuser", currentUser.getUsername());
        verify(userRepository, times(1)).findByUsername("testuser");
    }



    @Test
    void testGetCurrentUserNotFound() {

        Authentication mockAuthentication = mock(Authentication.class);
        UserDetails mockUserDetails = mock(UserDetails.class);

        when(mockUserDetails.getUsername()).thenReturn("user5");

        when(mockAuthentication.getPrincipal()).thenReturn(mockUserDetails);

        when(securityContext.getAuthentication()).thenReturn(mockAuthentication);
        SecurityContextHolder.setContext(securityContext);

        when(userRepository.findByUsername("user5")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.getCurrentUser());

        assertEquals("User not found: user5", exception.getMessage());
        verify(userRepository, times(1)).findByUsername("user5");
    }


    @Test
    void testGetCurrentUserInvalidPrincipal() {
        Authentication mockAuthentication = mock(Authentication.class);
        when(mockAuthentication.getPrincipal()).thenReturn(new Object()); // principal is not correct
        when(securityContext.getAuthentication()).thenReturn(mockAuthentication);
        SecurityContextHolder.setContext(securityContext);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.getCurrentUser());

        assertEquals("Authentication principal is not of type UserDetails", exception.getMessage());
    }

}
