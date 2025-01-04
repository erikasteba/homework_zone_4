package coursesit.services;


import coursesit.Repositories.UserProfileRepository;
import coursesit.Repositories.UserRepository;
import coursesit.entities.User;
import coursesit.entities.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserProfileRepository userProfileRepository;

    public boolean isUsernameTaken(String username) {
        return userRepository.existsByUsername(username);
    }

    public void registerUser(String username, String email, String password) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("ROLE_ADMIN");
        userRepository.save(user);

        // Создаём профиль для пользователя
        UserProfile userProfile = new UserProfile();
        userProfile.setUser(user);
        userProfileRepository.save(userProfile);
    }

    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            String username = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
            return userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found: " + username));
        } else {
            throw new RuntimeException("Authentication principal is not of type UserDetails");
        }
    }
}
