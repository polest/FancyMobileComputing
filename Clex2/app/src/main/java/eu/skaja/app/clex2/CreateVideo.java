package eu.skaja.app.clex2;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import org.jcodec.api.android.SequenceEncoder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.content.ContentValues.TAG;

//Wird als Objekt genutzt, welches in JEncode geladen wird.

public class CreateVideo{

    private ArrayList<String> imagePathList;
    private int fps;
    private String musicPath;
    private String filename;
    private String videoPath;
    private final String folder = "Clex";
    private static final int MAX_SAMPLE_SIZE = 256 * 1024;



    public CreateVideo(ArrayList<String> imagePathList, String musicPath, int fps) throws IOException {
        this.imagePathList = imagePathList;
        this.musicPath = musicPath;
        this.fps = fps;
        this.filename = getVideoTitle();

        try {
            //String filepath = context.getApplicationInfo().dataDir;
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), this.filename);

            //File file = new File(filepath + File.separator + folder, this.filename);

            SequenceEncoder encoder = new SequenceEncoder(file);
            // GOP size will be supported in 0.2
            //encoder.getEncoder().setKeyInterval(25);

            for (String path : this.imagePathList) {
                for(int i = 0; i < fps - 1; i++){
                    Bitmap image = BitmapFactory.decodeFile(path);
                    encoder.encodeImage(image);
                }
            }
            /*for(int j = 0; j < imagePathList.size()-1; j++){
                for(int i = 0; i < fps; i++){
                    Bitmap image = BitmapFactory.decodeFile(imagePathList.get(j));
                    encoder.encodeImage(image);
                }
            }*/

            encoder.finish();
            videoPath = file.getAbsolutePath();

            //MP4-PARSER
            //startMuxingParser(videoPath, musicPath);


            //startMuxing();
            //Toast.makeText(,"Video has been created!", Toast.LENGTH_LONG);
        }catch (IOException ex){
            ex.printStackTrace();
        }
}

    /*public void startMuxingParser(String videoPath, String soundPath) {
        try {
            H264TrackImpl h264Track = new H264TrackImpl(new FileDataSourceImpl(videoPath));
            AACTrackImpl aacTrack = new AACTrackImpl(new FileDataSourceImpl(soundPath));

            Movie movie = new Movie();
            movie.addTrack(h264Track);
            movie.addTrack(aacTrack);

            Container mp4file = new DefaultMp4Builder().build(movie);

            FileChannel fc = new FileOutputStream(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + File.separator + folder + File.separator + filename)).getChannel();
            mp4file.writeContainer(fc);
            fc.close();


        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public void startMuxing(){
        MediaMuxer muxer = null;
        MediaFormat VideoFormat = null;
        //Resources mResources = context.getResources();
        //int sourceVideo = R.raw.vid;
        String outputVideoFileName = filename;
        try {
            muxer = new MediaMuxer(outputVideoFileName, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        } catch (IOException e) {
            e.printStackTrace();
        }
        MediaExtractor extractorVideo = new MediaExtractor();
        try {
            *//*AssetFileDescriptor srcVideoFd = mResources.)
            extractorVideo.setDataSource((srcVideoFd.getFileDescriptor(), srcVideoFd.getStartOffset(), srcVideoFd.getLength());*//*
            extractorVideo.setDataSource(videoPath);
            int tracks = extractorVideo.getTrackCount();
            for (int i = 0; i < tracks; i++) {
                MediaFormat mf = extractorVideo.getTrackFormat(i);
                String mime = mf.getString(MediaFormat.KEY_MIME);
                if (mime.startsWith("video/")) {
                    extractorVideo.selectTrack(i);
                    VideoFormat = extractorVideo.getTrackFormat(i);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        MediaExtractor extractorAudio = new MediaExtractor();
        try {
            extractorAudio.setDataSource(musicPath);
            int tracks = extractorAudio.getTrackCount();
            extractorAudio.selectTrack(0);

            MediaFormat AudioFormat = extractorAudio.getTrackFormat(0);
            int audioTrackIndex = muxer.addTrack(AudioFormat);
            int videoTrackIndex = muxer.addTrack(VideoFormat);

            boolean sawEOS = false;
            boolean sawAudioEOS = false;
            int bufferSize = MAX_SAMPLE_SIZE;
            ByteBuffer dstBuf = ByteBuffer.allocate(bufferSize);
            int offset = 100;
            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            muxer.start();

            while (!sawEOS) {
                bufferInfo.offset = offset;
                bufferInfo.size = extractorVideo.readSampleData(dstBuf, offset);
                if (bufferInfo.size < 0) {
                    sawEOS = true;
                    bufferInfo.size = 0;
                } else {
                    bufferInfo.presentationTimeUs = extractorVideo.getSampleTime();
                    //bufferInfo.flags = extractorVideo.getSampleFlags();
                    int trackIndex = extractorVideo.getSampleTrackIndex();
                    muxer.writeSampleData(videoTrackIndex, dstBuf, bufferInfo);
                    extractorVideo.advance();
                }
            }
            ByteBuffer audioBuf = ByteBuffer.allocate(bufferSize);
            while (!sawAudioEOS) {
                bufferInfo.offset = offset;
                bufferInfo.size = extractorAudio.readSampleData(audioBuf, offset);
                if (bufferInfo.size < 0) {
                    sawAudioEOS = true;
                    bufferInfo.size = 0;
                } else {
                    bufferInfo.presentationTimeUs = extractorAudio.getSampleTime();
                    //bufferInfo.flags = extractorAudio.getSampleFlags();
                    int trackIndex = extractorAudio.getSampleTrackIndex();
                    muxer.writeSampleData(audioTrackIndex, audioBuf, bufferInfo);
                    extractorAudio.advance();
                }
            }
            muxer.stop();
            muxer.release();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }*/

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
