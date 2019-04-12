package com.slingHealth.reCentr.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.jjoe64.graphview.series.BarGraphSeries
import com.jjoe64.graphview.series.DataPoint
import com.slingHealth.reCentr.R
import com.slingHealth.reCentr.models.DayStatistic
import kotlinx.android.synthetic.main.activity_stats.*
import java.text.SimpleDateFormat
import java.util.*

class StatsActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var statReference: DatabaseReference
    private lateinit var today: DayStatistic
    private lateinit var stringDate: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

    }


    override fun onStart() {
        super.onStart()

        val date = Date()
        val newDate = Date(date.time + 604800000L * 2 + 24 * 60 * 60)
        val dt = SimpleDateFormat("yyyy-MM-dd")

        stringDate = dt.format(newDate)

        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()

        statReference = FirebaseDatabase.getInstance().reference
            .child("statistics").child(auth.currentUser!!.uid).child(stringDate)

        newStat()
    }

    // [START write_fan_out]
    private fun writeStatUpdate() {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        val key = stringDate


        val statValues = today.toMap()

        val childUpdates = HashMap<String, Any>()
        childUpdates["/statistics/${auth.currentUser!!.uid}/$key"] = statValues

        database.updateChildren(childUpdates)
        initGraph()
    }
    // [END write_fan_out]

    private fun newStat() {
        statReference.addListenerForSingleValueEvent(
            object : ValueEventListener {

                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val currentStat = dataSnapshot.getValue(DayStatistic::class.java)

                    if (currentStat == null) {
                        today = DayStatistic()
                        statReference.setValue(today)
                    } else {
                        today = currentStat
                    }
                    total.text = "Total: ${today.total}"
                    maximum.text = "Maximum: ${today.maximum}"
                    initGraph()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w("", "getUser:onCancelled", databaseError.toException())
                }

            })

    }

    private fun initGraph() {

//        val rightNow = Calendar.getInstance()
//        val currentHour = rightNow.get(Calendar.HOUR)

        //val graph = view!!.findViewById(R.id.graph) as GraphView
        val series = BarGraphSeries<DataPoint>(
            arrayOf(
                DataPoint(0.0, today.data[0]),
                DataPoint(1.0, today.data[1]),
                DataPoint(2.0, today.data[2]),
                DataPoint(3.0, today.data[3]),
                DataPoint(4.0, today.data[4]),
                DataPoint(5.0, today.data[5]),
                DataPoint(6.0, today.data[6]),
                DataPoint(7.0, today.data[7]),
                DataPoint(8.0, today.data[8]),
                DataPoint(9.0, today.data[9]),
                DataPoint(10.0, today.data[10]),
                DataPoint(11.0, today.data[11])
            )
        )
        series.spacing = 10
        series.dataWidth = 0.5
        series.isAnimated = true
        graph.addSeries(series)
        graph.gridLabelRenderer.numHorizontalLabels = 7

    }

    private fun checkData(data: Double) {
        if (data > today.maximum) {
            today.maximum = data
        }
        if (data > 10.0) {
            today.total++
            today.data[Calendar.getInstance().get(Calendar.HOUR)]++
        }
        writeStatUpdate()
    }


}