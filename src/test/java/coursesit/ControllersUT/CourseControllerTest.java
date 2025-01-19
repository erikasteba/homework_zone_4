package coursesit.ControllersUT;

import coursesit.repositories.CourseRepository;
import coursesit.repositories.TopicRepository;
import coursesit.repositories.UserProfileRepository;
import coursesit.controllers.CourseController;
import coursesit.entities.Course;
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


    @Test
    void testShowHomepage() {
        Page<Course> coursePage = new PageImpl<>(List.of()); // empty list on Page
        when(courseRepository.findAll(any(Pageable.class))).thenReturn(coursePage);

        String viewName = courseController.showHomepage(model, 0);

        verify(model).addAttribute("courses", coursePage.getContent());
        verify(model).addAttribute("currentPage", 0);
        verify(model).addAttribute("totalPages", coursePage.getTotalPages());
        assertEquals("homepage", viewName);
    }

    @Test
    void testShowCourseDetails() {
        Course course = new Course();
        course.setId(1L);
        course.setTitle("Course");
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        String viewName = courseController.showCourseDetails(1L, model);
        verify(model).addAttribute("course", course);
        assertEquals("course-details", viewName);
    }


    @Test
    void testShowCourseDetailsNotFound() {
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        try {
            courseController.showCourseDetails(1L, model);
        } catch (RuntimeException e) {
            assertEquals("Course not found with id: 1", e.getMessage());
        }
    }


    @Test
    void testDeleteCourse() {
        doNothing().when(courseRepository).deleteById(1L);

        String viewName = courseController.deleteCourse(1L);
        verify(courseRepository).deleteById(1L);
        assertEquals("redirect:/homepage", viewName);
    }



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
