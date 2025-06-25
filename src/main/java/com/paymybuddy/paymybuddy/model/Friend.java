package com.paymybuddy.paymybuddy.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Entity
@IdClass(FriendId.class)
@Table(name = "friend")
public class Friend {

    @Id
    @ManyToOne
    @JoinColumn(name = "idUser_1", referencedColumnName = "idUser", nullable = false)
    private User user1;

    @Id
    @ManyToOne
    @JoinColumn(name = "idUser_2", referencedColumnName = "idUser", nullable = false)
    private User user2;
}