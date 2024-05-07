package com.mozhimen.gamek.emulator.test.feature.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mozhimen.gamek.emulator.test.R

/**
 * @ClassName MainActivity
 * @Description TODO
 * @Author Mozhimen & Kolin Zhao
 * @Date 2024/5/7
 * @Version 1.0
 */
class MainActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeActivity()
    }
}