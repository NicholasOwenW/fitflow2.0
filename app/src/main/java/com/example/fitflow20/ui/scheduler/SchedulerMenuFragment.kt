package com.example.fitflow20.ui.scheduler

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.navigation.fragment.findNavController
import com.example.fitflow20.R
import com.example.fitflow20.api.Workout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SchedulerMenuFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SchedulerMenuFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var auth: FirebaseAuth
    lateinit var ref: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_scheduler_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //DAY BUTTON

        val cardMonday = view.findViewById<CardView>(R.id.mondayCard)
        cardMonday.setOnClickListener() {
            openSchedulerFragment("Monday")
        }
        val cardTuesday = view.findViewById<CardView>(R.id.tuesdayCard)
        cardTuesday.setOnClickListener() {
            openSchedulerFragment("Tuesday")
        }
        val cardWednesday = view.findViewById<CardView>(R.id.wednesdayCard)
        cardWednesday.setOnClickListener() {
            openSchedulerFragment("Wednesday")
        }
        val cardThursday = view.findViewById<CardView>(R.id.thursdayCard)
        cardThursday.setOnClickListener() {
            openSchedulerFragment("Thursday")
        }
        val cardFriday = view.findViewById<CardView>(R.id.fridayCard)
        cardFriday.setOnClickListener() {
            openSchedulerFragment("Friday")
        }
        val cardSaturday = view.findViewById<CardView>(R.id.saturdayCard)
        cardSaturday.setOnClickListener() {
            openSchedulerFragment("Saturday")
        }
        val cardSunday = view.findViewById<CardView>(R.id.sundayCard)
        cardSunday.setOnClickListener() {
            openSchedulerFragment("Sunday")
        }

        //DELETE BUTTON

        val deleteBtn_monday = view.findViewById<CardView>(R.id.delete_btn_monday)
        deleteBtn_monday.setOnClickListener(){
            val emptyWorkoutList: List<Workout> = emptyList()
            saveWorkoutsForDay("Monday", emptyWorkoutList)
        }
        val deleteBtn_tuesday = view.findViewById<CardView>(R.id.delete_btn_tuesday)
        deleteBtn_tuesday.setOnClickListener(){
            val emptyWorkoutList: List<Workout> = emptyList()
            saveWorkoutsForDay("Tuesday", emptyWorkoutList)
        }
        val deleteBtn_wednesday = view.findViewById<CardView>(R.id.delete_btn_wednesday)
        deleteBtn_wednesday.setOnClickListener(){
            val emptyWorkoutList: List<Workout> = emptyList()
            saveWorkoutsForDay("Wednesday", emptyWorkoutList)
        }
        val deleteBtn_thursday = view.findViewById<CardView>(R.id.delete_btn_thursday)

        deleteBtn_thursday.setOnClickListener(){
            val emptyWorkoutList: List<Workout> = emptyList()
            saveWorkoutsForDay("Thursday", emptyWorkoutList)
        }
        val deleteBtn_friday = view.findViewById<CardView>(R.id.delete_btn_friday)
        deleteBtn_friday.setOnClickListener(){
            val emptyWorkoutList: List<Workout> = emptyList()
            saveWorkoutsForDay("Friday", emptyWorkoutList)
        }
        val deleteBtn_saturday = view.findViewById<CardView>(R.id.delete_btn_saturday)
        deleteBtn_saturday.setOnClickListener(){
            val emptyWorkoutList: List<Workout> = emptyList()
            saveWorkoutsForDay("Saturday", emptyWorkoutList)
        }
        val deleteBtn_sunday = view.findViewById<CardView>(R.id.delete_btn_sunday)
        deleteBtn_sunday.setOnClickListener(){
            val emptyWorkoutList: List<Workout> = emptyList()
            saveWorkoutsForDay("Sunday", emptyWorkoutList)
        }
    }

    private fun openSchedulerFragment(day: String) {
        val schedulerFragment = SchedulerFragment()
        schedulerFragment.arguments = Bundle().apply {
            putString("selected_day", day)
        }

        val navController = findNavController()
        navController.navigate(R.id.schedulerFragment, schedulerFragment.arguments)

    }

    private fun saveWorkoutsForDay(day: String, workouts: List<Workout>) {
        auth = FirebaseAuth.getInstance()
        val currentUserId = auth.currentUser?.uid

        if (currentUserId != null) {
            ref = FirebaseDatabase.getInstance("https://fitflow-id-default-rtdb.asia-southeast1.firebasedatabase.app/")
                .reference.child("USERS").child(currentUserId).child("workoutDays")
            val userWorkoutsRef = ref.child(day)

            // Convert the list of Workout objects to a list of strings
            val workoutNames = workouts.map { it.name }

            userWorkoutsRef.setValue(workoutNames).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(requireContext(), "Workout schedule for $day successfully deleted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Workout schedule failed to be deleted", Toast.LENGTH_SHORT).show()
                    Log.e(SchedulerFragment.TAG, "Error: ${it.exception}")
                }
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SchedulerMenuFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SchedulerMenuFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
