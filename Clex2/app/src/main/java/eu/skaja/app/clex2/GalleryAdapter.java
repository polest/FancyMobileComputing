package eu.skaja.app.clex2;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class GalleryAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater infalter;
	private ArrayList<ImageObject> data = new ArrayList<ImageObject>();
	ImageLoader imageLoader;
    private String selectedPath= null;

	// Constructor
	public GalleryAdapter(Context c, ImageLoader imageLoader) {
		infalter = (LayoutInflater) c
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContext = c;
		this.imageLoader = imageLoader;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public ImageObject getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	// Saves the selected image path in the variable
    public void setSelectedPath(String path)
    {
        selectedPath=path;
    }

    // Empties the selectedPath variable so no image will be selected / highlighted
    public void setUnselectedPath()
    {
        selectedPath=null;
    }

    // Add all objects to the gridView
	public void addAll(ArrayList<ImageObject> files) {
		try {
			this.data.clear();
			this.data.addAll(files);

		} catch (Exception e) {
			e.printStackTrace();
		}
		// Refreshed the GridView
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final ViewHolder holder;
		if (convertView == null) {

			convertView = infalter.inflate(R.layout.gallery_item, null);
			holder = new ViewHolder();
			holder.imgQueue = (ImageView) convertView
					.findViewById(R.id.imgQueue);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.imgQueue.setTag(position);

		try {

			// Loads the images in the gridView (Recycle View)
			imageLoader.displayImage("file://" + data.get(position).sdcardPath,
					holder.imgQueue, new SimpleImageLoadingListener() {
						@Override
						public void onLoadingStarted(String imageUri, View view) {
							holder.imgQueue
									.setImageResource(R.drawable.loading);
							super.onLoadingStarted(imageUri, view);
						}
					});

		} catch (Exception e) {
			e.printStackTrace();
		}

		// Highlights / Selects the image with the saved path
        if(data.get(position).sdcardPath==selectedPath)
        {
            convertView.setBackgroundColor(0xFF1cc845);
        } else {
            convertView.setBackgroundColor(0);
        }

		return convertView;
	}


	public class ViewHolder {
		ImageView imgQueue;
	}

}
