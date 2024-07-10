package org.example.learnspring2.services

import org.example.learnspring2.sockets.WebSocketConnectionBuffer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.stereotype.Service
import org.springframework.web.socket.WebSocketSession

@Service
class WebSocketService {

    @Autowired
    private lateinit var jwtDecoder: JwtDecoder

    @Autowired
    private lateinit var webSocketConnectionBuffer: WebSocketConnectionBuffer

    fun getUserIdBySession(session: WebSocketSession): Long {

        val token = session.uri!!.path.split("/").last()
        val jwt = jwtDecoder.decode(token)

        return jwt.subject.toLong()
    }

    fun tokenValid(session: WebSocketSession): Boolean {
        try {
            val token = session.uri!!.path.split("/").last()
            val jwt = jwtDecoder.decode(token)
            return true
        } catch (e: Exception) {
            return false
        }
    }

    fun addSession(session: WebSocketSession) {

        val token = session.uri!!.path.split("/").last()
        val jwt = jwtDecoder.decode(token)

        webSocketConnectionBuffer.addSession(jwt.subject.toLong(), session)
    }

    fun deleteSession(session: WebSocketSession) {

        val token = session.uri!!.path.split("/").last()
        val jwt = jwtDecoder.decode(token)

        webSocketConnectionBuffer.deleteSession(jwt.subject.toLong())
    }
}