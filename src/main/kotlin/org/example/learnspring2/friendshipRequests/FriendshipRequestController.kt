package org.example.learnspring2.friendshipRequests

import com.fasterxml.jackson.annotation.JsonView
import jakarta.annotation.security.RolesAllowed
import org.example.learnspring2.users.JsonViews
import org.example.learnspring2.users.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*


@RestController
@RolesAllowed("USER")
@RequestMapping("/friendship")
class FriendshipRequestController {

    @Autowired
    var userService: UserService? = null

    @Autowired
    var friendshipRequestService: FriendshipRequestService? = null

    @PostMapping("/{id}")
    fun sendFriendshipRequest(@PathVariable id: Long): ResponseEntity<String> {

        val authentication = SecurityContextHolder.getContext().authentication

        val sender = userService!!.findByUsername(authentication.name)
        val receiver = userService!!.findById(id)
        val friendshipRequest = FriendshipRequest(sender, receiver)

        try {
            friendshipRequestService!!.save(friendshipRequest)
        } catch (e: Exception) {
            return ResponseEntity(e.message, HttpStatus.BAD_REQUEST)
        }

        return ResponseEntity("Request sent", HttpStatus.CREATED)
    }

    @JsonView(JsonViews.RequestReceiver::class)
    @GetMapping("/for_me")
    fun getForMe(): ResponseEntity<Any> {

        val authentication = SecurityContextHolder.getContext().authentication
        val user = userService!!.findByUsername(authentication.name)

        val requestSenders = userService!!.findFriendshipRequestSenders(user)
            .map{ userService!!.toDtoByRequester(it, user) }

        return ResponseEntity(requestSenders, HttpStatus.OK)
    }

    @PostMapping("/accept/{senderId}")
    fun accept(@PathVariable senderId: Long) : ResponseEntity<String> {

        val username = SecurityContextHolder.getContext().authentication.name
        val user = userService!!.findByUsername(username)
        val sender = userService!!.findById(senderId)
        
        try {
            friendshipRequestService!!.acceptBySender(sender, user)
        } catch (e: Exception) {
            return ResponseEntity("Request does not exist", HttpStatus.NOT_FOUND)
        }

        return ResponseEntity("Request accepted", HttpStatus.OK)
    }

    @PostMapping("/reject/{senderId}")
    fun reject(@PathVariable senderId: Long) : ResponseEntity<String> {

        val username = SecurityContextHolder.getContext().authentication.name
        val user = userService!!.findByUsername(username)
        val sender = userService!!.findById(senderId)

        try {
            friendshipRequestService!!.rejectBySender(sender, user)
        } catch (e: Exception) {
            return ResponseEntity("Request does not exist", HttpStatus.NOT_FOUND)
        }

        return ResponseEntity("Request rejected", HttpStatus.OK)
    }

    @PostMapping("/cancel/{senderId}")
    fun cancel(@PathVariable senderId: Long) : ResponseEntity<String> {

        val username = SecurityContextHolder.getContext().authentication.name
        val user = userService!!.findByUsername(username)
        val sender = userService!!.findById(senderId)

        try {
            friendshipRequestService!!.cancelBySender(sender, user)
        } catch (e: Exception) {
            return ResponseEntity("Request does not exist", HttpStatus.NOT_FOUND)
        }

        return ResponseEntity("Request canceled", HttpStatus.OK)
    }
}
