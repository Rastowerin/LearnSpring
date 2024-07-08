package org.example.learnspring2.users

import com.fasterxml.jackson.annotation.JsonFilter
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonView
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter
import com.fasterxml.jackson.databind.ser.FilterProvider
import com.fasterxml.jackson.databind.ser.PropertyFilter
import com.fasterxml.jackson.databind.ser.PropertyWriter
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider
import jakarta.persistence.*
import org.example.learnspring2.friendshipRequests.FriendshipRequest
import org.example.learnspring2.friendships.Friendship
import org.example.learnspring2.friendships.FriendshipService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*


@Entity
@Table(name="users")
class User : UserDetails {

    enum class Visibility {
        ALL, FRIENDS, NOBODY
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0

    @Column(name = "username", unique = true)
    private var username: String? = null
        override fun getUsername(): String? {
            return username
        }

    @Column(name = "password")
    private var password: String? = null
        override fun getPassword(): String? {
            return password
        }

    @Column(name = "email")
    var email: String? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "email_visibility")
    var emailVisibility: Visibility = Visibility.ALL

    @Column(name = "phone")
    var phone: String? = null

    @Enumerated(EnumType.STRING)
    @Column(name = "phone_visibility")
    var phoneVisibility: Visibility = Visibility.ALL

    @Column(name = "first_name")
    var firstName: String? = null

    @Column(name = "middle_name")
    var middleName: String? = null

    @Column(name = "last_name")
    var lastName: String? = null

    @JsonIgnore
    @ManyToMany(mappedBy = "friends")
    var friendships: Set<Friendship>? = null

    @JsonIgnore
    @OneToMany(mappedBy = "sender", fetch = FetchType.EAGER)
    var sentFriendshipRequests : List<FriendshipRequest>? = null

    @JsonIgnore
    @OneToMany(mappedBy = "receiver", fetch = FetchType.EAGER)
    var receivedFriendshipRequests : List<FriendshipRequest>? = null

    @JsonIgnore
    @Column(name = "is_admin")
    var isAdmin: Boolean = false

    @JsonIgnore
    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return mutableListOf(SimpleGrantedAuthority(
            when (isAdmin) {
                true -> "ROLE_ADMIN"
                false -> "ROLE_USER"
            }
        ))
    }

    constructor()

    constructor(username: String?, password: String?, email: String?, phone: String?, firstName: String?,
                middleName: String?, lastName: String?) {
        this.username = username
        this.password = password
        this.email = email
        this.phone = phone
        this.firstName = firstName
        this.middleName = middleName
        this.lastName = lastName
    }
}
