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
import androidx.lifecycle.ViewModelProvider
import com.example.fitflow20.R
import com.example.fitflow20.databinding.FragmentHomeBinding
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var handler: Handler
    lateinit var viewModel: HomeViewModel

    private val updateCounterRunnable = object : Runnable {
        override fun run() {
            if (viewModel.currentOngoing.value == true) {
                Log.d("OKAY", "MANNNNNN")
                viewModel.currentTime.value = viewModel.currentTime.value?.plus(1000) // Update every 1 second
                binding.timeCounter.text = viewModel.currentTime.value?.let { formatTime(it) }
                handler.postDelayed(this, 1000)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)

        if(viewModel.currentOngoing.value == null) {
            viewModel.currentOngoing.value = false
        }
        if(viewModel.currentDone.value == null) {
            viewModel.currentDone.value = false
        }
        if(viewModel.currentTime.value == null) {
            viewModel.currentTime.value = 0L
        }
    }

    companion object {
        const val REQUEST_CAMERA = 100
        const val CAMERA_PERMISSION_CODE = 101
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val Red = getResources().getColor(R.color.red)
        val White = getResources().getColor(R.color.white)
        val Green = getResources().getColor(R.color.main_green)

        val textViewDate: TextView = binding.root.findViewById(R.id.current_date)
        val currentDate = getCurrentDate()
        textViewDate.text = "$currentDate"

        val textViewDay: TextView = binding.root.findViewById(R.id.current_day)
        val currentDay = getCurrentDay()
        textViewDay.text = "$currentDay"


        // PLAY BUTTON TIME WOWOOWOW
        val playBtn: ImageView = binding.root.findViewById(R.id.play_button)
        playBtn.setOnClickListener {
            val current = viewModel.currentOngoing.value
            if (viewModel.currentOngoing.value == false && viewModel.currentDone.value == false) {
                val message = "Are you sure you want to start now?"
                showCustomDialogBoxConfirm(message) {
                    workoutActive()
                }

            } else if (viewModel.currentOngoing.value == true) {
                val message = "Are you done with your workout?"
                showCustomDialogBoxConfirm(message) {
                    workoutActive()
                    val message = "Do you want to take a selfie to record your progress?"
                    showCustomDialogBoxConfirm(message) {
                        checkCameraPermission()
                    }
                }

            } else if (viewModel.currentDone.value == true) {
                val message = "Do you want to restart your workout?"
                showCustomDialogBoxConfirm(message) {
                    workoutActive()
                }

            }
        }


        return root
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

    private fun workoutActive() {
        val Red = getResources().getColor(R.color.red)
        val White = getResources().getColor(R.color.white)
        val Green = getResources().getColor(R.color.main_green)
        if (viewModel.currentOngoing.value == false) {
            binding.playButton.setBackgroundResource(R.drawable.baseline_pause_circle_24)
            val status: TextView = binding.root.findViewById(R.id.status_text)
            status.text = "ongoing"
            status.setTextColor(White)
            startCounter()
        } else {
            binding.playButton.setBackgroundResource(R.drawable.baseline_replay_circle_filled_24)
            val status: TextView = binding.root.findViewById(R.id.status_text)
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

        val tvMessage: TextView =  dialog.findViewById(R.id.confirm_text)
        val btnYes: Button =  dialog.findViewById(R.id.confirm_yes)
        val btnNo: Button =  dialog.findViewById(R.id.confirm_no)

        tvMessage.text = message

        btnYes.setOnClickListener() {
            dialog.dismiss()
            onYesClicked()
        }

        btnNo.setOnClickListener() {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun startCounter() {
        viewModel.currentOngoing.value = true
        viewModel.currentTime.value = 0
        handler = Handler(Looper.getMainLooper())
        handler.post(updateCounterRunnable)
    }

    private fun stopCounter() {
        viewModel.currentOngoing.value = false
        viewModel.currentDone.value = true
        handler.removeCallbacks(updateCounterRunnable)
    }

    private fun formatTime(timeInMillis: Long): String {
        val format = SimpleDateFormat("H:mm:ss", Locale.getDefault())
        format.timeZone = TimeZone.getTimeZone("UTC")
        return format.format(Date(timeInMillis))
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        if (viewModel.currentOngoing.value == true) {
            stopCounter()
        }
    }

    private fun getCurrentDate(): String {
        val currentDate = Date()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return dateFormat.format(currentDate)
    }

    private fun getCurrentDay(): String {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val daysArray = arrayOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
        return daysArray[dayOfWeek - 1]
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

}