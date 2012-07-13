package list.pack;

import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class gmapActivity extends MapActivity {
    private MapView mMapView;
    private MapController mMapController;
    private GeoPoint mGeoPoint;
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gmap);
		mMapView = (MapView) findViewById(R.id.GMapView);
        mMapController = mMapView.getController(); 
        mMapView.setEnabled(true);
        mMapView.setClickable(true);
        //设置地图支持缩放
        mMapView.setBuiltInZoomControls(true); 
        mGeoPoint = new GeoPoint((int) (30.659259 * 1000000), (int) (104.065762 * 1000000));
        //定位到成都
        mMapController.animateTo(mGeoPoint); 
        //设置倍数(1-21)
        mMapController.setZoom(15); 
        
        
        
        //添加Overlay，用于显示标注信息
//        MyLocationOverlay myLocationOverlay = new MyLocationOverlay();
//        List<Overlay> list = mMapView.getOverlays();
//        list.add(myLocationOverlay);
	}
}
