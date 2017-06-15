
package eu.skaja.app.clex2;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import static com.nostra13.universalimageloader.core.ImageLoader.TAG;

public class MusicPicker extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_picker);

        //final MediaPlayer catSoundMediaPlayer = MediaPlayer.create(MusicPicker.this);

        final Button playCatMeow = (Button) this.findViewById(R.id.playSound);

        playCatMeow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //catSoundMediaPlayer.start();
            }
        });

    }
}
