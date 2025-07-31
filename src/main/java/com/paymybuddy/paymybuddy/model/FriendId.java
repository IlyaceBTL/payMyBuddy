package com.paymybuddy.paymybuddy.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

/**
 * Composite key class for the Friend entity.
 */
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class FriendId implements Serializable {
    /**
     * The UUID of the first user in the friendship.
     */
    private UUID user1;

    /**
     * The UUID of the second user in the friendship.
     */
    private UUID user2;
}