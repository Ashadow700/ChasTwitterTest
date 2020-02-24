package springboot.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springboot.dataEntities.Tweet;
import springboot.repository.TweetRepository;
import springboot.repository.UserRepository;
import springboot.sqlQueries.TweetSqlQueries;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping(path="/tweet")
public class TweetApi {

    @Autowired
    private TweetRepository tweetRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    private final static Logger LOG = LoggerFactory.getLogger(TweetApi.class);

    @PostMapping(path="/post")
    public ResponseEntity<String> postTweet(@RequestBody @Valid Tweet tweet) {

        LOG.info("Received POST Tweet request with for username " + tweet.getUsername());

        if(!userRepository.existsById(tweet.getUsername())) {
            String message = "User with username " + tweet.getUsername() + " does not exist in database. Tweet was not posted";
            LOG.warn(message);
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }

        tweetRepository.save(tweet);
        return new ResponseEntity<>("Successfully posted tweet to database", HttpStatus.CREATED);
    }

    @GetMapping(path="/get/all")
    public ResponseEntity<Object> getAllTweets() {

        LOG.info("Received GET all Tweets request");

        Iterable<Tweet> tweets = tweetRepository.findAll();
        return new ResponseEntity<>(tweets, HttpStatus.FOUND);
    }


    /*
    Some duplicated code in the following methods. We could turn parts of this into a private method, or even its own
    separate database access class. Because there is so little duplicated code though, I decided not to as I believe doing so
    would make the code more complex then it needs to be
     */
    @GetMapping(path="/get/{username}")
    public ResponseEntity<Object> getTweetsFromUser(@PathVariable String username) {

        LOG.info("Received GET all Tweets request for user " + username);

        if(!userRepository.existsById(username)) {
            String message = "User with username " + username + " does not exist in database";
            LOG.info(message);
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }

        String sql = TweetSqlQueries.getAllTweetsFromUser();
        List list = jdbcTemplate.queryForList(sql, new Object[] {username});

        return new ResponseEntity<>(list, HttpStatus.FOUND);
    }


    @GetMapping(path="/get/FollowedUsersTweets/{username}")
    public ResponseEntity<Object> getTweetsFromFollowedUsers(@PathVariable String username) {

        LOG.info("Received GET all Tweets request for user " + username);

        if(!userRepository.existsById(username)) {
            String message = "User with username " + username + " does not exist in database";
            LOG.info(message);
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }

        String sql = TweetSqlQueries.getAllTweetsFromFollowedUsers();
        List list = jdbcTemplate.queryForList(sql, new Object[] {username});

        return new ResponseEntity<>(list, HttpStatus.FOUND);
    }
}