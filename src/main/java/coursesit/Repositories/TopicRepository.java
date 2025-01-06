package coursesit.Repositories;

import coursesit.entities.Topic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TopicRepository extends JpaRepository<Topic, Long> {
    void deleteById(Long id);

    List<Topic> findByCourseId(Long courseId);

}
