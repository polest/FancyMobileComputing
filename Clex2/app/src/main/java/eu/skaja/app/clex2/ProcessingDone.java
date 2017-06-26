package eu.skaja.app.clex2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class ProcessingDone extends Activity {

    private static ProgressDialog waiting;
    private CreateVideo video;
    private ArrayList<String> selectedImagesPathList;
    private String musicPath;
    private int duration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processing_done);

        //Bundle
        Bundle bundle;
        bundle = getIntent().getExtras();
        selectedImagesPathList = bundle.getStringArrayList("selectedImagesPathList");
        musicPath = bundle.getString("musicPath");
        duration = bundle.getInt("duration");

        //Dialog
        waiting = new ProgressDialog(this);
        waiting.setMessage("Video will be created");
        waiting.setIndeterminate(false);
        waiting.setCancelable(false);

        try {
            // Create video with given image paths and music path
            video = new CreateVideo(selectedImagesPathList, musicPath, 26*duration);

        } catch (IOException e) {
            e.printStackTrace();
        }

        waiting.dismiss();

        Toast.makeText(this, "Video was created in your movie folder", Toast.LENGTH_LONG).show();

        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }
}
