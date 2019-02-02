package com.slingHealth.reCentr.fragments


import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.slingHealth.reCentr.R
import com.slingHealth.reCentr.activities.MainActivity
import kotlinx.android.synthetic.main.home_fragment.view.*


@SuppressLint("ValidFragment")
class HomeFragment(context: Context) : Fragment() {
    private var parentContext: Context = context

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.home_fragment, container, false)


        view.b_bluetooth.setOnClickListener { MainActivity.showFragment(StatsFragment) }

        view.b_stats.setOnClickListener { }

        view.b_pt.setOnClickListener { }

        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()

    }


}