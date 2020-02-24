package springboot.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springboot.dataEntities.FollowedUser;
import springboot.sqlQueries.FollowedUserSqlQueries;
import springboot.repository.FollowedUserRepository;
import springboot.repository.UserRepository;

import javax.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping(path="/followUser/")
public class FollowUserApi {

    @Autowired
    private FollowedUserRepository followedUserRepository;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    JdbcTemplate jdbcTemplate;

    private final static Logger LOG = LoggerFactory.getLogger(FollowUserApi.class);


    /*
    This class has grown large enough to where breaking it down into two classes; an API and a database manager is warranted.
    I've decided not to though, since the class is not expected to grow anytime soon and to save time
     */
    @PostMapping(path="/post")
    public ResponseEntity<String> postFollowUser(@RequestBody @Valid FollowedUser followedUser){

        LOG.info("Received POST follow user request for User " + followedUser.getUsername());

        if(!userRepository.existsById(followedUser.getUsername())) {
            String message = "User " + followedUser.getUsername() + " does not exist in database. Database was not altered";
            LOG.warn(message);
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }

        if(!userRepository.existsById(followedUser.getFollowedUsername())) {
            String message = "User " + followedUser.getFollowedUsername() + " does not exist in database. Database was not altered";
            LOG.warn(message);
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }


        if(userAlreadyFollows(followedUser)) {
            String message = "Users " + followedUser.getUsername() + " already follows " + followedUser.getFollowedUsername() + ". Database was not altered";
            LOG.warn(message);
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }

        followedUserRepository.save(followedUser);
        return new ResponseEntity<>("Database was successfully updated. " + followedUser.getUsername() + " followed " + followedUser.getFollowedUsername(), HttpStatus.CREATED);
    }

    @GetMapping(path="/get/all")
    public ResponseEntity<Object> getAllFollowedUsers(){

        LOG.info("Received GET all Tweets request");

        Iterable<FollowedUser> followedUsers = followedUserRepository.findAll();
        return new ResponseEntity<>(followedUsers, HttpStatus.FOUND);
    }

    @DeleteMapping(path="/delete")
    public ResponseEntity<String> deleteFollowUser(@RequestBody @Valid FollowedUser followedUser){

        LOG.info("Received DELETE follow user request for username " + followedUser.getUsername() + " and followedUsername " + followedUser.getFollowedUsername() );

        Optional<Integer> id = getFollowedUserId(followedUser);

        if(!id.isPresent()) {
            String message = "User with username " + followedUser.getUsername() + " does not follow " + followedUser.getFollowedUsername() + ". Database was not altered";
            LOG.warn(message);
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }

        followedUserRepository.deleteById(id.get());

        return new ResponseEntity<>("Database was successfully updated. " + followedUser.getUsername() + " unfollowed " + followedUser.getFollowedUsername(), HttpStatus.OK);
    }

    private boolean userAlreadyFollows(FollowedUser followedUser) {
        Optional<Integer> id = getFollowedUserId(followedUser);
        return id.isPresent();
    }

    private Optional<Integer> getFollowedUserId(FollowedUser followedUser) {
        String sql = FollowedUserSqlQueries.countLinesUserHasFollowedOtherUser();
        try {
            Integer id = jdbcTemplate.queryForObject(sql, new Object[] {followedUser.getUsername(), followedUser.getFollowedUsername()}, Integer.class);
            return Optional.of(id);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}