package springboot.sqlQueries;

public class FollowedUserSqlQueries {

    public static String countLinesUserHasFollowedOtherUser() {
        return "select id\n" +
                "from followed_users\n" +
                "where followed_users.username = ? && followed_users.followedUsername = ?";
    }
}
