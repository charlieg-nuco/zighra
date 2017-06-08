package com.zighra.kineticsdkquickstartexample;


import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.zighra.kineticlib.API.Kinetic;
import com.zighra.kineticlib.API.KineticFactory;
import com.zighra.kineticlib.touch.OnKineticTouchListener;

import org.json.JSONObject;

public class GestureTapAuthActivity extends AppCompatActivity {

    private static final String TAG  =  "GestureTapAuthActivity";

    private Kinetic mKinetic;
    private Button mTap1;
    private Button mTap2;
    private Button mSubmit;

    private int mModelNotReady = 0;
    private float swipeThreshold = 70f;
    private Activity mActivity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture_tap_auth);

        mTap1 = (Button) findViewById(R.id.tap1);
        mTap2 = (Button) findViewById(R.id.tap2);
        mSubmit = (Button) findViewById(R.id.Submit);

        //Integrate Gesture Authentication
        mKinetic = KineticFactory.getKinetic(this);
        mKinetic.attachTapAuthenticator(mTap1, "TapTest", "tap1", new Kinetic.KineticTouchListener() {
            @Override
            public void onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                    final AlertDialog dialog = new AlertDialog.Builder(mActivity)
                            .setTitle("Button")
                            .setMessage("Tap1 Clicked!!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).create();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.show();
                        }
                    });
                }

            }
        });

        //Either passing null in context or no context specified means attaching button to last used context.
        mKinetic.attachTapAuthenticator(mTap2, null, "tap2", new Kinetic.KineticTouchListener() {
            @Override
            public void onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                    final AlertDialog dialog = new AlertDialog.Builder(mActivity)
                            .setTitle("Button")
                            .setMessage("Tap2 Clicked!!")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).create();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.show();
                        }
                    });
                }
            }
        });

        mKinetic.attachTapAuthenticator(mSubmit, "tap3", Kinetic.ControlType.FINISH,
                new Kinetic.OnWillAuthenticationListener() {
                    @Override
                    public void onWillAuthentication() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Authentication started", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                },
                new Kinetic.OnAuthenticationSuccessListener() {
                    @Override
                    public void onSuccess(final Kinetic.AuthenticationResponse authenticationResponse) {
                        try {
                            JSONObject data;
                            JSONObject authResponse;

                            Kinetic.AuthStatus status = authenticationResponse.getStatus();
                            switch (status) {
                                case modelNotReady:
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "Training in progress", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    mKinetic.reportActionForAuth("allow", new Kinetic.OnReportActionSuccessListener() {
                                        @Override
                                        public void onSuccess(final Kinetic.ReportResponse reportResponse) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(getApplicationContext(), "Report action successful: " + reportResponse.getReplace(), Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                        }
                                    }, new Kinetic.OnReportActionFailureListener() {
                                        @Override
                                        public void onFailure(final Kinetic.ReportResponse reportResponse) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(getApplicationContext(), "Report action failure: " + reportResponse.getErrorMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    });
                                    break;
                                case success:
                                    // Examine the gesture score (swipePer)
                                    if (authenticationResponse.getScore() > swipeThreshold) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getApplicationContext(), "Gesture authenticated", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                        //Action Reporting
                                        mKinetic.reportActionForAuth("allow", new Kinetic.OnReportActionSuccessListener() {
                                            @Override
                                            public void onSuccess(final Kinetic.ReportResponse reportResponse) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(getApplicationContext(), "Report action successful: " + reportResponse.getReplace(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        }, new Kinetic.OnReportActionFailureListener() {
                                            @Override
                                            public void onFailure(final Kinetic.ReportResponse reportResponse) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(getApplicationContext(), "Report action failed: " + reportResponse.getErrorMessage().toString(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        });
                                    } else {
                                        // Authentication failed. Present authentication UI.
                                        Log.d(TAG, "Gesture failed");
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getApplicationContext(), "Gesture not authenticated", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                    break;
                                default:
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(getApplicationContext(), "Gesture authenticated failure" + authenticationResponse.getErrorMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    break;
                            }
                        } catch (Exception e) {

                        }
                    }
                },
                new Kinetic.OnAuthenticationFailureListener() {
                    @Override
                    public void onFailure(final Kinetic.AuthenticationResponse authenticationResponse) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Gesture auth failed: " + authenticationResponse.getRawErrors().toString(), Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                },
                new Kinetic.OnDidAuthenticationListener() {
                    @Override
                    public void onDidAuthentication() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Authentication completed !!", Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                },
                new Kinetic.KineticTouchListener() {
                    @Override
                    public void onTouch(View view, MotionEvent motionEvent) {
                        if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                            final AlertDialog dialog = new AlertDialog.Builder(mActivity)
                                    .setTitle("Button")
                                    .setMessage("Submit Clicked!!")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    }).create();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    dialog.show();
                                }
                            });


                        }
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
