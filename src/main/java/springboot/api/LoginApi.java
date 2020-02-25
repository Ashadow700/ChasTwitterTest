package springboot.api;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import springboot.dataEntities.User;
import springboot.repository.UserRepository;

import java.util.Optional;

@Controller
@RequestMapping(path="/login")
public class LoginApi {

    @Autowired
    private UserRepository userRepository;

    private final static Logger LOG = LoggerFactory.getLogger(LoginApi.class);

    /*
    This is obviously not a secure login API. To make a proper login API, one needs to generate and return a Web Token.
    Because all the other APIs are wide open anyway however as security was not a requirement for the test,
    I figured I could save some time by not implementing Web Token functionality
     */
    @GetMapping
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password) {

        LOG.info("Login request for username " + username);

        Optional<User> user = userRepository.findById(username);

        if(!user.isPresent()) {
            String message = "User with username " + username + " does not exist";
            LOG.info(message);
            return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
        }

        if(user.get().getPassword().equals(password)) {
            LOG.info("User " + username + " successfully logged in!");
            return new ResponseEntity<>("Logged In", HttpStatus.OK);
        } else {
            LOG.info("Found wrong password for user " + username);
            return new ResponseEntity<>("Failed", HttpStatus.BAD_REQUEST);
        }
    }
}
