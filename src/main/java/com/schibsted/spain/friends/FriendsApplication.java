package com.schibsted.spain.friends;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.servlet.ServletContext;

@SpringBootApplication
public class FriendsApplication {

    private ServletContext servletContext;

    public static void main(String[] args) {
        SpringApplication.run(FriendsApplication.class, args);
    }

}
