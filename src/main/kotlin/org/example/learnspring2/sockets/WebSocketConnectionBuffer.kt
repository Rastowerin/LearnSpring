package org.example.learnspring2.sockets

import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketSession

@Component
class WebSocketConnectionBuffer {

    private val map: MutableMap<Long,WebSocketSession> = mutableMapOf()

    fun getSession(id: Long): WebSocketSession? {
        return map[id]
    }

    fun addSession(id: Long, session: WebSocketSession) {
        map[id] = session
    }

    fun deleteSession(id: Long) {
        map.remove(id)
    }
}