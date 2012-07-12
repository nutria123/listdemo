package list.pack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import list.pack.albumActivity.GetImageTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class UploadActivity extends Activity {
	// private String newName ="image.jpg";
	private String uploadFile = "请选择";
	private String referUrl = "http://www.babytree.com/user/upload.php?upload_type=basic";
	private String actionUrl = "http://upload.babytree.com/content/post_myphoto.php";

	private TextView mText1;
	private static String sessionid = null;
	// private TextView mText2;
	private Button mButton, selectButton;
	private String upload_session_id;
	private String upload_session_sign;
	private String upload_album_id;
	private int RESULT_LOAD_IMAGE;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			Bundle Extra = getIntent().getExtras();

			if (Extra != null) {
				sessionid = Extra.getString("this_sessionId");
				upload_album_id = Extra.getString("album_id");
			}

			setContentView(R.layout.uploadview);
			mText1 = (TextView) findViewById(R.id.image_path);
			// "文件路径：\n"+
			mText1.setText(uploadFile);
			selectButton = (Button) findViewById(R.id.select_image);
			selectButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					Intent i = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(i, RESULT_LOAD_IMAGE);
				}
			});
			mButton = (Button) findViewById(R.id.up_confirm);
			mButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					uploadImageTask task = new uploadImageTask(UploadActivity.this);
					task.execute();
				}
			});

			URL url = new URL(referUrl);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			Log.i(getClass().getName(), "sessionId=" + sessionid);
			connection.setRequestProperty("Cookie", sessionid);
			connection.setRequestMethod("GET");
			InputStream inputStream = connection.getInputStream();
			byte[] data = NetTool.readStream(inputStream);
			String returnstr = new String(data);

			Document doc = Jsoup.parse(returnstr);
			upload_session_id = doc.getElementById("session_id").val();
			upload_session_sign = doc.getElementById("session_sign").val();
			Log.i(getClass().getName(), "upload_session_id" + upload_session_id);
			Log.i(getClass().getName(), "upload_session_sign"
					+ upload_session_sign);
			Log.i(getClass().getName(), "album_id=" + upload_album_id);
		} catch (Exception e) {
			Log.i(getClass().getName(), e.toString());
		}
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK
				&& null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };

			Cursor cursor = getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			uploadFile = cursor.getString(columnIndex);
			cursor.close();
			// "文件路径：\n"+
			mText1.setText(uploadFile);
			ImageView imageView = (ImageView) findViewById(R.id.selectedImage);
			imageView.setImageBitmap(BitmapFactory.decodeFile(uploadFile));
			// String picturePath contains the path of selected Image
		}
	}
	

	class uploadImageTask extends AsyncTask<String, Integer, Integer> {

		ProgressDialog pdialog;
//		private int fileprogress, progress;

		public uploadImageTask(Context context) {
			pdialog = new ProgressDialog(context, 0);
			pdialog.setTitle("正在上传照片请稍候....");
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
		protected Integer doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			try {
				publishProgress(0);
				HttpClient httpclient = new DefaultHttpClient();
				httpclient.getParams().setParameter(
						CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

				HttpPost httppost = new HttpPost(actionUrl);
				httppost.addHeader(
						"User-Agent",
						"Mozilla/5.0 (Windows; U; Windows NT 5.1; zh-CN; rv:1.9.2.13) Gecko/20101203 Firefox/3.6.13)");

				httppost.addHeader("Referer", referUrl);
				httppost.addHeader("Keep-Alive", "115");
				httppost.addHeader("Cookie", sessionid);
				// httppost.addHeader("Connection", "Keep-Alive");
				httppost.addHeader("Proxy-Connection", "keep-alive");
				// httppost.addHeader("Charset", "UTF-8");
				File file = new File(uploadFile);
				publishProgress(10);
				MultipartEntity mpEntity = new MultipartEntity(
						HttpMultipartMode.BROWSER_COMPATIBLE);

				ContentBody cbFile = new FileBody(file, "image/jpeg");
				// ContentBody cbFile2 = new FileBody(null,
				// "application/octet-stream");
				mpEntity.addPart(new FormBodyPart("action", new StringBody("do")));
				mpEntity.addPart(new FormBodyPart("jsoncallback", new StringBody(
						"afterUpload")));
				mpEntity.addPart(new FormBodyPart("domain", new StringBody("")));
				mpEntity.addPart(new FormBodyPart("mode", new StringBody("iframe")));
				mpEntity.addPart(new FormBodyPart("new_upload", new StringBody("1")));
				mpEntity.addPart(new FormBodyPart("session_id", new StringBody(
						upload_session_id)));
				mpEntity.addPart(new FormBodyPart("sign", new StringBody(
						upload_session_sign)));
				mpEntity.addPart("title[]", new StringBody("upbyand"));
				mpEntity.addPart("upload_file[]", cbFile);
				mpEntity.addPart(new FormBodyPart("title[]", new StringBody("")));
				mpEntity.addPart("upload_file[]", new StringBody(""));
				mpEntity.addPart(new FormBodyPart("title[]", new StringBody("")));
				mpEntity.addPart(new FormBodyPart("upload_file[];filename=\"\"",
						new StringBody("")));
				mpEntity.addPart("title[]", cbFile);
				mpEntity.addPart(new FormBodyPart("title[]", new StringBody("")));
				mpEntity.addPart(new FormBodyPart("upload_file[];filename=\"\"",
						new StringBody("")));
				mpEntity.addPart(new FormBodyPart("title[]", new StringBody("")));
				// AbstractContentBody mycb=new AbstractContentBody();
				mpEntity.addPart(new FormBodyPart("upload_file[];filename=\"\"",
						new StringBody("")));
				mpEntity.addPart(new FormBodyPart("album_id", new StringBody(
						upload_album_id)));
				httppost.setEntity(mpEntity);
				publishProgress(20);
				System.out
						.println("executing request " + httppost.getRequestLine());
				HttpResponse response = httpclient.execute(httppost);
				publishProgress(80);
				HttpEntity resEntity = response.getEntity();

				// Log.i(getClass().getName(),response.getStatusLine().toString());
				if (resEntity != null) {
					Log.i(getClass().getName(), EntityUtils.toString(resEntity));
				}
				if (resEntity != null) {
					resEntity.consumeContent();
				}
				httpclient.getConnectionManager().shutdown();
				publishProgress(100);
			} catch(FileNotFoundException e)
			{
				Log.i(getClass().getName(),"失败"+e);
				return -2;
			}
			catch (Exception e) {
				Log.i(getClass().getName(),"上传失败"+e);
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
			if(result==0)
			{
			Toast.makeText(UploadActivity.this, "上传完成", Toast.LENGTH_LONG).show();
			Log.i(getClass().getName(), "上传完成");
			}
			else if(result==-2)
			{
				Toast.makeText(UploadActivity.this, "请选择文件", Toast.LENGTH_LONG).show();
				Log.i(getClass().getName(), "上传失败，文件不存在");
			}
			else
			{
				Toast.makeText(UploadActivity.this, "上传失败，请重试", Toast.LENGTH_LONG).show();
				Log.i(getClass().getName(), "上传失败");
			}
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
