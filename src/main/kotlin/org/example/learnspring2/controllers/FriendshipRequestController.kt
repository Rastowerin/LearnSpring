package org.example.learnspring2.controllers

import com.fasterxml.jackson.annotation.JsonView
import jakarta.annotation.security.RolesAllowed
import org.example.learnspring2.entities.FriendshipRequest
import org.example.learnspring2.services.FriendshipRequestService
import org.example.learnspring2.etc.JsonViews
import org.example.learnspring2.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*


@RestController
@RolesAllowed("USER")
@RequestMapping("/friendship")
class FriendshipRequestController {

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var friendshipRequestService: FriendshipRequestService

    @PostMapping("/{id}")
    fun sendFriendshipRequest(@PathVariable id: Long): ResponseEntity<String> {

        val authentication = SecurityContextHolder.getContext().authentication

        val sender = userService.findById(authentication.name.toLong())!!
        val receiver = userService.findById(id) ?: return ResponseEntity("No such user", HttpStatus.NOT_FOUND)
        val friendshipRequest = FriendshipRequest(sender, receiver)

        try {
            friendshipRequestService.save(friendshipRequest)
        } catch (e: Exception) {
            return ResponseEntity(e.message, HttpStatus.BAD_REQUEST)
        }

        return ResponseEntity("Request sent", HttpStatus.CREATED)
    }

    @JsonView(JsonViews.RequestReceiver::class)
    @GetMapping("/for_me")
    fun getForMe(): ResponseEntity<Any> {

        val authentication = SecurityContextHolder.getContext().authentication
        val user = userService.findById(authentication.name.toLong())!!

        val requestSenders = userService.findFriendshipRequestSenders(user)
            .map{ userService.toDto(it, user) }

        return ResponseEntity(requestSenders, HttpStatus.OK)
    }

    @PostMapping("/accept/{senderId}")
    fun accept(@PathVariable senderId: Long) : ResponseEntity<String> {

        val id = SecurityContextHolder.getContext().authentication.name.toLong()
        val user = userService.findById(id)!!
        val sender = userService.findById(senderId) ?: return ResponseEntity("No such user", HttpStatus.NOT_FOUND)
        
        try {
            friendshipRequestService.acceptBySender(sender, user)
        } catch (e: Exception) {
            return ResponseEntity("Request does not exist", HttpStatus.NOT_FOUND)
        }

        return ResponseEntity("Request accepted", HttpStatus.OK)
    }

    @PostMapping("/reject/{senderId}")
    fun reject(@PathVariable senderId: Long) : ResponseEntity<String> {

        val id = SecurityContextHolder.getContext().authentication.name.toLong()
        val user = userService.findById(id)!!
        val sender = userService.findById(senderId) ?: return ResponseEntity("No such user", HttpStatus.NOT_FOUND)

        try {
            friendshipRequestService.rejectBySender(sender, user)
        } catch (e: Exception) {
            return ResponseEntity("Request does not exist", HttpStatus.NOT_FOUND)
        }

        return ResponseEntity("Request rejected", HttpStatus.OK)
    }

    @PostMapping("/cancel/{receiverId}")
    fun cancel(@PathVariable receiverId: Long) : ResponseEntity<String> {

        val id = SecurityContextHolder.getContext().authentication.name.toLong()
        val user = userService.findById(id)!!
        val receiver = userService.findById(receiverId) ?: return ResponseEntity("No such user", HttpStatus.NOT_FOUND)

        friendshipRequestService.cancelByReceiver(receiver, user)


        return ResponseEntity("Request canceled", HttpStatus.OK)
    }
}