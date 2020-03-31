package com.schibsted.spain.friends.service;

import com.schibsted.spain.friends.model.User;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintViolationException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {

    public static final String REGISTERED_USERS = "registeredUsers";
    private static final String MSG_USER_REGISTERED = "User %s has already been registered";
    private static final String MSG_USERS_NOT_REGISTERED = "There are no registered users yet";
    private static final String MSG_USER_NOT_REGISTERED = "User %s has not been registered yet";
    private static final String MSG_USER_FRIENDSHIP_NOT_HIMSELF = "User cannot request friendship to himself";
    private static final String MSG_USER_FRIENDSHIP_PENDING = "User cannot request friendship to a user that " +
            "already has a pending request from him";
    private static final String MSG_USER_FRIENDSHIP_NOT_PENDING = "User %s does not have a pending request from %s";
    private static final String MSG_USERS_ALREADY_FRIENDS = "User %s and user %s are already friends";
    private static final String MSG_USER_PASSWORD = "Incorrect user %s password";

    /**
     * Requests to register a new user
     *
     * @param user            New {@link User} to be registered
     * @param registeredUsers Registered users of the system
     * @return Registered users of the system updated
     */
    public Map<String, User> signUp(User user, Map<String, User> registeredUsers) {
        if (registeredUsers == null) {
            registeredUsers = new ConcurrentHashMap<>();
        }
        if (!registeredUsers.containsKey(user.getUserName())) {
            registeredUsers.put(user.getUserName(), user);
            return registeredUsers;
        } else {
            throw new ConstraintViolationException(String.format(MSG_USER_REGISTERED, user.getUserName()), null);
        }
    }

    /**
     * Requests a friendship from userNameFrom to userNameTo
     *
     * @param userNameFrom    Username of the user requesting friendship
     * @param userNameTo      Username of the user receiving the friendship request
     * @param password        Password of the the user requesting friendship
     * @param registeredUsers Registered users of the system
     * @return Registered users of the system updated
     */
    public Map<String, User> requestFriendship(String userNameFrom, String userNameTo, String password,
                                               Map<String, User> registeredUsers) {

        validateUsersAreRegistered(registeredUsers, userNameFrom, userNameTo);
        validatePassword(userNameFrom, password, registeredUsers);

        if (userNameFrom.equals(userNameTo)) {
            throw new ConstraintViolationException(MSG_USER_FRIENDSHIP_NOT_HIMSELF, null);
        }

        // Check if there is not a pending request
        User userTo = registeredUsers.get(userNameTo);
        Set<String> pendingFriendships = userTo.getPendingFriendships();
        if (pendingFriendships.contains(userNameFrom)) {
            throw new ConstraintViolationException(MSG_USER_FRIENDSHIP_PENDING, null);
        }

        // Check if users are not already friends
        Set<String> friendsUserTo = userTo.getFriends();
        if (friendsUserTo.contains(userNameFrom)) {
            String msg = String.format(MSG_USERS_ALREADY_FRIENDS, userNameFrom, userNameTo);
            throw new ConstraintViolationException(msg, null);
        }

        // Add pending friendships request
        pendingFriendships.add(userNameFrom);

        registeredUsers.put(userNameTo, userTo);
        return registeredUsers;
    }

    /**
     * Accepts or declines a friendship
     *
     * @param userNameFrom    Username of the user with the pending friendship request to accept/decline
     * @param userNameTo      Username of the user receiving the friendship request
     * @param password        Password of the userNameFrom
     * @param registeredUsers Registered users of the system
     * @return Registered users of the system updated
     */
    public Map<String, User> acceptDeclineFriendship(boolean accept, String userNameFrom, String userNameTo,
                                                     String password, Map<String, User> registeredUsers) {

        validateUsersAreRegistered(registeredUsers, userNameFrom, userNameTo);
        validatePassword(userNameFrom, password, registeredUsers);

        // Check if userFrom have a pending request from userTo
        User userFrom = registeredUsers.get(userNameFrom);
        Set<String> pendingFriendships = userFrom.getPendingFriendships();
        if (!pendingFriendships.contains(userNameTo)) {
            String msg = String.format(MSG_USER_FRIENDSHIP_NOT_PENDING, userNameTo, userNameFrom);
            throw new ConstraintViolationException(msg, null);
        }

        // Remove pending request
        pendingFriendships.remove(userNameTo);

        User userTo = registeredUsers.get(userNameTo);
        if (accept) {
            // Make both users friends
            userFrom.getFriends().add(userNameTo);
            userTo.getFriends().add(userNameFrom);
        } else {
            // Make both users not friends
            userFrom.getFriends().remove(userNameTo);
            userTo.getFriends().remove(userNameFrom);
        }

        registeredUsers.put(userNameFrom, userFrom);
        registeredUsers.put(userNameTo, userTo);
        return registeredUsers;
    }

    /**
     * Lists the friends of a registered user
     *
     * @param userName        Username of the registered user
     * @param password        Password of the userName
     * @param registeredUsers Registered users of the system
     * @return Set of friends of the userName
     */
    public Set<String> listFriends(String userName, String password, Map<String, User> registeredUsers) {

        validateUsersAreRegistered(registeredUsers, userName);
        validatePassword(userName, password, registeredUsers);

        User user = registeredUsers.get(userName);
        return user.getFriends();
    }

    private void validateUsersAreRegistered(Map<String, User> registeredUsers, String... userNames) {
        if (CollectionUtils.isEmpty(registeredUsers)) {
            throw new ConstraintViolationException(MSG_USERS_NOT_REGISTERED, null);
        }
        for (String userName : userNames) {
            if (!registeredUsers.containsKey(userName)) {
                throw new ConstraintViolationException(String.format(MSG_USER_NOT_REGISTERED, userName), null);
            }
        }
    }

    private void validatePassword(String userName, String password, Map<String, User> registeredUsers) {
        if (!registeredUsers.get(userName).getPassword().equals(password)) {
            throw new ConstraintViolationException(String.format(MSG_USER_PASSWORD, userName), null);
        }
    }
}