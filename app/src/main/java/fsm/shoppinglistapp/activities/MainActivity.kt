package fsm.shoppinglistapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import fsm.shoppinglistapp.R
import fsm.shoppinglistapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setBottomNavListener()
    }

    private fun setBottomNavListener() {
        binding.bNav.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.settings -> {
                    Log.d("MyLog", "1")
                }
                R.id.notes -> {
                    Log.d("MyLog", "2")
                }
                R.id.shop_list -> {
                    Log.d("MyLog", "3")
                }
                R.id.new_item -> {
                    Log.d("MyLog", "4")
                }
            }
            true
        }
    }
}