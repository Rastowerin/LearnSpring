//package org.example.learnspring2.config.components
//
//import org.example.learnspring2.users.UserRepository
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.security.core.userdetails.UserDetails
//import org.springframework.security.core.userdetails.UserDetailsService
//import org.springframework.security.core.userdetails.UsernameNotFoundException
//import org.springframework.stereotype.Service
//
//@Service
//class InMemoryUserDetailsService() : UserDetailsService {
//
//    @Autowired
//    var userRepository: UserRepository? = null
//
//    @Throws(UsernameNotFoundException::class)
//    override fun loadUserByUsername(username: String): UserDetails? {
//        return userRepository!!.findByUsername(username)
//    }
//}
