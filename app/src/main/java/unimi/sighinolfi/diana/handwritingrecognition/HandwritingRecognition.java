package unimi.sighinolfi.diana.handwritingrecognition;

import android.gesture.Gesture;
import android.gesture.GestureOverlayView;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class HandwritingRecognition extends AppCompatActivity implements GestureOverlayView.OnGesturePerformedListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handwriting_recognition);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_title);
        setSupportActionBar(toolbar);

        GestureOverlayView overlay = (GestureOverlayView) findViewById(R.id.gestureOverlayView);
        //Setto il listner associandolo a questa classe.
        overlay.addOnGesturePerformedListener(this);
        overlay.setDrawingCacheEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_handwriting_recognition, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        //Creo un toast (messaggio in stile pop-up) alla fine della scrittura a schermo.
        Toast.makeText(this, "Fatto", Toast.LENGTH_SHORT)
                .show();

        Bitmap b = Bitmap.createBitmap(overlay.getDrawingCache());
        File f = new File(Environment.getExternalStorageDirectory()+ File.separator + "lettera.jpg");
        try {
            f.createNewFile();
            FileOutputStream os = new FileOutputStream(f);
            b.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
