package org.example.learnspring2.users

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonView
import jakarta.annotation.security.RolesAllowed
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.example.learnspring2.friendships.FriendshipRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.json.MappingJacksonValue
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import kotlin.math.min


@RestController
@RolesAllowed("USER")
@RequestMapping("/users")
class UserController {

    @Autowired
    var userService: UserService? = null

    @JsonView(JsonViews.MaybeFriends::class)
    @GetMapping("")
    fun getOtherUsers(
        @RequestParam("page_number")@Min(0) pageNumber: Int = 0,
        @RequestParam("page_size")@Min(1)@Max(50) pageSize: Int = 50
    ): ResponseEntity<List<UserDto>> {

        val authentication = SecurityContextHolder.getContext().authentication
        val user = userService!!.findByUsername(authentication.name)

        val users: MutableList<User> = userService!!.findAllExcludeOne(pageNumber, pageSize, user)
        return ResponseEntity(users.map { userService!!.toDtoByRequester(it, user) }, HttpStatus.OK)
    }

    @JsonView(JsonViews.Friends::class)
    @GetMapping("/friends")
    fun getMyFriends(
        @RequestParam("page_number")@Min(0) pageNumber: Int = 0,
        @RequestParam("page_size")@Min(1)@Max(50) pageSize: Int = 50
    ): ResponseEntity<Any> {

        val authentication = SecurityContextHolder.getContext().authentication
        val user = userService!!.findByUsername(authentication.name)

        val friends = userService!!.findFriendsByUsername(authentication.name)
        val paginatedFriends = friends.slice(
            min(pageNumber * pageSize, friends.size - 1)..
                min((pageNumber + 1) * pageSize - 1, friends.size - 1))

        return ResponseEntity(
            paginatedFriends.map { userService!!.toDtoByRequester(it, user) },
            HttpStatus.OK)
    }

    @PostMapping("/change_email_visibility")
    fun changeEmailVisibility(@RequestParam("to") visibility: String): ResponseEntity<String> {
        val authentication = SecurityContextHolder.getContext().authentication
        userService!!.changeEmailVisibilityByUsername(authentication.name,
            enumValueOf<User.Visibility>(visibility.uppercase()))
        return ResponseEntity("email visibility changed", HttpStatus.OK)
    }

    @JsonView(JsonViews.Self::class)
    @GetMapping("/me")
    fun getMe(): ResponseEntity<UserDto> {
        val authentication = SecurityContextHolder.getContext().authentication
        val user = userService!!.findByUsername(authentication.name)
        return ResponseEntity(userService!!.toDto(user), HttpStatus.OK)
    }

    @DeleteMapping("/me")
    fun deleteMe(): ResponseEntity<UserDto> {
        TODO()
    }

    @JsonView(JsonViews.Detail::class)
    @GetMapping("{id}")
    fun getUserById(@PathVariable id: Long): ResponseEntity<Any> {

        try {

            val user = userService!!.findById(id)
            val requesterName = SecurityContextHolder.getContext().authentication.name
            val requester = userService!!.findByUsername(requesterName)

            return ResponseEntity(userService!!.toDtoByRequester(user, requester), HttpStatus.OK)

        } catch (e: Exception) {
            return ResponseEntity("User does not exist", HttpStatus.NOT_FOUND)
        }
    }
}
