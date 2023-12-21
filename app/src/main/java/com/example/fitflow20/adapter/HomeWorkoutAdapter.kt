package com.example.fitflow20.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fitflow20.R
import com.example.fitflow20.api.Workout


class HomeWorkoutAdapter(var wOutList: MutableList<Workout>): RecyclerView.Adapter<HomeWorkoutAdapter.HomeViewHolder>(){

    class HomeViewHolder(itemView : View): RecyclerView.ViewHolder(itemView){
        val workoutName : TextView = itemView.findViewById(R.id.item_WorkoutTitle)
//        val workoutMuscle : TextView = itemView.findViewById(R.id.item_muscleType)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_home_workoutday,
            parent, false)
        return HomeViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return wOutList.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        val currentItem = wOutList[position]
        holder.workoutName.text = currentItem.name
//        holder.workoutMuscle.text = currentItem.muscle
    }
}