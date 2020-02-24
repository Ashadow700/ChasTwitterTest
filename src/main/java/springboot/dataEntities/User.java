package springboot.dataEntities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @NotNull(message = "Username must not be null")
    private String username;

    private String password;

    private String email;

    @OneToMany(cascade=CascadeType.ALL)
    @JoinColumn(name="username", referencedColumnName="username")
    private Set<Tweet> tweets;

    @OneToMany(cascade=CascadeType.ALL)
    @JoinColumn(name="username", referencedColumnName="username")
    private Set<FollowedUser> followedUsers;

    public User() {
        //Dummy constructor used for serialization
    }

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void censorPassword() {
        this.password = "-";
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonIgnore
    public Set<Tweet> getTweets() {
        return tweets;
    }

    @JsonIgnore
    public Set<FollowedUser> getFollowedUsers() {
        return followedUsers;
    }

}
