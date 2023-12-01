package studio.hcmc.reminisce.ui.activity.setting

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivityFriendAddBinding
import studio.hcmc.reminisce.databinding.LayoutAddFriendItemBinding
import studio.hcmc.reminisce.databinding.LayoutNotFoundBinding
import studio.hcmc.reminisce.dto.friend.FriendDTO.Post
import studio.hcmc.reminisce.ext.user.UserExtension
import studio.hcmc.reminisce.io.ktor_client.FriendIO
import studio.hcmc.reminisce.io.ktor_client.SpringException
import studio.hcmc.reminisce.io.ktor_client.UserIO
import studio.hcmc.reminisce.util.LocalLogger
import studio.hcmc.reminisce.util.setActivity
import studio.hcmc.reminisce.util.string

class AddFriendActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityFriendAddBinding
    private val context = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityFriendAddBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initView()
    }

    private fun initView() {
        viewBinding.addFriendAppbar.appbarTitle.text = getText(R.string.add_friend_title)
        viewBinding.addFriendAppbar.appbarActionButton1.isVisible = false
        viewBinding.addFriendAppbar.appbarBack.setOnClickListener { finish() }
        viewBinding.addFriendSearch.endIconDrawable = getDrawable(R.drawable.round_search_24)
        viewBinding.addFriendSearch.setEndIconOnClickListener {
            validateSearch(viewBinding.addFriendSearch.string)
        }
        CoroutineScope(Dispatchers.Main).launch { prepareUser() }
    }

    private suspend fun prepareUser() {
        val user = UserExtension.getUser(this)
        viewBinding.addFriendSearch.helperText = getString(R.string.add_friend_helper_text, user.email)
    }


    private fun validateSearch(opponentEmail: String) = CoroutineScope(Dispatchers.IO).launch {
        runCatching { UserIO.getByEmail(opponentEmail) }
            .onSuccess { searchResult(it.id, it.nickname, it.email) }
            .onFailure {
                withContext(Dispatchers.Main) { notFoundUser() }
                LocalLogger.e(it)
            }
    }

    private suspend fun searchResult(opponentId: Int, nickname: String, email: String) {
        withContext(Dispatchers.Main) {
            viewBinding.addFriendItems.removeAllViews()
            val cardView = LayoutAddFriendItemBinding.inflate(layoutInflater)
            cardView.addFriendItemEmail.text = email
            cardView.addFriendItemNickname.text = nickname
            cardView.root.setOnClickListener {
                AddFriendDialog(context, opponentId, nickname, addFriendDialogDelegate)
            }
            viewBinding.addFriendItems.addView(cardView.root)
        }
    }

    private fun notFoundUser() {
        viewBinding.addFriendItems.removeAllViews()
        val textView = LayoutNotFoundBinding.inflate(layoutInflater)
        viewBinding.addFriendItems.addView(textView.root)
    }

    private val addFriendDialogDelegate = object : AddFriendDialog.Delegate {
        override fun onAddClick(opponentId: Int) { preparePost(opponentId) }
    }

    private fun preparePost(opponentId: Int) {
        val dto = Post().apply {
            this.opponentId = opponentId
        }
        CoroutineScope(Dispatchers.IO).launch { validatePost(dto) }
    }

    private suspend fun validatePost(dto: Post) = coroutineScope {
        val user = UserExtension.getUser(context)
        try {
            val friend = FriendIO.post(user.id, dto)
            toFriends(friend.opponentId)
        } catch (e: SpringException) {
            LocalLogger.e(e)
            e.status?.let { handleStatusCode(it) }
        }
    }

    private suspend fun handleStatusCode(status: Int) {
        when(status) {
            HttpStatusCode.Conflict.value -> failedWithConflict()
            HttpStatusCode.BadRequest.value -> failedWithBadRequest()
            else -> onAddFailure()
        }
    }

    private suspend fun onAddFailure() {
        withContext(Dispatchers.Main) { Toast.makeText(context, getString(R.string.add_friend_error), Toast.LENGTH_SHORT).show() }
    }

    private suspend fun failedWithBadRequest() {
        withContext(Dispatchers.Main) { Toast.makeText(context, getString(R.string.add_friend_bad_request), Toast.LENGTH_SHORT).show() }
    }

    private suspend fun failedWithConflict() {
        withContext(Dispatchers.Main) { Toast.makeText(context, getString(R.string.add_friend_conflict), Toast.LENGTH_SHORT).show() }
    }

    private fun toFriends(opponentId: Int) {
        Intent()
            .putExtra("isAdded", true)
            .putExtra("opponentId", opponentId)
            .setActivity(this, Activity.RESULT_OK)
        finish()
    }
}
