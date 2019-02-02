package com.slingHealth.reCentr.fragments


import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.slingHealth.reCentr.R



@SuppressLint("ValidFragment")
class StatsFragment(context: Context) : Fragment() {
    private var parentContext: Context = context

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.stats_fragment, container, false)


        return view
    }

    override fun onStart() {
        super.onStart()

    }


}