package sovonthy.com.kotlinmessenger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*

class NewMessageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)

        supportActionBar?.title = "Select User"

        val adapter = GroupAdapter<ViewHolder>()
        adapter.add(UserItem())
        adapter.add(UserItem())
        adapter.add(UserItem())

        recyclerview_new_message.adapter = adapter

    }
}

class UserItem: Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {

    }

    override fun getLayout(): Int {
       return R.layout.user_row_message
    }
}