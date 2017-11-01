package com.packt.tellastory.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.google.android.gms.tasks.OnCompleteListener
import java.util.concurrent.TimeUnit
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import kotlinx.android.synthetic.main.activity_onboarding.*
import com.twitter.sdk.android.core.TwitterException
import com.twitter.sdk.android.core.Callback
import com.twitter.sdk.android.core.Result
import com.twitter.sdk.android.core.TwitterSession
import com.twitter.sdk.android.tweetcomposer.TweetComposer
import com.twitter.sdk.android.core.TwitterCore
import io.fabric.sdk.android.Fabric
import com.twitter.sdk.android.Twitter
import com.twitter.sdk.android.core.TwitterAuthConfig
import android.content.Intent
import com.google.firebase.auth.TwitterAuthProvider
import com.google.firebase.auth.AuthCredential
import com.packt.tellastory.AuthenticationHelper
import com.packt.tellastory.R
import com.packt.tellastory.models.Story
import android.app.Activity

class OnboardingActivity : AppCompatActivity() {

    companion object {
        val ARG_LATE = "ARG_LATE"
        val ARG_STORY = "ARG_STORY"
    }

    private val mAuth = FirebaseAuth.getInstance();
    private var mVerificationId:String? = null
    private var mResendToken: PhoneAuthProvider.ForceResendingToken? = null
    private var mTwitterSession: TwitterSession? = null
    private var mIsLateOnboarding : Boolean = false
    private var mStory: Story? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent?.extras != null) {
            mIsLateOnboarding = if (intent.extras.containsKey(ARG_LATE)) intent.extras.getBoolean(ARG_LATE) else false
            mStory = if (intent.extras.containsKey(ARG_STORY)) intent.extras.getParcelable(ARG_STORY) else null
        }

        initFabric()
        setContentView(R.layout.activity_onboarding)

        val currentUser = mAuth.currentUser
        if (currentUser != null){
            Log.i(javaClass.simpleName, "already logged in as ${currentUser.phoneNumber}")
        }

        onboarding_send_phone_button.setOnClickListener { sendPhone() }
        onboarding_send_code_button.setOnClickListener { sendCode() }
        onboarding_retry_button.setOnClickListener { retry() }
        onboarding_skip_button.setOnClickListener { skip() }

        setupTwitterLoginButton()

        onboarding_phone_layout.visibility = View.VISIBLE
        onboarding_code_layout.visibility = View.GONE

        showLateOnboarding(mIsLateOnboarding)
    }

    private fun showLateOnboarding(show: Boolean){
        onboarding_late_text.visibility = if (show) View.VISIBLE else View.GONE
        onboarding_skip_button.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun initFabric(){
        val authConfig = TwitterAuthConfig(getString(R.string.twitter_key), getString(R.string.twitter_secret))
        Fabric.with(this, Twitter(authConfig))
        Fabric.with(this, TwitterCore(authConfig), TweetComposer())
    }

    private fun setupTwitterLoginButton(){
       twitter_login_button.setCallback(object : Callback<TwitterSession>() {
            override fun success(result: Result<TwitterSession>) {
                mTwitterSession = result.data
                Log.i(javaClass.simpleName, "Twitter login @" + result.data.getUserName() + ")")

                val credential = TwitterAuthProvider.getCredential(
                        result.data.getAuthToken().token,
                        result.data.getAuthToken().secret)

                signinWithTwitterAuthCredential(credential)
            }

            override fun failure(exception: TwitterException) {
                Log.d(javaClass.simpleName, "Login with Twitter failure", exception)
            }
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        twitter_login_button.onActivityResult(requestCode, resultCode, data)
    }

    private fun sendPhone(){
        val number = onboarding_phone.text.toString()
         PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                 TimeUnit.SECONDS,
                this,
                getCallback());
    }

    private fun sendCode(){
        val verification = mVerificationId
        if (verification != null) {
            val code = onboarding_code.text.toString()
            val credential = PhoneAuthProvider.getCredential(verification, code)
            signInWithPhoneAuthCredential(credential)
        }
    }

    private fun retry(){
        onboarding_phone.setText("")
        onboarding_code.setText("")
        onboarding_phone_layout.visibility = View.VISIBLE
        onboarding_code_layout.visibility = View.GONE
    }

    private fun getCallback(): PhoneAuthProvider.OnVerificationStateChangedCallbacks {

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                 Log.i(javaClass.simpleName, "verification completed -> ${credential.toString()}")
            }

            override fun onVerificationFailed(e: FirebaseException) {
                if (e is FirebaseAuthInvalidCredentialsException) {
                    Log.i(javaClass.simpleName, "invalid request")
                } else if (e is FirebaseTooManyRequestsException) {
                    Log.i(javaClass.simpleName, "quota exceeded")
                }
            }

            override fun onCodeSent(verificationId: String?, token: PhoneAuthProvider.ForceResendingToken?) {
                Log.i(javaClass.simpleName.toString(), "on code sent ${verificationId}")
                mVerificationId = verificationId;
                mResendToken = token;
                onboarding_phone_layout.visibility = View.GONE
                onboarding_code_layout.visibility = View.VISIBLE
            }
        }
        return callbacks
    }

    private fun signinWithTwitterAuthCredential (credential: AuthCredential){
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, OnCompleteListener<AuthResult> {
                if (it.isSuccessful) {
                    AuthenticationHelper.user = it.result.user
                    Log.i(javaClass.simpleName,
                      "User logged in or registered with twitter name ${AuthenticationHelper.user?.displayName}")
                    continueFlow()
                } else {
                    if (it.exception is FirebaseAuthInvalidCredentialsException) {
                        onboarding_code_feedback_text.text = "Invalid code."
                    }
                }
            })
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, OnCompleteListener<AuthResult> {
                if (it.isSuccessful) {
                    AuthenticationHelper.user = it.result.user
                    Log.i(javaClass.simpleName,
                       "User logged in or registered with phone no ${AuthenticationHelper.user?.phoneNumber}")
                    continueFlow()
                } else {
                    if (it.exception is FirebaseAuthInvalidCredentialsException) {
                        onboarding_code_feedback_text.text = "Invalid code."
                    }
                }
            })
    }

    private fun skip(){
        continueFlow()
    }

    private fun continueFlow(){
        if (mIsLateOnboarding){
            val returnIntent = Intent()
            returnIntent.putExtra(OnboardingActivity.ARG_STORY, mStory)
            setResult(Activity.RESULT_OK, returnIntent)
            this.finish()
        }
        else {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}