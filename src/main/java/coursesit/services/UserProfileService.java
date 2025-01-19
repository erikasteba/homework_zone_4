package coursesit.services;

import coursesit.repositories.UserProfileRepository;
import coursesit.entities.Course;
import coursesit.entities.User;
import coursesit.entities.UserProfile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserService userService;

    public UserProfileService(UserProfileRepository userProfileRepository, UserService userService) {
        this.userProfileRepository = userProfileRepository;
        this.userService = userService;
    }

    public UserProfile getProfileForCurrentUser() {
        User currentUser = userService.getCurrentUser();
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