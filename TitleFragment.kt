package com.example.myprototype

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TitleFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TitleFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    var usertext: TextView? = null
    val db = Firebase.firestore
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =inflater.inflate(R.layout.fragment_title, container, false)
        auth = Firebase.auth
        usertext = view?.findViewById(R.id.cuurent_usename)
        val user = auth.currentUser
        view.findViewById<Button>(R.id.btn).setOnClickListener{
            findNavController().navigate(R.id.action_titleFragment_to_showImgFragment)
        }
        Log.d(TAG, "Hi")
        if(user != null){
            val username:String = user.displayName.toString()
            val message:String = "さんようこそ！"
            val combine = username+message
            usertext?.setText(combine)


            /*ここから下は，Registerでやってデータベースに追加すべき
            //ドキュメントIDは自分でわかりやすいものに設定しましょう
            val db_user = hashMapOf<String,Any>(
                "name" to username,
            )
            Log.d(TAG, "Hello")
            val ref = db.collection("users").document(username)
            Log.d(TAG, "Document ID: ${ref}")
            db.collection("users").document(username)
                .set(db_user)
                .addOnSuccessListener {
                    Log.d(TAG, "username added with ID: ${username}")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }

             */
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view?.findViewById<Button>(R.id.btn_logout).setOnClickListener(){
            //ログアウトして，ログイン画面に戻る を書け
            FirebaseAuth.getInstance().signOut()
            findNavController().navigate(R.id.action_titleFragment_to_loginFragment)
        }
    }
    companion object {
        val TAG ="TitleFragment"
    }

}