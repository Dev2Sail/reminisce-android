package studio.hcmc.reminisce.ui.activity.category


sealed interface EditableCategoryDetail

data class SummaryModal(
    val id: Int,
    val title: String,
    val visitedAt: String,
    val latitude: Double,
    val longitude: Double
): EditableCategoryDetail

data class FriendModal(
    val userId: Int,
    val opponentId: Int,
    val nickname: String?
): EditableCategoryDetail

