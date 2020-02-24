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
import springboot.dataEntities.FollowedUser;
import springboot.dataEntities.User;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "/application-integrationtest.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class FollowUserApiTest {

    @Autowired
    private MockMvc mvc;

    private ObjectMapper mapper;

    @Before
    public void init() {
        mapper = new ObjectMapper();
    }


    @Test
    public void postFollowUser() throws Exception {

        User testUser = new User("testUser", "testPassword", "testEmail");
        String testUserJson = mapper.writeValueAsString(testUser);

        User userToBeFollowed = new User("userToBeFollowed", "userToBeFollowedPassword", "userToBeFollowedEmail");
        String userToBeFollowedJson = mapper.writeValueAsString(userToBeFollowed);

        FollowedUser followedUser = new FollowedUser(testUser.getUsername(), userToBeFollowed.getUsername());
        String followedUserJson = mapper.writeValueAsString(followedUser);

        mvc.perform(post("/user/post")
                .content(testUserJson)
                .contentType(MediaType.APPLICATION_JSON));

        mvc.perform(post("/user/post")
                .content(userToBeFollowedJson)
                .contentType(MediaType.APPLICATION_JSON));

        mvc.perform(post("/followUser/post")
                .content(followedUserJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void postFollowUserForNonExistentUser() throws Exception {

        FollowedUser followedUser = new FollowedUser("testUser", "userToBeFollowed");
        String followedUserJson = mapper.writeValueAsString(followedUser);

        mvc.perform(post("/followUser/post")
                .content(followedUserJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void postAndGetAllFollowedUsers() throws Exception {

        User testUser = new User("testUser", "testPassword", "testEmail");
        String testUserJson = mapper.writeValueAsString(testUser);

        User userToBeFollowed = new User("userToBeFollowed", "userToBeFollowedPassword", "userToBeFollowedEmail");
        String userToBeFollowedJson = mapper.writeValueAsString(userToBeFollowed);

        FollowedUser followedUser = new FollowedUser(testUser.getUsername(), userToBeFollowed.getUsername());
        String followedUserJson = mapper.writeValueAsString(followedUser);

        List<FollowedUser> followedUsersList = Arrays.asList(new FollowedUser[]{followedUser});
        String expectedResult = mapper.writeValueAsString(followedUsersList);

        //Posts users to database
        mvc.perform(post("/user/post")
                .content(testUserJson)
                .contentType(MediaType.APPLICATION_JSON));

        mvc.perform(post("/user/post")
                .content(userToBeFollowedJson)
                .contentType(MediaType.APPLICATION_JSON));

        mvc.perform(post("/followUser/post")
                .content(followedUserJson)
                .contentType(MediaType.APPLICATION_JSON));

        mvc.perform(get("/followUser/get/all"))
                .andExpect(status().isFound())
                .andExpect(content().string(expectedResult));
    }
}