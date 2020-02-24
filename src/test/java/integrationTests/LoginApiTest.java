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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "/application-integrationtest.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class LoginApiTest {

    @Autowired
    private MockMvc mvc;

    private ObjectMapper mapper;

    @Before
    public void init() {
        mapper = new ObjectMapper();
    }

    @Test
    public void login() throws Exception {

        User testUser = new User("testUser", "testPassword", "testEmail");
        String testUserJson = mapper.writeValueAsString(testUser);

        mvc.perform(post("/user/post")
                .content(testUserJson)
                .contentType(MediaType.APPLICATION_JSON));

        mvc.perform(get("/login?username=testUser&password=testPassword"))
                .andExpect(status().isOk())
                .andExpect(content().string("Logged In"));
    }

    @Test
    public void failLogin() throws Exception {

        User testUser = new User("testUser", "testPassword", "testEmail");
        String testUserJson = mapper.writeValueAsString(testUser);

        mvc.perform(post("/user/post")
                .content(testUserJson)
                .contentType(MediaType.APPLICATION_JSON));

        mvc.perform(get("/login?username=testUser&password=wrongPassword"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Failed"));
    }
}
