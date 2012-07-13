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
        //���õ�ͼ֧������
        mMapView.setBuiltInZoomControls(true); 
        mGeoPoint = new GeoPoint((int) (30.659259 * 1000000), (int) (104.065762 * 1000000));
        //��λ���ɶ�
        mMapController.animateTo(mGeoPoint); 
        //���ñ���(1-21)
        mMapController.setZoom(15); 
        
        
        
        //���Overlay��������ʾ��ע��Ϣ
//        MyLocationOverlay myLocationOverlay = new MyLocationOverlay();
//        List<Overlay> list = mMapView.getOverlays();
//        list.add(myLocationOverlay);
	}
}
