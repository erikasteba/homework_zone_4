package coursesit.services;

import coursesit.Repositories.UserProfileRepository;
import coursesit.entities.Course;
import coursesit.entities.User;
import coursesit.entities.UserProfile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserService userService; // Предполагается, что у вас уже есть UserService

    public UserProfileService(UserProfileRepository userProfileRepository, UserService userService) {
        this.userProfileRepository = userProfileRepository;
        this.userService = userService;
    }

    public UserProfile getProfileForCurrentUser() {
        User currentUser = userService.getCurrentUser(); // Получаем текущего пользователя
        return userProfileRepository.findByUser(currentUser)
                .orElseThrow(() -> new RuntimeException("Profile not found"));
    }

    public UserProfile updateProfile(UserProfile profile) {
        return userProfileRepository.save(profile);
    }

    public List<Course> getCoursesForCurrentUser() {
        UserProfile profile = getProfileForCurrentUser();
        return profile.getCourses();
    }

}