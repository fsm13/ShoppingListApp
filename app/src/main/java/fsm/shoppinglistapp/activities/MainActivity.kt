package fsm.shoppinglistapp.activities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import fsm.shoppinglistapp.R
import fsm.shoppinglistapp.databinding.ActivityMainBinding
import fsm.shoppinglistapp.fragments.FragmentManager
import fsm.shoppinglistapp.fragments.NoteFragment
import fsm.shoppinglistapp.fragments.ShopListNamesFragment

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FragmentManager.setFragment(ShopListNamesFragment.newInstance(), this)
        setBottomNavListener()
    }

    private fun setBottomNavListener() {
        binding.bNav.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.settings -> {
                    Log.d("MyLog", "1")
                }
                R.id.notes -> {
                    FragmentManager.setFragment(NoteFragment.newInstance(), this)
                }
                R.id.shop_list -> {
                    FragmentManager.setFragment(ShopListNamesFragment.newInstance(), this)
                }
                R.id.new_item -> {
                    FragmentManager.currentFrag?.onClickNew()
                }
            }
            true
        }
    }
}