package com.paymybuddy.paymybuddy.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class FriendId implements Serializable {
    private Integer user1;
    private Integer user2;

}