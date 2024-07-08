package org.example.learnspring2.users

import org.example.learnspring2.friendshipRequests.FriendshipRequestService
import org.example.learnspring2.friendships.FriendshipService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class UserService {

    @Autowired
    private val userRepository: UserRepository? = null

    @Autowired
    private val friendshipService: FriendshipService? = null

    @Autowired
    private val friendshipRequestService: FriendshipRequestService? = null

    @Autowired
    private val jwtEncoder: JwtEncoder? = null

    @Autowired
    private var passwordEncoder: PasswordEncoder? = null

    fun findAll(pageNumber: Int, pageSize: Int): MutableList<User> {
        return userRepository!!.findAll(PageRequest.of(pageNumber, pageSize)).map { it!! }.toMutableList()
    }

    fun findAllExcludeOne(pageNumber: Int, pageSize: Int, excluded: User): MutableList<User> {
        val users = userRepository!!.findAll(PageRequest.of(pageNumber, pageSize)).map{ it!! }.toMutableList()
        users.remove(excluded)
        return users
    }

    fun findById(id: Long) : User {
        return userRepository!!.findById(id).get()
    }

    fun findByUsername(username: String) : User {
        return userRepository!!.findByUsername(username)
    }

    fun findFriendsByUsername(username: String): List<User> {
        val user = userRepository!!.findByUsername(username)
        return user.friendships!!.map {
            when (user.id) {
                it.friends!!.elementAt(0).id -> it.friends!!.elementAt(1)
                it.friends!!.elementAt(1).id -> it.friends!!.elementAt(0)
                else -> throw Exception("User does not belong to this friendship")
            }
        }
    }

    fun findFriendshipRequestSenders(user: User): List<User> {
        return friendshipRequestService!!.getAllByReceiver(user).map { it.sender!! }
    }

    fun changeEmailVisibilityByUsername(username: String, visibility: User.Visibility) {
        val user = userRepository!!.findByUsername(username)
        user.emailVisibility = visibility
        userRepository.save(user)
    }

    fun getAccessToken(user: User): String {
        val now = Instant.now()
        val expiry = 36000L

         val scope: List<String> = user.authorities.stream()
        .map{obj: GrantedAuthority -> obj.authority}
        .toList()
         val claims = JwtClaimsSet.builder()
        .issuer("self")
        .issuedAt(now)
        .expiresAt(now.plusSeconds(expiry))
        .subject(user.username)
        .claim("authorities", scope)
        .build()

        val token = jwtEncoder!!.encode(JwtEncoderParameters.from(claims)).tokenValue

        return token
    }

    fun getAccessTokenByDto(userDto: UserDto): String {
        val user = this.findByDto(userDto)
        return this.getAccessToken(user)
    }

    fun getAccessTokenByLogin(login: LoginDto): String {
        val user = userRepository!!.findByUsername(login.username)
        if (!passwordEncoder!!.matches(login.password, user.password)) { throw Exception("Invalid password") }
        return this.getAccessToken(user)
    }

    fun findByDto(userDto: UserDto): User {
        return userRepository!!.findByUsername(userDto.username)
    }

    fun createFromDto(userDto: UserDto) {

        val encodedPassword = passwordEncoder!!.encode(userDto.password)
        val user = User(
            username=userDto.username,
            password=encodedPassword,
            firstName=userDto.firstName,
            middleName=userDto.middleName,
            lastName=userDto.lastName,
            email=userDto.email,
            phone=userDto.phone
        )

        if (userRepository!!.existsUserByUsername(user.username!!)) {
           throw Exception("User with that username already exists.")
        }

        if (userRepository.existsUserByEmail(user.email!!)) {
            throw Exception("User with that email already exists.")
        }

        userRepository.save(user)
    }

    fun toDto(user: User): UserDto {

        return UserDto(
            id=user.id,
            username=user.username!!,
            password=user.password!!,
            firstName=user.firstName!!,
            middleName=user.middleName!!,
            lastName=user.lastName!!,
            email=user.email!!,
            phone=user.phone!!,
            emailVisibility=user.emailVisibility,
            phoneVisibility=user.phoneVisibility,
            friendsNumber=user.friendships!!.size
        )
    }

    fun toDtoByRequester(user: User, requester: User): UserDto {

        val isFriends = friendshipService!!.isFriends(user, requester)
        val isSelf = user.id == requester.id

        val friendship = when (isFriends) {
            true -> friendshipService.get(user, requester)
            false -> null
        }

        return UserDto(
            id=user.id,
            username=user.username!!,
            password=user.password!!,
            firstName=user.firstName!!,
            middleName=user.middleName!!,
            lastName=user.lastName!!,
            email=valueOrHidden(user.email, user.emailVisibility, isFriends, isSelf),
            emailVisibility=user.emailVisibility,
            phone=valueOrHidden(user.phone, user.phoneVisibility, isFriends, isSelf),
            phoneVisibility=user.phoneVisibility,
            isFriend=isFriends,
            friendshipRequestSent=friendshipRequestService!!.isSent(requester, user),
            friendshipRequestSendDate=friendshipRequestService.sendDateOrNull(user, requester),
            addedToFriends=friendship?.created,
            friendsNumber=user.friendships!!.size
        )
    }
}
