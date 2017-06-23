package eu.skaja.app.clex2;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.widget.Toast;

import org.jcodec.api.android.SequenceEncoder;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

//Wird als Objekt genutzt, welches in JEncode geladen wird.
public class CreateVideo extends Activity {

    private List<String> imagePathList;
    private String musicPath;
    private String filename;
    private final String folder = "Clex";

    public CreateVideo(List<String> imagePathList, String musicPath) throws IOException {
        this.imagePathList = imagePathList;
        this.musicPath = musicPath;
        this.filename = getVideoTitle();

        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), this.filename);

            SequenceEncoder encoder = new SequenceEncoder(file);
            // GOP size will be supported in 0.2
            //encoder.getEncoder().setKeyInterval(25);

            for (String path : this.imagePathList) {
                //for(int i = 1; i <= 24; i++){
                    Bitmap image = BitmapFactory.decodeFile(path);
                    encoder.encodeImage(image);
                //}
            }
            encoder.finish();
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }

    public String getVideoTitle() {
        Date date = new Date(); // your date
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        Integer year = cal.get(Calendar.YEAR);
        Integer month = cal.get(Calendar.MONTH) + 1;
        Integer day = cal.get(Calendar.DAY_OF_MONTH);
        Integer hour = cal.get(Calendar.HOUR_OF_DAY);
        Integer minutes = cal.get(Calendar.MINUTE);
        Integer seconds = cal.get(Calendar.SECOND);
        String timestamp = year.toString() + month.toString() + day.toString() + hour.toString() + minutes.toString() + seconds.toString();
        String filename = "clex_" + timestamp + ".mp4";

        return filename;
    }
}
