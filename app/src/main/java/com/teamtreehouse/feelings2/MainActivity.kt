package com.teamtreehouse.feelings2

import android.graphics.Color
import android.graphics.PorterDuff
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.android.extension.responseJson
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.json.JSONObject

//username: bfdeitch
//password: feelings1
class MainActivity : AppCompatActivity() {
  val clientId = "M8ulwdmDUIktZ1nfp4KZtH5KrDRfXnEb1wj7rGRv"
  val clientSecret = "HX1KOcuR5XSKXiu2W1hzzfn3GM4ATOXuiDbFNhpk8YdGJYuVt47UnLE3sJAzZvNS4Fo92mwKwd9QWvp3t60QVhvtJaCcfMNRyxWIng69W9J2Kot0b7qNdqyVLB6ahXES"
  var verticalLayout: LinearLayout? = null
  val thoughtAdapter = ThoughtAdapter()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    verticalLayout = verticalLayout {
      if (prefs.authToken == "") {
        val userNameEditText = editText {
          hint = "username"
          singleLine = true
        }
        val passwordEditText = editText {
          hint = "password"
          singleLine = true
          inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        button("Submit") {
          onClick {
            val username = userNameEditText.text.toString()
            val password = passwordEditText.text.toString()
            login(username, password)
          }
        }
      } else {
        recyclerView {
          adapter = thoughtAdapter
          layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
      }
    }
  }

  override fun onStart() {
    super.onStart()
    if (prefs.authToken.isNotEmpty()) {
      getThoughts()
    }
  }

  fun getThoughts() {
    e("getThoughts")
    val url = "https://treehouse-django-feelings.herokuapp.com/api/thoughts.json"
    val header = mapOf("Authorization" to "Bearer ${prefs.authToken}")
    Fuel.get(url).header(header).responseJson { request, response, result ->
      result.fold({
        thoughtAdapter.thoughts.clear()
        val thoughts = it.obj().getJSONArray("results")
        val numThoughts = thoughts.length()
        for (i in 0..numThoughts-1) {
          val json = thoughts[i] as JSONObject
          val condition = json["condition_display"].toString()
          val notes = json["notes"].toString()
          thoughtAdapter.thoughts.add(Thought(condition, notes))
          thoughtAdapter.notifyDataSetChanged()
        }
      }, {
        e("getThoughts: $result")
      })
    }
  }

  fun login(username: String, password: String) {
    val url = "https://treehouse-django-feelings.herokuapp.com/o/token/"
    val body = "client_id=$clientId&client_secret=$clientSecret&grant_type=password&username=$username&password=$password"
    Fuel.post(url).body(body).responseJson { request, response, result ->
      result.fold({
        val token = it.obj()["access_token"]
        prefs.authToken = token.toString()
      },{
        e("login: $result")
      })
    }
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    val menuItem = menu.add("New Feeling")
    menuItem.setIcon(R.drawable.ic_add_box_black_24dp)
    menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
    menuItem.icon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    startActivity<NewFeelingActivity>()
    return super.onOptionsItemSelected(item)
  }
}
