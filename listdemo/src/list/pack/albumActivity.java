package list.pack;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;

public class albumActivity extends Activity {

	private ImageCache imageCache;
	private Element pages;
	private List<ImageAndText> list = new ArrayList<ImageAndText>();
	private static String sessionid = null;
	private String album_id;
	private String mypath;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle Extra = getIntent().getExtras();
		setContentView(R.layout.gridview);
		if (Extra != null) {
			String url = Extra.getString("URL");
			String name = Extra.getString("NAME");
			sessionid = Extra.getString("this_sessionId");
			mypath=getString(R.string.host_name).toString()+url;
			Uri myuri=Uri.parse(mypath);
			album_id=myuri.getQueryParameter("aid");
			setTitle(name);
			prepareData(url);
		}
		GridView gridview = (GridView) findViewById(R.id.gridView1);
		// AutoLoadListener autoLoadListener = new AutoLoadListener(callBack);
		// gridview.setOnScrollListener(autoLoadListener);
		gridview.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				ImageAndText item = (ImageAndText) parent
						.getItemAtPosition(position);
				Log.i(getClass().getName(), item.toString());
				intent.putExtra("URL", position);
				
				intent.putExtra("POSITION", position);
				Log.i(getClass().getName(), "DetailUrl" + item.getAlbumurl()
						+ ";postion=" + position);
				
				intent.putExtra("LIST", (Serializable)list);
				intent.putExtra("NAME", item.getText());
				
				intent.setClass(albumActivity.this, DetailActivity.class);
				startActivity(intent);
			}
		});
		Button upload_bt=(Button) findViewById(R.id.upload_bt);
		upload_bt.setOnClickListener(new Button.OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("this_sessionId", sessionid);
				intent.putExtra("album_id", album_id);
				intent.setClass(albumActivity.this, UploadActivity.class);
				startActivity(intent);
			}
			
		}); 
				
	}

	private void prepareData(String url) {
		// 设置gridview内容
		GridView gview = (GridView) findViewById(R.id.gridView1);
		gview.setDrawingCacheEnabled(true);
		Log.i(getClass().getName(), "开始准备list数据");
		GetImageTask task = new GetImageTask(this);
		task.execute(url, getString(R.string.host_name).toString());

	}

	class GetImageTask extends
			AsyncTask<String, Integer, ArrayList<ImageAndText>> {

		private int pagenum = -1;
		ProgressDialog pdialog;
		InputStream is = null;
		private Element pages;

		public GetImageTask(Context context) {
			pdialog = new ProgressDialog(context, 0);
			pdialog.setTitle("正在获取相册数据请稍候....");
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
		}

		@Override
		protected ArrayList<ImageAndText> doInBackground(String... params) {

			// pdialog.setCancelable(true);

			prepareListData(params[0], params[1]);

			return (ArrayList<ImageAndText>) list;
		}

		@Override
		protected void onCancelled() {

			// TODO Auto-generated method stub

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
			GridView gview = (GridView) findViewById(R.id.gridView1);
			gview.setAdapter(new GridImageAdapter(albumActivity.this, list,
					gview));
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

		private void prepareListData(String url, String host_name) {
			String pageno = new String();
			String pageno2 = new String();
			int progress = 0;
			try {

				String path = host_name + url;
				Log.i(getClass().getName(), "This Page Path=" + path);
				if (url.contains("pg")) {
					pageno = url.substring(url.indexOf("pg") + 3);
				} else {
					pageno = "1";
				}

				String htmlstring = NetTool.getHtml(path);

				Document doc = Jsoup.parse(htmlstring);
				Elements picurl = doc.getElementsByClass("thumb").select(
						"img[src*=sm.jpg]");
				// 获取总页数
				if (pagenum == -1) {
					Elements allpages = doc.getElementsByClass("lookPagejump")
							.first().select("a");
					pagenum = (allpages.size() > 0) ? allpages.size() : 1;
				}
				Integer ipageno = new Integer(pageno);
				float picnum = picurl.size();
				int pic_id = 0;
				for (Element src : picurl) {
					pic_id++;
					String imgpath = src.attr("src");
					String imgname = src.attr("alt");
					// String imgur = doc.before(src).attr("href");
					String imgur2 = src.parent().attr("href");
					// Log.i(getClass().getName(),"imgpath="+imgpath+"imgname="+imgname
					// +"imgur2="+imgur2);

					// GridView gview=(GridView)findViewById(R.id.gridView1);

					ImageAndText item = new ImageAndText(imgpath, imgname,
							imgur2);
					list.add(item);

					progress = (int) ((((float) ipageno - 1 + pic_id / picnum) / (float) pagenum) * 100);
					publishProgress((int) progress);
					Log.i(getClass().getName(), "progress" + progress
							+ ";pic_id=" + pic_id);
				}
				// 开始获取下一页
				Element pageurl = doc.getElementsByClass("lookPagejump")
						.first();
				pages = pageurl.select("a").last();

				if (pages != null) {
					url = pages.attr("href");
					if (url.contains("pg")) {
						pageno2 = url.substring(url.indexOf("pg") + 3);
						// Integer.parseInt(pageno2);
						if (pageno2.compareToIgnoreCase(pageno) < 0) {
							Log.i(getClass().getName(), "页号小于当前页号");
							return;
						}
					} else {
						Log.i(getClass().getName(), "无分页，直接设置为空");
						return;
					}
				} else {
					Log.i(getClass().getName(), "无分页，直接设置为空");
					return;
				}

				prepareListData(url, host_name);

				// Log.i(getClass().getName(), "getElementsContainingText=" +
				// pages.getElementsContainingText("gt").toString());
				// Log.i(getClass().getName(), "pageurl=" + pages.toString());

			} catch (Exception ex) {
				Log.e(getClass().getName(), ex.getMessage(), ex);
			}

		}

	}

}
