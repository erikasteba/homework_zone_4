package coursesit.ServicesUT;

import coursesit.Repositories.UserProfileRepository;
import coursesit.entities.Course;
import coursesit.entities.User;
import coursesit.entities.UserProfile;
import coursesit.services.UserProfileService;
import coursesit.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserProfileServiceTest {

    @InjectMocks
    private UserProfileService userProfileService;

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private UserService userService;

    private User mockUser;
    private UserProfile mockProfile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Создаем мокированные данные
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testuser");

        mockProfile = new UserProfile();
        mockProfile.setId(1L);
        mockProfile.setUser(mockUser);
        mockProfile.setCourses(new ArrayList<>());
    }

    @Test
    void testGetProfileForCurrentUserSuccess() {
        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(userProfileRepository.findByUser(mockUser)).thenReturn(Optional.of(mockProfile));

        UserProfile profile = userProfileService.getProfileForCurrentUser();

        assertNotNull(profile);
        assertEquals(mockUser, profile.getUser());
        verify(userService, times(1)).getCurrentUser();
        verify(userProfileRepository, times(1)).findByUser(mockUser);
    }

    @Test
    void testGetProfileForCurrentUserNotFound() {
        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(userProfileRepository.findByUser(mockUser)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                userProfileService.getProfileForCurrentUser());

        assertEquals("Profile not found", exception.getMessage());
        verify(userService, times(1)).getCurrentUser();
        verify(userProfileRepository, times(1)).findByUser(mockUser);
    }

    @Test
    void testUpdateProfile() {
        when(userProfileRepository.save(mockProfile)).thenReturn(mockProfile);

        UserProfile updatedProfile = userProfileService.updateProfile(mockProfile);

        assertNotNull(updatedProfile);
        assertEquals(mockProfile, updatedProfile);
        verify(userProfileRepository, times(1)).save(mockProfile);
    }

    @Test
    void testGetCoursesForCurrentUser() {
        List<Course> mockCourses = new ArrayList<>();
        Course course1 = new Course();
        course1.setTitle("Course 1");
        mockCourses.add(course1);

        mockProfile.setCourses(mockCourses);
        when(userService.getCurrentUser()).thenReturn(mockUser);
        when(userProfileRepository.findByUser(mockUser)).thenReturn(Optional.of(mockProfile));

        List<Course> courses = userProfileService.getCoursesForCurrentUser();

        assertNotNull(courses);
        assertEquals(1, courses.size());
        assertEquals("Course 1", courses.get(0).getTitle());
        verify(userService, times(1)).getCurrentUser();
        verify(userProfileRepository, times(1)).findByUser(mockUser);
    }
}
