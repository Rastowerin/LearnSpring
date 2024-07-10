package org.example.learnspring2.repositories

import org.example.learnspring2.entities.FriendshipRequest
import org.example.learnspring2.entities.User
import org.springframework.data.jpa.repository.JpaRepository

interface FriendshipRequestRepository : JpaRepository<FriendshipRequest?, Long?> {

    fun existsBySenderAndReceiver(sender: User, receiver: User) : Boolean

    fun findBySenderAndReceiver(sender: User, receiver: User) : FriendshipRequest

    fun findAllByReceiver(receiver: User): MutableList<FriendshipRequest>

    fun findAllBySender(sender: User): MutableList<FriendshipRequest>
}