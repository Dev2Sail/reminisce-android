package studio.hcmc.reminisce.ui.activity.category


sealed interface EditableCategoryDetail

data class SummaryModal(
    val id: Int,
    val title: String,
    val visitedAt: String,
    val latitude: Double,
    val longitude: Double
//    val visitedCount: Int,
//    val withFriends: String?,
//    val hashtags: String?,
//    val checkFlag: Boolean
): EditableCategoryDetail

data class FriendModal(
    val userId: Int,
    val opponentId: Int,
    val nickname: String?
): EditableCategoryDetail

