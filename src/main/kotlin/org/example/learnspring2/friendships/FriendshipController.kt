package org.example.learnspring2.friendships

import jakarta.annotation.security.RolesAllowed
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.example.learnspring2.users.User
import org.example.learnspring2.users.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*


@RestController
@RolesAllowed("USER")
@RequestMapping("/friends")
class FriendshipController {

    @Autowired
    var userService: UserService? = null

    @Autowired
    var friendshipRequestService: FriendshipService? = null

    @GetMapping("")
    fun myFriends(
        @RequestParam("page_number")@Min(0) pageNumber: Int = 0,
        @RequestParam("page_size")@Min(1)@Max(50) pageSize: Int = 50
    ): ResponseEntity<List<User>?> {

        val friends: MutableList<User> = userService!!.findAll(pageNumber, pageSize)
        return ResponseEntity(friends, HttpStatus.OK)
    }
}
