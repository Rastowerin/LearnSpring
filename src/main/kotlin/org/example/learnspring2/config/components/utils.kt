package org.example.learnspring2.config.components

import com.nimbusds.jose.shaded.gson.JsonObject
import org.example.learnspring2.users.User
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession

fun WebSocketSession.sendFriendshipRequestsNumber(number: Int) {
    val message = JsonObject()
    message.addProperty("friendship_requests_number", number)
    this.sendMessage(TextMessage(message.toString()))
}
