package springboot.dataEntities;

import javax.persistence.*;

/*
This Entity manages which users follows which other users. "followed_users" is not a very good name for the table, but
it was the least bad name I could come up with"
 */
@Entity
@Table(name = "followed_users")
public class FollowedUser {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String username;

    private String followedUsername;

    public FollowedUser() {
        //Dummy constructor used for serialization
    }

    public FollowedUser(String username, String followedUsername) {
        this.username = username;
        this.followedUsername = followedUsername;
    }

    public String getUsername() {
        return username;
    }

    public String getFollowedUsername() {
        return followedUsername;
    }
}