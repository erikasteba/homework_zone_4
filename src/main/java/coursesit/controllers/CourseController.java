package coursesit.controllers;

import coursesit.Repositories.CourseRepository;
import coursesit.Repositories.TopicRepository;
import coursesit.Repositories.UserProfileRepository;
import coursesit.entities.*;
import coursesit.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Controller
public class CourseController {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private TopicRepository topicRepository;

    @GetMapping("/homepage")
    public String showHomepage(Model model, @RequestParam(defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 3); // 3 courses are shown per page
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

    @PostMapping("/add-course")
    public String addCourse(
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam(name = "topicsTitles", required = false) List<String> topicsTitles,
            @RequestParam(name = "topicsContents", required = false) List<String> topicsContents,
            @RequestParam(required = false) List<String> testQuestions,
            @RequestParam(required = false) List<String> testAnswers1,
            @RequestParam(required = false) List<String> testAnswers2,
            @RequestParam(required = false) List<String> testAnswers3,
            @RequestParam(required = false) List<Integer> correctAnswers,
            Model model) {

        // check if there are topics
        if (topicsTitles == null || topicsContents == null
                || topicsTitles.isEmpty() || topicsContents.isEmpty()) {
            model.addAttribute("errorMessage", "Add at least 1 topic with content!");
            model.addAttribute("title", title);
            model.addAttribute("description", description);
            return "add_course";
        }

        // check topics are valid
        for (int i = 0; i < topicsTitles.size(); i++) {
            if (topicsTitles.get(i).isEmpty() || topicsContents.get(i).isEmpty()) {
                model.addAttribute("errorMessage", "Each topic must have a title and content!");
                model.addAttribute("title", title);
                model.addAttribute("description", description);
                return "add_course";
            }
        }

        Course course = new Course();
        course.setTitle(title);
        course.setDescription(description);

        for (int i = 0; i < topicsTitles.size(); i++) {
            Topic topic = new Topic();
            topic.setTitle(topicsTitles.get(i));
            topic.setContent(topicsContents.get(i));
            topic.setCourse(course);
            course.getTopics().add(topic);

            // adding tests
            if (testQuestions != null && testQuestions.size() > i
                    && !testQuestions.get(i).isEmpty()) {
                Test test = new Test();
                test.setQuestion(testQuestions.get(i));
                test.setAnswer1(testAnswers1.get(i));
                test.setAnswer2(testAnswers2.get(i));
                test.setAnswer3(testAnswers3.get(i));
                test.setCorrectAnswer(correctAnswers.get(i));
                test.setTopic(topic);
                topic.setTest(test);
            }
        }

        courseRepository.save(course);
        System.out.println("Course and topics successfully added.");
        return "redirect:/homepage";
    }



    @GetMapping("/course/{id}")
    public String showCourseDetails(@PathVariable Long id, Model model) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));
        model.addAttribute("course", course);
        model.addAttribute("topics", course.getTopics());

        User currentUser = userService.getCurrentUser();
        boolean hasAccess = userProfileRepository.findByUser(currentUser)
                .map(profile -> profile.getCourses().contains(course))
                .orElse(false);
        model.addAttribute("hasAccess", hasAccess);

        System.out.println("Course is successfully shown");
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

        // adding course to user`s profile
        if (!userProfile.getCourses().contains(course)) {
            userProfile.getCourses().add(course);
            userProfileRepository.save(userProfile);
        }

        System.out.println("User successfully applied to course");
        return "redirect:/profile";
    }

    @Transactional
    @DeleteMapping("/course/{id}/unsubscribe")
    public String unsubscribeFromCourse(@PathVariable Long id) {
        User currentUser = userService.getCurrentUser();
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));

        UserProfile userProfile = userProfileRepository.findByUser(currentUser)
                .orElseThrow(() -> new RuntimeException("Profile not found for user: " + currentUser.getUsername()));

        if (userProfile.getCourses().contains(course)) {
            userProfile.getCourses().remove(course);
            System.out.println("Removed user`s courses");
            userProfileRepository.save(userProfile);
            System.out.println("Updated user`s courses");
        }

        System.out.println("User successfully unsubscribed from course");
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
    public String updateCourse(@PathVariable Long id,
                               @RequestParam String title,
                               @RequestParam String description,
                               @RequestParam String content,
                               @RequestParam List<Long> topicIds,
                               @RequestParam List<String> topicsTitles,
                               @RequestParam List<String> topicsContents,
                               @RequestParam(required = false) List<String> testQuestions,
                               @RequestParam(required = false) List<String> testAnswer1s,
                               @RequestParam(required = false) List<String> testAnswer2s,
                               @RequestParam(required = false) List<String> testAnswer3s,
                               @RequestParam(required = false) List<Integer> testCorrectAnswers,
                               @RequestParam(required = false) List<Long> removedTopicIds) {

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + id));

        if (removedTopicIds != null) {
            removedTopicIds.forEach(topicRepository::deleteById);
        }

        // editing topics
        for (int i = 0; i < topicIds.size(); i++) {
            Long topicId = topicIds.get(i);
            Topic topic = topicRepository.findById(topicId)
                    .orElseThrow(() -> new RuntimeException("Topic not found with id: " + topicId));

            topic.setTitle(topicsTitles.get(i));
            topic.setContent(topicsContents.get(i));
            System.out.println("Successfully edited topic information");

            if (testQuestions != null && i < testQuestions.size()) {
                Test test = topic.getTest();
                if (test != null) {
                    test.setQuestion(testQuestions.get(i));
                    test.setAnswer1(testAnswer1s.get(i));
                    test.setAnswer2(testAnswer2s.get(i));
                    test.setAnswer3(testAnswer3s.get(i));
                    test.setCorrectAnswer(testCorrectAnswers.get(i));
                }
            }
        }

        course.setTitle(title);
        course.setDescription(description);
        course.setContent(content);
        courseRepository.save(course);

        System.out.println("Successfully edited course information");
        return "redirect:/course/" + id;
    }

    @GetMapping("/course/{courseId}/topics")
    public String showTopics(@PathVariable Long courseId, Model model) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));

        List<Topic> topics = topicRepository.findByCourseId(courseId);
        model.addAttribute("course", course);
        model.addAttribute("topics", topics);
        return "topics";
    }

    @GetMapping("/course/{courseId}/topic/{topicId}")
    public String showTopic(@PathVariable Long courseId, @PathVariable Long topicId, Model model) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found with id: " + courseId));
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new RuntimeException("Topic not found with id: " + topicId));

        model.addAttribute("course", course);
        model.addAttribute("topics", course.getTopics());
        model.addAttribute("topic", topic);

        System.out.println("Topic is successfully loaded");
        return "topic";
    }


    @PostMapping("/course/{id}/delete")
    public String deleteCourse(@PathVariable Long id) {
        courseRepository.deleteById(id);

        System.out.println("Course is successfully deleted");
        return "redirect:/homepage";
    }

}

