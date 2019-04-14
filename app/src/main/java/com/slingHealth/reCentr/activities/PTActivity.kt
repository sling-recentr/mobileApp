package com.slingHealth.reCentr.activities

import android.app.ActionBar
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.slingHealth.reCentr.R
import com.slingHealth.reCentr.models.Exercise
import kotlinx.android.synthetic.main.activity_pt.*
import kotlinx.android.synthetic.main.exercise_item.view.*

class PTActivity : AppCompatActivity() {

    private lateinit var adapter: ExerciseAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var exerciseReference: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pt)
        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()



        exerciseReference = database
            .child("exercises").child(auth.currentUser!!.uid)

        adapter = ExerciseAdapter(this, exerciseReference)
        pt_recycler_view.layoutManager = LinearLayoutManager(this)
        fab.setOnClickListener { displayDialog(R.layout.dialog_exercise) }
    }

    override fun onStart() {
        super.onStart()

        pt_recycler_view.adapter = adapter
    }

    private fun displayDialog(layout: Int) {
        val dialog = Dialog(this)
        dialog.setContentView(layout)

        val textViewName: EditText = dialog.findViewById(R.id.et_exercise_name)
        val textViewInfo: EditText = dialog.findViewById(R.id.et_exercise_info)

        val window = dialog.window
        window?.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)



        dialog.findViewById<Button>(R.id.b_done).setOnClickListener {
            when {
                textViewName.text.toString() == "" -> Toast.makeText(
                    this,
                    "Please enter exercise name",
                    Toast.LENGTH_SHORT
                ).show()

                else -> {
                    val name = textViewName.text.toString()
                    val info = textViewInfo.text.toString()
                    val exercise = Exercise(name, info, false)
                    exerciseReference.push().setValue(exercise)

                    dialog.dismiss()
                }
            }
        }
        dialog.show()
    }

    private class ExerciseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(exercise: Exercise) {

            itemView.tv_exercise_name.text = exercise.name
            itemView.tv_exercise_info.text = exercise.info



        }
    }

    private class ExerciseAdapter(
        private val context: Context,
        databaseReference: DatabaseReference
    ) : RecyclerView.Adapter<ExerciseViewHolder>() {

        private val childEventListener: ChildEventListener?

        private val exerciseIds = ArrayList<String>()
        private val exercises = ArrayList<Exercise>()

        init {

            // Create child event listener
            // [START child_event_listener_recycler]
            val childEventListener = object : ChildEventListener {
                override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                    Log.d("", "onChildAdded:" + dataSnapshot.key!!)

                    // A new comment has been added, add it to the displayed list
                    val exercise = dataSnapshot.getValue(Exercise::class.java)

                    // [START_EXCLUDE]
                    // Update RecyclerView
                    exerciseIds.add(dataSnapshot.key!!)
                    exercises.add(exercise!!)
                    notifyItemInserted(exercises.size - 1)
                    // [END_EXCLUDE]
                }

                override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                    Log.d("", "onChildChanged: ${dataSnapshot.key}")


                    val newExercise = dataSnapshot.getValue(Exercise::class.java)
                    val exerciseKey = dataSnapshot.key

                    // [START_EXCLUDE]
                    val exerciseIndex = exerciseIds.indexOf(exerciseKey)
                    if (exerciseIndex > -1 && newExercise != null) {
                        // Replace with the new data
                        exercises[exerciseIndex] = newExercise

                        // Update the RecyclerView
                        notifyItemChanged(exerciseIndex)
                    } else {
                        Log.w("", "onChildChanged:unknown_child: $exerciseKey")
                    }
                    // [END_EXCLUDE]
                }

                override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                    Log.d("", "onChildRemoved:" + dataSnapshot.key!!)

                    val exerciseKey = dataSnapshot.key

                    // [START_EXCLUDE]
                    val exerciseIndex = exerciseIds.indexOf(exerciseKey)
                    if (exerciseIndex > -1) {
                        // Remove data from the list
                        exerciseIds.removeAt(exerciseIndex)
                        exercises.removeAt(exerciseIndex)

                        // Update the RecyclerView
                        notifyItemRemoved(exerciseIndex)
                    } else {
                        Log.w("", "onChildRemoved:unknown_child:" + exerciseKey!!)
                    }
                    // [END_EXCLUDE]
                }

                override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                    Log.d("", "onChildMoved:" + dataSnapshot.key!!)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w("", "postComments:onCancelled", databaseError.toException())
                    Toast.makeText(
                        context, "Failed to load comments.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            databaseReference.addChildEventListener(childEventListener)
            // [END child_event_listener_recycler]

            // Store reference to listener so it can be removed on app stop
            this.childEventListener = childEventListener
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
            val inflater = LayoutInflater.from(context)
            val view = inflater.inflate(R.layout.exercise_item, parent, false)
            return ExerciseViewHolder(view)
        }

        override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
            holder.bind(exercises[position])

        }

        override fun getItemCount(): Int = exercises.size
        }

    }
