package fsm.shoppinglistapp.activities
import android.content.Intent
import android.content.SharedPreferences

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.preference.PreferenceManager
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import fsm.shoppinglistapp.R
import fsm.shoppinglistapp.billing.BillingManager
import fsm.shoppinglistapp.databinding.ActivityMainBinding
import fsm.shoppinglistapp.fragments.FragmentManager
import fsm.shoppinglistapp.fragments.NoteFragment
import fsm.shoppinglistapp.fragments.ShopListNamesFragment
import fsm.shoppinglistapp.settings.SettingsActivity
import fsm.shoppinglistapp.settings.SettingsFragment

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private var currentMenuItemId = R.id.shop_list
    private lateinit var defPref: SharedPreferences
    private var currentTheme = ""
    private var iAd: InterstitialAd? = null
    private lateinit var pref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        defPref = PreferenceManager.getDefaultSharedPreferences(this)
        currentTheme = defPref.getString("theme_key", "green").toString()
        setTheme(getSelectedTheme())
        super.onCreate(savedInstanceState)
        pref = getSharedPreferences(BillingManager.MAIN_PREF, MODE_PRIVATE)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FragmentManager.setFragment(ShopListNamesFragment.newInstance(), this)
        setBottomNavListener()
        if (!pref.getBoolean(BillingManager.REMOVE_ADS_KEY, false)) loadInterAd()
    }

    private fun loadInterAd(){
        val request = AdRequest.Builder().build()
        InterstitialAd.load(this, getString(R.string.inter_ad_id),
            request, object : InterstitialAdLoadCallback(){
                override fun onAdLoaded(ad: InterstitialAd) {
                    iAd = ad
                }

                override fun onAdFailedToLoad(ad: LoadAdError) {
                    iAd = null
                }
            })
    }

    private fun showInterAd(adListener: AdListener){
        if (iAd != null) {
            iAd?.fullScreenContentCallback = object : FullScreenContentCallback(){
                override fun onAdDismissedFullScreenContent() {
                    iAd = null
                    loadInterAd()
                    adListener.onFinish()
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    iAd = null
                    loadInterAd()
                }

                override fun onAdShowedFullScreenContent() {
                    iAd = null
                    loadInterAd()
                }
            }
            iAd?.show(this)
    } else {
            adListener.onFinish()
        }
        }

    private fun setBottomNavListener() {
        binding.bNav.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.settings -> {
                    showInterAd(object : AdListener{
                        override fun onFinish() {
                            startActivity(
                                Intent(this@MainActivity,SettingsActivity::class.java))
                        }
                    })
                }
                R.id.notes -> {
                    currentMenuItemId = R.id.notes
                    FragmentManager.setFragment(NoteFragment.newInstance(), this)
                }
                R.id.shop_list -> {
                    currentMenuItemId = R.id.shop_list
                    FragmentManager.setFragment(ShopListNamesFragment.newInstance(), this)
                }
                R.id.new_item -> {
                    FragmentManager.currentFrag?.onClickNew()
                }
            }
            true
        }
    }

    override fun onResume() {
        super.onResume()
        binding.bNav.selectedItemId = currentMenuItemId
        if (defPref.getString("theme_key", "green") != currentTheme) recreate()
    }

    private fun getSelectedTheme(): Int {
        return if (defPref.getString("theme_key", "green") == "green")
             R.style.Theme_ShoppingListAppGreen
         else
             R.style.Theme_ShoppingListAppBlue
    }

    interface AdListener{
        fun onFinish()
    }
}