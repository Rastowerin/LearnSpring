package org.example.learnspring2.entities

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "friendships")
class Friendship {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "friends",
               joinColumns = [JoinColumn(name = "friendship_id", referencedColumnName = "id")],
               inverseJoinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")])
    var friends : Set<User>? = null

    @Column(name = "friends_hash")
    var friendsHash: Int? = null

    @Column(name = "created")
    var created: Date? = null

    constructor()

    constructor(firstUser: User, secondUser: User) {
        this.friends = setOf(firstUser, secondUser)
        this.friendsHash = setOf(firstUser.id, secondUser.id).hashCode()
        this.created = Date(System.currentTimeMillis())
    }
}
