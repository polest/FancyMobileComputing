package eu.skaja.app.clex2;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class MainActivity extends Activity {

	GridView gridGallery;
	Handler handler;
	GalleryAdapter adapter;

	ImageView imgSinglePick;
	Button btnGalleryPick;
	Button btnGalleryPickMul;

	String action;
	ViewSwitcher viewSwitcher;
	ImageLoader imageLoader;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);

		initImageLoader();
		init();
	}

	private void initImageLoader() {
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.cacheOnDisc().imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
				.bitmapConfig(Bitmap.Config.RGB_565).build();
		ImageLoaderConfiguration.Builder builder = new ImageLoaderConfiguration.Builder(
				this).defaultDisplayImageOptions(defaultOptions).memoryCache(
				new WeakMemoryCache());

		ImageLoaderConfiguration config = builder.build();
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(config);
	}

	private void init() {

		handler = new Handler();
		gridGallery = (GridView) findViewById(R.id.gridGallery);
		gridGallery.setFastScrollEnabled(true);
		adapter = new GalleryAdapter(getApplicationContext(), imageLoader);
		adapter.setMultiplePick(false);






        AdapterView.OnItemClickListener mItemMulClickListener = new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> l, View v, int position, long id) {

                Drawable highlight = getResources().getDrawable( R.drawable.border );

                Toast.makeText(MainActivity.this, adapter.getItem(position).sdcardPath, Toast.LENGTH_SHORT).show();
                //adapter.getItem(position).

                v.setBackground(highlight);

            }
        };




        gridGallery.setOnItemClickListener(mItemMulClickListener);

        gridGallery.setAdapter(adapter);

		viewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher);
		viewSwitcher.setDisplayedChild(1);

		imgSinglePick = (ImageView) findViewById(R.id.imgSinglePick);

		btnGalleryPick = (Button) findViewById(R.id.btnGalleryPick);
		btnGalleryPick.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent();
				intent.setType("image/*");
				intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(Intent.createChooser(intent,"Select Picture"), 1);

			}
		});

		btnGalleryPickMul = (Button) findViewById(R.id.btnGalleryPickMul);
		btnGalleryPickMul.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(Action.ACTION_MULTIPLE_PICK);
				startActivityForResult(i, 200);
			}
		});

	}

	



	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

        Log.d("Clex", "testing");


		if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
			// ### delte / löschen ###
            adapter.clear();

			viewSwitcher.setDisplayedChild(1);
			String single_path = data.getStringExtra("single_path");
			imageLoader.displayImage("file://" + single_path, imgSinglePick);

		} else if (requestCode == 200 && resultCode == Activity.RESULT_OK) {
			String[] all_path = data.getStringArrayExtra("all_path");

            // Get paths for all selected images
            for (String path : all_path){
                //Toast.makeText(this, path, Toast.LENGTH_SHORT).show();
            }

			ArrayList<CustomGallery> dataT = new ArrayList<CustomGallery>();
			for (String string : all_path) {
				CustomGallery item = new CustomGallery();
				item.sdcardPath = string;
				dataT.add(item);
			}

			viewSwitcher.setDisplayedChild(0);
			adapter.addAll(dataT);
		}
	}


	/*
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			// When an Image is picked
			if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK
					&& null != data) {
				// Get the Image from data

				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				imagesEncodedList = new ArrayList<String>();
				if(data.getData()!=null){

					Uri mImageUri=data.getData();

					// Get the cursor
					Cursor cursor = getContentResolver().query(mImageUri,
							filePathColumn, null, null, null);
					// Move to first row
					cursor.moveToFirst();

					int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
					imageEncoded  = cursor.getString(columnIndex);
					cursor.close();

				}else {
					if (data.getClipData() != null) {
						ClipData mClipData = data.getClipData();
						ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
						for (int i = 0; i < mClipData.getItemCount(); i++) {

							ClipData.Item item = mClipData.getItemAt(i);
							Uri uri = item.getUri();
							mArrayUri.add(uri);
							// Get the cursor
							Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
							// Move to first row
							cursor.moveToFirst();

							int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
							imageEncoded  = cursor.getString(columnIndex);
							imagesEncodedList.add(imageEncoded);
							cursor.close();

						}
						Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());
					}
				}
			} else {
				Toast.makeText(this, "You haven't picked Image",
						Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
					.show();
		}

		super.onActivityResult(requestCode, resultCode, data);
	}
	*/


}
