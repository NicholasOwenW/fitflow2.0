package com.example.fitflow20.ui.bmi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.fitflow20.R
import com.example.fitflow20.databinding.FragmentBmiBinding
class BmiFragment : Fragment() {

    private lateinit var binding: FragmentBmiBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bmi, container, false)
        binding = FragmentBmiBinding.bind(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activity != null) {
            (activity as AppCompatActivity?)!!.supportActionBar!!.title = "BMI Calculator"
        }

        binding.weightPicker.minValue = 30
        binding.weightPicker.maxValue = 150

        binding.heightPicker.minValue = 130
        binding.heightPicker.maxValue = 250

        binding.weightPicker.setOnValueChangedListener{_,_,_ ->
            calculateBMI()
        }

        binding.heightPicker.setOnValueChangedListener{_,_,_ ->
            calculateBMI()
        }
    }
    private fun calculateBMI(){
        val height = binding.heightPicker.value
        val doubleHeight = height.toDouble() / 100

        val weight = binding.weightPicker.value

        val bmi = weight.toDouble() / (doubleHeight * doubleHeight)

        binding.resultsTV.text = String.format("Your BMI is: %.2f", bmi)
        binding.healthyTV.text = String.format("Considered: %s", healthyMessage(bmi))
    }

    private fun healthyMessage(bmi: Double): String{
        if (bmi < 18.5)
            return "Under Weight"
        if (bmi < 25.0)
            return "Healthy"
        if (bmi < 30.0)
            return "Over Weight"

        return "Obese"
    }
}