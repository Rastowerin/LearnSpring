package org.example.learnspring2.entities

import jakarta.persistence.*
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.util.Date

@Entity
@Table(name="friendship_requests")
class FriendshipRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0

    @ManyToOne
    @OnDelete(action = OnDeleteAction.SET_DEFAULT)
    @JoinColumn(name = "sender_id", referencedColumnName = "id")
    var sender : User? = null

    @ManyToOne(cascade = [CascadeType.PERSIST])
    @JoinColumn(name = "receiver_id", referencedColumnName = "id")
    var receiver : User? = null

    @Column(name = "send_date")
    var sendDate: Date = Date(System.currentTimeMillis())

    @PreRemove
    fun dismissUsers() {

        sender!!.dismissFriendshipRequest(this)
        receiver!!.dismissFriendshipRequest(this)
        sender = null
        receiver = null
    }

    constructor()

    constructor(sender: User, receiver: User) {
        this.sender = sender
        this.receiver = receiver
    }
}