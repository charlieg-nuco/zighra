package com.zighra.kineticsdkquickstartexample;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zighra.kineticlib.API.Kinetic;
import com.zighra.kineticlib.API.KineticFactory;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG  =  "LoginActivity";

    private EditText mUserNameEditText;
    private EditText mUCodeEditText;
    private Button mLoginButton;
    private Button mSwipeAuthButton;
    private Activity mActivity;

    private Kinetic mKinetic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mActivity = this;

        mUserNameEditText = (EditText) findViewById(R.id.uName);
        mUCodeEditText = (EditText) findViewById(R.id.uCode);
        mLoginButton = (Button) findViewById(R.id.login);
        mSwipeAuthButton = (Button) findViewById(R.id.swipe);
        mKinetic = KineticFactory.getKinetic(this);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });
        mSwipeAuthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gestureAuthIntent = new Intent();
                gestureAuthIntent = new Intent(getApplicationContext(), GestureSwipeAuthActivity.class);
                startActivity(gestureAuthIntent);
            }
        });
    }

     private void loginUser() {

        String uName = mUserNameEditText.getText().toString();
        String uCode = mUCodeEditText.getText().toString();

        if (!uName.isEmpty() && !uCode.isEmpty() && mKinetic != null) {
            //User Profile
            mKinetic.setProfile(uName, uName,
                    new Kinetic.OnSetProfileSuccessListener() {
                        @Override
                        public void onSuccess(Kinetic.ProfileResponse profileResponse) {
                            performDeviceCheck();
                            mSwipeAuthButton.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorPrimary));
                            mSwipeAuthButton.setEnabled(true);
                        }},
                    new Kinetic.OnSetProfileFailureListener() {
                        @Override
                        public void onFailure(Kinetic.ProfileResponse profileResponse) {
                            Toast.makeText(getApplicationContext(), "Create Profile Failed" , Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }

    private void performDeviceCheck() {

        if(mKinetic != null ) {
            //Device Checks
            mKinetic.checkDevice(new Kinetic.OnCheckDeviceSuccessListener() {
                @Override
                public void onSuccess(Kinetic.CheckDeviceResponse checkDeviceResponse) {
                    Log.d(TAG, "Check Device Success, response = " + checkDeviceResponse.getStatus().name());
                }
            }, new Kinetic.OnCheckDeviceFailureListener() {
                @Override
                public void onFailure(Kinetic.CheckDeviceResponse checkDeviceResponse) {
                    if(checkDeviceResponse == null) {
                        Toast.makeText(getApplicationContext(), "Check Device failed" + " No profile Set", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d(TAG, "Check Device failed, response = " + checkDeviceResponse.getRawErrors().toString());
                    }
                }
            });
        }
    }



}
