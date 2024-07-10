package org.example.learnspring2.controllers

import com.fasterxml.jackson.annotation.JsonView
import jakarta.annotation.security.RolesAllowed
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.example.learnspring2.entities.User
import org.example.learnspring2.etc.JsonViews
import org.example.learnspring2.dto.UserDto
import org.example.learnspring2.services.FriendshipService
import org.example.learnspring2.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*


@RestController
@RolesAllowed("USER")
@RequestMapping("/users")
class UserController {

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var friendshipService: FriendshipService

    @JsonView(JsonViews.MaybeFriends::class)
    @GetMapping("")
    fun getOtherUsers(
        @RequestParam("page_number")@Min(0) pageNumber: Int = 0,
        @RequestParam("page_size")@Min(1)@Max(50) pageSize: Int = 50
    ): ResponseEntity<List<UserDto>> {

        val authentication = SecurityContextHolder.getContext().authentication
        val user = userService.findById(authentication.name.toLong())!!

        val users: MutableList<User> = userService.findAllExcludeOne(pageNumber, pageSize, user)
        return ResponseEntity(users.map { userService.toDto(it, user) }, HttpStatus.OK)
    }

    @JsonView(JsonViews.Friends::class)
    @GetMapping("friends")
    fun getMyFriends(
        @RequestParam("page_number")@Min(0) pageNumber: Int = 0,
        @RequestParam("page_size")@Min(1)@Max(50) pageSize: Int = 50
    ): ResponseEntity<Any> {

        val authentication = SecurityContextHolder.getContext().authentication
        val user = userService.findById(authentication.name.toLong())

        val friends = userService.findFriendsById(pageNumber, pageSize, authentication.name.toLong())

        return ResponseEntity(
            friends.map { userService.toDto(it, user) },
            HttpStatus.OK)
    }

    @PostMapping("friends/delete/{id}")
    fun deleteFromFriends(@PathVariable id: Long): ResponseEntity<String> {

        val authentication = SecurityContextHolder.getContext().authentication
        val user = userService.findById(authentication.name.toLong())!!
        val friend = userService.findById(id) ?: return ResponseEntity("No such user", HttpStatus.NOT_FOUND)

        friendshipService.deleteByUsers(user, friend)

        return ResponseEntity("Friend deleted", HttpStatus.OK)
    }

    @PostMapping("change_email_visibility")
    fun changeEmailVisibility(@RequestParam("to") visibility: String): ResponseEntity<String> {
        val authentication = SecurityContextHolder.getContext().authentication
        userService.changeEmailVisibilityById(authentication.name.toLong(),
            enumValueOf<User.Visibility>(visibility.uppercase()))
        return ResponseEntity("email visibility changed", HttpStatus.OK)
    }

    @JsonView(JsonViews.Self::class)
    @GetMapping("me")
    fun getMe(): ResponseEntity<UserDto> {
        val authentication = SecurityContextHolder.getContext().authentication
        val user = userService.findById(authentication.name.toLong())!!
        return ResponseEntity(userService.toDto(user), HttpStatus.OK)
    }

    @DeleteMapping("me")
    fun deleteMe(): ResponseEntity<String> {
        val authentication = SecurityContextHolder.getContext().authentication
        val user = userService.findById(authentication.name.toLong())!!

        userService.delete(user)

        return ResponseEntity("Account deleted", HttpStatus.OK)
    }

    @JsonView(JsonViews.Detail::class)
    @GetMapping("{id}")
    fun getUserById(@PathVariable id: Long): ResponseEntity<Any> {

        try {

            val user = userService.findById(id) ?: return ResponseEntity("No such user", HttpStatus.NOT_FOUND)
            val requesterId = SecurityContextHolder.getContext().authentication.name.toLong()
            val requester = userService.findById(requesterId)!!

            return ResponseEntity(userService.toDto(user, requester), HttpStatus.OK)

        } catch (e: Exception) {
            return ResponseEntity("User does not exist", HttpStatus.NOT_FOUND)
        }
    }
}
