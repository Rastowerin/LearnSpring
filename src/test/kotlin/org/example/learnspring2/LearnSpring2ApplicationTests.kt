package org.example.learnspring2

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

    @Test
    fun `a`() {
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
