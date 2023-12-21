package com.example.fitflow20.ui.scheduler

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitflow20.adapter.WorkoutListAdapter
import RetrofitInstance
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.example.fitflow20.HomeActivity
import com.example.fitflow20.R
import com.example.fitflow20.api.Workout
import com.example.fitflow20.databinding.ActivityRegisterBinding
import com.example.fitflow20.databinding.FragmentSchedulerBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SchedulerFragment : Fragment() {

    lateinit var auth: FirebaseAuth
    lateinit var ref: DatabaseReference
    private var _binding: FragmentSchedulerBinding? = null
    private val binding get() = _binding!!

    private val workoutAdapter by lazy { WorkoutListAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        Log.e(TAG, "DI SINI MULAI ERROR")
        _binding = FragmentSchedulerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setActionBarTitle("Workout Scheduler")
        val selectedDay = arguments?.getString("selected_day")

        val searchView = view.findViewById<SearchView>(R.id.searchbar)
        val textColor = ContextCompat.getColor(requireContext(), R.color.main_green)
        val searchEditText = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchEditText.setTextColor(textColor)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Handle search when user submits the query (optional)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Handle search as the user types
                if (newText != null) {
                    observeViewModel(newText)
                }
                return true
            }
        })


        val addBtn = view.findViewById<Button>(R.id.add_button)
        addBtn.setOnClickListener() {
            val selectedDay = arguments?.getString("selected_day")
            val listOfWorkouts = getCheckedWorkouts()

            if (selectedDay != null) {
                saveWorkoutsForDay(selectedDay, listOfWorkouts)
            }
        }
    }

    private fun saveWorkoutsForDay(day: String, workouts: List<Workout>) {
        auth = FirebaseAuth.getInstance()
        val currentUserId = auth.currentUser?.uid

        if (currentUserId != null) {
            ref = FirebaseDatabase.getInstance("https://fitflow-id-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .reference.child("USERS").child(currentUserId).child("workoutDays")
            val userWorkoutsRef = ref.child(day)

            // Convert the list of Workout objects to a list of strings
            val workoutData = workouts.map { workout ->
                mapOf(
                    "name" to workout.name,
                    "type" to workout.type
                )
            }

            userWorkoutsRef.setValue(workoutData).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(requireContext(), "Changes applied to $day", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Failed to apply changes", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, "Failed to save workouts: ${it.exception}")
                }
            }
        }
    }


    private fun setupRecyclerView() {
        binding.workoutrecycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = workoutAdapter
        }
    }

    fun getCheckedWorkouts(): List<Workout> {
        return workoutAdapter.getCheckedWorkouts()
    }


    private fun observeViewModel(query: String) {
        lifecycleScope.launch {
            val todoAdapter by lazy { WorkoutListAdapter() }
            binding.progressBar.isVisible = true
            try {
                val response = RetrofitInstance.api.getWorkout(query)
                if (response.isSuccessful) {
//                    workouts = response.body() ?: emptyList()
                    workoutAdapter.workouts = response.body() ?: emptyList()
                } else {
                    Log.e(TAG, "Response not successful: ${response.code()}")
                }
            } catch (e: IOException) {
                Log.e(TAG, "IOException, you might not have an internet connection")
            } catch (e: HttpException) {
                Log.e(TAG, "HttpException, unexpected response: ${e.code()}")
            } finally {
                binding.progressBar.isVisible = false
            }
        }
    }


    private fun setActionBarTitle(title: String) {
        (requireActivity() as? AppCompatActivity)?.supportActionBar?.title = title
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "SchedulerFragment"
    }
}
