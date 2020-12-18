package com.example.pantryappver1

import android.app.Application
import com.parse.Parse

class ParseApplication:Application(){
    override fun onCreate() {
        super.onCreate()
        Parse.initialize(
            Parse.Configuration.Builder(this)
            .applicationId("DAcuY2KcJU0YR0OWtI4uMgLqmfP92LLxGME0a3vo")
            .clientKey("Lpak3c9y0Gu9Gcw1b5dMObABUUNajYYkVMINC17P")
            .server("https://parseapi.back4app.com")
            .build()
        );
    }

}