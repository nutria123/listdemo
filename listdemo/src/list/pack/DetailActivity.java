package list.pack;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import list.pack.ImageCache.ImageCallback;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher.ViewFactory;
import android.widget.ZoomControls;

public class DetailActivity extends Activity implements OnItemSelectedListener,
		ViewFactory {
	private ImageSwitcher mswitch;
	private Gallery gl;
	private Button forward,backward;
	private List<ImageAndText> list;
	private ZoomControls zoomControls;
	private int downX, upX, cur_position;
	private DownloadManager mgr=null; 
	private long lastDownload=-1L; 
	private int index=0;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle Extra = getIntent().getExtras();
		setContentView(R.layout.detailview);
		if (Extra != null) {
			cur_position = Extra.getInt("POSITION");
            
			// Extra.get("POSISTION").toString();
			// String url = getString(R.string.host_name).toString()
			// + Extra.getString("URL");
			// String name = Extra.getString("NAME");

			list = (List<ImageAndText>) getIntent().getSerializableExtra("LIST");
			
			mswitch = (ImageSwitcher) findViewById(R.id.imageSwitcher1);
			mswitch.setFactory(this);
			
			// mswitch.setFactory(this);

			mswitch.setInAnimation(AnimationUtils.loadAnimation(this,
					android.R.anim.fade_in));

			mswitch.setOutAnimation(AnimationUtils.loadAnimation(this,
					android.R.anim.fade_out));
//
//			mswitch.setOnTouchListener(new OnTouchListener() {
//				public boolean onTouch(View v, MotionEvent event) {
//					// TODO Auto-generated method stub
//					if (event.getAction() == MotionEvent.ACTION_DOWN) {
//						downX = (int) event.getX();// 取得按下时的坐标
//						return true;
//					} else if (event.getAction() == MotionEvent.ACTION_UP) {
//						upX = (int) event.getX();// 取得松开时的坐标
//						int index = 0;
//						if (upX - downX > 100)// 从左拖到右，即看前一张
//						{
//							// 如果是第一，则去到尾部
//							if (gl.getSelectedItemPosition() == 0)
//								index = gl.getCount() - 1;
//							else
//								index = gl.getSelectedItemPosition() - 1;
//						} else if (downX - upX > 100)// 从右拖到左，即看后一张
//						{
//							// 如果是最后，则去到第一
//							if (gl.getSelectedItemPosition() == (gl.getCount() - 1))
//								index = 0;
//							else
//								index = gl.getSelectedItemPosition() + 1;
//						}
//						// 改变gallery图片所选，自动触发ImageSwitcher的setOnItemSelectedListener
//						gl.setSelection(index, true);
//						return true;
//					}
//					return false;
//				}
//			});
		      backward = (Button) findViewById(R.id.backward);  
		      forward = (Button) findViewById(R.id.forward);  
//		       switcher = (ImageSwitcher) findViewById(R.id.image);  
//		       switcher.setFactory(this);  
//		       switcher.setImageResource(imagelist[index]);  
		 
		       // 上一张   
		       backward.setOnClickListener(new View.OnClickListener()  
		       { 
		           public void onClick(View view)  
		           {
                       index=gl.getSelectedItemPosition();
		               if (index == 0)
		               {  
		                   index = gl.getCount() - 1;  
		               }  
		               else
		               {
		            	   index--;
		               }
		               gl.setSelection(index, true);
		           }  
		       });  
		       // 下一张   
		       forward.setOnClickListener(new View.OnClickListener()  
		       {
		           public void onClick(View view)  
		           {  
		        	   index=gl.getSelectedItemPosition();
		               if (index == gl.getCount()-1)  
		               {
		                   index = 0;  
		               }  
		               else
		               {
		            	   index++;
		               }
		               gl.setSelection(index, true);
		           }
		       });
  
		 
		
			gl = (Gallery) findViewById(R.id.gallery1);
			gl.setAdapter(new DetailImageAdapter(DetailActivity.this, list, gl));
			Log.i(getClass().getName(), "DetailImageAdapter 设置完成");

			gl.setOnItemSelectedListener(this);
			gl.setSelection(cur_position, true);
		}

		Button dl_bt = (Button) findViewById(R.id.download_btn);
		dl_bt.setOnClickListener(dlbutton_listener);
	}

	private Button.OnClickListener dlbutton_listener = new Button.OnClickListener() {
		public void onClick(View v) {
			// TODO Auto-generated method stub
			// Intent intent = new Intent();
			// intent.setClass(HiActivity.this, TextActivity.class);
			// startActivity(intent);
			String picurl = list.get(cur_position).getImageUrl();
			String bigpicurl = picurl.replace("sm", "b").replace("thumbs",
					"photos");
			String picname=bigpicurl.substring(bigpicurl.lastIndexOf('/')+1,bigpicurl.lastIndexOf('?')-1);
			try{
		    mgr =(DownloadManager)getSystemService(DOWNLOAD_SERVICE);  
			Uri uri=Uri.parse(bigpicurl); 
			Log.i("bigpicurl", "bigpicurl url=" + bigpicurl);
			Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).mkdirs();
			Request dwreq=new DownloadManager.Request(uri); 
			  
			dwreq.setTitle(picname);
			dwreq.setDescription("babytree download");
			dwreq.setDestinationInExternalPublicDir(Environment.DIRECTORY_DCIM,picname); 
			dwreq.setVisibleInDownloadsUi(true);
			dwreq.setShowRunningNotification(true); 
			  
			lastDownload=mgr.enqueue(dwreq);
			}
			catch(Exception e){
			Log.e(getClass().getName(), e.getMessage(), e);
			}

		}
	};

	public View makeView() {
		// TODO Auto-generated method stub
		ImageView i = new ImageView(this);
		i.setBackgroundColor(0xFF000000);
		i.setScaleType(ImageView.ScaleType.FIT_CENTER);
		i.setLayoutParams(new ImageSwitcher.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		return i;

	}

	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		// mswitch.setImageResource(mImageIds[position]);
		// mswitch.set
		cur_position = position;
		final ProgressDialog pd = new ProgressDialog(this);
		pd.setMessage("正在载入图片");
		pd.show();
		ImageCache imagecache = new ImageCache();
		String picurl = list.get(position).getImageUrl();
		String bigpicurl = picurl.replace("sm", "b")
				.replace("thumbs", "photos");

		Drawable cachedImage = imagecache.loadDrawable(bigpicurl,
				new ImageCallback() {
					public void imageLoaded(Drawable imageDrawable,
							String bigpicurl) {
						mswitch.setImageDrawable(imageDrawable);
						pd.cancel();
						// ImageView imageview = (ImageView)
						// findViewById(R.id.detailImage);
						// imageview.setImageDrawable(imageDrawable);
					}
				});
		// mswitch.setImageDrawable(cachedImage);
	}

	public void onNothingSelected(AdapterView<?> parent) {
		// TODO Auto-generated method stub

	}

	class downloadImageTask extends AsyncTask<String, Integer, Integer> {
		private int FILESIZE = 8 * 1024;
		ProgressDialog pdialog;
		InputStream is = null;
		private int fileprogress, progress;

		public downloadImageTask(Context context) {
			pdialog = new ProgressDialog(context, 0);
			pdialog.setTitle("正在下载照片请稍候....");
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
		protected Integer doInBackground(String... params) {

			// pdialog.setCancelable(true);
			String path = params[0];
			String fileName = params[1];
			String urlStr = params[2];
			int count = 0;
			// prepareListData(params[0], params[1]);
			try {
				FileUtils fileUtils = new FileUtils();
				if (fileUtils.isFileExist(path + fileName)) {
					return 1;
				} else {
					URL url = new URL(urlStr);
					Log.i("NetTool", "File url=" + urlStr);
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();

					conn.setRequestMethod("GET");
					conn.setConnectTimeout(6 * 1000);
					int imagesize = conn.getContentLength();
					InputStream inputStream = conn.getInputStream();
					// inputStream = getInputStreamFromURL(urlStr);
					File file = null;
					OutputStream output = null;
					try {
						
						fileUtils.createSDDir(path);
						file = fileUtils.createSDFile(path + fileName);
						output = new FileOutputStream(file);
						byte[] buffer = new byte[FILESIZE];
						fileprogress = 0;
						count = 0;
						while ((count = inputStream.read(buffer)) != -1) {
							output.write(buffer);
							fileprogress += count;
							progress = (int) (((float) fileprogress / (float) imagesize) * 100);
							// Log.i("downloadtask", "progress=" + progress
							// + "imagesize=" + imagesize + "fileprogress"
							// + fileprogress);
							publishProgress((int) progress);
						}
						output.flush();
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						try {
							output.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					//Log.i("NetTool", "fileName =" + path+fileName);
					if (file == null) {
						return -1;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				return -1;
			}

			return 0;
		}

		@Override
		protected void onCancelled() {

			// TODO Auto-generated method stub

			super.onCancelled();
		}

		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			// mImageAndTextView.setImage(result);
			//
			// mImageAndTextView.postInvalidate(0, 0 , mScreenWidth
			// ,mImageHeight +
			// 30); //只更新稍比图片大一些的区域
			Log.i(getClass().getName(), "下载完成");
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

	}

}
