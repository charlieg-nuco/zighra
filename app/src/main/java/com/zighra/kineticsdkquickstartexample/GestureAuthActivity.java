package com.zighra.kineticsdkquickstartexample;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.zighra.kineticlib.authenticators.KineticAuthenticator;
import com.zighra.kineticlib.helpers.KineticPlatformHelper;
import com.zighra.kineticlib.models.KineticAuthenticationResponse;
import com.zighra.kineticlib.models.ReportActionResponse;
import com.zighra.kineticlib.touch.OnKineticTouchListener;

import org.json.JSONException;
import org.json.JSONObject;

public class GestureAuthActivity extends AppCompatActivity {

    private static final String TAG  =  "GestureAuthActivity";

    private KineticPlatformHelper mKineticPlatformHelper;
    private OnKineticTouchListener mOnKineticTouchListener;
    private Button mGestureButton;

    private int mModelNotReady = 0;
    private float swipeThreshold = 70f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_auth);

        mGestureButton = (Button) findViewById(R.id.gestureAuth);

        //Integrate Gesture Authentication
        mKineticPlatformHelper = KineticPlatformHelper.getInstance(this);
        mOnKineticTouchListener = mKineticPlatformHelper.createAutoAuthOnKineticTouchListenerWithSwipe();
        mGestureButton.setOnTouchListener(mOnKineticTouchListener);


        //Integrate Gesture Authentication - authentication listener
        mKineticPlatformHelper.setOnAuthenticationResultListener(new KineticAuthenticator.OnAuthenticationResultListener() {
            @Override
            public void onAuthenticationSuccess(KineticAuthenticationResponse kineticAuthenticationResponse) {


                try {
                    JSONObject data;
                    JSONObject authResponse;

                    data = kineticAuthenticationResponse.getData();
                    authResponse = data.getJSONArray("authResponse").getJSONObject(0);

                    // Auth model still in training mode.
                    // Call reportAction-allow to add the gesture to the model.
                    if(authResponse.getInt("shield") <= mModelNotReady) {
                        //Action Reporting
                        Toast.makeText(getApplicationContext(), "Training in progress", Toast.LENGTH_SHORT).show();
                       mKineticPlatformHelper.reportActionForAuth("allow");
                    } else if (authResponse.getInt("shield") > mModelNotReady) {
                        // Examine the gesture score (swipePer)
                        if(authResponse.getDouble("swipePer") > swipeThreshold  ) {
                            // Gesture authenticated successfully. Respond with report action-allow
                            Toast.makeText(getApplicationContext(), "Gesture authenticated", Toast.LENGTH_SHORT).show();
                            //Action Reporting
                            mKineticPlatformHelper.reportActionForAuth("allow");
                        } else if(authResponse.getDouble("swipePer") <= swipeThreshold  ){
                            // Authentication failed. Present authentication UI.
                            Log.d(TAG,"Gesture failed");
                            Toast.makeText(getApplicationContext(), "Gesture not authenticated", Toast.LENGTH_SHORT).show();
                        }

                    }



                } catch (JSONException e){

                }
            }

            @Override
            public void onAuthenticationFail(KineticAuthenticationResponse kineticAuthenticationResponse) {
                Toast.makeText(getApplicationContext(), "Gesture auth failed: " + kineticAuthenticationResponse.getErrors().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        //Action Reporting - Report action listener
        mKineticPlatformHelper.setOnReportActionResultListener(new KineticAuthenticator.OnReportActionResultListener() {

            @Override
            public void onReportActionSuccess(ReportActionResponse reportActionResponse) {
                Toast.makeText(getApplicationContext(), "Report action successful: " + reportActionResponse.getData().toString(), Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onReportActionFail(ReportActionResponse reportActionResponse) {
                Toast.makeText(getApplicationContext(), "Report action failed: " + reportActionResponse.getErrors().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Integrate Gesture Authentication
        mKineticPlatformHelper.initSensors();
    }

    @Override
    protected void onPause() {
        super.onPause();

        //Integrate Gesture Authentication
        mKineticPlatformHelper.stopSensors();
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return true;
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }


}
