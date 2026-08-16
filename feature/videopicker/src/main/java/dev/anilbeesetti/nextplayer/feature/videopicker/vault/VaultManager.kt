package dev.anilbeesetti.nextplayer.feature.videopicker.vault

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent

class VaultManager(private val context: Context) {

    private val hiddenVideoUris = mutableSetOf<String>()

    fun isDeviceSecure(): Boolean {
        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
        return keyguardManager?.isDeviceSecure == true
    }

    fun createConfirmDeviceCredentialIntent(title: String, description: String): Intent? {
        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as? KeyguardManager
        return keyguardManager?.createConfirmDeviceCredentialIntent(title, description)
    }

    fun hideVideo(uri: String) {
        hiddenVideoUris.add(uri)
    }

    fun unhideVideo(uri: String) {
        hiddenVideoUris.remove(uri)
    }

    fun isHidden(uri: String): Boolean {
        return hiddenVideoUris.contains(uri)
    }

    fun getHiddenVideos(): Set<String> = hiddenVideoUris
}
