package apiEngine;

//The advantage it gives is that suppose any of the route changes. We won't have to make changes everywhere. It's just at a single place in the Routes class.
public class Routes {

    private static final String BOOKSTORE = "/BookStore";
    private static final String ACCOUNT = "/Account";
    private static final String VERSION = "/v1";

    public static String generateToken() {
        return ACCOUNT + VERSION + "/GenerateToken";
    }

    public static String books() {
        return BOOKSTORE + VERSION + "/Books";
    }

    public static String book() {
        return BOOKSTORE + VERSION + "/Book";
    }

    public static String userAccount() {
        return ACCOUNT + VERSION + "/User/{UUID}";
    }


}
