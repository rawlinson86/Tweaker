package com.rw.tweaks.prefs.secure

import android.content.Context
import android.util.AttributeSet
import androidx.preference.ListPreference
import androidx.preference.Preference
import com.rw.tweaks.R
import com.rw.tweaks.util.ISecurePreference
import com.rw.tweaks.util.SettingsType
import com.rw.tweaks.util.getSetting
import com.rw.tweaks.util.verifiers.BaseListPreferenceVerifier
import com.rw.tweaks.util.verifiers.BaseVisibilityVerifier
import com.rw.tweaks.util.writeSetting

class SecureListPreference(context: Context, attrs: AttributeSet) : Preference.OnPreferenceChangeListener, ListPreference(context, attrs), ISecurePreference {
    override var type = SettingsType.UNDEFINED
    override var writeKey: String? = null
        get() = field ?: key
    override var dangerous = false
    override var visibilityVerifier: BaseVisibilityVerifier? = null

    private var verifier: BaseListPreferenceVerifier? = null

    private var _onPreferenceChangeListener: OnPreferenceChangeListener? = null

    init {
        val array = context.theme.obtainStyledAttributes(attrs, R.styleable.SecureListPreference, 0, 0)

        type = SettingsType.values().find { it.value ==  array.getInt(R.styleable.SecureListPreference_settings_type, SettingsType.UNDEFINED.value)} ?: SettingsType.UNDEFINED
        writeKey = array.getString(R.styleable.SecureListPreference_differing_key)
        dangerous = array.getBoolean(R.styleable.SecureListPreference_dangerous, false)

        array.getString(R.styleable.SecureListPreference_verifier)?.let {
            verifier = context.classLoader.loadClass(it)
                .getConstructor(Context::class.java)
                .newInstance(context) as BaseListPreferenceVerifier

            verifier!!.verifyEntries(entries, entryValues).apply {
                entries = first
                entryValues = second
            }
        }

        val clazz = array.getString(R.styleable.SecureListPreference_visibility_verifier)
        if (clazz != null) {
            visibilityVerifier = context.classLoader.loadClass(clazz)
                .getConstructor(Context::class.java)
                .newInstance(context) as BaseVisibilityVerifier
        }

        visibilityVerifier?.let {
            isVisible = it.shouldShow
        }

        array.recycle()

        dialogMessage = summary

        super.setOnPreferenceChangeListener(this)
    }

    override fun onSetInitialValue(defaultValue: Any?) {
        value = context.getSetting(type, key) ?: defaultValue?.toString() ?: entryValues[0].toString()
    }

    override fun setOnPreferenceChangeListener(onPreferenceChangeListener: OnPreferenceChangeListener?) {
        _onPreferenceChangeListener = onPreferenceChangeListener
    }

    override fun getOnPreferenceChangeListener(): OnPreferenceChangeListener? {
        return _onPreferenceChangeListener
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
        val update = _onPreferenceChangeListener?.onPreferenceChange(preference, newValue) ?: true

        if (update) {
            context.writeSetting(type, writeKey, newValue.toString().toInt())
        }

        return update
    }

    override fun onValueChanged(newValue: Any?, key: String?) {}
}