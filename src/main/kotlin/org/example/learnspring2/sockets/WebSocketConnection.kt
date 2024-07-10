package org.example.learnspring2.sockets

import org.example.learnspring2.config.components.sendFriendshipRequestsNumber
import org.example.learnspring2.services.WebSocketService
import org.example.learnspring2.services.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler

@Component
class WebSocketConnection : TextWebSocketHandler() {

    @Autowired
    private lateinit var webSocketService: WebSocketService

    @Autowired
    private lateinit var userService: UserService

    override fun afterConnectionEstablished(session: WebSocketSession) {

        if (!webSocketService.tokenValid(session)) {
            session.close()
            return
        }

        super.afterConnectionEstablished(session)

        webSocketService.addSession(session)

        val id = webSocketService.getUserIdBySession(session)
        val user = userService.findById(id)!!
        session.sendFriendshipRequestsNumber(user.receivedFriendshipRequests.size)
    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        super.afterConnectionClosed(session, status)

        try {
            webSocketService.deleteSession(session)
        } catch (e: Exception) {
            return
        }
    }
}