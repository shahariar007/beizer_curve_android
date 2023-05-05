package com.mortuza.drawproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity(),CustomTouchListener {
    var mapView:CustomMapView?=null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mapView = findViewById(R.id.mapView)
        mapView?.setZoomListener(this)

    }

    override fun onZoomLevel(level: Float, action: String, yValue: Float) {
     // do whatever you want.
    }
}