package list.pack;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ListdemoActivity extends Activity {
	/** Called when the activity is first created. */
	private static String this_sessionId = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		System.out.println("Start");
		setContentView(R.layout.main);
		Button button = (Button) findViewById(R.id.list_button);
		Button log_button = (Button) findViewById(R.id.logon_button);

		button.setOnClickListener(button_listener);
		log_button.setOnClickListener(log_button_listener);
	}

	private Button.OnClickListener button_listener = new Button.OnClickListener() {
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.setClass(ListdemoActivity.this, ListShow.class);
			intent.putExtra("sessionid", this_sessionId);
			startActivity(intent);
		}
	};

	private Button.OnClickListener log_button_listener = new Button.OnClickListener() {
		public void onClick(View v) {

			// TODO Auto-generated method stub
			// Intent intent = new Intent();
			// intent.setClass(ListdemoActivity.this, ListShow.class);
			// startActivity(intent);
			Intent intent = new Intent();
			intent.setClass(ListdemoActivity.this, LogonActivity.class);
			//intent.putExtra("sessionid", this_sessionId);
			startActivity(intent);
			
		}
	};

}