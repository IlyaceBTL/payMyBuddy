package com.paymybuddy.paymybuddy.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class FriendId implements Serializable {
    private UUID user1;
    private UUID user2;
}