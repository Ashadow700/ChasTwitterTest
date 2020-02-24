package springboot.sqlQueries;

public class TweetSqlQueries {

    public static String getAllTweetsFromUser() {
        return "SELECT users.username, tweets.tweet, tweets.timestamp\n" +
                "FROM users left outer join tweets on (users.username = tweets.username)\n" +
                "where  users.username = ?\n" +
                "order by tweets.timestamp";
    }


    public static String getAllTweetsFromFollowedUsers() {
        return "select followed_users.followedUsername as \"username\", tweets.tweet, tweets.timestamp \n" +
                "from users left outer join followed_users on (users.username = followed_users.username)\n" +
                "left outer join tweets on (followed_users.followedUsername = tweets.username)\n" +
                "where users.username = ?\n" +
                "order by timestamp";
    }
}
