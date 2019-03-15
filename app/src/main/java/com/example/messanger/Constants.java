package com.example.messanger;

public class Constants {
    public static final String SERVER_IP = "192.168.1.5";
    public static final int SERVER_PORT = 8080;
    public static final String ADD_USER =     "/messenger/user/add";
    public static final String ADD_MESSAGE =  "/messenger/messages/add";
    public static final String GET_MESSAGES = "/messenger/messages/get";
    public static final String LOGIN_USER =   "/messenger/user/login";
    public static final String LOGOUT_USER =  "/messenger/user/logout";
    public static final String GET_USERS  =   "/messenger/user/others";

    private static String currentUser;
    private static String currentPassword;
    private static String currentFriend;

    public static String getCurrentUser() {
        return currentUser;
    }

    public static String getCurrentPassword() {
        return currentPassword;
    }

    public static void setCurrentPassword(String currentPassword) {
        Constants.currentPassword = currentPassword;
    }

    public static void setCurrentUser(String user) {
        currentUser = user;
    }

    public static String getCurrentFriend() {
        return currentFriend;
    }

    public static void setCurrentFriend(String friend) {
        currentFriend = friend;
    }


}
