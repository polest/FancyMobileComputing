package eu.skaja.app.clex2;

import android.app.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

import java.io.File;

import ly.img.android.sdk.models.constant.Directory;
import ly.img.android.sdk.models.state.CameraSettings;
import ly.img.android.sdk.models.state.EditorLoadSettings;
import ly.img.android.sdk.models.state.EditorSaveSettings;
import ly.img.android.ui.activities.CameraPreviewActivity;
import ly.img.android.ui.activities.CameraPreviewBuilder;
import ly.img.android.ui.activities.ImgLyIntent;
import ly.img.android.ui.activities.PhotoEditorBuilder;
import ly.img.android.ui.utilities.PermissionRequest;
import ly.img.android.sdk.models.state.manager.SettingsList;

// SDK Activity
public class PhotoEditor extends Activity implements PermissionRequest.Response {

    public static int CAMERA_PREVIEW_RESULT = 1;
    private String selectedImagePath;
    private int selectedImagePos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Gets the image position and path from the main activity that was selected
        Bundle b;
        b = getIntent().getExtras();
        selectedImagePath = b.getString("selectedImagePath");
        selectedImagePos = b.getInt("selectedImagePos");

        // Sets the settings like the output folder for all edited images
        SettingsList settingsList = new SettingsList();
        // Loads the selected image in the SDK
        String myPicture = selectedImagePath;
        settingsList
                .getSettingsModel(EditorLoadSettings.class)
                .setImageSourcePath(myPicture, true) // Load with delete protection true!

                .getSettingsModel(EditorSaveSettings.class)
                .setExportDir(Directory.DCIM, "Clex")
                .setExportPrefix("result_")
                .setSavePolicy(
                        EditorSaveSettings.SavePolicy.KEEP_SOURCE_AND_CREATE_ALWAYS_OUTPUT
                );

        new PhotoEditorBuilder(this)
                .setSettingsList(settingsList)
                .startActivityForResult(this, CAMERA_PREVIEW_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // When we get a result from the SDK, we handle the data and transmit the real path to the main activity
        if (resultCode == RESULT_OK && requestCode == CAMERA_PREVIEW_RESULT) {
            String resultPath =
                    data.getStringExtra(ImgLyIntent.RESULT_IMAGE_PATH);
            String sourcePath =
                    data.getStringExtra(ImgLyIntent.SOURCE_IMAGE_PATH);

            if (resultPath != null) {
                // Scan result file
                File file =  new File(resultPath);
                Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(file);
                scanIntent.setData(contentUri);
                sendBroadcast(scanIntent);
            }

            if (sourcePath != null) {
                // Scan camera file
                File file =  new File(sourcePath);
                Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(file);
                scanIntent.setData(contentUri);
                sendBroadcast(scanIntent);
            }

            // Sets the result for the main activity
            Intent returnIntent = new Intent();
            returnIntent.putExtra("getNewPath",resultPath);
            returnIntent.putExtra("getImagePos", selectedImagePos);
            setResult(Activity.RESULT_OK,returnIntent);
            finish();

        }

        finish();

    }

    // Important permission request for Android 6.0 and above
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionRequest.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // In case the permission is granted
    @Override
    public void permissionGranted() {

    }

    // IN case the permisson is denied
    @Override
    public void permissionDenied() {
        // TODO for you: Show a Hint to the User
    }


}
