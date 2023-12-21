package com.example.fitflow20.ui.home

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitflow20.R
import com.example.fitflow20.adapter.HomeWorkoutAdapter
import com.example.fitflow20.adapter.WorkoutListAdapter
import com.example.fitflow20.api.Workout
import com.example.fitflow20.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.GenericTypeIndicator
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private lateinit var ref: DatabaseReference
    private lateinit var homeRecyclerView: RecyclerView
    private lateinit var workoutHomeList: MutableList<Workout>
    private val homeAdapter: HomeWorkoutAdapter by lazy { HomeWorkoutAdapter(workoutHomeList) }

    private lateinit var handler: Handler
    private var isOngoing: Boolean = false
    private var timeRun: Long = 0L
    private var isDone: Boolean = false

    private val updateCounterRunnable = object : Runnable {
        override fun run() {
            if (isOngoing) {
                timeRun += 1000 // Update every 1 second
                binding.timeCounter.text = formatTime(timeRun)
                handler.postDelayed(this, 1000)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val Red = resources.getColor(R.color.red)
        val White = resources.getColor(R.color.white)
        val Green = resources.getColor(R.color.main_green)

        val textViewDate: TextView = binding.currentDate
        val currentDate = getCurrentDate()
        textViewDate.text = "$currentDate"

        val textViewDay: TextView = binding.currentDay
        val currentDay = getCurrentDay()
        textViewDay.text = "$currentDay"

        // PLAY BUTTON TIME WOWOOWOW
        val playBtn: ImageView = binding.playButton
        playBtn.setOnClickListener {
            if (!isOngoing && !isDone) {
                val message = "Are you sure you want to start now?"
                showCustomDialogBoxConfirm(message) {
                    workoutActive()
                }

            } else if (isOngoing) {
                val message = "Are you done with your workout?"
                showCustomDialogBoxConfirm(message) {
                    workoutActive()
                }

            } else if (isDone) {
                val message = "Do you want to restart your workout?"
                showCustomDialogBoxConfirm(message) {
                    workoutActive()
                }

            }
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeRecyclerView = binding.homeList
        homeRecyclerView.layoutManager = LinearLayoutManager(requireContext())  // Add this line
        workoutHomeList = mutableListOf<Workout>()
        getHomeWorkout()

    }

    private fun workoutActive() {
        val Red = resources.getColor(R.color.red)
        val White = resources.getColor(R.color.white)
        val Green = resources.getColor(R.color.main_green)
        if (!isOngoing) {
            binding.playButton.setBackgroundResource(R.drawable.baseline_pause_circle_24)
            val status: TextView = binding.statusText
            status.text = "ongoing"
            status.setTextColor(White)
            startCounter()
        } else {
            binding.playButton.setBackgroundResource(R.drawable.baseline_replay_circle_filled_24)
            val status: TextView = binding.statusText
            status.text = "done"
            status.setTextColor(Green)
            stopCounter()
        }
    }

    private fun showCustomDialogBoxConfirm(message: String?, onYesClicked: () -> Unit) {
        val dialog = Dialog(requireContext())
        var okGo: Boolean = false
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_custom_confirmations)

        val tvMessage: TextView = dialog.findViewById(R.id.confirm_text)
        val btnYes: Button = dialog.findViewById(R.id.confirm_yes)
        val btnNo: Button = dialog.findViewById(R.id.confirm_no)

        tvMessage.text = message

        btnYes.setOnClickListener {
            dialog.dismiss()
            onYesClicked()
        }

        btnNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun startCounter() {
        isOngoing = true
        timeRun = 0
        handler = Handler(Looper.getMainLooper())
        handler.post(updateCounterRunnable)
    }

    private fun stopCounter() {
        isOngoing = false
        isDone = true
        handler.removeCallbacks(updateCounterRunnable)
        val timeRunned = timeRun
        // Save the timeRun value or perform other actions
    }

    private fun formatTime(timeInMillis: Long): String {
        val format = SimpleDateFormat("H:mm:ss", Locale.getDefault())
        format.timeZone = TimeZone.getTimeZone("UTC")
        return format.format(Date(timeInMillis))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        if (isOngoing) {
            stopCounter()
        }
    }

    private fun getCurrentDate(): String {
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("dd MM yyyy", Locale.getDefault())
        return dateFormat.format(currentDate)
    }

    private fun getCurrentDay(): String {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val daysArray =
            arrayOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
        return daysArray[dayOfWeek - 1]
    }

    private fun getHomeWorkout() {
        auth = FirebaseAuth.getInstance()
        val currentUserId = auth.currentUser?.uid

        if (currentUserId != null) {
            ref = FirebaseDatabase.getInstance("https://fitflow-id-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .reference.child("USERS").child(currentUserId).child("workoutDays")

            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Clear the existing list before adding new items
                        workoutHomeList.clear()

                        // Get the current day
                        val currentDay = getCurrentDay()

                        // Retrieve workouts for the current day using GenericTypeIndicator
                        val exercisesForCurrentDay = snapshot.child(currentDay)
                            .getValue(object : GenericTypeIndicator<List<String>>() {})

                        Log.d("WorkoutData", "Exercises for $currentDay: $exercisesForCurrentDay")

                        // Create a Workout object for each exercise in the current day
                        exercisesForCurrentDay?.forEach { exercise ->
                            workoutHomeList.add(
                                Workout(
                                    difficulty = "",  // Set the actual value
                                    equipment = "",   // Set the actual value
                                    instructions = "", // Set the actual value
                                    muscle = "",       // Set the actual value
                                    name = exercise,
                                    type = ""        // Set the actual value
                                )
                            )
                        }
                    }
                    Log.d("WorkoutData", "Workout list: $workoutHomeList")
                    homeRecyclerView.adapter = homeAdapter
                    Log.d("WorkoutData", "Adapter items count: ${homeRecyclerView.adapter?.itemCount}")
                    (homeRecyclerView.adapter as? HomeWorkoutAdapter)?.wOutList?.let {
                        Log.d("WorkoutData", "Adapter items: $it")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle the error case
                    Log.e("WorkoutData", "Error: ${error.message}")
                }
            })
        }
    }
}
