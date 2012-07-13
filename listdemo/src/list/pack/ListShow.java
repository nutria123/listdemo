package list.pack;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import list.pack.ImageCache.ImageCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class ListShow extends ListActivity {
	int MAXVIEWSIZE = 50;

	private static String sessionid = null;
	private boolean boo = true;
	List<ImageAndText> data = new ArrayList<ImageAndText>();
	private PopupWindow popupWindow;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle Extra = getIntent().getExtras();

		if (Extra != null) {
			sessionid = Extra.getString("this_sessionId");
		}

		setContentView(R.layout.list_view);
		// 尝试登陆
		try_to_logon();
		Log.i(getClass().getName(), "开始准备list数据");
		// 异步调用获取相册列表
		GetListTask task = new GetListTask(ListShow.this);
		task.execute();
		// prepareListData();
		// ListView list = (ListView) findViewById(android.R.id.list);
		// list.setDrawingCacheEnabled(true);
		// list.setAdapter(new ListImageAdapter(this, data, list));

		// 设置两个按钮
		Button logon_bt = (Button) findViewById(R.id.nav_logon);
		logon_bt.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(ListShow.this, LogonActivity.class);
				// intent.putExtra("sessionid", this_sessionId);
				// startActivityForResult(intent, MAXVIEWSIZE);
				startActivity(intent);
			}
		});
		Button refresh_bt = (Button) findViewById(R.id.refresh);
		refresh_bt.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// prepareListData();
				// ListView list = (ListView) findViewById(android.R.id.list);
				GetListTask task = new GetListTask(ListShow.this);
				task.execute();
			}
		});
	}

	@Override
	protected void onListItemClick(ListView listview, View v, int position,
			long id) {
		super.onListItemClick(listview, v, position, id);
		Intent intent = new Intent();
		ImageAndText item = (ImageAndText) listview.getItemAtPosition(position);

		intent.putExtra("URL", item.getAlbumurl());
		Log.i(getClass().getName(), "AlbumUrl" + item.getAlbumurl());
		intent.putExtra("NAME", item.getText());
		intent.putExtra("this_sessionId", sessionid);
		intent.setClass(ListShow.this, albumActivity.class);
		startActivity(intent);
	}

	class GetListTask extends
			AsyncTask<String, Integer, ArrayList<ImageAndText>> {

		private int pagenum = -1;
		ProgressDialog pdialog;
		InputStream is = null;
		private Element pages;

		public GetListTask(Context context) {
			Log.i(getClass().getName(), "GetListTask构造函数开始");
			pdialog = new ProgressDialog(context, 0);
			pdialog.setTitle("正在获取相册列表请稍候....");
			pdialog.setButton("cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int i) {
					dialog.cancel();
				}
			});
			pdialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				public void onCancel(DialogInterface dialog) {
					finish();
				}
			});
			pdialog.setMax(100);
			pdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pdialog.show();
			Log.i(getClass().getName(), "GetListTask构造函数完成");
		}

		@Override
		protected ArrayList<ImageAndText> doInBackground(String... params) {

			 pdialog.setCancelable(true);
			Log.i(getClass().getName(), "GetListTask准备进入prepareListData");
			prepareListData();
			Log.i(getClass().getName(), "GetListTask 运行 prepareListData完成");
			return (ArrayList<ImageAndText>) data;
		}

		@Override
		protected void onCancelled() {

			// TODO Auto-generated method stub
			pdialog.dismiss();
			super.onCancelled();

		}

		@Override
		protected void onPostExecute(ArrayList<ImageAndText> result) {
			// TODO Auto-generated method stub
			// mImageAndTextView.setImage(result);
			//
			// mImageAndTextView.postInvalidate(0, 0 , mScreenWidth
			// ,mImageHeight +
			// 30); //只更新稍比图片大一些的区域
			Log.i(getClass().getName(), "list数据准备结束，开始准备adepter图片数据");
			ListView gview = (ListView) findViewById(android.R.id.list);
			gview.setAdapter(new ListImageAdapter(ListShow.this, data, gview));
			Log.i(getClass().getName(), "adapter装载完成");
			pdialog.dismiss();
			super.onPostExecute(result);
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			// 更新进度
			// System.out.println("" + values[0]);
			// message.setText(""+values[0]);
			pdialog.setProgress(values[0]);
		}

		private void prepareListData() {
			String returnstr = null;
			try {
				URL url = new URL(getString(R.string.photo_tab).toString());
				Log.i(getClass().getName(), "photo_tab_url" + url.toString());
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				Log.i(getClass().getName(), "sessionId=" + sessionid);
				connection.setRequestProperty("Cookie", sessionid);
				connection.setRequestMethod("GET");
				publishProgress(20);
				InputStream inputStream = connection.getInputStream();
				byte[] data = NetTool.readStream(inputStream);
				returnstr = new String(data);
				// Log.i(getClass().getName(), "returnstr=" + returnstr);
			} catch (Exception e) {
				Log.e(getClass().getName(), "登录失败=" + e.toString());
				return;
			}

			publishProgress(50);

			Document Doc = Jsoup.parse(returnstr);
			int list_size = Doc.select("[src]").size();
			int progress = 0;
			if (Doc.select("[src]").size() > 0) {
				int i = 0;
				for (Element src : Doc.getElementsByClass("thumb")) {
					i++;
					Log.i(getClass().getName(),
							"i=" + i + ";thumbsrc" + src.toString());
					// Map item = new HashMap<String, Object>();
					ImageAndText item = new ImageAndText(src.select("img")
							.attr("src"), src.select("img").attr("title"), src
							.select("a").attr("href"));

					data.add(item);
					if (i > MAXVIEWSIZE)
						break;
					progress = (int) ((i / (float) list_size) * 100) + 50;
					publishProgress((int) progress);
				}
			} else {
			}
		}
	}

	private void try_to_logon() {

		NetTool nt = new NetTool();
		String username = null;
		String password = null;
		sessionid = NetTool.sessionId;
		if (sessionid == null || !sessionid.contains("L")) {
			try {
				SharedPreferences nameeditor = getSharedPreferences(
						"babytreeID",
						android.content.Context.MODE_WORLD_READABLE);
				Log.i(getClass().getName(), "获取SharedPreferences成功");
				username = nameeditor.getString("username", "");
				Log.i(getClass().getName(), "获取username=" + username);
				password = nameeditor.getString("password", "");
			} catch (Exception e) {
				Log.e(getClass().getName(),
						"获取SharedPreferences失败" + e.toString());
				return;
			}
			if (username == null || username.equalsIgnoreCase("")
					|| password == null || password.equalsIgnoreCase("")) {
				Intent intent = new Intent();
				intent.setClass(ListShow.this, LogonActivity.class);
				// intent.putExtra("sessionid", this_sessionId);
				// startActivityForResult(intent, MAXVIEWSIZE);
				startActivity(intent);
				// try {
				// SharedPreferences nameeditor = getSharedPreferences(
				// "babytreeID",
				// android.content.Context.MODE_WORLD_READABLE);
				// Log.i(getClass().getName(), "logon后获取SharedPreferences成功");
				// username = nameeditor.getString("username", "");
				// Log.i(getClass().getName(), "logon后获取username=" + username);
				// password = nameeditor.getString("password", "");
				// } catch (Exception e) {
				// Log.e(getClass().getName(),
				// "获取SharedPreferences失败" + e.toString());
				// return;
				// }
			}

			sessionid = nt.Logon(getString(R.string.login_url).toString(),
					username, password);
			if (sessionid == null || !sessionid.contains("L")) {
				Toast.makeText(ListShow.this, "登录失败", Toast.LENGTH_LONG).show();
				Intent intent = new Intent();
				intent.setClass(ListShow.this, LogonActivity.class);
				// intent.putExtra("sessionid", this_sessionId);
				// startActivityForResult(intent, MAXVIEWSIZE);
				startActivity(intent);
				return;
			}
			// Toast suc_toast = Toast.makeText(ListShow.this, "登录成功",
			// Toast.LENGTH_LONG);
			// suc_toast.setGravity(Gravity.CENTER, 0, 0);
			// suc_toast.show();
			Log.i(getClass().getName(), "Logon成功,sessionid=" + sessionid);
		}
	}

	class ListImageAdapter extends ArrayAdapter<ImageAndText> {

		private ListView listView;
		private ImageCache iCache;
		private Context mContext;

		public ListImageAdapter(Activity activity,
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
									imageViewByTag
											.setImageDrawable(imageDrawable);
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

				imageView.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Log.i(getClass().getName(), "PopMenu init");

						if (boo) {
							View contentViewMenu = LayoutInflater.from(
									getApplicationContext()).inflate(
									R.layout.grid_popmenu, null);
							// LinearLayout menuGridView = (LinearLayout)
							// View.inflate(
							// ListShow.this, R.layout.grid_popmenu, null);
							popupWindow = new PopupWindow(contentViewMenu, 200,
									100);
							// 为弹出框设定自定义的布局
							popupWindow.setContentView(contentViewMenu);

							popupWindow.setFocusable(true);
							popupWindow.showAsDropDown(v);
							Button bt_open = (Button) contentViewMenu
									.findViewById(R.id.btn_open);
							bt_open.setOnClickListener(new Button.OnClickListener() {
								public void onClick(View btv) {
									// TODO Auto-generated method stub
									popupWindow.dismiss();
									boo = true;
								}
							});
							Button bt_close = (Button) contentViewMenu
									.findViewById(R.id.btn_close);
							bt_close.setOnClickListener(new Button.OnClickListener() {
								public void onClick(View btv) {
									// TODO Auto-generated method stub
									popupWindow.dismiss();
									boo = true;
								}
							});
							boo = false;
						} else {
							popupWindow.dismiss();
							boo = true;
						}
					}
				});

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

}
