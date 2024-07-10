package org.example.learnspring2.services

import org.example.learnspring2.entities.Friendship
import org.example.learnspring2.entities.FriendshipRequest
import org.example.learnspring2.repositories.FriendshipRepository
import org.example.learnspring2.entities.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class FriendshipService {

    @Autowired
    private lateinit var friendshipRepository: FriendshipRepository

    fun createFromRequest(request: FriendshipRequest) {
        val friendship = Friendship(request.sender!!, request.receiver!!)
        friendshipRepository.save(friendship)
    }

    fun isFriends(firstUser: User, secondUser: User): Boolean {
        val hash = setOf(firstUser.id, secondUser.id).hashCode()
        return friendshipRepository.existsByFriendsHash(hash)
    }

    fun get(firstUser: User, secondUser: User): Friendship {
        val hash = setOf(firstUser.id, secondUser.id).hashCode()
        return friendshipRepository.getFriendshipByFriendsHash(hash)
    }

    fun deleteByUsers(firstUser: User, secondUser: User) {
        val friendship = this.get(firstUser, secondUser)
        friendshipRepository.delete(friendship)
    }
}