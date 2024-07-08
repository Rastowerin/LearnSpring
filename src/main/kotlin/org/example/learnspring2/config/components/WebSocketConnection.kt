package org.example.learnspring2.config.components

import com.nimbusds.jose.shaded.gson.JsonObject
import org.example.learnspring2.users.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

@Component
class WebSocketConnection : TextWebSocketHandler() {

    @Autowired
    val userService: UserService? = null

    @Autowired
    val webSocketConnectionBuffer: WebSocketConnectionBuffer? = null

    override fun afterConnectionEstablished(session: WebSocketSession) {
        super.afterConnectionEstablished(session)

        val id = session.uri!!.path.toString().split("/").last().toLong()

        webSocketConnectionBuffer!!.addSession(id, session)

        val user = userService!!.findById(id)
        session.sendFriendshipRequestsNumber(user.receivedFriendshipRequests!!.size)
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        super.afterConnectionClosed(session, status)

        val id = session.uri!!.path.toString().split("/").last().toLong()
        webSocketConnectionBuffer!!.deleteSession(id)
    }
}
