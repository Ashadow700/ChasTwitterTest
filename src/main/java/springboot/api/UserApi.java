package springboot.api;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springboot.dataEntities.User;
import springboot.repository.UserRepository;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Controller
@RequestMapping(path="/user")
public class UserApi {

    @Autowired
    private UserRepository userRepository;

    private final static Logger LOG = LoggerFactory.getLogger(UserApi.class);

    @PostMapping(path="/post")
    public ResponseEntity<String> postUser(@RequestBody @Valid User user){

        LOG.info("Received POST User request with username " + user.getUsername());

        if(userRepository.existsById(user.getUsername())) {
            String message = "User with id " + user.getUsername() + " already exists in database. User was not posted";
            LOG.debug(message);
            return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
        }

        userRepository.save(user);

        return new ResponseEntity<>("Successfully posted user to database", HttpStatus.CREATED);
    }

    @GetMapping(path="/get/{username}")
    public ResponseEntity<Object> getUser(@PathVariable String username){

        LOG.info("Received Get  user request with for username " + username);

        Optional<User> user = userRepository.findById(username);
        if(user.isPresent()){
            LOG.info("Found user " + username);
            User foundUser = user.get();
            foundUser.censorPassword();
            return new ResponseEntity<>(foundUser, HttpStatus.FOUND);

        } else {
            String message = "User with username " + username + " was not found in the database";
            LOG.info(message);
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping(path="/get/all")
    public ResponseEntity<Object> getAllUsers() {
        LOG.info("Received GET all users request");

        Iterable<User> users = userRepository.findAll();

        List<User> usersList = StreamSupport.stream(users.spliterator(), false)
                .peek(user -> user.censorPassword())
                .collect(Collectors.toList());

        return new ResponseEntity<>(usersList, HttpStatus.FOUND);
    }
}