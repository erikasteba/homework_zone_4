package coursesit.ControllersUT;

import coursesit.controllers.UserProfileController;
import coursesit.entities.UserProfile;
import coursesit.services.UserProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserProfileControllerTest {

    @InjectMocks
    private UserProfileController userProfileController;

    @Mock
    private UserProfileService userProfileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetProfileSuccess() {
        // Подготовка данных
        UserProfile mockProfile = new UserProfile();
        mockProfile.setFirstName("John");
        mockProfile.setLastName("Doe");

        when(userProfileService.getProfileForCurrentUser()).thenReturn(mockProfile);

        // Вызов тестируемого метода
        ResponseEntity<UserProfile> response = userProfileController.getProfile();

        // Проверка
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockProfile, response.getBody());
        verify(userProfileService, times(1)).getProfileForCurrentUser();
    }

    @Test
    void testUpdateProfileSuccess() {
        // Подготовка данных
        UserProfile inputProfile = new UserProfile();
        inputProfile.setFirstName("Jane");
        inputProfile.setLastName("Smith");

        UserProfile updatedProfile = new UserProfile();
        updatedProfile.setFirstName("Jane");
        updatedProfile.setLastName("Smith");

        when(userProfileService.updateProfile(inputProfile)).thenReturn(updatedProfile);

        // Вызов тестируемого метода
        ResponseEntity<UserProfile> response = userProfileController.updateProfile(inputProfile);

        // Проверка
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(updatedProfile, response.getBody());
        verify(userProfileService, times(1)).updateProfile(inputProfile);
    }
}
