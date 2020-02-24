package springboot.dataEntities;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "tweets")
public class Tweet {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String username;

    @Size(max = 160)
    private String tweet;

    private LocalDateTime timestamp;

    public Tweet() {
        //Dummy constructor used for serialization
        this.timestamp = LocalDateTime.now();
    }

    public Tweet(String user, String tweet) {
        this.username = user;
        this.tweet = tweet;
        this.timestamp = LocalDateTime.now();
    }

    public Tweet(String user, String tweet, LocalDateTime timestamp) {
        this.username = user;
        this.tweet = tweet;
        this.timestamp = timestamp;
    }

    public Integer getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getTweet() {
        return tweet;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
