package com.slingHealth.reCentr.activities

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import com.slingHealth.reCentr.R
import com.slingHealth.reCentr.fragments.TrackFragment
import com.slingHealth.reCentr.fragments.TrendFragment
import kotlinx.android.synthetic.main.activity_stats.*

class StatsActivity : AppCompatActivity() {
    private val fm = this.supportFragmentManager!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)
        // init 2 page tab view
        val fragmentAdapter = MyPagerAdapter(fm, this)
        viewpager_main.adapter = fragmentAdapter

        tabs_main.setupWithViewPager(viewpager_main)


    }

    class MyPagerAdapter(fm: FragmentManager, context: Context) :
        FragmentPagerAdapter(fm) {
        private val parentContext = context

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> {
                    TrackFragment(this.parentContext)
                }
                else -> TrendFragment(this.parentContext)
            }
        }

        override fun getCount(): Int {
            return 2
        }

        override fun getPageTitle(position: Int): CharSequence {
            return when (position) {
                0 -> "Track"
                else -> {
                    return "Trends"
                }
            }
        }
    }

}