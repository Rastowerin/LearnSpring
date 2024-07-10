package org.example.learnspring2.etc

import org.example.learnspring2.entities.User
import java.util.*

fun valueOrHidden(value: String?, visibility: User.Visibility?, isFriends: Boolean?, isSelf: Boolean?): String {

    if (value == null || isFriends == null || isSelf == null) { return "hidden" }

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

fun <T> Optional<T>.unwrap(): T? = orElse(null)