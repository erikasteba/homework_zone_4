package coursesit.controllers;

import coursesit.entities.UserProfile;
import coursesit.services.UserProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile") // Убедитесь, что путь уникален
public class UserProfileController {
    private final UserProfileService userProfileService;

    public UserProfileController(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @GetMapping
    public ResponseEntity<UserProfile> getProfile() {
        UserProfile profile = userProfileService.getProfileForCurrentUser();
        return ResponseEntity.ok(profile);
    }

    @PostMapping
    public ResponseEntity<UserProfile> updateProfile(@RequestBody UserProfile profile) {
        UserProfile updatedProfile = userProfileService.updateProfile(profile);
        return ResponseEntity.ok(updatedProfile);
    }
}
