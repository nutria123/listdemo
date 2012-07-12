package list.pack;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class backupOfUploadActivity extends Activity {
//    private String newName ="image.jpg";
    private String uploadFile ="/sdcard/DCIM/4216936c1e2c2c163b6b96_b.jpg";
    private String actionUrl ="http://www.babytree.com/user/upload.php?upload_type=basic";
    private TextView mText1;
    private static String sessionid = null;
//    private TextView mText2;
    private Button mButton;
    @Override
      public void onCreate(Bundle savedInstanceState)
      {
        super.onCreate(savedInstanceState);
		Bundle Extra = getIntent().getExtras();

		if (Extra != null) {
			sessionid = Extra.getString("this_sessionId");
		}
		
        setContentView(R.layout.uploadview);
        mText1 = (TextView) findViewById(R.id.image_path);
        //"文件路径：\n"+
        mText1.setText(uploadFile);
//        mText2 = (TextView) findViewById(R.id.myText3);
        //"上传网址：\n"+
//        mText2.setText(actionUrl);
        /* 设置mButton的onClick事件处理 */    
        mButton = (Button) findViewById(R.id.up_confirm);
        mButton.setOnClickListener(new View.OnClickListener()
        {
          public void onClick(View v)
          {
            uploadFile();
          }
        });
      }
      /* 上传文件至Server的方法 */
      private void uploadFile()
      {
        String end ="\r\n";
        String twoHyphens ="--";
        String boundary ="*****";
        try
        {
    	
        	
          URL url =new URL(actionUrl);
          HttpURLConnection con=(HttpURLConnection)url.openConnection();
          /* 允许Input、Output，不使用Cache */
          con.setDoInput(true);
          con.setDoOutput(true);
          con.setUseCaches(false);
          /* 设置传送的method=POST */
          con.setRequestMethod("POST");
          /* setRequestProperty */
          con.setRequestProperty("Connection", "Keep-Alive");
          con.setRequestProperty("Charset", "UTF-8");
          Log.i(getClass().getName(),"sessionid="+sessionid);
          con.setRequestProperty("Cookie", sessionid);
          con.setRequestProperty("Content-Type",
                             "multipart/form-data;boundary="+boundary);
          /* 设置DataOutputStream */
          DataOutputStream ds =
            new DataOutputStream(con.getOutputStream());
          ds.writeBytes(twoHyphens + boundary + end);
//          ds.writeBytes("Content-Disposition: form-data; "+
//                        "name=\"file1\";filename=\""+
//                        newName +"\""+ end);
          ds.writeBytes("Content-Disposition: form-data; name=\"action\""+end+end);
          ds.writeBytes("do"+end);
          ds.writeBytes(twoHyphens + boundary + end);
          ds.writeBytes("Content-Disposition: form-data; name=\"jsoncallback\""+end+end);
          ds.writeBytes("afterUpload"+end);  
          ds.writeBytes(twoHyphens + boundary + end);
          ds.writeBytes("Content-Disposition: form-data; name=\"domain\""+end);
          ds.writeBytes(""+end);  
          ds.writeBytes(twoHyphens + boundary + end);
          ds.writeBytes("Content-Disposition: form-data; name=\"mode\""+end+end);
          ds.writeBytes("iframe"+end);            
          ds.writeBytes(twoHyphens + boundary + end);
          ds.writeBytes("Content-Disposition: form-data; name=\"new_upload\""+end+end);
          ds.writeBytes("1"+end);      
          ds.writeBytes(twoHyphens + boundary + end);         
          
          ds.writeBytes("Content-Disposition: form-data; name=\"title[]\""+end+end);
         
          ds.writeBytes("Content-Disposition: form-data; name=\"upload_file[]\"; filename=\"4216936c1e2c2c163b6b96_b.jpg\""+end);
          ds.writeBytes("Content-Type: application/octet-stream"+end+end);
          
          /* 取得文件的FileInputStream */
          FileInputStream fStream =new FileInputStream(uploadFile);
          /* 设置每次写入1024bytes */
          int bufferSize =1024;
          byte[] buffer =new byte[bufferSize];
          int length =-1;
          /* 从文件读取数据至缓冲区 */
          while((length = fStream.read(buffer)) !=-1)
          {
            /* 将资料写入DataOutputStream中 */
            ds.write(buffer, 0, length);
          }
          ds.writeBytes(end);
          ds.writeBytes(twoHyphens + boundary + end);
          ds.writeBytes("Content-Disposition: form-data; name=\"title[]\""+end+end);
          ds.writeBytes(twoHyphens + boundary + end);
          ds.writeBytes("Content-Disposition: form-data; name=\"upload_file[]\"; filename=\"4216936c1e2c2c163b6b96_b.jpg\""+end);
          ds.writeBytes("Content-Type: application/octet-stream"+end+end);
          ds.writeBytes(twoHyphens + boundary + end);
          ds.writeBytes("Content-Disposition: form-data; name=\"title[]\""+end+end);
          ds.writeBytes(twoHyphens + boundary + end);
          ds.writeBytes("Content-Disposition: form-data; name=\"upload_file[]\"; filename=\"4216936c1e2c2c163b6b96_b.jpg\""+end);
          ds.writeBytes("Content-Type: application/octet-stream"+end+end);
          ds.writeBytes(twoHyphens + boundary + end);
          ds.writeBytes("Content-Disposition: form-data; name=\"title[]\""+end+end);
          ds.writeBytes(twoHyphens + boundary + end);
          ds.writeBytes("Content-Disposition: form-data; name=\"upload_file[]\"; filename=\"4216936c1e2c2c163b6b96_b.jpg\""+end);
          ds.writeBytes("Content-Type: application/octet-stream"+end+end);
          ds.writeBytes(twoHyphens + boundary + end);
          ds.writeBytes("Content-Disposition: form-data; name=\"title[]\""+end+end);
          ds.writeBytes(twoHyphens + boundary + end);
          ds.writeBytes("Content-Disposition: form-data; name=\"upload_file[]\"; filename=\"4216936c1e2c2c163b6b96_b.jpg\""+end);
          ds.writeBytes("Content-Type: application/octet-stream"+end+end);
          ds.writeBytes(twoHyphens + boundary + end);
          ds.writeBytes("Content-Disposition: form-data; name=\"album_id\""+end+end);
          ds.writeBytes(twoHyphens + boundary + end);
          ds.writeBytes("4980704"+end);      
          ds.writeBytes(twoHyphens + boundary + end);
          /* close streams */
          fStream.close();
          ds.flush();
          /* 取得Response内容 */
          InputStream is = con.getInputStream();
          int ch;
          StringBuffer b =new StringBuffer();
          while( ( ch = is.read() ) !=-1 )
          {
            b.append( (char)ch );
          }
          /* 将Response显示于Dialog */
          showDialog("上传成功"+b.toString().trim());
          /* 关闭DataOutputStream */
          ds.close();
        }
        catch(Exception e)
        {
          showDialog("上传失败"+e);
        }
      }
      /* 显示Dialog的method */
      private void showDialog(String mess)
      {
        new AlertDialog.Builder(backupOfUploadActivity.this).setTitle("Message")
         .setMessage(mess)
         .setNegativeButton("确定",new DialogInterface.OnClickListener()
         {
           public void onClick(DialogInterface dialog, int which)
           {
           }
         })
         .show();
      }
    }

