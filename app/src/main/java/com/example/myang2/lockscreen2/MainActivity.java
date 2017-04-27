package com.example.myang2.lockscreen2;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends Activity {
    private MyGLSurfaceView mGLView;
    private static String[] fileNames = {
            "airplane.ply", "ant.ply", "apple.ply", "balance.ply","beethoven.ply", "big_atc.ply",
            "big_dodge.ply", "big_porsche.ply", "big_spider.ply", "canstick.ply", "chopper.ply", "cow.ply",
            "dolphins.ply", "egret.ply", "f16.ply", "footbones.ply", "fracttree.ply", "galleon.ply",
            "hammerhead.ply", "helix.ply", "hind.ply", "kerolamp.ply", "ketchup.ply", "mug.ply",
            "part.ply", "pickup_big.ply", "pump.ply", "pumpa_tb.ply", "sandal.ply", "saratoga.ply",
            "scissors.ply", "shark.ply", "steeringweel.ply", "stratocaster.ply", "street_lamp.ply", "teapot.ply",
            "tennis_shoe.ply", "tommygun.ply", "trashcan.ply", "turbine.ply","urn2.ply","walkman.ply", "weathervane.ply"
    };

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private int index = 0;
    //private float[] maxMin = new float[6];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final LinearLayout layout = (LinearLayout) findViewById(R.id.layout);

        mGLView = new MyGLSurfaceView(this);
        layout.addView(mGLView);

        try {
            InputStream in = getAssets().open(fileNames[index]);
            PlyReader plyReader = new PlyReader(in);
            plyReader.ParsePly();
            mGLView.setFaces(plyReader.getFaces());
            mGLView.setColors(plyReader.getVertices().length);
            mGLView.setFile(index);
            mGLView.setMaxMin(plyReader.getMaxMin());
            mGLView.setVertices(plyReader.getVertices());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Button button = (Button) findViewById(R.id.button);
        button.setText("UnLock");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unlockScreen(v);
            }
        });

        makeFullScreen();
        startService(new Intent(this, LockScreenService.class));
        layout.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            public void onSwipeRight() {
                //mGLView = new MyGLSurfaceView(MainActivity.this);
                if (index < 42) {
                    index += 1;
                } else {
                    index = 0;
                }
                try {
                    InputStream in = getAssets().open(fileNames[index]);
                    PlyReader plyReader1 = new PlyReader(in);
                    plyReader1.ParsePly();
                    mGLView.setFaces(plyReader1.getFaces());
                    mGLView.setColors(plyReader1.getVertices().length);
                    mGLView.setFile(index);
                    mGLView.setMaxMin(plyReader1.getMaxMin());
                    mGLView.setVertices(plyReader1.getVertices());
                    //layout.addView(mGLView);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(MainActivity.this, "right" + index + fileNames[index], Toast.LENGTH_SHORT).show();
            }
            public void onSwipeLeft() {
                if (index > 0) {
                    index -= 1;
                } else {
                    index = 42;
                }
                try {
                    InputStream in = getAssets().open(fileNames[index]);
                    PlyReader plyReader1 = new PlyReader(in);
                    plyReader1.ParsePly();
                    mGLView.setFaces(plyReader1.getFaces());
                    mGLView.setFile(index);
                    mGLView.setMaxMin(plyReader1.getMaxMin());
                    mGLView.setVertices(plyReader1.getVertices());
                    //layout.addView(mGLView);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(MainActivity.this, "left" + index + fileNames[index], Toast.LENGTH_SHORT).show();
            }
        });

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /**
     * A simple method that sets the screen to fullscreen.  It removes the Notifications bar,
     *   the Actionbar and the virtual keys (if they are on the phone)
     */
    public void makeFullScreen() {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if(Build.VERSION.SDK_INT < 19) { //View.SYSTEM_UI_FLAG_IMMERSIVE is only on API 19+
            this.getWindow().getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        } else {
            this.getWindow().getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }
    }

    public void unlockScreen(View view) {
        //Instead of using finish(), this totally destroys the process
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    protected void onPause() {
        super.onPause();
        // The following call pauses the rendering thread.
        // If your OpenGL application is memory intensive,
        // you should consider de-allocating objects that
        // consume significant memory here.
        mGLView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // The following call resumes a paused rendering thread.
        // If you de-allocated graphic objects for onPause()
        // this is a good place to re-allocate them.
        mGLView.onResume();
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
