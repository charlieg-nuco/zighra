package com.zighra.kineticsdkquickstartexample;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zighra.kineticlib.authenticators.KineticAuthenticator;
import com.zighra.kineticlib.helpers.KineticPlatformHelper;
import com.zighra.kineticlib.models.KineticCheckDeviceResponse;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG  =  "LoginActivity";

    private EditText mUserNameEditText;
    private EditText mUCodeEditText;
    private Button mLoginButton;

    private KineticPlatformHelper mKineticPlatformHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUserNameEditText = (EditText) findViewById(R.id.uName);
        mUCodeEditText = (EditText) findViewById(R.id.uCode);
        mLoginButton = (Button) findViewById(R.id.login);

        mKineticPlatformHelper = KineticPlatformHelper.getInstance(this);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });


        //User Profile
        mKineticPlatformHelper.setOnSetProfileSuccessListener(new KineticPlatformHelper.OnSetProfileSuccessListener() {
            @Override
            public void onSetProfileSuccess() {
               //Device Checks
               performDeviceCheck();
               goToGestureAuth();
            }

        });

        //User Profile
        mKineticPlatformHelper.setOnProfileCreationFailedListener(new KineticPlatformHelper.OnSetProfileFailedListener() {
            @Override
            public void onSetProfileFailed() {
                Toast.makeText(getApplicationContext(), "Create Profile Failed" , Toast.LENGTH_SHORT).show();

            }
        });

        //Device Checks
        mKineticPlatformHelper.setOnCheckDeviceResultListener(new KineticAuthenticator.OnCheckDeviceResultListener() {
            @Override
            public void onCheckDeviceSuccess(KineticCheckDeviceResponse kineticCheckDeviceResponse) {
                Log.d(TAG, "Check Device Success, response = " + kineticCheckDeviceResponse.getData().toString());
            }

            @Override
            public void onCheckDeviceFail(KineticCheckDeviceResponse kineticCheckDeviceResponse) {
                if(kineticCheckDeviceResponse == null) {
                    Toast.makeText(getApplicationContext(), "Check Device failed" + " No profile Set", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "Check Device failed, response = " + kineticCheckDeviceResponse.getErrors().toString());
                }
            }
        });
    }

     private void loginUser() {

        String uName = mUserNameEditText.getText().toString();
        String uCode = mUCodeEditText.getText().toString();

        if (!uName.isEmpty() && !uCode.isEmpty() && mKineticPlatformHelper != null) {
            //User Profile
            mKineticPlatformHelper.setProfile(uName, uCode);
        }
    }

    private void performDeviceCheck() {

        if(mKineticPlatformHelper != null ) {
            //Device Checks
            mKineticPlatformHelper.checkDevice();
        }
    }

    private void goToGestureAuth() {
        Intent gestureAuthIntent = new Intent();
        gestureAuthIntent = new Intent(getApplicationContext(), GestureAuthActivity.class);
        startActivity(gestureAuthIntent);
    }

}
