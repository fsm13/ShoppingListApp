package fsm.shoppinglistapp.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import fsm.shoppinglistapp.R

class SettingsFragment: PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings_preference, rootKey)
    }
}