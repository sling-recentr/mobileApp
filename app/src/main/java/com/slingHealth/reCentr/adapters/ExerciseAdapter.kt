package com.slingHealth.reCentr.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.slingHealth.reCentr.R
import com.slingHealth.reCentr.models.Exercise


class ExerciseAdapter(context: Context, exercises: ArrayList<Exercise>) : ArrayAdapter<Exercise>(context, 0, exercises) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        // Get the data item for this position
        val exercise = getItem(position)
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.exercise_item, parent, false)
        }
        // Lookup view for data population
        val tvName = convertView!!.findViewById(R.id.tv_exercise_name) as TextView
        // Populate the data into the template view using the data object
        tvName.text = exercise!!.name
        // Return the completed view to render on screen
        return convertView
    }
}