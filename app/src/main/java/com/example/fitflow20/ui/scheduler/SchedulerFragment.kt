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
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import com.example.fitflow20.R
import com.example.fitflow20.api.Workout
import com.example.fitflow20.databinding.FragmentSchedulerBinding
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class SchedulerFragment : Fragment() {

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
        addBtn.setOnClickListener(){
            val ListofWorkouts: List<Workout> = getCheckedWorkouts()
            val day = selectedDay

            // TODO :Betulin FIREBASE REALTIME biar workoutsdays muncul pas register user
            // TODO: MASUKIN LISTOFWORKOUTS SAMA DAY KE DATABASE
            // TODO: lu cari cara buat reference WorkoutDay > Monday
            //  TODO: Mondaynya dimasukin ListOfWorkouts

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
        private const val TAG = "SchedulerFragment"
    }
}
