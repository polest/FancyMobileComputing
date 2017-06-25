
package eu.skaja.app.clex2;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import static android.R.id.list;
import static com.nostra13.universalimageloader.core.ImageLoader.TAG;

public class MusicPicker extends Activity {

    private ListView musicList;
    private MediaPlayer mPlayer = null;
    private Button btnConfirmMusic;
    private String musicPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_picker);

        btnConfirmMusic = (Button)findViewById(R.id.btnConfirmMusic);
        btnConfirmMusic.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MusicPicker.this, musicPath, Toast.LENGTH_SHORT).show();
                        mPlayer.reset();
                        Intent intent = new Intent(MusicPicker.this, MainActivity.class);
                        intent.putExtra("selectedMusicPath", musicPath);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                }
        );

        musicList = (ListView)findViewById(R.id.musicList);

        String[] values = new String[] {
                "Inspirational",
                "Hearthbeat",
                "Sadly",
                "TheBuildup",
                "Inspirational",
                "Hearthbeat",
                "Sadly",
                "TheBuildup",
                "Inspirational",
                "Hearthbeat",
                "Sadly",
                "TheBuildup",
                "Inspirational",
                "Hearthbeat",
                "Sadly",
                "TheBuildup",
                "Inspirational",
                "Hearthbeat",
                "Sadly",
                "TheBuildup",
                "Inspirational",
                "Hearthbeat",
                "Sadly",
                "TheBuildup"

        };
        final Integer[] mp3s = new Integer[] {
                R.raw.inspirational,
                R.raw.heartbeat_full,
                R.raw.sadly,
                R.raw.thebuildup
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.list_item, R.id.nameOfTitle, values);

        // Assign adapter to ListView
        musicList.setAdapter(adapter);

        musicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                if(mPlayer != null)
                    mPlayer.reset();

                // ListView Clicked item index
                int itemPosition     = position;

                // ListView Clicked item value
                musicPath    = (String) musicList.getItemAtPosition(position);

                mPlayer = MediaPlayer.create(MusicPicker.this, mp3s[position]);
                mPlayer.start();
            }

        });



    }
}
