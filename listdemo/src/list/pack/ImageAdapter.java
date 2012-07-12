package list.pack;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.GridView;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    public ImageAdapter(Context c)
    {
    	this.mContext=c;
    }
    
	public int getCount() {
		// TODO Auto-generated method stub
		return mThumbIds.length;
	}

	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ImageView imageview;
		if(convertView==null)
		{
			imageview=new ImageView(mContext);
			imageview.setLayoutParams(new GridView.LayoutParams(85,85));
			imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imageview.setPadding(8,8,8,8);
		}
		else
		{
			imageview=(ImageView) convertView;
		}
		//imageview.setImageURI(uri)
		imageview.setImageResource(mThumbIds[position]);
		return imageview;
	}
    private Integer[] mThumbIds=
    	{

    	};
    		
}
