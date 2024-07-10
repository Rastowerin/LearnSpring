package org.example.learnspring2.repositories

import org.example.learnspring2.entities.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User?, Long?> {

    fun findByUsername(username: String): User

    fun existsUserByUsername(username: String): Boolean

    fun existsUserByEmail(email: String): Boolean
}
