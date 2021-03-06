
package eu.skaja.app.clex2;


import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class MusicPicker extends Activity {

    private ListView musicList;
    private MediaPlayer mPlayer = null;
    private Button btnConfirmMusic;
    Uri musicPath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_picker);

        btnConfirmMusic = (Button)findViewById(R.id.btnConfirmMusic);
        btnConfirmMusic.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // When the confirm button is clicked set the result for the main activity
                        if(musicPath != null) {
                            Toast.makeText(MusicPicker.this, musicPath.toString(), Toast.LENGTH_LONG).show();
                            mPlayer.reset();
                            Intent intent = new Intent(MusicPicker.this, MainActivity.class);
                            intent.putExtra("selectedMusicPath", musicPath);
                            setResult(Activity.RESULT_OK, intent);
                        }
                        finish();
                    }
                }
        );

        musicList = (ListView)findViewById(R.id.musicList);

        // List of all intern music names
        String[] values = new String[] {
                "Africa",
                "Hip Hop Japanese",
                "Honorable Battle",
                "Inspirational",
                "Japanese Jingle",
                "Jazz Trio",
                "Not What It Seems",
                "Sadly",
                "Shoulder Angel (no vocals)",
                "Shoulder Angel (vocals)",
                "The Build Up",
                "Traditional Japanese",
                "Traditional Japanese 2",
                "Ufo Slowing",
                "Ufo Speeding",
                "World War 3",
                "Yegods",
                "Zombie Farm"
        };

        // List of all intern music files
        final Integer[] mp3s = new Integer[] {
                R.raw.africa,
                R.raw.hip_hop_japanese,
                R.raw.honorable_battle,
                R.raw.inspirational,
                R.raw.japanese_jingle,
                R.raw.jazz_trio,
                R.raw.not_what_it_seems,
                R.raw.sadly,
                R.raw.shoulder_angel_no_vocals,
                R.raw.shoulder_angel_with_vocals,
                R.raw.the_build_up,
                R.raw.traditional_japanese,
                R.raw.traditional_japanese_2,
                R.raw.ufo_slowing,
                R.raw.ufo_speeding,
                R.raw.world_war_3,
                R.raw.yegods,
                R.raw.zombie_farm
        };

        // Create the adapter
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
                int itemPosition = position;

                // ListView Clicked item value
                //uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.usa_for_africa_we_are_the_world);
                musicPath = Uri.parse("android.resource://"+getPackageName()+"/raw/" + mp3s[itemPosition] + ".aac");

                // Starts to play the music
                mPlayer = MediaPlayer.create(MusicPicker.this, mp3s[position]);
                mPlayer.start();
            }

        });



    }

    @Override
    public void onBackPressed() {
        // When the back button is pressed, the music will stop
        mPlayer.stop();
        finish();
    }


}
