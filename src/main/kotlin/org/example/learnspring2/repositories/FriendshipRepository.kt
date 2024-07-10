package org.example.learnspring2.repositories

import org.example.learnspring2.entities.Friendship
import org.springframework.data.jpa.repository.JpaRepository

interface FriendshipRepository : JpaRepository<Friendship?, Long?> {

    fun existsByFriendsHash(friendsHash: Int): Boolean

    fun getFriendshipByFriendsHash(friendsHash: Int): Friendship
}
