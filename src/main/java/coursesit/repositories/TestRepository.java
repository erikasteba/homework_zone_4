package coursesit.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import coursesit.entities.Test;

public interface TestRepository extends JpaRepository<Test, Long> {
}