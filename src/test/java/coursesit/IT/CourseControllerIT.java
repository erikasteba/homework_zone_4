package coursesit.IT;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class CourseControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testAddCourseWithValidData() throws Exception {
        mockMvc.perform(post("/add-course")
                        .param("title", "Sample Course")
                        .param("description", "This is a sample course.")
                        .param("topicsTitles", "Topic 1", "Topic 2")
                        .param("topicsContents", "Content 1", "Content 2")
                        .param("testQuestions", "Question 1", "Question 2")
                        .param("testAnswers1", "Answer 1.1", "Answer 2.1")
                        .param("testAnswers2", "Answer 1.2", "Answer 2.2")
                        .param("testAnswers3", "Answer 1.3", "Answer 2.3")
                        .param("correctAnswers", "1", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/homepage"));
    }

    @Test
    void testAddCourseWithoutTopics() throws Exception {
        mockMvc.perform(post("/add-course")
                        .param("title", "Sample Course")
                        .param("description", "This is a sample course."))
                .andExpect(status().isOk())
                .andExpect(view().name("add_course"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", containsString("Add at least 1 topic with content!")));
    }

    @Test
    void testAddCourseWithEmptyTopic() throws Exception {
        mockMvc.perform(post("/add-course")
                        .param("title", "Sample Course")
                        .param("description", "This is a sample course.")
                        .param("topicsTitles", "Topic 1", "")
                        .param("topicsContents", "Content 1", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("add_course"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", containsString("Each topic must have a title and content!")));
    }

    @Test
    void testAddCourseWithPartialTestAnswers() throws Exception {
        mockMvc.perform(post("/add-course")
                        .param("title", "Sample Course")
                        .param("description", "This is a sample course.")
                        .param("topicsTitles", "Topic 1")
                        .param("topicsContents", "Content 1")
                        .param("testQuestions", "Question 1")
                        .param("testAnswers1", "Answer 1.1")
                        .param("testAnswers2", "Answer 1.2")
                        .param("testAnswers3", "")
                        .param("correctAnswers", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/homepage"));
    }
}