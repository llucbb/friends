package com.schibsted.spain.friends.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Builder
@EqualsAndHashCode(exclude = "password")
public final class User {

    private String userName;

    private String password;

    @Builder.Default
    private Set<String> pendingFriendships = new HashSet<>();

    @Builder.Default
    private Set<String> friends = new HashSet<>();
}
