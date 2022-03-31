package com.example.CapstonDesign

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
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

class Logout: Fragment() {

//    internal interface OnButtonClickListener {
//        fun onJoinButtonClick()
//        fun onResetPwClick()
//        fun onSignClick()
//    }
//
//    var db = FirebaseFirestore.getInstance()
//    private var mListener: OnButtonClickListener? = null
//    private var naverLoginButton: OAuthLoginButton? = null
//    private var kakaoLoginButton: View? = null
//    private var loginButton: Button? = null
//    private var tvJoin : TextView? = null
//    val mOAuthLogin = OAuthLogin.getInstance()
//    private var resetPwButton: TextView? = null
//    private var mContext: Context? = null
//    private var btnLogout : Button? = null
//    var mOauthLoginHandler: OAuthLoginHandler? = null
////    private var email : EditText? = null
////    private var passw : EditText? = null
//    private var stringEmail : String = ""
//    private var stringPsw : String = ""
//    private var isEmailEmpty : Boolean = true
//    private var isPswEmpty : Boolean = true
//    private var documentExist : Boolean = false
//
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        this.mListener = requireContext() as OnButtonClickListener
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        //네이버 아이디로 로그인 인스턴스를 초기화
//        mContext = requireContext()
//        mOAuthLogin.init(mContext,
//            getString(R.string.naver_client_id),
//            getString(R.string.naver_client_secret),
//            getString(R.string.naver_client_name))
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?,
//    ): View? {
//        FirebaseApp.initializeApp(requireContext())
//        val view = inflater.inflate(R.layout.container, false)
//        kakaoLoginButton = view.findViewById(R.id.btnKakaoLogin)
//        naverLoginButton = view.findViewById(R.id.btnNaverLogin)
//        loginButton = view.findViewById(R.id.btnLogin)
//        tvJoin = view.findViewById(R.id.tvJoin)
//        resetPwButton = view.findViewById(R.id.tvResetPw)
//        var email : EditText = view.findViewById(R.id.email)    //이메일
//        var passw : EditText = view.findViewById(R.id.passw)    //비밀번호
//
//
//        logoutTv?.setOnClickListener {
//            if (sns != null){
//                mOAuthLogin.logout(mContext)
//                kakaoLogout()
//                LoginManager.getInstance().logOut()
//
//                when (sns) {
//                    "naver" -> {
//                        naverLogout()
//                        val outIntent = Intent(this, MainActivity::class.java)
//                        startActivity(outIntent)
//                        overridePendingTransition(R.anim.horizon_enter2, R.anim.horizon_exit2)
//                        finish()
//                    }
//                    "kakao" -> {
//                        kakaoLoginCheck()
//                        val outIntent = Intent(this, MainActivity::class.java)
//                        startActivity(outIntent)
//                        overridePendingTransition(R.anim.horizon_enter2, R.anim.horizon_exit2)
//                        finish()
//                    }
//                    "facebook" -> {
//                        facebookLoginCheck()
//                        val outIntent = Intent(this, MainActivity::class.java)
//                        startActivity(outIntent)
//                        overridePendingTransition(R.anim.horizon_enter2, R.anim.horizon_exit2)
//                        finish()
//                    }
//                }
//
//                kakaoLoginButton.isEnabled = true
//                naverLoginButton.isEnabled = true
//                autoLogin.isEnabled = true
//                faceBookBtn.isEnabled = true
//                loginTv.text = "로그인"
//                logoutTv?.visibility = View.INVISIBLE
//            }
//            val auto = getSharedPreferences("auto", AppCompatActivity.MODE_PRIVATE)
//            val editor = auto.edit()
//            editor.putBoolean("autoLogin",false)
//            editor.clear()
//            editor.commit()
//
//
//        }
//
//
//
//    }
//
//    private fun init() {
//        loginButton!!.setOnClickListener {
//            openMap()
//            //getEmail()
//        }
////        kakaoLoginButton!!.setOnClickListener {
////
////            //로그인 정보가 있다면
////            if (AuthApiClient.instance.hasToken()) {
////                UserApiClient.instance.accessTokenInfo { _, error ->
////                    if (error != null) {
////                        if (error is KakaoSdkError && error.isInvalidTokenError()) {
////                            //로그인 필요
////                            kakaoLogin()
////                        }
////                        else {
////                            //기타 에러
////                            kakaoLogout()
////                        }
////                    }
////                    else {
////                        //토큰 유효성 체크 성공(필요 시 토큰 갱신됨)
////                        //openMap()
////                    }
////                }
////            }
////            //토큰이 없다면
////            else {
////                kakaoLogin()
////                //로그인 필요
////            }
////        } //카카오 로그인 클릭 시
//
//
//
//        btnLogout!!.setOnClickListener {
//
//            //카카오 로그아웃
//            kakaoLogout()
//
//            //네이버 로그아웃
//
////            if (mOAuthLogin != null) {
////                DeleteTokenTask(mContext, mOAuthLogin).execute()
////                Toast.makeText(mContext, "로그아웃 하셨습니다.", Toast.LENGTH_SHORT).show()
////            }
////              자바 코드
////            if (mOAuthLoginModule!=null){
////                new DeleteTokenTask(mContext, mOAuthLoginModule).execute();
////                Toast.makeText(mContext, "로그아웃 하셨습니다." , Toast.LENGTH_SHORT).show();
////            }
//
//        }
//    }
//
//    fun kakaoLogin(){
//        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
//            if (error != null) {
//                Toast.makeText(context, "카카오 로그인 실패", Toast.LENGTH_SHORT).show()
//                Log.e(ContentValues.TAG, "로그인 실패", error)
//            } else if (token != null) {
//                Toast.makeText(context, "카카오 로그인 성공", Toast.LENGTH_SHORT).show()
//                Log.i(ContentValues.TAG, "로그인 성공 ${token.accessToken}")
//                getKakaoId()
//
//            }
//        }
//
//        // 카카오톡이 설치되어 있으면 카카오톡으로 로그인, 아니면 카카오계정으로 로그인
//        if (UserApiClient.instance.isKakaoTalkLoginAvailable(requireContext())) {
//            UserApiClient.instance.loginWithKakaoTalk(requireContext(), callback = callback)
//        } else {
//            UserApiClient.instance.loginWithKakaoAccount(requireContext(), callback = callback)
//        }
//    }
//    fun kakaoLogout(){
//        UserApiClient.instance.logout { error ->
//            if (error != null) {
//                Toast.makeText(context, "로그아웃 실패", Toast.LENGTH_SHORT).show()
//            }
//            else {
//                Toast.makeText(context, "로그아웃 성공", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//    fun getKakaoId(){
//        var kakaoEmpty = false
//        var strE = ""
//        UserApiClient.instance.me { user, error ->
//            if (error != null) {
//                Log.e(ContentValues.TAG, "사용자 정보 요청 실패", error)
//                Log.d("kakao", "사용자 정보 요청 실패")
//            }
//            else if (user != null) {
//                Toast.makeText(context, "사용자 정보 요청 성공" +
//                        "\n회원번호: ${user.id}" +
//                        "\n이메일: ${user.kakaoAccount?.email}"
//                    , Toast.LENGTH_SHORT).show()
//                strE = user.kakaoAccount?.email.toString()
//                //DB에 정보없ㅇ면 회원가입, 있으면 openmap
//                //이메일 동의 여부도 체크
//
//                db.collection("users")
//                    .whereEqualTo("email", strE)
//                    .whereEqualTo("snsType", "kakao")
//                    .get()
//                    .addOnSuccessListener { documents ->
//                        kakaoEmpty = true
//                        for (document in documents) {
//                            Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
//                            var n: String = document.data.get("nickname") as String
//
//                            UserPreferenceData.setString(activity, "autoID", document.id)
//                            UserPreferenceData.setString(activity, "userEmail", stringEmail)
//                            UserPreferenceData.setString(activity, "userNickname", n)
//                            UserPreferenceData.setString(activity, "userSNSType", "kakao")
//
//                            //홈 페이지 오픈
//                            activity!!.finish()
//                            val intent = Intent(
//                                activity,
//                                MapActivity::class.java
//                            )
//                            startActivity(intent)
//                        }
//                        //회원가입으로
//                        if (!kakaoEmpty){
//                            //open joinMembershipFragment
//                            val fragment = JoinMembershipFragment()  // Fragment 생성
//                            val bundle = Bundle()
//                            bundle.putString("newEmail", strE)  // Key, Value
//                            bundle.putString("newSNS", "kakao")  // Key, Value
//                            fragment.arguments = bundle
//
//                            activity!!.supportFragmentManager.beginTransaction()
//                                .replace(R.id.fragment_container, fragment)
//                                .addToBackStack(null)
//                                .commit()
//                        }
//                    }
//                    .addOnFailureListener { exception ->
//                        Log.w(ContentValues.TAG, "Error getting documents: ", exception)
//                    }
//            }
//
//        }
//    }
//    fun openMap() {
//        if (!isPswEmpty && !isEmailEmpty) {         //임시 if문 -> 원래는 kakao나 naver를 통해 snsLogin만 걸러내면 됨
//            Log.d("uuuuuuseremail", "$stringEmail")
//            Log.d("uuuuuuserpsw", "$stringPsw")
//
//            if (!documentExist) {
//                Toast.makeText(requireContext(), "계정 정보가 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
//            }else{
//                //홈 페이지 오픈
//                activity!!.finish()
//                val intent = Intent(
//                    activity,
//                    MapActivity::class.java
//                )
//                startActivity(intent)
//            }
//
//            //myToast()
//
//        }else {
//            activity!!.finish()
//            val intent = Intent(
//                activity,
//                MapActivity::class.java
//            )
//            startActivity(intent)
//        }
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        Log.e(ContentValues.TAG,"onActivity result = ${requestCode}, reseltcode=${resultCode}")
//    }
//
////    private fun getEmail() {
////        db.collection("users")
////            .whereEqualTo("email", "dbeotkd@gmail.com")
////            .get()
////            .addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->
////                if (task.isSuccessful) {
////                    for (document in task.result) {
////                        Log.d("🐣🐣",""+document.id + " => " + document.data)
////                    }
////                } else {
////                    Toast.makeText(requireContext(),
////                        "Error getting documents: " + task.exception,
////                        Toast.LENGTH_SHORT).show()
////                }
////            })
////    }
//

}