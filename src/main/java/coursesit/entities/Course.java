package coursesit.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Data
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    @ManyToMany(mappedBy = "courses")
    private List<UserProfile> profiles;

    // Геттеры и сеттеры
}
