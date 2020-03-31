package com.schibsted.spain.friends.legacy;

import com.schibsted.spain.friends.model.User;
import com.schibsted.spain.friends.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
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
@RequestMapping("/signup")
@Validated
@RequiredArgsConstructor
public class SignupLegacyController implements ServletContextAware {

    private ServletContext servletContext;

    private final UserService userService;

    @SuppressWarnings("unchecked")
    @PostMapping
    void signUp(@RequestParam(value = "username")
                @Size(min = 5, max = 10)
                @Pattern(regexp = REGEXP_ALPHANUMERIC, message = MSG_ALPHANUMERIC_CHARS) String username,

                @RequestHeader("X-Password")
                @Size(min = 8, max = 12)
                @Pattern(regexp = REGEXP_ALPHANUMERIC, message = MSG_ALPHANUMERIC_CHARS) String password) {

        User user = User.builder()
                .userName(username)
                .password(password)
                .build();
        Map<String, User> registeredUsers = (Map<String, User>) servletContext.getAttribute(REGISTERED_USERS);
        servletContext.setAttribute(REGISTERED_USERS, userService.signUp(user, registeredUsers));
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
