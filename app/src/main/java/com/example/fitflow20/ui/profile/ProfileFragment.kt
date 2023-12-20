package com.example.fitflow20.ui.profile

import android.app.Dialog
import android.content.Intent
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.fitflow20.MainActivity
import com.example.fitflow20.R
import com.example.fitflow20.RegisterActivity
import com.example.fitflow20.Users
import com.example.fitflow20.databinding.FragmentHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [SchedulerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    lateinit var auth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    private var updatephone = "0"

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        auth = FirebaseAuth.getInstance()
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val tv_user = view.findViewById<TextView>(R.id.tv_user)
        val tv_isi_email = view.findViewById<TextView>(R.id.t_isi_email)
        val tv_isi_phone = view.findViewById<TextView>(R.id.t_isi_phone)
        userInfo()

        //PROFILE UPDATE PHONE NUMBA
        val editBtn = view.findViewById<TextView>(R.id.edit_phone)

        editBtn.setOnClickListener() {
            showCustomDialogBoxConfirm()
        }

        val logoutBtn = view.findViewById<Button>(R.id.btn_logout)

        logoutBtn.setOnClickListener() {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
        }

        return view
    }

    private fun showCustomDialogBoxConfirm() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.layout_custom_edit_phone)

        val newphone: EditText = dialog.findViewById(R.id.new_phone)
        val btnEdit: Button = dialog.findViewById(R.id.confirm_edit)
        val btnCancel: Button = dialog.findViewById(R.id.cancel_edit)

        btnEdit.setOnClickListener() {
            // Retrieve the new phone number from the EditText
            val updatedPhone = newphone.text.toString().trim()
            if (updatedPhone.isNotEmpty()) {
                // Update the updatephone variable with the new value
                updatephone = updatedPhone
                // Log the updated value
                Log.d("TAGGERSSSSSSSSSSSSSSSS", "Here is updatephone: $updatephone")
                // Dismiss the dialog
                updatePhoneNumber(updatedPhone)
                dialog.dismiss()
            } else {
                // Handle empty phone number input, show a message or take appropriate action
            }
        }

        btnCancel.setOnClickListener() {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun userInfo(){
        val userRef = FirebaseDatabase.getInstance("https://fitflow-id-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("USERS").child(firebaseUser.uid)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue<Users>(Users::class.java)
                    user?.let {
                        val tv_user = view?.findViewById<TextView>(R.id.tv_user)
                        val tv_isi_email = view?.findViewById<TextView>(R.id.t_isi_email)
                        val tv_isi_phone = view?.findViewById<TextView>(R.id.t_isi_phone)

                        tv_user?.text = it.userNama
                        tv_isi_email?.text = it.email
                        tv_isi_phone?.text = it.phoneNumber
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
    private fun updatePhoneNumber(newPhone: String) {
        val userRef = FirebaseDatabase.getInstance("https://fitflow-id-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .reference.child("USERS").child(firebaseUser.uid)

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    userRef.child("phoneNumber").setValue(newPhone)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle onCancelled
            }
        })
    }
}