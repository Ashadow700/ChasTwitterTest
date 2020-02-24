package integrationTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import springboot.Main;
import springboot.dataEntities.User;

import java.util.Arrays;
import java.util.List;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/*
Running tests with mocked web client MockMvc. Replacing Mysql database with in-memory database H2 for th tests.
Database is considered dirty after each test, and is therefor rebuilt before every test
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main.class, webEnvironment = WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "/application-integrationtest.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class UserApiTest {

    @Autowired
    private MockMvc mvc;

    private ObjectMapper mapper;

    @Before
    public void init() {
        mapper = new ObjectMapper();
    }

    @Test
    public void postUser() throws Exception {

        User testUser = new User("testUser", "testPassword", "testEmail");
        String testUserJson = mapper.writeValueAsString(testUser);

        mvc.perform(post("/user/post")
                .content(testUserJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void postAndGetUser() throws Exception {

        User testUser = new User("testUser", "testPassword", "testEmail");
        String testUserJson = mapper.writeValueAsString(testUser);

        testUser.censorPassword();
        String expectedResult = mapper.writeValueAsString(testUser);

        mvc.perform(post("/user/post")
                .content(testUserJson)
                .contentType(MediaType.APPLICATION_JSON));

        mvc.perform(get("/user/get/testUser"))
                .andExpect(status().isFound())
                .andExpect(content().string(expectedResult));
    }

    @Test
    public void postAndGetAllUsers() throws Exception {

        User testUser = new User("testUser", "testPassword", "testEmail");
        String testUserJson = mapper.writeValueAsString(testUser);
        User testUser2 = new User("secondTestUser", "secondTestPassword", "secondTestEmail");
        String testUserJson2 = mapper.writeValueAsString(testUser2);

        testUser.censorPassword();
        testUser2.censorPassword();

        List<User> usersList = Arrays.asList(new User[]{testUser, testUser2});
        String expectedResult = mapper.writeValueAsString(usersList);

        mvc.perform(post("/user/post")
                .content(testUserJson)
                .contentType(MediaType.APPLICATION_JSON));

        mvc.perform(post("/user/post")
                .content(testUserJson2)
                .contentType(MediaType.APPLICATION_JSON));

        mvc.perform(get("/user/get/all"))
                .andExpect(status().isFound())
                .andExpect(content().string(expectedResult));
    }

    @Test
    public void postSameUserTwice() throws Exception {

        User testUser = new User("testUser", "testPassword", "testEmail");
        String testUserJson = mapper.writeValueAsString(testUser);

        testUser.censorPassword();
        List<User> usersList = Arrays.asList(new User[]{testUser});
        String expectedResult = mapper.writeValueAsString(usersList);

        mvc.perform(post("/user/post")
                .content(testUserJson)
                .contentType(MediaType.APPLICATION_JSON));

        mvc.perform(post("/user/post")
                .content(testUserJson)
                .contentType(MediaType.APPLICATION_JSON));

        mvc.perform(get("/user/get/all"))
                .andExpect(status().isFound())
                .andExpect(content().string(expectedResult));
    }
}
