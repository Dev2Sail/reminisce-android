package studio.hcmc.reminisce.ui.activity.setting

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivityFriendAddBinding
import studio.hcmc.reminisce.databinding.LayoutAddFriendItemBinding
import studio.hcmc.reminisce.databinding.LayoutNotFoundBinding
import studio.hcmc.reminisce.dto.friend.FriendDTO
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.FriendIO
import studio.hcmc.reminisce.io.ktor_client.UserIO
import studio.hcmc.reminisce.util.string
import studio.hcmc.reminisce.util.text

class AddFriendActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityFriendAddBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityFriendAddBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.addFriendAppbar.appbarTitle.text = getText(R.string.add_friend_title)
        viewBinding.addFriendAppbar.appbarActionButton1.isVisible = false
        viewBinding.addFriendAppbar.appbarBack.setOnClickListener { finish() }
        viewBinding.addFriendSearch.endIconDrawable = getDrawable(R.drawable.round_search_24)
        viewBinding.addFriendSearch.setEndIconOnClickListener { searchUser() }

        CoroutineScope(Dispatchers.IO).launch { prepareUser() }
    }
    private suspend fun prepareUser() = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@AddFriendActivity)
        val builder = StringBuilder()
        builder.append("내 이메일 : ")
        builder.append(user.email)
        viewBinding.addFriendSearch.helperText = builder.toString()
    }

    private fun searchUser() = CoroutineScope(Dispatchers.Main).launch {
        runCatching { UserIO.getByEmail(viewBinding.addFriendSearch.string) }
            .onSuccess {
                onResult(it.id, it.nickname, it.email)
            }
            .onFailure {
                notFoundUser()
            }
    }

    private fun onResult(opponentId: Int, nickname: String, email: String) {
        // 검색 결과는 1 or 0
        viewBinding.addFriendItems.removeAllViews()
        val cardView = LayoutAddFriendItemBinding.inflate(layoutInflater)
        cardView.addFriendItemEmail.text = email
        cardView.addFriendItemNickname.text = nickname

        cardView.root.setOnClickListener {
            AddFriendDialog(this, opponentId, nickname, addFriendDialogDelegate)
        }

        viewBinding.addFriendItems.addView(cardView.root)
    }

    private fun notFoundUser() {
        viewBinding.addFriendItems.removeAllViews()
        val textView = LayoutNotFoundBinding.inflate(layoutInflater)
        viewBinding.addFriendItems.addView(textView.root)
    }

    private val addFriendDialogDelegate = object : AddFriendDialog.Delegate {
        override fun onAddClick(opponentId: Int) {
            onAddReady(opponentId)
        }
    }

    private fun onAddReady(friendId: Int) = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@AddFriendActivity)
        val opponentInfo = FriendDTO.Post().apply {
            opponentId = friendId
            nickname = null
        }
        runCatching { FriendIO.post(user.id, opponentInfo) }
            .onSuccess {
                onAddSuccess()
                viewBinding.addFriendSearch.text.clear()
            }
            .onFailure {
                onAddFailure()
                it.cause
                it.message
                it.stackTrace
            }
    }

    private fun onAddSuccess() = CoroutineScope(Dispatchers.Main).launch {
        Toast.makeText(this@AddFriendActivity, "친구로 등록되었어요", Toast.LENGTH_SHORT).show()
    }

    private fun onAddFailure() = CoroutineScope(Dispatchers.Main).launch {
        Toast.makeText(this@AddFriendActivity, "친구 등록에 실패했어요. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show()
    }
}
