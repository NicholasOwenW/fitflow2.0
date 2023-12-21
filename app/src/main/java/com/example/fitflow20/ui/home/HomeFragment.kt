package com.example.fitflow20.ui.home

import android.Manifest
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitflow20.R
import com.example.fitflow20.adapter.HomeWorkoutAdapter
import com.example.fitflow20.api.Workout
import com.example.fitflow20.databinding.FragmentHomeBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
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
    private lateinit var homeViewModel: HomeViewModel
    private var isFragmentFirstLoad = true


    private lateinit var handler: Handler
    private var isOngoing: Boolean = false
    private var timeRun: Long = 0L
    private var isDone: Boolean = false

//    val HomeViewModel by lazy {
//        activity?.let {
//            ViewModelProvider(it).get(HomeViewModel::class.java)
//        } ?: throw IllegalStateException("Invalid Activity")
//    }
    private fun refreshFragment() {
        // Add your logic to refresh data or perform any necessary actions
        getHomeWorkout()
    findNavController().navigate(R.id.action_nav_home_self)
    }
    private val updateCounterRunnable = object : Runnable {
        override fun run() {
            if (isOngoing) {
                timeRun += 1000 // Update every 1 second
                binding.timeCounter.text = formatTime(timeRun)
                handler.postDelayed(this, 1000)
            }
        }
    }
//    private val homeViewModel: HomeViewModel by lazy {
//        ViewModelProvider(this).get(HomeViewModel::class.java)
//    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
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
                    val message = "Do you want to take a selfie to record your progress?"
                    showCustomDialogBoxConfirm(message) {
                        checkCameraPermission()
                    }
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

//    override fun onResume() {
//        super.onResume()
//
//        // If the fragment is being resumed after being in the background, trigger refresh
//        if (!isFragmentFirstLoad) {
//            homeViewModel.triggerRefresh()
//        }
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        workoutHomeList = mutableListOf() // Initialize workoutHomeList here
        homeViewModel.refreshEvent.observe(viewLifecycleOwner) {
            // Handle the refresh event here
            // Update UI or perform any action needed for refresh
            getHomeWorkout()
        }
        homeRecyclerView = binding.homeList
        homeRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        homeRecyclerView.adapter = homeAdapter // Set the adapter here

        getHomeWorkout()

        val refreshBtn = view.findViewById<FloatingActionButton>(R.id.refbtn)
        refreshBtn.setOnClickListener(){
            refreshFragment()
        }
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
//    override fun onResume() {
//        super.onResume()
//        getHomeWorkout()
//    }
    // Inside the HomeFragment class
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
                            // Get the type for the current exercise
                            val typeReference = snapshot.child(currentDay).child(exercise)
                                .child("type")
                            val type = typeReference.getValue(String::class.java) ?: ""

                            workoutHomeList.add(
                                Workout(
                                    difficulty = "",  // Set the actual value
                                    equipment = "",   // Set the actual value
                                    instructions = "", // Set the actual value
                                    muscle = "",       // Set the actual value
                                    name = exercise,
                                    type = type        // Set the type from the database
                                )
                            )

                            // Log the contents of the added Workout item
                            Log.d("WorkoutData", "Added Workout item: $exercise, Type: $type")
                        }
                    }
                    Log.d("WorkoutData", "Workout list: $workoutHomeList")
                    homeAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle the error case
                    Log.e("WorkoutData", "Error: ${error.message}")
                }
            })
        }
    }

    private fun intentCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            activity?.packageManager?.let {
                intent.resolveActivity(it).also {
                    startActivityForResult(intent, REQUEST_CAMERA)
                }
            }
        }
    }

    private fun saveImageToGallery(bitmap: Bitmap?) {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "IMG_$timeStamp.jpg"

        val resolver = requireContext().contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM + "/fitflow2.0") // Use your app's directory
        }

        val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        try {
            resolver.openOutputStream(imageUri!!)?.use { outputStream ->
                bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
            Toast.makeText(requireContext(), "Image saved to gallery", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("HomeFragment", "Error saving image to gallery: ${e.message}")
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                intentCamera()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Camera permission denied. Unable to take a photo.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            intentCamera()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CAMERA && resultCode == androidx.appcompat.app.AppCompatActivity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap?
            if (imageBitmap != null) {
                saveImageToGallery(imageBitmap)
            } else {
                Toast.makeText(requireContext(), "Error capturing image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        const val REQUEST_CAMERA = 100
        const val CAMERA_PERMISSION_CODE = 101
    }
}
