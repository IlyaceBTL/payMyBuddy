package com.paymybuddy.paymybuddy.model;

import java.io.Serializable;
import java.util.Objects;

public class FriendId implements Serializable {
    private Integer user1;
    private Integer user2;

    public FriendId() {}
    public FriendId(Integer user1, Integer user2) {
        this.user1 = user1;
        this.user2 = user2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FriendId)) return false;
        FriendId that = (FriendId) o;
        return Objects.equals(user1, that.user1) && Objects.equals(user2, that.user2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user1, user2);
    }
}