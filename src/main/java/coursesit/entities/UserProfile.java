package coursesit.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
@Data
@Entity
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Связь с сущностью User

    private String firstName;
    private String lastName;

    public List<Course> getCourses() {
        return courses;
    }

    @ManyToMany
    @JoinTable(
            name = "user_courses",
            joinColumns = @JoinColumn(name = "profile_id"),
            inverseJoinColumns = @JoinColumn(name = "course_id")
    )
    private List<Course> courses;

}
