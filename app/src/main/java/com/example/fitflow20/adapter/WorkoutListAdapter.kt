package com.example.fitflow20.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.fitflow20.api.Workout
import com.example.fitflow20.databinding.ItemWorkoutBinding

class WorkoutListAdapter: RecyclerView.Adapter<WorkoutListAdapter.WorkoutViewHolder>(){

    inner class WorkoutViewHolder(val binding: ItemWorkoutBinding): RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object: DiffUtil.ItemCallback<Workout>(){

        override fun areItemsTheSame(oldItem: Workout, newItem: Workout): Boolean{
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Workout, newItem: Workout): Boolean {
            return oldItem.name == newItem.name
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)
    var workouts: List<Workout>
        get() = differ.currentList
        set(value){differ.submitList(value)}

    override fun getItemCount() = workouts.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        return WorkoutViewHolder(ItemWorkoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    private val checkedWorkouts = ArrayList<Workout>()

    // Callback to notify item click
    private var onItemClickListener: ((Workout) -> Unit)? = null

    // Setter for item click listener
    fun setOnItemClickListener(listener: (Workout) -> Unit) {
        onItemClickListener = listener
    }

    // Getter for checked items
    fun getCheckedWorkouts(): List<Workout> {
        return checkedWorkouts
    }
    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        holder.binding.apply {
            val workout = workouts[position]
            workoutTitle.text = workout.name
            workoutDifficulty.text = workout.difficulty
            workoutMuscle.text = workout.muscle

            workoutCB.setOnClickListener {
                if (workoutCB.isChecked) {
                    checkedWorkouts.add(workout)
                } else {
                    checkedWorkouts.remove(workout)
                }

                // Notify item click listener
                onItemClickListener?.invoke(workout)

            }
        }
    }
}