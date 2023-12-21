package com.example.fitflow20.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fitflow20.api.Workout

class HomeViewModel : ViewModel() {

    private val _refreshEvent = MutableLiveData<Unit>()
    val refreshEvent: LiveData<Unit> get() = _refreshEvent

    fun triggerRefresh() {
        _refreshEvent.value = Unit
    }
    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }

    val text: LiveData<String> = _text

    // MutableLiveData to hold the list of workouts
    private val _workoutHomeList = MutableLiveData<List<Workout>>()

    // Expose an immutable LiveData for external observers
    val workoutHomeList: LiveData<List<Workout>> get() = _workoutHomeList

    // Function to update the list of workouts
    fun updateWorkouts(workouts: List<Workout>) {
        _workoutHomeList.value = workouts
    }
}
