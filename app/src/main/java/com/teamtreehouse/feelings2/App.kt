package com.teamtreehouse.feelings2

import android.app.Application

val prefs: Prefs by lazy { App.appPrefs!! }

class App : Application() {
  companion object {
    var appPrefs: Prefs? = null
  }
  override fun onCreate() {
    appPrefs = Prefs(applicationContext)
    super.onCreate()
  }
}