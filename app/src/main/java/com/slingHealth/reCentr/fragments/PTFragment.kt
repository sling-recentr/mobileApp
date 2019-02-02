package com.slingHealth.reCentr.fragments


import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import com.slingHealth.reCentr.R
import com.slingHealth.reCentr.adapters.ExerciseAdapter
import com.slingHealth.reCentr.models.Exercise
import kotlinx.android.synthetic.main.pt_fragment.view.*


@SuppressLint("ValidFragment")
class PTFragment(context: Context) : Fragment() {
    private var parentContext: Context = context
    private var exerciseList: ArrayList<Exercise> = ArrayList()
    private val adapter = ExerciseAdapter(this.parentContext, exerciseList)
    private lateinit var listView: ListView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.pt_fragment, container, false)

        view.fab.setOnClickListener { displayDialog(R.layout.dialog_exercise) }

        this.listView = view.findViewById<ListView>(R.id.lv_items)
        listView.adapter = adapter

        return view
    }

    private fun displayDialog(layout: Int) {
        val dialog = Dialog(this.parentContext)
        dialog.setContentView(layout)

        val exertv: EditText = dialog.findViewById(R.id.et_exercise_name)

        val window = dialog.window
        window?.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)



        dialog.findViewById<Button>(R.id.b_done).setOnClickListener {
            when {
                exertv.text.toString() == "" -> Toast.makeText(
                    this.parentContext,
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