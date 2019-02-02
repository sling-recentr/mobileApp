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
class LoginFragment(context: Context) : Fragment() {
    private var parentContext: Context = context

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.home_fragment, container, false)


        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()

    }


}