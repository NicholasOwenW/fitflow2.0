package com.example.fitflow20.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {

    val currentOngoing : MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }
    val currentDone : MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }
    val currentTime : MutableLiveData<Long> by lazy {
        MutableLiveData<Long>()
    }
}