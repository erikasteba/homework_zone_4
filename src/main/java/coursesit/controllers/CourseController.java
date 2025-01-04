package coursesit.controllers;

import coursesit.Repositories.CourseRepository;
import coursesit.Repositories.UserProfileRepository;
import coursesit.entities.Course;
import coursesit.entities.User;
import coursesit.entities.UserProfile;
import coursesit.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
@Controller
public class CourseController {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;
    @Autowired
    private UserService userService;


    @GetMapping("/homepage")
    public String showHomepage(Model model, @RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 5); // 5 курсов на странице
        Page<Course> coursePage = courseRepository.findAll(pageable);

        model.addAttribute("courses", coursePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", coursePage.getTotalPages());

        return "homepage";
    }

    @GetMapping("/add-course")
    public String showAddCourseForm(Model model) {
        model.addAttribute("course", new Course());
        return "add_course";
    }


    @Transactional
    @PostMapping("/add-course")
    public String addCourse(@RequestParam String title,
                            @RequestParam String description,
                            @RequestParam String content) {
        Course course = new Course();
        course.setTitle(title);
        course.setDescription(description);
        course.setContent(content);
        courseRepository.save(course);
        return "redirect:/homepage";
    }


    @GetMapping("/course/{id}")
    public String showCourseDetails(@PathVariable Long id, Model model) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));
        model.addAttribute("course", course);
        return "course-details";
    }



    @Transactional
    @PostMapping("/course/{id}/apply")
    public String applyToCourse(@PathVariable Long id) {
        User currentUser = userService.getCurrentUser();
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));

        UserProfile userProfile = userProfileRepository.findByUser(currentUser)
                .orElseThrow(() -> new RuntimeException("Profile not found for user: " + currentUser.getUsername()));

        // Добавляем курс к профилю пользователя
        if (!userProfile.getCourses().contains(course)) {
            userProfile.getCourses().add(course);
            userProfileRepository.save(userProfile);
        }

        return "redirect:/profile";
    }

    @GetMapping("/course/{id}/edit")
    public String showEditCourseForm(@PathVariable Long id, Model model) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));
        model.addAttribute("course", course);
        return "edit-course";
    }

    @PostMapping("/course/{id}/edit")
    public String updateCourse(@PathVariable Long id, @ModelAttribute("course") Course updatedCourse) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));

        course.setTitle(updatedCourse.getTitle());
        course.setDescription(updatedCourse.getDescription());
        course.setContent(updatedCourse.getContent());
        courseRepository.save(course);

        return "redirect:/course/" + id;
    }

}

