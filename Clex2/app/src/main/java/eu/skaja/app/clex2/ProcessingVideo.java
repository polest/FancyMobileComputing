package eu.skaja.app.clex2;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;

public class ProcessingVideo extends Activity {

    public Button btnProcessVideo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_processing_video);

        btnProcessVideo = (Button) findViewById(R.id.btnProcessVideo);

        btnProcessVideo.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //HALLO DENNIS
                    }
                }
        );


    }
}
