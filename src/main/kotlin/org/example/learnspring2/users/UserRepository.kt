package org.example.learnspring2.users

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface UserRepository : JpaRepository<User?, Long?> {

    fun findByUsername(username: String): User

    fun findByUsernameAndPassword(username: String, password: String): User

    fun existsUserByUsername(username: String): Boolean

    fun existsUserByEmail(email: String): Boolean
}
