package com.paymybuddy.paymybuddy.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing a friendship between two users.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@IdClass(FriendId.class)
@Table(name = "friend")
public class Friend {

    /**
     * The first user in the friendship.
     */
    @Id
    @ManyToOne
    @JoinColumn(name = "id_user_1", referencedColumnName = "id_user", nullable = false)
    private User user1;

    /**
     * The second user in the friendship.
     */
    @Id
    @ManyToOne
    @JoinColumn(name = "id_user_2", referencedColumnName = "id_user", nullable = false)
    private User user2;
}