package com.example.CapstonDesign

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.kakao.sdk.auth.AuthApiClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.KakaoSdkError
import com.kakao.sdk.user.UserApiClient
import com.nhn.android.naverlogin.OAuthLogin
import com.nhn.android.naverlogin.OAuthLoginHandler
import com.nhn.android.naverlogin.ui.view.OAuthLoginButton


class LoginActivity : Fragment() {

    internal interface OnButtonClickListener {
        fun onJoinButtonClick()
        fun onResetPwClick()
        fun onSignClick()
    }

    var db = FirebaseFirestore.getInstance()
    private var mListener: OnButtonClickListener? = null
    private var naverLoginButton: OAuthLoginButton? = null
    private var kakaoLoginButton: View? = null
    private var loginButton: Button? = null
    private var tvJoin : TextView? = null
    val mOAuthLogin = OAuthLogin.getInstance()
    private var resetPwButton: TextView? = null
    private var mContext: Context? = null
    private var btnLogout : Button? = null
    var mOauthLoginHandler: OAuthLoginHandler? = null
//    private var email : EditText? = null
//    private var passw : EditText? = null
    private var stringEmail : String = ""
    private var stringPsw : String = ""
    private var isEmailEmpty : Boolean = true
    private var isPswEmpty : Boolean = true
    private var documentExist : Boolean = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mListener = requireContext() as OnButtonClickListener
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //????????? ???????????? ????????? ??????????????? ?????????
        mContext = requireContext()
        mOAuthLogin.init(mContext,
            getString(R.string.naver_client_id),
            getString(R.string.naver_client_secret),
            getString(R.string.naver_client_name))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        FirebaseApp.initializeApp(requireContext())
        val view = inflater.inflate(R.layout.fragment_login_activity, container, false)
        kakaoLoginButton = view.findViewById(R.id.btnKakaoLogin)
        naverLoginButton = view.findViewById(R.id.btnNaverLogin)
        loginButton = view.findViewById(R.id.btnLogin)
        tvJoin = view.findViewById(R.id.tvJoin)
        resetPwButton = view.findViewById(R.id.tvResetPw)
        var email : EditText = view.findViewById(R.id.email)    //?????????
        var passw : EditText = view.findViewById(R.id.passw)    //????????????

        email.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!email.text.toString().isBlank()){
                    stringEmail = email.text.toString()
                    isEmailEmpty = false
                }else{
                    isEmailEmpty = true
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        passw.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (!passw.text.toString().isBlank()){
                    stringPsw = passw.text.toString()
                    isPswEmpty = false
                }else{
                    isPswEmpty = true
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        if(AuthApiClient.instance.hasToken()){
            //openMap() //??????????????? ????????? ?????? ????????????
            init()
        }else {
            init()
        }
        return view

    }

    private fun init() {
        loginButton!!.setOnClickListener {
            openMap()
            //getEmail()
        }
        kakaoLoginButton!!.setOnClickListener {

            //????????? ????????? ?????????
            if (AuthApiClient.instance.hasToken()) {
                UserApiClient.instance.accessTokenInfo { _, error ->
                    if (error != null) {
                        if (error is KakaoSdkError && error.isInvalidTokenError()) {
                            //????????? ??????
                            kakaoLogin()
                        }
                        else {
                            //?????? ??????
                            kakaoLogout()
                        }
                    }
                    else {
                        //?????? ????????? ?????? ??????(?????? ??? ?????? ?????????)
                        //openMap()
                    }
                }
            }
            //????????? ?????????
            else {
                kakaoLogin()
                //????????? ??????
            }
        } //????????? ????????? ?????? ???

        @SuppressLint("HandlerLeak")
        mOauthLoginHandler = object : OAuthLoginHandler() {
                override fun run(success: Boolean) {
                    //????????? ?????? ?????? ??????
                    if (success) {
                        //?????? ?????? ??????
                        if (mOAuthLogin?.getAccessToken(requireContext()) != null) {

                            //Toast.makeText(mContext, "?????? ????????? ????????? ??????", Toast.LENGTH_SHORT).show()
                            val accessToken = mOAuthLogin?.getAccessToken(mContext)
                            RequestApiTask(mContext, mOAuthLogin).execute()

                            //Toast.makeText(mContext, "Success : $accessToken", Toast.LENGTH_SHORT).show()
                            //Toast.makeText(mContext, mOAuthLogin!!.getTokenType(requireContext()), Toast.LENGTH_SHORT).show()

                        } else {
                            Toast.makeText(mContext, "????????????", Toast.LENGTH_SHORT).show()
                            mOAuthLogin?.init(mContext,
                                getString(R.string.naver_client_id),
                                getString(R.string.naver_client_secret),
                                getString(R.string.naver_client_name))
                        }

                    } else {
                        Toast.makeText(mContext, "????????? ????????? ??????", Toast.LENGTH_SHORT).show()
                        val errorCode = mOAuthLogin?.getLastErrorCode(mContext)?.getCode();
                        val errorDesc = mOAuthLogin?.getLastErrorDesc(mContext);
                        Toast.makeText(mContext, "errorCode:$errorCode, errorDesc:$errorDesc", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        naverLoginButton!!.setOAuthLoginHandler(mOauthLoginHandler)
        tvJoin!!.setOnClickListener { if (mListener != null) mListener!!.onJoinButtonClick() }

        resetPwButton!!.setOnClickListener {
//            //?????? ???????????? ?????? - kakao
//            kakaoLogout()
//
//            //?????? ???????????? ?????? - naver
//            if (mOAuthLogin != null) {
//                DeleteTokenTask(mContext, mOAuthLogin).execute()
//                //Toast.makeText(mContext, "???????????? ???????????????.", Toast.LENGTH_SHORT).show()
//            }

            if (mListener != null) mListener!!.onResetPwClick()
        }

//        btnLogout!!.setOnClickListener {
//
//            //????????? ????????????
//            kakaoLogout()
//
//            //????????? ????????????
//
////            if (mOAuthLogin != null) {
////                DeleteTokenTask(mContext, mOAuthLogin).execute()
////                Toast.makeText(mContext, "???????????? ???????????????.", Toast.LENGTH_SHORT).show()
////            }
////              ?????? ??????
////            if (mOAuthLoginModule!=null){
////                new DeleteTokenTask(mContext, mOAuthLoginModule).execute();
////                Toast.makeText(mContext, "???????????? ???????????????." , Toast.LENGTH_SHORT).show();
////            }
//
//        }
    }

    fun kakaoLogin(){
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                Toast.makeText(context, "????????? ????????? ??????", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "????????? ??????", error)
            } else if (token != null) {
                Toast.makeText(context, "????????? ????????? ??????", Toast.LENGTH_SHORT).show()
                Log.i(TAG, "????????? ?????? ${token.accessToken}")
                getKakaoId()

            }
        }
        // ??????????????? ???????????? ????????? ?????????????????? ?????????, ????????? ????????????????????? ?????????
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(requireContext())) {
            UserApiClient.instance.loginWithKakaoTalk(requireContext(), callback = callback)
        } else {
            UserApiClient.instance.loginWithKakaoAccount(requireContext(), callback = callback)
        }
    }

    fun kakaoLogout(){
        UserApiClient.instance.logout { error ->
            if (error != null) {
                Toast.makeText(context, "???????????? ??????", Toast.LENGTH_SHORT).show()
            }
            else {
                Toast.makeText(context, "???????????? ??????", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun getKakaoId(){
        var kakaoEmpty = false
        var strE : String = ""
        var strE2 : String = ""

        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.e(TAG, "????????? ?????? ?????? ??????", error)
                Log.d("kakao", "????????? ?????? ?????? ??????")
            }
            else if (user != null) {
//                Toast.makeText(context, "????????? ?????? ?????? ??????" +
//                        "\n????????????: ${user.id}" +
//                        "\n?????????: ${user.kakaoAccount?.email}"
//                        , Toast.LENGTH_SHORT).show()
                strE = user.kakaoAccount?.email.toString()
                strE2 = user.id.toString()

                //DB??? ??????????????? ????????????, ????????? openmap, ????????? ?????? ????????? ??????
                db.collection("users")
                    .whereEqualTo("snsID", strE2)
                    .whereEqualTo("snsType", "kakao")
                    .get()
                    .addOnSuccessListener { documents ->

                        for (document in documents) {
                            kakaoEmpty = true

                            Log.d(TAG, "${document.id} => ${document.data}")
                            var n: String = document.data.get("nickname") as String
                            var n2: String = document.data.get("email") as String
                           // Log.d("kakaokakao", ""+ n2)

                            UserPreferenceData.setString(activity, "autoID", document.id)
                            UserPreferenceData.setString(activity, "userEmail", n2)
                            UserPreferenceData.setString(activity, "userNickname", n)
                            UserPreferenceData.setString(activity, "userSNSType", "kakao")

                            //??? ????????? ??????
                            activity!!.finish()
                            val intent = Intent(
                                activity,
                                MapActivity::class.java
                            )
                            startActivity(intent)
                        }

                        //??????????????????
                        if (!kakaoEmpty){
                            //open joinMembershipFragment
                            val fragment = JoinMembershipFragment()  // Fragment ??????
                            val bundle = Bundle()
                            bundle.putString("newEmail", strE)  // Key, Value
                            bundle.putString("newKakaoID", strE2)
                            bundle.putString("newSNS", "kakao")  // Key, Value
                            fragment.arguments = bundle

                            activity!!.supportFragmentManager.beginTransaction()
                                .replace(R.id.fragment_container, fragment)
                                .addToBackStack(null)
                                .commit()
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.w(TAG, "Error getting documents: ", exception)
                    }
            }
        }
    }

    fun openMap() {
        if (!isPswEmpty && !isEmailEmpty) {         //?????? if??? -> ????????? kakao??? naver??? ?????? snsLogin??? ???????????? ???
            Log.d("uuuuuuseremail", "$stringEmail")
            Log.d("uuuuuuserpsw", "$stringPsw")

            db.collection("users")
                .whereEqualTo("email", stringEmail)
                .whereEqualTo("password", stringPsw)
                .whereEqualTo("snsType", "normal")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        documentExist = true

                        Log.d(TAG, "${document.id} => ${document.data}")
                        var n: String = document.data.get("nickname") as String
                        UserPreferenceData.setString(activity, "autoID", document.id)
                        UserPreferenceData.setString(activity, "userEmail", stringEmail)
                        UserPreferenceData.setString(activity, "userNickname", n)
                        UserPreferenceData.setString(activity, "userSNSType", "normal")

                        //??? ????????? ??????
                        activity!!.finish()
                        val intent = Intent(
                            activity,
                            MapActivity::class.java
                        )
                        startActivity(intent)
                    }
                    if (!documentExist) {
                        Toast.makeText(requireContext(), "?????? ????????? ???????????? ????????????.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.e(TAG,"onActivity result = ${requestCode}, reseltcode=${resultCode}")
    }

//    private fun getEmail() {
//        db.collection("users")
//            .whereEqualTo("email", "dbeotkd@gmail.com")
//            .get()
//            .addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->
//                if (task.isSuccessful) {
//                    for (document in task.result) {
//                        Log.d("????????",""+document.id + " => " + document.data)
//                    }
//                } else {
//                    Toast.makeText(requireContext(),
//                        "Error getting documents: " + task.exception,
//                        Toast.LENGTH_SHORT).show()
//                }
//            })
//    }
}