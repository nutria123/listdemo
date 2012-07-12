package list.pack;

import java.util.List;

import list.pack.ImageCache.ImageCallback;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class backupOfListImageAdapter extends ArrayAdapter<ImageAndText> {

	private ListView listView;
	private ImageCache iCache;
	private Context mContext;

	public backupOfListImageAdapter(Activity activity,
			List<ImageAndText> imageAndTexts, ListView listView) {
		super(activity, 0, imageAndTexts);
		mContext = activity;
		this.listView = listView;
		iCache = new ImageCache();
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
			rowView = inflater.inflate(R.layout.rowview, null);
			viewCache = new ViewCache(rowView);
			rowView.setTag(viewCache);
		} else {
			viewCache = (ViewCache) rowView.getTag();
		}
		ImageAndText imageAndText = getItem(position);

		// Load the image and set it on the ImageView
		String imageUrl = imageAndText.getImageUrl();
		ImageView imageView = viewCache.getImageView();
		imageView.setTag(imageUrl);
		ImageCache imagecache = new ImageCache();
		try {

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

			// imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imageView.setAdjustViewBounds(true);
			imageView.setMaxHeight(80);
			imageView.setMaxWidth(80);
			imageView.setPadding(8, 8, 8, 8);
			OnClickListener imageClick = null;
			imageView.setOnClickListener(imageClick);

			// imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
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
		TextView textView = viewCache.getTextView();
		textView.setText(imageAndText.getText());

		return rowView;
	}

}
