package com.nanda.inappbilling.activity

import android.os.Bundle
import android.os.Handler
import com.nanda.inappbilling.R
import com.nanda.inappbilling.activity.HomeActivity.Companion.getCallingIntent
import com.nanda.inappbilling.base.BaseActivity

class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler().postDelayed({
            startActivity(getCallingIntent(this@SplashActivity))
            finish()
        }, 2000)

    }
}