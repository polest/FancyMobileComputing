package eu.skaja.app.clex2;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Environment;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.FileDataSourceImpl;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.tracks.AACTrackImpl;
import com.googlecode.mp4parser.authoring.tracks.h264.H264TrackImpl;

import org.jcodec.api.android.SequenceEncoder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CreateVideo{

    private ArrayList<String> imagePathList;
    private int fps;
    private String musicPath;
    private String filename;
    private String videoPath;

    public CreateVideo(ArrayList<String> imagePathList, String musicPath, int fps) throws IOException {
        this.imagePathList = imagePathList;
        this.musicPath = musicPath;
        this.fps = fps;
        this.filename = getVideoTitle();

        try {
            // Sets the output directory for the video
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), this.filename);

            // Sets the Sequence Encoder
            SequenceEncoder encoder = new SequenceEncoder(file);

            // Gets all images from the GridView
            for (String path : this.imagePathList) {
                for(int i = 0; i < fps - 1; i++){
                    Bitmap image = BitmapFactory.decodeFile(path);
                    encoder.encodeImage(image);
                }
            }

            // Closes the encoder
            encoder.finish();

            // Saves the absolute video path
            videoPath = file.getAbsolutePath();

        }catch (IOException ex){
            ex.printStackTrace();
        }
    }

    // Our tries to muxing the audio into the video
    /*
    public void startMuxingParser(String videoPath, String soundPath) {
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

    }
    */



    // returns the defined video details
    public String getVideoTitle() {
        Date date = new Date();
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
