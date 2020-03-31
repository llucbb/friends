package com.schibsted.spain.friends.legacy;

import com.schibsted.spain.friends.model.User;
import com.schibsted.spain.friends.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Map;

import static com.schibsted.spain.friends.service.UserService.REGISTERED_USERS;
import static com.schibsted.spain.friends.util.Constants.MSG_ALPHANUMERIC_CHARS;
import static com.schibsted.spain.friends.util.Constants.REGEXP_ALPHANUMERIC;

@RestController
@RequestMapping("/friendship")
@Validated
@RequiredArgsConstructor
@SuppressWarnings("unchecked")
public class FriendshipLegacyController implements ServletContextAware {

    private ServletContext servletContext;

    private final UserService userService;

    @PostMapping("/request")
    void requestFriendship(@RequestParam("usernameFrom") @Size(min = 5, max = 10)
                           @Pattern(regexp = REGEXP_ALPHANUMERIC, message = MSG_ALPHANUMERIC_CHARS) String usernameFrom,

                           @RequestParam("usernameTo") @Size(min = 5, max = 10)
                           @Pattern(regexp = REGEXP_ALPHANUMERIC, message = MSG_ALPHANUMERIC_CHARS) String usernameTo,

                           @RequestHeader("X-Password") @Size(min = 8, max = 12)
                           @Pattern(regexp = REGEXP_ALPHANUMERIC, message = MSG_ALPHANUMERIC_CHARS) String password) {

        Map<String, User> registeredUsers = (Map<String, User>) servletContext.getAttribute(REGISTERED_USERS);
        servletContext.setAttribute(REGISTERED_USERS,
                userService.requestFriendship(usernameFrom, usernameTo, password, registeredUsers));
    }

    @PostMapping("/accept")
    void acceptFriendship(@RequestParam("usernameFrom") @Size(min = 5, max = 10)
                          @Pattern(regexp = REGEXP_ALPHANUMERIC, message = MSG_ALPHANUMERIC_CHARS) String usernameFrom,

                          @RequestParam("usernameTo") @Size(min = 5, max = 10)
                          @Pattern(regexp = REGEXP_ALPHANUMERIC, message = MSG_ALPHANUMERIC_CHARS) String usernameTo,

                          @RequestHeader("X-Password") @Size(min = 8, max = 12)
                          @Pattern(regexp = REGEXP_ALPHANUMERIC, message = MSG_ALPHANUMERIC_CHARS) String password) {

        Map<String, User> registeredUsers = (Map<String, User>) servletContext.getAttribute(REGISTERED_USERS);
        servletContext.setAttribute(REGISTERED_USERS,
                userService.acceptDeclineFriendship(true, usernameFrom, usernameTo, password, registeredUsers));
    }

    @PostMapping("/decline")
    void declineFriendship(@RequestParam("usernameFrom") @Size(min = 5, max = 10)
                           @Pattern(regexp = REGEXP_ALPHANUMERIC, message = MSG_ALPHANUMERIC_CHARS) String usernameFrom,

                           @RequestParam("usernameTo") @Size(min = 5, max = 10)
                           @Pattern(regexp = REGEXP_ALPHANUMERIC, message = MSG_ALPHANUMERIC_CHARS) String usernameTo,

                           @RequestHeader("X-Password") @Size(min = 8, max = 12)
                           @Pattern(regexp = REGEXP_ALPHANUMERIC, message = MSG_ALPHANUMERIC_CHARS) String password) {

        Map<String, User> registeredUsers = (Map<String, User>) servletContext.getAttribute(REGISTERED_USERS);
        servletContext.setAttribute(REGISTERED_USERS,
                userService.acceptDeclineFriendship(false, usernameFrom, usernameTo, password, registeredUsers));
    }

    @GetMapping("/list")
    Object listFriends(@RequestParam("username") @Size(min = 5, max = 10)
                       @Pattern(regexp = REGEXP_ALPHANUMERIC, message = MSG_ALPHANUMERIC_CHARS) String username,

                       @RequestHeader("X-Password") @Size(min = 8, max = 12)
                       @Pattern(regexp = REGEXP_ALPHANUMERIC, message = MSG_ALPHANUMERIC_CHARS) String password) {

        Map<String, User> registeredUsers = (Map<String, User>) servletContext.getAttribute(REGISTERED_USERS);
        return userService.listFriends(username, password, registeredUsers);
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
