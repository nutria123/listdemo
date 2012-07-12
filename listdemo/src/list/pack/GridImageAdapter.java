package list.pack;

import java.util.List;

import list.pack.ImageCache.ImageCallback;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class GridImageAdapter extends ArrayAdapter<ImageAndText> {

	private GridView listView;
	private int dispwid=0;
	
	
	//private ImageCache iCache;
	//private Context mContext;

	public GridImageAdapter(Activity activity,
			List<ImageAndText> imageAndTexts, GridView listView) {
		super(activity, 0, imageAndTexts);
		//mContext = activity;
		this.listView = listView;
		DisplayMetrics metrics  = new DisplayMetrics();

		WindowManager WM = (WindowManager)activity.getSystemService(Context.WINDOW_SERVICE);
		WM.getDefaultDisplay().getMetrics(metrics);
		 Log.i(getClass().getName(),"metrics"+ metrics.toString());
		 dispwid=metrics.widthPixels/3;
		 Log.i(getClass().getName(),"dispwid"+ dispwid);
		//iCache = new ImageCache();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		// Activity activity = (Activity) getContext();

		// Inflate the views from XML
		Activity activity = (Activity) getContext();

		// Inflate the views from XML
		View rowView = convertView;
		ViewCache viewCache;
		if (rowView == null) {
			LayoutInflater inflater = activity.getLayoutInflater();
			rowView = inflater.inflate(R.layout.grid_rowview, null);
			viewCache = new ViewCache(rowView);
			rowView.setTag(viewCache);
			rowView.setLayoutParams(new GridView.LayoutParams(dispwid-1,dispwid-1));
			rowView.setPadding(4,4,4,4);
		} else {
			viewCache = (ViewCache) rowView.getTag();
		}
		ImageAndText imageAndText = getItem(position);

		// Load the image and set it on the ImageView
		String imageUrl = imageAndText.getImageUrl();
		ImageView imageView = viewCache.getImageView();
//		imageView.setLayoutParams(new GridView.LayoutParams(85,85));
//		imageView.setAdjustViewBounds(false);
//		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//		imageView.setPadding(8,8,8,8);
		imageView.setTag(imageUrl);
		ImageCache imagecache = new ImageCache();
		try {
			Log.i(getClass().getName(), "Adapter Url" + imageUrl);
			Drawable cachedImage = imagecache.loadDrawable(imageUrl,
					new ImageCallback() {
						public void imageLoaded(Drawable imageDrawable,
								String imageUrl) {
							ImageView imageViewByTag = (ImageView) listView
									.findViewWithTag(imageUrl);
							if (imageViewByTag != null) {
								imageViewByTag.setImageDrawable(imageDrawable);
							}
						}
					});
			if (cachedImage == null) {
				imageView.setImageResource(R.drawable.ic_launcher);
			} else {
				imageView.setImageDrawable(cachedImage);
			}

			//imageView.setScaleType(android.widget.GridView.);
		} catch (Exception ex) {
			Log.e(getClass().getName(), ex.getMessage(), ex);
		}

		// if (cachedImage == null) {
		// imageView.setImageResource(R.drawable.ic_launcher);
		// Log.e("Adapter", "null");
		// }else{
		// imageView.setImageDrawable(cachedImage);
		// }
		// Set the text on the TextView
		//TextView textView = viewCache.getTextView();
		//textView.setText(imageAndText.getText());

		return rowView;
	}

}
