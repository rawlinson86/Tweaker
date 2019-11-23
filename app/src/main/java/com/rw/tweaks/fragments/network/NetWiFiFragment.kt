package com.rw.tweaks.fragments.network

import android.os.Bundle
import com.rw.tweaks.R
import com.rw.tweaks.fragments.BasePrefFragment
import com.rw.tweaks.util.updateTitle

class NetWiFiFragment : BasePrefFragment() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs_net_wifi, rootKey)
    }

    override fun onResume() {
        super.onResume()

        updateTitle(R.string.sub_wifi)
    }
}