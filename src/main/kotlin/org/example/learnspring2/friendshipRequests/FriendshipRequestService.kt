package org.example.learnspring2.friendshipRequests

import org.example.learnspring2.config.components.WebSocketConnectionBuffer
import org.example.learnspring2.config.components.sendFriendshipRequestsNumber
import org.example.learnspring2.friendships.FriendshipService
import org.example.learnspring2.users.User
import org.example.learnspring2.users.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class FriendshipRequestService {

    @Autowired
    private val friendshipRequestRepository: FriendshipRequestRepository? = null

    @Autowired
    private val webSocketConnectionBuffer: WebSocketConnectionBuffer? = null

    @Autowired
    private val friendshipService: FriendshipService? = null

    fun save(friendshipRequest: FriendshipRequest) {

        if (friendshipRequestRepository!!.existsBySenderAndReceiver(
                friendshipRequest.sender!!, friendshipRequest.receiver!!)) {
            throw Exception("You already send friendship request to this user.")
        }

        if (friendshipService!!.isFriends(friendshipRequest.sender!!, friendshipRequest.receiver!!)) {
            throw Exception("You are already friends")
        }

        friendshipRequestRepository.save(friendshipRequest)

        val session = webSocketConnectionBuffer!!.getSession(friendshipRequest.receiver!!.id)
        val friendshipRequestsNumber = friendshipRequest.receiver!!.receivedFriendshipRequests!!.size + 1
        session?.sendFriendshipRequestsNumber(friendshipRequestsNumber)
    }

    private fun delete(request: FriendshipRequest) {

        val session = webSocketConnectionBuffer!!.getSession(request.receiver!!.id)
        val friendshipRequestsNumber = request.receiver!!.receivedFriendshipRequests!!.size - 1
        session?.sendFriendshipRequestsNumber(friendshipRequestsNumber)
        friendshipRequestRepository!!.delete(request)
    }

    fun getAllByReceiver(receiver: User) : MutableList<FriendshipRequest> {
        return friendshipRequestRepository!!.findAllByReceiver(receiver)
    }

    fun acceptBySender(sender: User, actor: User) {
        val request = friendshipRequestRepository!!.findBySenderAndReceiver(sender, actor)
        if (request.receiver!!.id != actor.id) { throw Exception("That request is not to you") }
        friendshipService!!.createFromRequest(request)
        this.delete(request)
    }

    fun rejectBySender(sender: User, actor: User) {
        val request = friendshipRequestRepository!!.findBySenderAndReceiver(sender, actor)
        if (request.receiver!!.id != actor.id) { throw Exception("That request is not to you") }
        this.delete(request)
    }

    fun cancelBySender(sender: User, actor: User) {
        val request = friendshipRequestRepository!!.findBySenderAndReceiver(sender, actor)
        if (request.receiver!!.id != actor.id) { throw Exception("That request is not yours") }
        this.delete(request)
    }

    fun isSent(sender: User, receiver: User): Boolean {
        return friendshipRequestRepository!!.existsBySenderAndReceiver(sender, receiver)
    }

    fun sendDateOrNull(sender: User, receiver: User): Date? {

        if (!friendshipRequestRepository!!.existsBySenderAndReceiver(sender, receiver)) {
            return null
        }

        val request = friendshipRequestRepository.findBySenderAndReceiver(sender, receiver)
        return request.sendDate
    }
}
