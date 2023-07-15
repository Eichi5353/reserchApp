package com.example.myprototype

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RegisterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
//account1: abc@gmail.com -> Pass:abcdef

class RegisterFragment : Fragment() {
    var editTextEmail: EditText? = null
    var editTextPassword: EditText? = null
    var editTextName:EditText? = null
    var buttonReg:Button? = null
    var progressBar: ProgressBar? = null
    //var textView:TextView? = null
    private lateinit var auth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_register, container, false)
        view.findViewById<TextView>(R.id.loginNow).setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)///あれ？？？？
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        editTextEmail = view?.findViewById(R.id.email)
        editTextPassword = view?.findViewById(R.id.password)
        editTextName = view?.findViewById(R.id.username)
        progressBar = view?.findViewById(R.id.progressBar)

        val context: Context? = requireActivity().applicationContext
        auth = Firebase.auth
        view.findViewById<Button>(R.id.btn_register).setOnClickListener {
            var email: String = editTextEmail?.getText().toString()
            var password: String = editTextPassword?.getText().toString()
            val username:String = editTextName?.getText().toString()

            progressBar?.visibility = ProgressBar.VISIBLE
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(context, "Enter Email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(password)){
                Toast.makeText(context, "Enter Password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    progressBar?.visibility = ProgressBar.GONE
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success")
                        val user = auth.currentUser
                        val profileUpdates = userProfileChangeRequest {
                            displayName = username
                        }
                        user!!.updateProfile(profileUpdates)
                            .addOnCompleteListener { updateTask ->
                                if (updateTask.isSuccessful) {
                                    Log.d(TAG, "User profile updated.")
                                }
                                else
                                    Log.d(TAG, "User profile could not update.")
                            }
                        Toast.makeText(context, "Account create.", Toast.LENGTH_SHORT,).show()
                        findNavController().navigate(R.id.action_registerFragment_to_titleFragment)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.exception)
                        Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT,).show()
                        //updateUI(null)
                    }
                }

        }




    }

    companion object {
        val TAG ="RegisterFragment"
    }
}