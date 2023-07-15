package com.example.myprototype

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import okhttp3.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ResultFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ResultFragment : Fragment() {

    private lateinit var bitmapViewModel: BitmapViewModel
    var scoreText: TextView? =null
    var imgTest: ImageView? =null
    var imgTest2: ImageView? =null
    var live_bitmap: MutableLiveData<Bitmap>? =null
    var bitmap1:Bitmap?=null
    var bitmap2:Bitmap?=null
    var num:TextView?=null
    var responseData: String = "1"//結果の類似度
    private lateinit var auth: FirebaseAuth
    val db = Firebase.firestore
    var user: FirebaseUser? = null

    //M_Project内の「flask_server.py」を実行したうえで結果を表示する
    //一応このプロジェクトのPythonファイル群の中にも入っている
    private var url = "http://192.168.101.39:5000/"

    private val POST = "POST"
    private val GET = "GET"

    // putXXXXに対応するgetXXXXで値を取得 //TakeImgFragmentからImgのString取得
    //val stringImg2 = arguments?.getString("takeImg")
    val TAG = "ResultFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_result, container, false)
        auth = Firebase.auth
        user = auth.currentUser

        activity?.run {
            bitmapViewModel = ViewModelProvider(this).get(BitmapViewModel::class.java)
            Log.i(TAG, "Called ViewModelProvider.get")
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imgTest=view.findViewById(R.id.imageView4)
        imgTest2=view.findViewById(R.id.imageView5)

        scoreText = view.findViewById(R.id.score)

        //Python読み込みのやつ
        //問題画像
        bitmap1 = bitmapViewModel.mBitmap.getValue()
        val stringImg1 =getStringImage(bitmap1)
        //Log.d(TAG,"stringImg2の値は$stringImg1")
        //撮影画像の取得
        bitmap2 = bitmapViewModel.tBitmap.getValue()
        val stringImg2 =getStringImage(bitmap2)
        //Log.d(TAG,"stringImg2の値は$stringImg2")

        //確認用（問題画像）
        Glide.with(this)
            .load(bitmap1)
            .into(imgTest!!)
        //確認用（撮影画像）
        Glide.with(this)
            .load(bitmap2)
            .into(imgTest2!!)

            /*
        //python
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(requireContext()))
        }
        val py = Python.getInstance()
        val module = py.getModule("culc_sim2")
        Log.d(TAG,"pyモジュール取得")
        //類似度判定
        num = view.findViewById<TextView>(R.id.score)
        //うーん　ボタン押したら変わるとかの処理がいいですか？　　imgを取得しなければ，
        view.findViewById<Button>(R.id.btn_test).setOnClickListener {
            val time = measureTimeMillis {
                try {
                    val sim: String =
                        module.callAttr("main", stringImg1, stringImg2)
                            .toJava(String()::class.java)
                    Log.d(TAG, "結果を出せた")
                    num?.setText(sim + "点！")
                } catch (e: NumberFormatException) {
                    Log.d(TAG, "impossible")
                }
            }
            Log.d(TAG,"処理時間は${time}ミリ秒です")
        }

             */


        view.findViewById<Button>(R.id.btn_test).setOnClickListener {
            val time = measureTimeMillis {
                if (stringImg1?.isEmpty() == true && stringImg2?.isEmpty() == true) {
                    Log.e(TAG, "cannot send")
                    //Log.d(TAG,stImg1)
                    scoreText?.setError("This cannot be empty for post request")
                } else {
                    /*if name text is not empty,then call the function to make the post request*/
                    Log.d(TAG, "sendPost")
                    //Log.d(TAG,stImg1!!)//ちゃんとした値が存在
                    //ここが問題　値がサーバー側へしっかり与えられていない
                    sendRequest(POST, "img/post", "img1", stringImg1, "img2", stringImg2)
                    //sendRequest(POST, "img/post", )
                    // textView_response2?.setText("POST img!")
                    //sendRequest(POST, "getimg", "img2", stImg2)
                }
            }
            Log.d(TAG,"処理時間は${time}ミリ秒です")
        }

    }

    private fun getStringImage(bitmap: Bitmap?):String?{//Bitmap? or Disposable? どっちでもいける？
        val baos = ByteArrayOutputStream() //byte型，string型変換のための川（通り道）
        //bitmapをcompressによって圧縮
        //第二引数は０~100でふつう100でおk　第三引数のbaosは上のコードと合わせて決まりみたいなもの？
        bitmap?.compress(Bitmap.CompressFormat.JPEG,100,baos)//baosの中に画像データが入ってる？
        val imageBytes =baos.toByteArray()
        val encodeImage = Base64.encodeToString(imageBytes, Base64.DEFAULT)
        return encodeImage
    }

    fun sendRequest(type: String, method: String, paramname1: String?, value1: String?,paramname2: String?, value2: String?) {

        /* if url is of our get request, it should not have parameters according to our implementation.
        * But our post request should have 'name' parameter. */
        val fullURL = url + "/" + method //+ if (param == null) "" else "/$param"
        val request: Request
        val client: OkHttpClient = OkHttpClient().newBuilder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS).build()

        /* If it is a post request, then we have to pass the parameters inside the request body*/request =
            if (type == POST) {
                val formBody: RequestBody = FormBody.Builder()
                    .add(paramname1!!, value1!!)//ここで値を渡すことができるparamnameをpython側で指定すればvalueを得られる
                    .add(paramname2!!, value2!!)//ここで値を渡すことができるparamnameをpython側で指定すればvalueを得られる
                    .build()
                Request.Builder()
                    .url(fullURL)
                    .post(formBody)
                    .build()
            } else {
                /*If it's our get request, it doen't require parameters, hence just sending with the url*/
                Request.Builder()
                    .url(fullURL)
                    .build()
            }
        /* this is how the callback get handled */
        client.newCall(request)
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    Log.e(TAG,e.toString())

                }

                //@Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {

                    // Read data on the worker thread
                    responseData = response.body!!.string()
                    Log.d(TAG,responseData)
                    //なぜかHTML全体のコードが出力される
                    //bodyであっている？

                    // Run view-related code back on the main thread.
                    // Here we display the response message in our text view
                    getActivity()?.runOnUiThread {
                        scoreText!!.text = "得点は"+responseData+"点！！"
                    }

                    //add point to database
                    if(user != null){
                        val username:String = user!!.displayName.toString()
                        //データベースに追加する得点データ
                        Log.d(TAG, "Point is ${responseData} Point")
                        val point_data = hashMapOf(
                            "point" to responseData.toString()
                        )
                        db.collection("users").document(username)
                            .update(point_data as Map<String, String>)
                            .addOnSuccessListener {
                                Log.d(TAG, "DocumentSnapshot added with ID: ${username}:${responseData}")
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Error adding document", e)
                            }
                    }else
                        Log.d(TAG, "Could not find user")
                }
            })
    }
}
/*
view.findViewById<Button>(R.id.btn_test).setOnClickListener {

    if (bitmapViewModel.getBitmap() != null) {
        val a = true
        Log.d(TAG, "ViewModel.getは$a")
        Log.d(TAG, "${bitmapViewModel.getBitmap()}")
        live_bitmap = bitmapViewModel.getBitmap()
        bitmap1 = bitmapViewModel.mBitmap.getValue()
        Log.d(TAG,"bitmapはーーー$bitmap1")
        //ViewModelの中身がないから表示ができていない
        Glide.with(this)
            .load(bitmap1)
            .into(imgTest!!)
    } else {
        //中身ないってよ
        val a = false
        Log.d(TAG, "ViewModel.getは$a")
    }
}

 */