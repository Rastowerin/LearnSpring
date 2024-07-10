package org.example.learnspring2.dto
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonView
import jakarta.validation.constraints.*
import org.example.learnspring2.entities.User
import org.example.learnspring2.etc.JsonViews
import java.util.*

@JsonView(JsonViews.All::class)
data class UserDto(

    @JsonView(JsonViews.AllExcludeSelf::class)
    val id: Long,

    @field:NotBlank(message = "username cant be blank")
    val username: String,

    @JsonView(JsonViews.Nobody::class)
    @field:NotBlank(message = "password cant be blank")
    val password: String,

    @JsonProperty("first_name")
    @field:NotBlank(message = "first_name cant be blank")
    val firstName: String,

    @JsonProperty("middle_name")
    @field:NotBlank(message = "middle_name cant be blank")
    val middleName: String,

    @JsonProperty("last_name")
    @field:NotBlank(message = "last_name cant be blank")
    val lastName: String,

    @field:Pattern(regexp = "^[a-zA-Z0-9_!#\$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+\$", message = "Incorrect email")
    @field:NotBlank(message = "email cant be blank")
    val email: String,

    @field:Pattern(regexp = "^\\+\\d{11}\$", message = "Incorrect phone number")
    @field:NotBlank(message = "phone cant be blank")
    val phone: String,

    @JsonView(JsonViews.Self::class)
    val emailVisibility: User.Visibility = User.Visibility.ALL,

    @JsonView(JsonViews.Self::class)
    val phoneVisibility: User.Visibility = User.Visibility.ALL,

    @JsonProperty("friends_number")
    @JsonView(JsonViews.Detail::class)
    val friendsNumber: Int? = null,

    @JsonProperty("is_friend")
    @JsonView(JsonViews.MaybeFriends::class)
    val isFriend: Boolean? = null,

    @JsonProperty("friendship_request_sent")
    @JsonView(JsonViews.MaybeFriends::class)
    val friendshipRequestSent: Boolean? = null,

    @JsonProperty("friendship_request_send_date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonView(JsonViews.RequestReceiver::class)
    val friendshipRequestSendDate: Date? = null,

    @JsonProperty("added_to_friends")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonView(JsonViews.Friends::class)
    val addedToFriends: Date? = null
)
