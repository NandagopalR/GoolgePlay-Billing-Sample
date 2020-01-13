package com.nanda.inappbilling.base

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun showToast(input: String?) {
        Toast.makeText(this, input, Toast.LENGTH_SHORT).show()
    }
}