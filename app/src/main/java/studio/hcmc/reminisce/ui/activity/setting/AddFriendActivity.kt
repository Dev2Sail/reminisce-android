package studio.hcmc.reminisce.ui.activity.setting

import android.content.Intent
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

//        CoroutineScope(Dispatchers.IO).launch { prepareUser() }
        prepareUser()
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

        cardView.root.setOnClickListener { AddFriendDialog(this, opponentId, nickname, addFriendDialogDelegate) }
        viewBinding.addFriendItems.addView(cardView.root)
    }

    private fun notFoundUser() {
        viewBinding.addFriendItems.removeAllViews()
        val textView = LayoutNotFoundBinding.inflate(layoutInflater)
        viewBinding.addFriendItems.addView(textView.root)
    }

    private val addFriendDialogDelegate = object : AddFriendDialog.Delegate {
        override fun onAddClick(opponentId: Int) {
            onAddContent(opponentId)
        }
    }

    private fun onAddContent(opponentId: Int) = CoroutineScope(Dispatchers.IO).launch {
        val user = UserExtension.getUser(this@AddFriendActivity)
        val dto = FriendDTO.Post().apply {
            this.opponentId = opponentId
        }

        val result = runCatching {
            FriendIO.post(user.id, dto)
        }
            .onSuccess {
                // TODO Intent Friends ACtivity move
                viewBinding.addFriendSearch.text.clear()
            }
            .onFailure {

                onAddFailure()
//                onFriendDuplicated()
//                LocalLogger.e(it)
//                onError(it.cause)
                LocalLogger.e(it)



//                onError(HttpResponse)
            }
        if (result.isSuccess) {
            Intent(this@AddFriendActivity, FriendsActivity::class.java).apply {
                startActivity(this)
                finish()
            }
        } else {
//            LocalLogger.v("${result.exceptionOrNull(SpringException::)}")
        }
    }


    private fun onError(body: SpringException) {
        // TODO response status로 구분 지어서 오류 멘트 수정

        when (body.error) {
            "Bad Request" -> {LocalLogger.v("self request")}
            "Duplicate" -> { LocalLogger.v("friend duplicate")}

        }
        when (body.status) {
            400 -> {
                /* self request */
                LocalLogger.v("status 400 : bad request")
            }
            409 -> {
                /* duplicate */
                LocalLogger.v("status 409 : duplicate")
            }
        }
    }


    private fun onAddFailure() = CoroutineScope(Dispatchers.Main).launch {
        Toast.makeText(this@AddFriendActivity, "친구 등록에 실패했어요. 다시 시도해 주세요.", Toast.LENGTH_SHORT).show()
    }

}
