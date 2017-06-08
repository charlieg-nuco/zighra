package com.zighra.kineticsdkquickstartexample;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;


import com.zighra.kineticlib.API.Kinetic;
import com.zighra.kineticlib.API.KineticFactory;

import org.json.JSONObject;

public class GestureSwipeAuthActivity extends AppCompatActivity {

    private static final String TAG  =  "SwipeAuth";

    private Kinetic mKinetic;
    private Button mGestureButton;

    private int mModelNotReady = 0;
    private float swipeThreshold = 70f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_swipe_auth);

        mGestureButton = (Button) findViewById(R.id.gestureAuth);

        //Integrate Gesture Authentication
        mKinetic = KineticFactory.getKinetic(this);
        mKinetic.attachSwipeAuthenticator(mGestureButton,
                new Kinetic.OnAuthenticationSuccessListener() {
                     @Override
                     public void onSuccess(Kinetic.AuthenticationResponse authenticationResponse) {
                     try {
                         Kinetic.AuthStatus status = authenticationResponse.getStatus();
                         switch (status){
                           case  modelNotReady:
                                Toast.makeText(getApplicationContext(), "Training in progress", Toast.LENGTH_SHORT).show();
                                mKinetic.reportActionForAuth("allow", new Kinetic.OnReportActionSuccessListener() {
                                    @Override
                                    public void onSuccess(Kinetic.ReportResponse reportResponse) {
                                        Toast.makeText(getApplicationContext(), "Report action successful: " + reportResponse.getStatus(), Toast.LENGTH_SHORT).show();
                                    }
                                }, new Kinetic.OnReportActionFailureListener() {
                                    @Override
                                    public void onFailure(Kinetic.ReportResponse reportResponse) {

                                    }
                                });
                            break;
                            case success:
                                // Examine the gesture score
                                if(authenticationResponse.getScore() > swipeThreshold  ) {
                                    // Gesture authenticated successfully. Respond with report action-allow
                                    Toast.makeText(getApplicationContext(), "Gesture authenticated", Toast.LENGTH_SHORT).show();
                                    //Action Reporting
                                    mKinetic.reportActionForAuth("allow", new Kinetic.OnReportActionSuccessListener() {
                                        @Override
                                        public void onSuccess(Kinetic.ReportResponse reportResponse) {
                                            Toast.makeText(getApplicationContext(), "Report action successful: " + reportResponse.getStatus(), Toast.LENGTH_SHORT).show();

                                        }
                                    }, new Kinetic.OnReportActionFailureListener() {
                                        @Override
                                        public void onFailure(Kinetic.ReportResponse reportResponse) {
                                            Toast.makeText(getApplicationContext(), "Report action failed: " + reportResponse.getErrorMessage().toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    // Authentication failed. Present authentication UI.
                                    Log.d(TAG, "Gesture failed");
                                    Toast.makeText(getApplicationContext(), "Gesture not authenticated", Toast.LENGTH_SHORT).show();
                                }
                            break;
                        default:
                            Toast.makeText(getApplicationContext(), "Gesture authenticated failure"+authenticationResponse.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e){

                }
            }
        }, new Kinetic.OnAuthenticationFailureListener() {
            @Override
            public void onFailure(Kinetic.AuthenticationResponse authenticationResponse) {
                Toast.makeText(getApplicationContext(), "Gesture auth failed: " + authenticationResponse.getRawErrors().toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Integrate Gesture Authentication
        mKinetic.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        //Integrate Gesture Authentication
        mKinetic.onPause();
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
