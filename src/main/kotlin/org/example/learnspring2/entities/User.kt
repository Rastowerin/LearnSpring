package org.example.learnspring2.entities

import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails


@Entity
@Table(name="users")
class User : UserDetails {

    enum class Visibility {
        ALL, FRIENDS, NOBODY
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0

    @Column(name = "username", nullable = false)
    private lateinit var username: String
        override fun getUsername(): String {
            return username
        }

    @Column(name = "password")
    private lateinit var password: String
        override fun getPassword(): String {
            return password
        }

    @Column(name = "email")
    lateinit var email: String

    @Enumerated(EnumType.STRING)
    @Column(name = "email_visibility")
    var emailVisibility: Visibility = Visibility.ALL

    @Column(name = "phone")
    lateinit var phone: String

    @Enumerated(EnumType.STRING)
    @Column(name = "phone_visibility")
    var phoneVisibility: Visibility = Visibility.ALL

    @Column(name = "first_name")
    lateinit var firstName: String

    @Column(name = "middle_name")
    lateinit var middleName: String

    @Column(name = "last_name")
    lateinit var lastName: String

    @ManyToMany(mappedBy = "friends", cascade = [CascadeType.ALL])
    lateinit var friendships: Set<Friendship>

    @OneToMany(mappedBy = "sender", fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    lateinit var sentFriendshipRequests : MutableSet<FriendshipRequest>

    @OneToMany(mappedBy = "receiver", fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    lateinit var receivedFriendshipRequests : MutableSet<FriendshipRequest>

    @Column(name = "is_admin")
    var isAdmin: Boolean = false

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf(SimpleGrantedAuthority(
            when (isAdmin) {
                true -> "ROLE_ADMIN"
                false -> "ROLE_USER"
            }
        ))
    }

    fun dismissFriendshipRequest(friendshipRequest: FriendshipRequest) {
        sentFriendshipRequests.remove(friendshipRequest)
        receivedFriendshipRequests.remove(friendshipRequest)
    }

    constructor()

    constructor(username: String, password: String, email: String, phone: String, firstName: String,
                middleName: String, lastName: String) {
        this.username = username
        this.password = password
        this.email = email
        this.phone = phone
        this.firstName = firstName
        this.middleName = middleName
        this.lastName = lastName
    }
}
