package org.example.learnspring2.users

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.jwt.JwtClaimsSet
import org.springframework.security.oauth2.jwt.JwtEncoder
import org.springframework.security.oauth2.jwt.JwtEncoderParameters
import java.time.Instant

fun valueOrHidden(value: String?, visibility: User.Visibility?, isFriends: Boolean, isSelf: Boolean): String {

    if (value == null) { return "hidden" }

    return when(visibility) {
            User.Visibility.ALL -> value
            User.Visibility.FRIENDS -> when (isFriends) {
                true -> value
                false -> "hidden"
            }
            User.Visibility.NOBODY -> when(isSelf) {
                true -> value
                false -> "hidden"
            }
            null -> value
        }
}
