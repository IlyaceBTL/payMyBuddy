package com.paymybuddy.paymybuddy.repository;

import com.paymybuddy.paymybuddy.model.Friend;
import com.paymybuddy.paymybuddy.model.FriendId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FriendRepository extends JpaRepository<Friend, FriendId> {

    boolean existsById(FriendId id);

}
