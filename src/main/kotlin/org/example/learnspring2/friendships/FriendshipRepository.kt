package org.example.learnspring2.friendships

import org.example.learnspring2.users.User
import org.springframework.data.jpa.repository.JpaRepository

interface FriendshipRepository : JpaRepository<Friendship?, Long?> {

    fun existsByFriendsHash(friendsHash: Int): Boolean

    fun getFriendshipByFriendsHash(friendsHash: Int): Friendship
}
