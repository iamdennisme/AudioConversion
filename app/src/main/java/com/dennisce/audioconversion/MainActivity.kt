package com.dennisce.audioconversion

import android.Manifest
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.hbbclub.m4aencoder.aacenc.todoroo.aacenc.PCM2M4A
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.schedulers.Schedulers

class MainActivity : AppCompatActivity() {
    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {

        val path = "$externalCacheDir/test"

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        RxPermissions(this)
            .request(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ).subscribe { granted ->
                if (!granted) {
                    finish()
                } else {
                    PCM2M4A()
                        .covert(this@MainActivity, "$path/test.pcm")
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            Log.d("success", it)
                        }, {
                            Log.d("fail", it.message)
                        })
                }
            }


    }
}
