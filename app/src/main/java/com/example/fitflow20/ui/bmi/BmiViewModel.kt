package com.example.fitflow20.ui.bmi

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BmiViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Bmi Fragment"
    }
    val text: LiveData<String> = _text
}