package org.example.learnspring2.friendshipRequests

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import jakarta.persistence.*
import org.example.learnspring2.users.User
import org.example.learnspring2.users.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import java.util.Date

@Entity
//@JsonSerialize(using = FriendshipRequestSerializer::class)
@Table(name="friendship_requests")
class FriendshipRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0

    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "sender_id", referencedColumnName = "id")
    var sender : User? = null

    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "receiver_id", referencedColumnName = "id")
    var receiver : User? = null

    @Column(name = "send_date")
    var sendDate: Date = Date(System.currentTimeMillis())

    constructor()

    constructor(sender: User, receiver: User) {
        this.sender = sender
        this.receiver = receiver
    }
}
