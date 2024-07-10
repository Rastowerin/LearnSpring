package org.example.learnspring2.services

import org.example.learnspring2.config.components.sendFriendshipRequestsNumber
import org.example.learnspring2.entities.FriendshipRequest
import org.example.learnspring2.repositories.FriendshipRequestRepository
import org.example.learnspring2.entities.User
import org.example.learnspring2.sockets.WebSocketConnectionBuffer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class FriendshipRequestService {

    @Autowired
    private lateinit var friendshipRequestRepository: FriendshipRequestRepository

    @Autowired
    private lateinit var webSocketConnectionBuffer: WebSocketConnectionBuffer

    @Autowired
    private lateinit var friendshipService: FriendshipService

    fun save(friendshipRequest: FriendshipRequest) {

        if (friendshipRequest.sender == friendshipRequest.receiver) {
            throw Exception("You cant be friends with yourself")
        }

        if (friendshipRequestRepository.existsBySenderAndReceiver(
                friendshipRequest.sender!!, friendshipRequest.receiver!!)) {
            throw Exception("You already send friendship request to this user")
        }

        if (friendshipRequestRepository.existsBySenderAndReceiver(
                friendshipRequest.receiver!!, friendshipRequest.sender!!)) {
            throw Exception("This user already sent request to you")
        }

        if (friendshipService.isFriends(friendshipRequest.sender!!, friendshipRequest.receiver!!)) {
            throw Exception("You are already friends")
        }

        val session = webSocketConnectionBuffer.getSession(friendshipRequest.receiver!!.id)
        session?.sendFriendshipRequestsNumber(friendshipRequest.receiver!!.receivedFriendshipRequests.size + 1)

        friendshipRequestRepository.save(friendshipRequest)
    }

    private fun delete(request: FriendshipRequest) {

        val session = webSocketConnectionBuffer.getSession(request.receiver!!.id)
        session?.sendFriendshipRequestsNumber(request.receiver!!.receivedFriendshipRequests.size - 1)

        friendshipRequestRepository.deleteById(request.id)
    }

    fun deleteAllBySender(sender: User) {
        friendshipRequestRepository.findAllBySender(sender).map { this.delete(it) }
    }

    fun getAllByReceiver(receiver: User) : MutableList<FriendshipRequest> {
        return friendshipRequestRepository.findAllByReceiver(receiver)
    }

    fun acceptBySender(sender: User, actor: User) {
        val request = friendshipRequestRepository.findBySenderAndReceiver(sender, actor)
        if (request.receiver!!.id != actor.id) { throw Exception("That request is not to you") }
        friendshipService.createFromRequest(request)
        this.delete(request)
    }

    fun rejectBySender(sender: User, actor: User) {
        val request = friendshipRequestRepository.findBySenderAndReceiver(sender, actor)
        if (request.receiver!!.id != actor.id) { throw Exception("That request is not to you") }
        this.delete(request)
    }

    fun cancelByReceiver(receiver: User, actor: User) {
        val request = friendshipRequestRepository.findBySenderAndReceiver(actor, receiver)
        if (request.sender!!.id != actor.id) { throw Exception("That request is not yours") }
        this.delete(request)
    }

    fun isSent(sender: User, receiver: User): Boolean {
        return friendshipRequestRepository.existsBySenderAndReceiver(sender, receiver)
    }

    fun sendDateOrNull(sender: User, receiver: User): Date? {

        if (!friendshipRequestRepository.existsBySenderAndReceiver(sender, receiver)) {
            return null
        }

        val request = friendshipRequestRepository.findBySenderAndReceiver(sender, receiver)
        return request.sendDate
    }
}