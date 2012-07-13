package list.pack;

import android.app.ActivityGroup;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

public class menuGroupActivity extends ActivityGroup {

	private LinearLayout container;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	      requestWindowFeature(Window.FEATURE_NO_TITLE);
	        // 设置视图
	        setContentView(R.layout.group_main);

	        container = (LinearLayout) findViewById(R.id.scroll_container);

	        // 模块1
	        Button btnAlbumAct = (Button) findViewById(R.id.album_act_btn);
	        btnAlbumAct.setOnClickListener(new OnClickListener() {
	            public void onClick(View v) {
	                container.removeAllViews();
	                container.addView(getLocalActivityManager().startActivity(
	                        "Module1",
	                        new Intent(menuGroupActivity.this, ListShow.class)
	                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
	                        .getDecorView());
	            }
	            
	        });

	        // 模块2
	        Button btnLogonAct = (Button) findViewById(R.id.logon_act_btn);
	        btnLogonAct.setOnClickListener(new OnClickListener() {
	            public void onClick(View v) {
	                container.removeAllViews();
	                container.addView(getLocalActivityManager().startActivity(
	                        "Module2",
	                        new Intent(menuGroupActivity.this, LogonActivity.class)
	                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
	                        .getDecorView());
	            }
	        });
	        
	        Button btnGmapAct = (Button) findViewById(R.id.gmap_act_btn);
	        btnGmapAct.setOnClickListener(new OnClickListener() {
	            public void onClick(View v) {
	                container.removeAllViews();
	                container.addView(getLocalActivityManager().startActivity(
	                        "Module3",
	                        new Intent(menuGroupActivity.this, gmapActivity.class)
	                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
	                        .getDecorView());
	            }
	        });
	}

}
