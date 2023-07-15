package com.example.myprototype

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.component1
import com.google.firebase.storage.ktx.component2
import com.google.firebase.storage.ktx.storage


class MainActivity : AppCompatActivity() {
    var range: IntRange? = null //乱数の範囲を取得するため
    var RefList: ArrayList<String>? = arrayListOf()//storage内の画像の場所を取得するための配列
    val storage = Firebase.storage
    var ONE_MEGABYTE: Long? = 1024 * 1024 * 10
    var gsReference: StorageReference? = null
    private lateinit var bitmapViewModel: BitmapViewModel
    private lateinit var auth: FirebaseAuth
    var loginFlag: Int = 0
    val mAuth = FirebaseAuth.getInstance()
    val TAG = "MainActivity"


    //Authenticationでログインしているかを確かめる
    /*val currentUser = auth.currentUser
        if (currentUser != null) {
            loginFlag = 1
        }\

         */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //非同期処理 何のため？
        val task = AsyncTaskClass()
        task.execute("")

        //auth = Firebase.auth


        bitmapViewModel = ViewModelProvider(this).get(BitmapViewModel::class.java)
        //storageの画像があるノードまで行く
        //いろんな画像
        //val listRef = storage.reference.child("images")
        //研究室内の画像
        val listRef = storage.reference.child("AISLab")
        //storageReference = storage.reference

        //そのノードにある画像の場所をすべて取得してRefList配列に格納する
        //そのあと取得した数分の大きさの乱数を生成し，ランダムで画像を表示するようにする
        listRef.listAll()
            .addOnSuccessListener { (items, prefixes) ->
                prefixes.forEach { prefix ->
                    // All the prefixes under listRef.
                    // You may call listAll() recursively on them.
                    //これは枕詞みたいなもんですか？
                    Log.i("Storage", "prefix is $prefix")
                }

                items.forEach { item ->
                    // All the items under listRef.
                    Log.i("Storage", "item is $item")
                    RefList?.add("$item")
                    Log.i("Storage", "items are $RefList")
                }

                //乱数生成（1から配列の大きさ)
                range = (1..RefList!!.size)
                val size = RefList!!.size
                Log.i("Storage", "array_size is $size")

                //関数にしたほうが見やすいか？
                //ここを撮影画像のみ選べるようにする　or 直前の撮影画像を持ってくる
                val random_num = range?.random()
                //選ばれた配列
                val result = RefList!![random_num!! - 1]
                Log.i("Storage", "random_num is $random_num")
                Log.i("Storage", "random_item is $result")

                gsReference = storage.getReferenceFromUrl(result)
                ONE_MEGABYTE = 1024 * 1024 * 10 //最大ダウンロードできるバイトサイズ　小さいとエラーになる
                gsReference?.getBytes(ONE_MEGABYTE!!)?.addOnSuccessListener {
                    //bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)

                    //ここ追加したぞ！！
                    bitmapViewModel.mBitmap.setValue(
                        BitmapFactory.decodeByteArray(
                            it,
                            0,
                            it.size
                        )
                    )
                }?.addOnFailureListener {
                    // Handle any errors
                    Log.e("FirebaseImg", "False")
                }

            }
            .addOnFailureListener {
                Log.e("ChoseImg", "False")// Uh-oh, an error occurred!
            }


        //bitmapViewModel= ViewModelProvider(this).get(BitmapViewModel::class.java)
    }
}