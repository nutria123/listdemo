package list.pack;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.util.Log;

public class NetTool extends Activity {
	public static String sessionId = null;

	/**
	 * 
	 * @param urlStr
	 * @param path
	 * @param fileName
	 * @return -1:文件下载出错 0:文件下载成功 1:文件已经存在
	 */
	public static int downFile(String urlStr, String path, String fileName) {
		// InputStream inputStream = null;
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
				InputStream inputStream = conn.getInputStream();
				// inputStream = getInputStreamFromURL(urlStr);
				File resultFile = fileUtils.write2SDFromInput(path, fileName,
						inputStream);
				Log.i("NetTool", "fileName =" + fileName);
				if (resultFile == null) {
					return -1;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		// finally{
		// try {
		// inputStream.close();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }
		// }
		return 0;
	}

	public static String getHtml(String path) throws Exception {
		URL url = new URL(path);

		HttpURLConnection conn = (HttpURLConnection) url.openConnection();

		conn.setRequestMethod("GET");
		conn.setConnectTimeout(6 * 1000);
		InputStream inputStream = conn.getInputStream();
		byte[] data = readStream(inputStream);
		String returnstr = new String(data);
		// System.out.println(returnstr);

		return returnstr;

		// 别超过10秒。
		//
		// if(conn.getResponseCode()==200){
		// InputStream inputStream=conn.getInputStream();
		// byte[] data=readStream(inputStream);
		// return new String(data,encoding);
		// }
		// return null;
	}

	/**
	 * 获取指定路径，的数据。
	 * 
	 * **/
	public static byte[] getImage(String urlpath) throws Exception {
		URL url = new URL(urlpath);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setConnectTimeout(6 * 1000);
		// 别超过10秒。
		if (conn.getResponseCode() == 200) {
			InputStream inputStream = conn.getInputStream();
			return readStream(inputStream);
		}
		return null;
	}

	/**
	 * 读取数据 输入流
	 * 
	 * */
	public static byte[] readStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outstream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		while ((len = inStream.read(buffer)) != -1) {
			outstream.write(buffer, 0, len);
		}
		outstream.close();
		inStream.close();

		return outstream.toByteArray();
	}

	public String Logon(String login_url, String username, String password) {
		sessionId = null;
		// if (sessionId != null&&sessionId.contains("L")) {
		// return sessionId;
		// } else {
		try {

			String urlParameters = new String();

			String username2 = username.replaceAll("@", "%40");
			urlParameters = "action=login&email="
					+ username2
					+ "&p=1&crumb=963ec6d7013a2126f2fc49ffe48df8774bd84b65&password="
					+ password;
			Log.i(getClass().getName(), "发送参数=" + urlParameters);
			URL url = new URL(login_url);
			Log.i(getClass().getName(), "login_url=" + login_url);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			connection.setRequestProperty("Content-Length",
					"" + Integer.toString(urlParameters.getBytes().length));
			connection.setRequestProperty("Content-Language", "en-US");
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);

			// Send request
			DataOutputStream wr = new DataOutputStream(
					connection.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();
			// get cookies
			Map<String, List<String>> headmap = connection.getHeaderFields();
			Log.i(getClass().getName(), "headmap" + headmap.toString());
			String cookieVal = null;
			String key = null;
			for (int i = 1; (key = connection.getHeaderFieldKey(i)) != null; i++) {
				if (key.equalsIgnoreCase("set-cookie")) {
					cookieVal = connection.getHeaderField(i);
					cookieVal = cookieVal.substring(0, cookieVal.indexOf(";"));
					if (sessionId == null) {
						sessionId = cookieVal + ";";
					} else {
						sessionId = sessionId + cookieVal + ";";
					}
				}
			}
			// Get Response
			// InputStream is = connection.getInputStream();
			//
			// BufferedReader rd = new BufferedReader(
			// new InputStreamReader(is));
			//
			// String line;
			// StringBuffer response = new StringBuffer();
			// while ((line = rd.readLine()) != null) {
			// response.append(line);
			// response.append('\r');
			// }
			// rd.close();
			// Log.i(getClass().getName(), "response=" + response);
		} catch (Exception e) {
			Log.e(getClass().getName(), "连接失败=" + e.toString());
			return null;
		}
		// 获取cookie完成
		return sessionId;
	}
	// }

}
