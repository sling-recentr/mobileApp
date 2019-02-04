package com.slingHealth.reCentr.activities

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.slingHealth.reCentr.R
import com.slingHealth.reCentr.fragments.HomeFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val fm = this.supportFragmentManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        showFragment(HomeFragment(this))

    }
     fun showFragment(fragment: Fragment) {

        fm.beginTransaction()
            .addToBackStack(null)
            .replace(R.id.frag_placeholder, fragment)
            .commit()

    }
}
