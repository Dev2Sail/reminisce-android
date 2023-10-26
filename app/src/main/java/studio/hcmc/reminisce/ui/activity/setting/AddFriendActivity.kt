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
import studio.hcmc.reminisce.io.ktor_client.SpringException
import studio.hcmc.reminisce.io.ktor_client.UserIO
import studio.hcmc.reminisce.ui.view.CommonError
import studio.hcmc.reminisce.util.LocalLogger
import studio.hcmc.reminisce.util.string
import studio.hcmc.reminisce.util.text

class AddFriendActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityFriendAddBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityFriendAddBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        initView()
    }

    private fun initView() {
        viewBinding.addFriendAppbar.apply {
            appbarTitle.text = getText(R.string.add_friend_title)
            appbarActionButton1.isVisible = false
            appbarBack.setOnClickListener { finish() }
        }
        viewBinding.addFriendSearch.apply {
            endIconDrawable = getDrawable(R.drawable.round_search_24)
            setEndIconOnClickListener { searchUser() }
        }

        CoroutineScope(Dispatchers.IO).launch { prepareUser() }
    }

    private fun prepareUser() = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@AddFriendActivity)
        val builder = StringBuilder().apply {
            append("내 이메일 : ")
            append(user.email)
        }
        viewBinding.addFriendSearch.helperText = builder.toString()
    }

    private fun searchUser() = CoroutineScope(Dispatchers.Main).launch {
        val email = viewBinding.addFriendSearch.string
        runCatching { UserIO.getByEmail(email) }
            .onSuccess { onResult(it.id, it.nickname, it.email) }
            .onFailure {
                notFoundUser()
                LocalLogger.e(it)
            }
    }

    private fun onResult(opponentId: Int, nickname: String, email: String) {
        // 검색 결과는 1 or 0
        viewBinding.addFriendItems.removeAllViews()
        val cardView = LayoutAddFriendItemBinding.inflate(layoutInflater).apply {
            addFriendItemEmail.text = email
            addFriendItemNickname.text = nickname
        }

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
        }
        runCatching { FriendIO.post(user.id, opponentInfo) }
            .onSuccess {
                // TODO Intent Friends ACtivity move
                viewBinding.addFriendSearch.text.clear()
            }
            .onFailure {
                onAddFailure()
//                onFriendDuplicated()
                LocalLogger.e(it)
            }
    }

    // TODO 친구 등록 실패
    // TODO 사용자 본인 이메일로 친구 신청한 경우
    // TODO 사용자가 검색한 이메일의 사용자가 탈퇴한 경우
    // TODO 이미 친구로 등록된 경우

    private fun onAddFailure() = CoroutineScope(Dispatchers.Main).launch {
        Toast.makeText(this@AddFriendActivity, "친구 등록에 실패했어요. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show()
    }

    private fun onNotFoundUser(body: SpringException) {
        // 해당 이메일로 등록된 사용자가 없는 경우 addview
        if (body.error == "Not Found") {

        }
    }
    private fun onFriendDuplicated(body: SpringException) {
        // 이미 친구로 등록된 사용자일 경우
        if (body.error == "Duplicate") {

            CommonError.onMessageDialog(this, "친구 등록 실패", "이미 등록된 친구입니다")
        }
    }

    private fun onBadRequest(body: SpringException) {
        // 사용자 본인 이메일로 요청 날린 경우
        if (body.error == "Bad Request") {

        }
    }
}
