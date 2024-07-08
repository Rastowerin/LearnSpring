package org.example.learnspring2.friendshipRequests

import org.example.learnspring2.users.User
import org.springframework.data.jpa.repository.JpaRepository

interface FriendshipRequestRepository : JpaRepository<FriendshipRequest?, Long?> {

    fun existsBySenderAndReceiver(sender: User, receiver: User) : Boolean

    fun findBySenderAndReceiver(sender: User, receiver: User) : FriendshipRequest

    fun findAllByReceiver(receiver: User): MutableList<FriendshipRequest>

    fun deleteBySenderAndReceiver(sender: User, receiver: User)
}
