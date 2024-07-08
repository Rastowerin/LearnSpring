package org.example.learnspring2.friendships

import org.example.learnspring2.config.components.WebSocketConnectionBuffer
import org.example.learnspring2.config.components.sendFriendshipRequestsNumber
import org.example.learnspring2.friendshipRequests.FriendshipRequest
import org.example.learnspring2.users.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class FriendshipService {

    @Autowired
    private var friendshipRepository: FriendshipRepository? = null

    fun createFromRequest(request: FriendshipRequest) {
        val friendship = Friendship(request.sender!!, request.receiver!!)
        friendshipRepository!!.save(friendship)
    }

    fun isFriends(firstUser: User, secondUser: User): Boolean {
        val hash = setOf(firstUser.id, secondUser.id).hashCode()
        return friendshipRepository!!.existsByFriendsHash(hash)
    }

    fun get(firstUser: User, secondUser: User): Friendship {
        val hash = setOf(firstUser.id, secondUser.id).hashCode()
        return friendshipRepository!!.getFriendshipByFriendsHash(hash)
    }
}
