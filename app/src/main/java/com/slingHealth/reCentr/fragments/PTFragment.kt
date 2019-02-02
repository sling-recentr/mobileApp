package com.slingHealth.reCentr.fragments


import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.slingHealth.reCentr.R
import com.slingHealth.reCentr.adapters.ExerciseAdapter
import com.slingHealth.reCentr.models.Exercise


@SuppressLint("ValidFragment")
class PTFragment(context: Context) : Fragment() {
    private var parentContext: Context = context
    private var exerciseList: ArrayList<Exercise> = ArrayList()
    private val adapter = ExerciseAdapter(this.parentContext, exerciseList)
    private lateinit var listView: ListView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.home_fragment, container, false)


        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()

    }


}