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
    @JoinColumn(name = "idUser_1", referencedColumnName = "idUser", nullable = false)
    private User user1;

    /**
     * The second user in the friendship.
     */
    @Id
    @ManyToOne
    @JoinColumn(name = "idUser_2", referencedColumnName = "idUser", nullable = false)
    private User user2;
}