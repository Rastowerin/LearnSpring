package org.example.learnspring2

import org.example.learnspring2.services.FriendshipRequestService
import org.example.learnspring2.services.FriendshipService
import org.example.learnspring2.services.UserService
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(
  locations = ["classpath:application-integrationtest.properties"])
class KotlinApplicationTests(@Autowired private val mockMvc: MockMvc) {

    @Autowired
    val userService: UserService? = null

    @Autowired
    val friendshipService: FriendshipService? = null

    @Autowired
    val friendshipRequestService: FriendshipRequestService? = null

    @Test
    fun a() {
        this.mockMvc.perform(post("/start/signup")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "username": "a",
                  "password": "1"
                }      
                """)
        ).andExpect(status().isCreated())
    }
}
