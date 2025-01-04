package coursesit.ControllersUT;

import coursesit.Repositories.CourseRepository;
import coursesit.Repositories.TopicRepository;
import coursesit.Repositories.UserProfileRepository;
import coursesit.controllers.CourseController;
import coursesit.entities.Course;
import coursesit.entities.Topic;
import coursesit.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;

import java.util.Optional;
import java.util.List;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class CourseControllerTest {

    @InjectMocks
    private CourseController courseController;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private UserService userService;

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private Model model;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Тест отображения главной страницы с курсами.
     */
    @Test
    void testShowHomepage() {
        Page<Course> coursePage = new PageImpl<>(List.of()); // Пустой список курсов в Page
        when(courseRepository.findAll(any(Pageable.class))).thenReturn(coursePage);

        String viewName = courseController.showHomepage(model, 0);

        verify(model).addAttribute(eq("courses"), eq(coursePage.getContent()));
        verify(model).addAttribute(eq("currentPage"), eq(0));
        verify(model).addAttribute(eq("totalPages"), eq(coursePage.getTotalPages()));
        assertEquals("homepage", viewName);
    }


    /**
     * Тест отображения деталей курса.
     */
    @Test
    void testShowCourseDetails() {
        Course course = new Course();
        course.setId(1L);
        course.setTitle("Sample Course");
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        String viewName = courseController.showCourseDetails(1L, model);
        verify(model).addAttribute("course", course);
        assertEquals("course-details", viewName);
    }

    /**
     * Тест отображения деталей несуществующего курса.
     */
    @Test
    void testShowCourseDetailsNotFound() {
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        try {
            courseController.showCourseDetails(1L, model);
        } catch (RuntimeException e) {
            assertEquals("Course not found with id: 1", e.getMessage());
        }
    }

    /**
     * Тест успешного удаления курса.
     */
    @Test
    void testDeleteCourse() {
        doNothing().when(courseRepository).deleteById(1L);

        String viewName = courseController.deleteCourse(1L);
        verify(courseRepository).deleteById(1L);
        assertEquals("redirect:/homepage", viewName);
    }

    /**
     * Тест редактирования курса.
     */
    @Test
    void testEditCourse() {
        Course course = new Course();
        course.setId(1L);
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        String viewName = courseController.showEditCourseForm(1L, model);
        verify(model).addAttribute("course", course);
        assertEquals("edit-course", viewName);
    }
}
