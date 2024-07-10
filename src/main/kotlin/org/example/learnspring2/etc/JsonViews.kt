package org.example.learnspring2.etc

class JsonViews {
    open class All
    open class AllExcludeSelf : All()
    open class Detail : All()
    class MaybeFriends : AllExcludeSelf()
    class Friends : AllExcludeSelf()
    class RequestReceiver : AllExcludeSelf()
    class Self : Detail()
    class Nobody
}
