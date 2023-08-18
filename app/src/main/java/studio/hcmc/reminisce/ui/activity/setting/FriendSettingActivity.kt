package studio.hcmc.reminisce.ui.activity.setting

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import studio.hcmc.reminisce.R
import studio.hcmc.reminisce.databinding.ActivitySettingFriendBinding
import studio.hcmc.reminisce.databinding.LayoutSettingFriendItemBinding
import studio.hcmc.reminisce.vo.friend.FriendVO

class FriendSettingActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivitySettingFriendBinding
    private val friendList = ArrayList<FriendVO>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivitySettingFriendBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.settingFriendAppbar.appbarTitle.setText(R.string.setting_friend)
        viewBinding.settingFriendAppbar.appbarActionButton1.isVisible = false
        viewBinding.settingFriendAppbar.appbarBack.setOnClickListener { finish() }

        viewBinding.settingFriendAdd.setOnClickListener {
            Intent(this, AddFriendActivity::class.java).apply {
                startActivity(this)
            }
        }

    }

    private fun addFriend(value: String) {
        val item = LayoutSettingFriendItemBinding.inflate(layoutInflater)
        item.settingFriendItem.text = value
        item.settingFriendItemIcon.setOnClickListener {
            UpdateFriendNicknameDialog(this, updateFriendNicknameDelegate)

        }
        item.settingFriendItem.setOnLongClickListener {
            DeleteFriendDialog(this, deleteFriendDelegate)

            false
        }
    }

    private val deleteFriendDelegate = object : DeleteFriendDialog.Delegate {
        // onDoneClick()시 settingFriendItemsContainer
        override fun onDoneClick() {

            Toast.makeText(viewBinding.root.context, "친구가 삭제되었어요.", Toast.LENGTH_SHORT).show()

        }
    }

    private val updateFriendNicknameDelegate = object : UpdateFriendNicknameDialog.Delegate {
        override fun onSaveClick(nickname: String) {
            viewBinding.settingFriendItemsContainer.let {
                LayoutSettingFriendItemBinding.inflate(layoutInflater, viewBinding.settingFriendItemsContainer, false) }
                .settingFriendItem.text = nickname
        }
    }


}

/*
for (tag in delegate.tags) {
            viewBinding.homeTagChips.addView(LayoutInflater.from(viewBinding.root.context)
                .let { ChipTagBinding.inflate(it, viewBinding.homeTagChips, false) }
                .root
                .apply {
                    text = tag.body
                    isCheckable = false
                    isSelected = true
                    setOnClickListener { delegate.onTagClick(tag) }
                }
            )
        }
 */