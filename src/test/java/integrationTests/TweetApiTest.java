package integrationTests;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Main.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "/application-integrationtest.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class TweetApiTest {

    @Autowired
    private MockMvc mvc;

    private ObjectMapper mapper;

    @Before
    public void init() {
        mapper = new ObjectMapper();
    }

    @Test
    public void postTweet() throws Exception {

        User testUser = new User("testUser", "testPassword", "testEmail");
        String testUserJson = mapper.writeValueAsString(testUser);

        String tweetJson = "{\n" +
                "  \"username\": \"testUser\",\n" +
                "\t\"tweet\": \"Text of the tweet\"\n" +
                "}";

        mvc.perform(post("/user/post")
                .content(testUserJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mvc.perform(post("/tweet/post")
                .content(tweetJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    public void postAndGetTweet() throws Exception {

        User testUser = new User("testUser", "testPassword", "testEmail");
        String testUserJson = mapper.writeValueAsString(testUser);

        String tweetJson = "{\n" +
                "  \"username\": \"testUser\",\n" +
                "\t\"tweet\": \"Text of the tweet\"\n" +
                "}";

        mvc.perform(post("/user/post")
                .content(testUserJson)
                .contentType(MediaType.APPLICATION_JSON));

        mvc.perform(post("/tweet/post")
                .content(tweetJson)
                .contentType(MediaType.APPLICATION_JSON));

        String result = mvc.perform(get("/tweet/get/testUser"))
                .andExpect(status().isFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode[] jsonResult = mapper.readValue(result, JsonNode[].class);

        Assert.assertEquals("testUser", jsonResult[0].get("USERNAME").asText());
        Assert.assertEquals("Text of the tweet", jsonResult[0].get("TWEET").asText());
    }

    @Test
    public void postAndGetAllTweets() throws Exception {

        User testUser = new User("testUser", "testPassword", "testEmail");
        String testUserJson = mapper.writeValueAsString(testUser);

        User testUser2 = new User("testUser2", "testPassword2", "testEmail2");
        String testUserJson2 = mapper.writeValueAsString(testUser2);

        String tweetJson = "{\n" +
                "  \"username\": \"testUser\",\n" +
                "\t\"tweet\": \"Text of the tweet\"\n" +
                "}";

        String tweetJson2 = "{\n" +
                "  \"username\": \"testUser2\",\n" +
                "\t\"tweet\": \"Text of the second tweet\"\n" +
                "}";

        mvc.perform(post("/user/post")
                .content(testUserJson)
                .contentType(MediaType.APPLICATION_JSON));

        mvc.perform(post("/tweet/post")
                .content(tweetJson)
                .contentType(MediaType.APPLICATION_JSON));

        mvc.perform(post("/user/post")
                .content(testUserJson2)
                .contentType(MediaType.APPLICATION_JSON));

        mvc.perform(post("/tweet/post")
                .content(tweetJson2)
                .contentType(MediaType.APPLICATION_JSON));


        String result = mvc.perform(get("/tweet/get/all"))
                .andExpect(status().isFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode[] jsonResult = mapper.readValue(result, JsonNode[].class);

        Assert.assertEquals("testUser", jsonResult[0].get("username").asText());
        Assert.assertEquals("Text of the tweet", jsonResult[0].get("tweet").asText());

        Assert.assertEquals("testUser2", jsonResult[1].get("username").asText());
        Assert.assertEquals("Text of the second tweet", jsonResult[1].get("tweet").asText());
    }



    @Test
    public void postAndGetFollowedUsersTweets() throws Exception {

        User testUser = new User("testUser", "testPassword", "testEmail");
        String testUserJson = mapper.writeValueAsString(testUser);

        User userToFollow = new User("userToFollowName", "testPassword2", "testEmail2");
        String testUserJson2 = mapper.writeValueAsString(userToFollow);

        FollowedUser followedUser = new FollowedUser(testUser.getUsername(), userToFollow.getUsername());
        String followedUserJson = mapper.writeValueAsString(followedUser);

        String tweetJson = "{\n" +
                "  \"username\": \"userToFollowName\",\n" +
                "\t\"tweet\": \"tweet From Followed User\"\n" +
                "}";

        mvc.perform(post("/user/post")
                .content(testUserJson)
                .contentType(MediaType.APPLICATION_JSON));

        mvc.perform(post("/user/post")
                .content(testUserJson2)
                .contentType(MediaType.APPLICATION_JSON));

        mvc.perform(post("/followUser/post")
                .content(followedUserJson)
                .contentType(MediaType.APPLICATION_JSON));

        mvc.perform(post("/tweet/post")
                .content(tweetJson)
                .contentType(MediaType.APPLICATION_JSON));

        String result = mvc.perform(get("/tweet/get/FollowedUsersTweets/testUser"))
                .andExpect(status().isFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode[] jsonResult = mapper.readValue(result, JsonNode[].class);

        Assert.assertEquals("userToFollowName", jsonResult[0].get("username").asText());
        Assert.assertEquals("tweet From Followed User", jsonResult[0].get("TWEET").asText());
    }
}