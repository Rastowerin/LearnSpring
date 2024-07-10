package org.example.learnspring2.config

import org.example.learnspring2.sockets.WebSocketConnection
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry

@EnableWebSocket
@Configuration
class WebsocketConfig : WebSocketConfigurer {

    @get:Bean
    val webSocketConnection: WebSocketConnection
        get() = WebSocketConnection()

    override fun registerWebSocketHandlers(webSocketHandlerRegistry: WebSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(webSocketConnection,
            "/friendship_requests_number/{token}")
    }
}