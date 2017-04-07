package com.teamtreehouse.feelings2

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.android.extension.responseJson
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.runBlocking
import org.jetbrains.anko.*
import org.json.JSONArray
import org.json.JSONObject

data class Condition(val value: Int, val label: String)
class NewFeelingActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    verticalLayout {
      runBlocking(CommonPool) {
        val conditions = getPossibleConditions()
        val stringConditions = conditions.map { it.label }
        val mySpinner = spinner {
          val adapter = ArrayAdapter<String>(context, R.layout.spinner_item, stringConditions.toTypedArray())
          adapter.setDropDownViewResource(R.layout.spinner_item);
          setAdapter(adapter)
        }
        val notesEditText = editText {
          hint = "Add any notes here..."
        }
        val submitButton = button {
          onClick {
            val url = "https://treehouse-django-feelings.herokuapp.com/api/thoughts/"
            val header = mapOf(
                "Authorization" to "Bearer ${prefs.authToken}",
                "Content-Type" to "application/json")
            val id = conditions[mySpinner.selectedItemPosition].value
            val notes = notesEditText.text.toString()
            val body = "{ \"condition\":$id, \"notes\": \"$notes\" }"
            Fuel.post(url).header(header).body(body).responseJson { request, response, result ->
              e(request)
              e(response)
              e(result)
            }
            finish()
          }
        }
      }
    }
  }

  suspend fun getPossibleConditions(): MutableList<Condition>  {
    val conditions = mutableListOf<Condition>()
    val url = "https://treehouse-django-feelings.herokuapp.com/api/conditions.json"
    val header = mapOf("Authorization" to "Bearer ${prefs.authToken}")
    val (request, response, result) = Fuel.get(url).header(header).responseJson()
    result.fold({
      val array = it.array()
      for (i in 0..array.length()-1) {
        val value = (array[i] as JSONObject)["value"].toString().toInt()
        val label = (array[i] as JSONObject)["label"].toString()
        conditions.add(Condition(value, label))
      }
    },{
      e(it)
    })
    return conditions
  }
}