package list.pack;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LogonActivity extends Activity {
	static final int Logon_fail = 1;
	static final int Logon_suc = 2;
	private EditText loctext = null;
	private LocationManager locationManager;
	public void onCreate(Bundle savedInstanceState) {
		String username = null;
		String password = null;
		super.onCreate(savedInstanceState);
		setTitle("登录");
		setContentView(R.layout.logon);
		try {
			SharedPreferences nameeditor = getSharedPreferences("babytreeID",
					android.content.Context.MODE_WORLD_READABLE);
			Log.i(getClass().getName(), "获取SharedPreferences成功");
			username = nameeditor.getString("username", "");
			Log.i(getClass().getName(), "获取username=" + username);
			password = nameeditor.getString("password", "");
		} catch (Exception e) {
			Log.e(getClass().getName(), "获取SharedPreferences失败" + e.toString());
		}
		EditText nametext = (EditText) findViewById(R.id.log_name);
		EditText passtext = (EditText) findViewById(R.id.pass_word);
		loctext = (EditText) findViewById(R.id.location_info);
		// nametext.setFilters(new InputFilter[]{
		// new InputFilter.LengthFilter(2)
		// });
		nametext.setText(username);
		passtext.setText(password);
		nametext.setFocusable(true);
		nametext.requestFocus();

		Button button = (Button) findViewById(R.id.Log_button);
		button.setOnClickListener(button_listener);

		Button locbutton = (Button) findViewById(R.id.btn_get_loc);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(
				LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
		locbutton.setOnClickListener(button_listener_loc);
	}
	LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location loc) {
			// TODO Auto-generated method stub
			if (loc != null) {
				Log.i(getClass().getName(),
						"Location changed : Lat: " + loc.getLatitude()
								+ " Lng: " + loc.getLongitude());
				loctext.setText("Location changed : Lat: "
						+ loc.getLatitude() + " Lng: "
						+ loc.getLongitude());
			}
		}

		public void onProviderDisabled(String arg0) {
			// TODO Auto-generated method stub
		}

		public void onProviderEnabled(String arg0) {
			// TODO Auto-generated method stub
		}

		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// TODO Auto-generated method stub
		}
	};
	private Button.OnClickListener button_listener = new Button.OnClickListener() {
		public void onClick(View v) {

			// TODO Auto-generated method stub
			EditText nametext = (EditText) findViewById(R.id.log_name);
			EditText passtext = (EditText) findViewById(R.id.pass_word);
			String username = nametext.getText().toString();
			String password = passtext.getText().toString();

			NetTool nt = new NetTool();
			String sessionid = nt.Logon(getString(R.string.login_url)
					.toString(), username, password);

			if (sessionid == null || !sessionid.contains("L")) {
				showDialog(Logon_fail);
				Log.e(getClass().getName(), "登录失败");

			} else {
				Log.i(getClass().getName(), "sessionid=" + sessionid);
				SharedPreferences.Editor nameeditor = getSharedPreferences(
						"babytreeID",
						android.content.Context.MODE_WORLD_WRITEABLE).edit();
				nameeditor.putString("username", username);
				nameeditor.putString("password", password);
				nameeditor.commit();
				Toast toast = Toast.makeText(getApplicationContext(), "登陆成功",
						Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				Intent intent = new Intent();
				intent.setClass(LogonActivity.this, ListShow.class);
				intent.putExtra("this_sessionId", sessionid);
				// startActivityForResult(intent, MAXVIEWSIZE);
				startActivity(intent);
				// finish();
			}
		}
	};

	private Button.OnClickListener button_listener_loc = new Button.OnClickListener() {
		public void onClick(View v) {
			Location location = locationManager
					.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (location != null) {
				Log.i(getClass().getName(),
						"Location changed : Lat: " + location.getLatitude()
								+ " Lng: " + location.getLongitude());
				loctext.setText("GPS Location changed : Lat: "
						+ location.getLatitude() + " Lng: "
						+ location.getLongitude());
			}
//			else{
//				Log.i(getClass().getName(),"GPS Location equals null");
//				locationManager.requestLocationUpdates(
//						LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
//				location = locationManager
//						.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//				if (location != null) {
//					Log.i(getClass().getName(),
//							"Location changed : Lat: " + location.getLatitude()
//									+ " Lng: " + location.getLongitude());
//					loctext.setText("NETWORK Location changed : Lat: "
//							+ location.getLatitude() + " Lng: "
//							+ location.getLongitude());
//				}
				else
				{
					loctext.setText("GPS Location fetch failed ");					
				}
//			}
			
		}
	};

	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		switch (id) {
		case Logon_fail:
			// do the work to define the pause Dialog
			AlertDialog.Builder builder = new AlertDialog.Builder(
					LogonActivity.this);
			builder.setTitle("提示：");
			builder.setMessage("登录失败");
			builder.setIcon(android.R.drawable.ic_dialog_alert);
			builder.setPositiveButton("确认", new OnClickListener() {
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub

				}
			});
			dialog = builder.create();
			return dialog;
		case Logon_suc:
			// do the work to define the game over Dialog
			break;
		default:
			dialog = null;
		}
		return null;
	}

}
