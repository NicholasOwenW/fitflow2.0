package com.example.fitflow20.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.fitflow20.R
import com.example.fitflow20.Users
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        auth = FirebaseAuth.getInstance()
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val tv_user = view.findViewById<TextView>(R.id.tv_user)
        val tv_isi_email = view.findViewById<TextView>(R.id.t_isi_email)
        val tv_isi_phone = view.findViewById<TextView>(R.id.t_isi_phone)
        userInfo()

        return view
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
}