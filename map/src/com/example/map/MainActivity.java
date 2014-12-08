package com.example.map;


import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;


import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.model.LatLng;



import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	MapView mMapView = null;  
	BaiduMap mBaiduMap;
	LocationClient mLocClient;
	LocationMode mCurrentMode;
	BDLocation mlocation;
	MKOfflineMap mOffline = null;
	BitmapDescriptor mCurrentMarker;
	BitmapDescriptor bdA;//
	LatLng llD;
	View viewCache;
	PopupOverlay mPopupOverlay  = null;
	Marker mMarkerA;
	boolean isFirstLoc = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());  
		setContentView(R.layout.activity_main);
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		mOffline = new MKOfflineMap();
		mBaiduMap.setMyLocationEnabled(true);
		mCurrentMode = LocationMode.NORMAL;
		viewCache = LayoutInflater.from(this).inflate(R.layout.pop_layout, null);

		
        mPopupOverlay = new PopupOverlay(mMapView ,new PopupClickListener() {
			
			@Override
			public void onClickedPopup(int arg0) {
				mPopupOverlay.hidePop();
			}
		});
		
		bdA = BitmapDescriptorFactory.fromResource(R.drawable.location_tips);
		
		// 定位初始化
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(new BDLocationListener() {
			
			@Override
			public void onReceiveLocation(BDLocation location) {
				// TODO Auto-generated method stub
				if (location == null || mMapView == null)
					return;
				
				mlocation = location;
//				Log.e("xxxx", "La: "+ mlocation.getLatitude()+",Long: "+ mlocation.getLongitude());
				
				MyLocationData locData = new MyLocationData.Builder()
						.accuracy(location.getRadius())
						// 此处设置开发者获取到的方向信息，顺时针0-360
						.direction(100).latitude(location.getLatitude())
						.longitude(location.getLongitude()).build();
				mBaiduMap.setMyLocationData(locData);
				if (isFirstLoc) {
					isFirstLoc = false;
					LatLng ll = new LatLng(location.getLatitude(),
							location.getLongitude());
					MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
					mBaiduMap.animateMapStatus(u);
				}
				
//				LatLng llA = new LatLng(this.mlocation.getLatitude()* 1e6,this.mlocation.getLongitude() *1e6);
				LatLng llA = new LatLng(mlocation.getLatitude(),mlocation.getLongitude());
				mMarkerA.setTitle("位置\n" + mlocation.getAddrStr());
//				
//				mMarkerA.setIcon(bdA);
				mMarkerA.setPosition(llA);
//				OverlayOptions ooA = new MarkerOptions().position(llA).icon(bdA).title("位置\n" + mlocation.getAddrStr())
//						.zIndex(9).draggable(false);
//				mBaiduMap.addOverlay(ooA);
			}
			
			public void onReceivePoi(BDLocation poiLocation) {
			}
		});
		
		
		
		llD = new LatLng(39.963175, 116.400244);

		OverlayOptions ooA = new MarkerOptions().position(llD).icon(bdA).title("xxxx").draggable(false);
		
		mMarkerA = (Marker) (mBaiduMap.addOverlay(ooA));

		
		
		
		mCurrentMarker = BitmapDescriptorFactory
				.fromResource(R.drawable.loading_point_medium);
		mBaiduMap
				.setMyLocationConfigeration(new MyLocationConfiguration(
						mCurrentMode, true, mCurrentMarker));
		
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();
		

		
		
//		importFromSDCard();
	}
	public void importFromSDCard() {  
	    int num = mOffline.importOfflineData();  
	    String msg = "";  
	    if (num == 0) {  
	        msg = "没有导入离线包，这可能是离线包放置位置不正确，或离线包已经导入过";  
	    } else {  
	        msg = String.format("成功导入 %d 个离线包，可以在下载管理查看", num);  
	    }  
	    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();  
	}
	
	
	
	private void showPopupOverlay(BDLocation location){
		 TextView popText = ((TextView)viewCache.findViewById(R.id.location_tips));
		 popText.setText("[我的位置]\n" + location.getAddrStr());
		 mPopupOverlay.showPopup(getBitmapFromView(popText),
					new GeoPoint((int)(location.getLatitude()*1e6), (int)(location.getLongitude()*1e6)),
					10);
	}
	
	
	@Override
	protected void onPause() {
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// 退出时销毁定位
		mLocClient.stop();
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mBaiduMap.clear();
		mMapView.onDestroy();
		
		mMapView = null;
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
