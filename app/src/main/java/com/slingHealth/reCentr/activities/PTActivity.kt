package com.slingHealth.reCentr.activities

import android.app.ActionBar
import android.app.Dialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import com.slingHealth.reCentr.R
import com.slingHealth.reCentr.adapters.ExerciseAdapter
import com.slingHealth.reCentr.models.Exercise
import kotlinx.android.synthetic.main.activity_pt.*

class PTActivity : AppCompatActivity() {

    private var exerciseList: ArrayList<Exercise> = ArrayList()
    private lateinit var adapter: ExerciseAdapter
    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pt)

        fab.setOnClickListener { displayDialog(R.layout.dialog_exercise) }

        adapter = ExerciseAdapter(this, exerciseList)

        this.listView = findViewById(R.id.lv_items)
        listView.adapter = adapter
    }

    private fun displayDialog(layout: Int) {
        val dialog = Dialog(this)
        dialog.setContentView(layout)

        val exertv: EditText = dialog.findViewById(R.id.et_exercise_name)

        val window = dialog.window
        window?.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)



        dialog.findViewById<Button>(R.id.b_done).setOnClickListener {
            when {
                exertv.text.toString() == "" -> Toast.makeText(
                    this,
                    "Please enter exercise name",
                    Toast.LENGTH_SHORT
                ).show()

                else -> {
                    val name: String = exertv.text.toString()


                    val newEntry = Exercise(name)
                    adapter.add(newEntry)

                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }


}