package org.example.learnspring2.services

import org.example.learnspring2.entities.User
import org.example.learnspring2.repositories.UserRepository
import org.example.learnspring2.dto.UserDto
import org.example.learnspring2.etc.unwrap
import org.example.learnspring2.etc.valueOrHidden
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import java.util.*
import kotlin.math.min

@Service
class UserService {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var friendshipService: FriendshipService

    @Autowired
    private lateinit var friendshipRequestService: FriendshipRequestService

    fun existsById(id: Long): Boolean {
        return userRepository.existsById(id)
    }

    fun existsByUsername(username: String): Boolean {
        return userRepository.existsUserByUsername(username)
    }

    fun existsByEmail(email: String): Boolean {
        return userRepository.existsUserByEmail(email)
    }

    fun findAll(pageNumber: Int, pageSize: Int): MutableList<User> {
        return userRepository.findAll(PageRequest.of(pageNumber, pageSize)).map { it!! }.toMutableList()
    }

    fun findAllExcludeOne(pageNumber: Int, pageSize: Int, excluded: User): MutableList<User> {
        val users = userRepository.findAll(PageRequest.of(pageNumber, pageSize)).map{ it!! }.toMutableList()
        users.remove(excluded)
        return users
    }

    fun findById(id: Long) : User? {
        return userRepository.findById(id).unwrap()
    }

    fun findByUsername(username: String) : User {
        return userRepository.findByUsername(username)
    }

    fun findFriendsById(pageNumber: Int, pageSize: Int, id: Long): List<User> {
        val user = userRepository.findById(id).get()

        if (user.friendships.size <= pageNumber * pageSize) {
            return listOf()
        }

        return user.friendships
            .toList()
            .sortedBy { -it.id }
            .slice(
                pageNumber * pageSize..
                min((pageNumber + 1) * pageSize - 1, user.friendships.size - 1))
            .map {
                when (user.id) {
                    it.friends!!.elementAt(0).id -> it.friends!!.elementAt(1)
                    it.friends!!.elementAt(1).id -> it.friends!!.elementAt(0)
                    else -> throw Exception("User does not belong to this friendship")
                }
        }
    }

    fun findFriendshipRequestSenders(user: User): List<User> {
        return friendshipRequestService.getAllByReceiver(user).map { it.sender!! }
    }

    fun changeEmailVisibilityById(id: Long, visibility: User.Visibility) {
        val user = userRepository.findById(id).get()
        user.emailVisibility = visibility
        userRepository.save(user)
    }

    fun save(user: User) {
        userRepository.save(user)
    }

    fun delete(user: User) {
        friendshipRequestService.deleteAllBySender(user)
        userRepository.deleteById(user.id)
    }

    fun findByDto(userDto: UserDto): User {
        return userRepository.findByUsername(userDto.username)
    }

    fun toDto(user: User, requester: User? = null): UserDto {

        val isFriends = if (requester != null) friendshipService.isFriends(user, requester) else null
        val isSelf = if (requester != null) user.id == requester.id else null

        val friendship = when (isFriends) {
            true -> friendshipService.get(user, requester!!)
            else -> null
        }

        return UserDto(
            id=user.id,
            username=user.username,
            password=user.password,
            firstName=user.firstName,
            middleName=user.middleName,
            lastName=user.lastName,
            email= valueOrHidden(user.email, user.emailVisibility, isFriends, isSelf),
            emailVisibility=user.emailVisibility,
            phone= valueOrHidden(user.phone, user.phoneVisibility, isFriends, isSelf),
            phoneVisibility=user.phoneVisibility,
            isFriend=isFriends,
            friendshipRequestSent=if (requester != null)
                friendshipRequestService.isSent(requester, user) else null,
            friendshipRequestSendDate=if (requester != null)
                friendshipRequestService.sendDateOrNull(user, requester) else null,
            addedToFriends=friendship?.created,
            friendsNumber=user.friendships.size
        )
    }
}
