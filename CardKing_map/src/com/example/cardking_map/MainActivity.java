package com.example.cardking_map;


import java.util.List;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.baidu.mapapi.overlayutil.TransitRouteOverlay;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.utils.e;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements BaiduMap.OnMapClickListener,OnGetRoutePlanResultListener{

    Button mBtnPre = null;//��һ���ڵ�
    Button mBtnNext = null;//��һ���ڵ�
    int nodeIndex = -1;//�ڵ�����,������ڵ�ʱʹ��
    RouteLine route = null;
    OverlayManager routeOverlay = null;
    boolean useDefaultIcon = false;
    private TextView popupText = null;//����view
    
    EditText editEn;
    TextView textSt;
    String address;
    LatLng suggest_latling;
    boolean Is_address_changed; 
    LocationClient mLocClient;
    BDLocation mlocation;
    boolean isFirstLoc = true;
    GeoCoder g;
    Builder end_build;
    List<PoiInfo> end_info;
    Handler myHandler;
    BDLocationListener bdlistener;
    OnGetGeoCoderResultListener geocoderListener;
    
    //��ͼ��أ�ʹ�ü̳�MapView��MyRouteMapViewĿ������дtouch�¼�ʵ�����ݴ���
    //���������touch�¼���������̳У�ֱ��ʹ��MapView����
    MapView mMapView = null;    // ��ͼView
    BaiduMap mBaidumap = null;
    //�������
    RoutePlanSearch mSearch = null;    // ����ģ�飬Ҳ��ȥ����ͼģ�����ʹ��

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());  
        setContentView(R.layout.activity_main);
//        CharSequence titleLable = "·�߹滮����";
//        setTitle(titleLable);
        //��ʼ����ͼ
        mMapView = (MapView) findViewById(R.id.map);
        mBaidumap = mMapView.getMap();
        mBtnPre = (Button) findViewById(R.id.pre);
        mBtnNext = (Button) findViewById(R.id.next);
        mBtnPre.setVisibility(View.INVISIBLE);
        mBtnNext.setVisibility(View.INVISIBLE);
        //��ͼ����¼�����
        mBaidumap.setOnMapClickListener(this);
        mBaidumap.setMyLocationEnabled(true);
        // ��ʼ������ģ�飬ע���¼�����
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);
        
        address = "";
        suggest_latling = null;
        Is_address_changed = false;
        editEn = (EditText) findViewById(R.id.end);
        textSt = (TextView) findViewById(R.id.start);
        end_info = null;
        end_build = new Builder(this);
        end_build.setTitle("destination is not accurate");
        
        
		g = GeoCoder.newInstance();
		geocoderListener = new OnGetGeoCoderResultListener() {
			
			@Override
			public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {
				// TODO Auto-generated method stub
				if(!address.equals(arg0.getAddress()))
				{
					address = arg0.getAddress();
					Is_address_changed = true;
//
			        Looper looper = Looper.getMainLooper(); //���̵߳�Looper����    
			        myHandler = new MyHandler(looper);  
			        Message msg = myHandler.obtainMessage(1,1,1,"�����̷߳���Ϣ��");        
			        myHandler.sendMessage(msg); //������Ϣ  
				}
				else
				{
					Is_address_changed = false;
				}
			}
			
			@Override
			public void onGetGeoCodeResult(GeoCodeResult arg0) {
				// TODO Auto-generated method stub
				suggest_latling = arg0.getLocation();
			}
		};
		g.setOnGetGeoCodeResultListener(geocoderListener);

        mLocClient = new LocationClient(this);
        bdlistener = new BDLocationListener() {
			
			@Override
			public void onReceiveLocation(BDLocation location) {
				// TODO Auto-generated method stub
				if (location == null || mMapView == null)
					return;
				
				mlocation = location;
				
//				Log.e("xxxx", "La: "+ mlocation.getLatitude()+",Long: "+ mlocation.getLongitude());
				LatLng llA = new LatLng(mlocation.getLatitude()+0.01,mlocation.getLongitude());
				
				

				g.reverseGeoCode(new ReverseGeoCodeOption().location(llA));
				
                
				String s = "My Position:\n"+ address;
//				Log.e("address", address);
		        popupText = new TextView(MainActivity.this);
		        popupText.setBackgroundResource(R.drawable.popup);
		        popupText.setTextColor(0xFF000000);
		        popupText.setText(s);
		        mBaidumap.showInfoWindow(new InfoWindow(popupText,llA, 0));
				

				
				MyLocationData locData = new MyLocationData.Builder()
						.accuracy(location.getRadius())
						// �˴����ÿ����߻�ȡ���ķ�����Ϣ��˳ʱ��0-360
						.direction(100).latitude(location.getLatitude())
						.longitude(location.getLongitude()).build();
				mBaidumap.setMyLocationData(locData);
				if (isFirstLoc) {
					isFirstLoc = false;
					LatLng ll = new LatLng(location.getLatitude(),
							location.getLongitude());
					MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
					mBaidumap.animateMapStatus(u);
				}
				
			}
		};
		mLocClient.registerLocationListener(bdlistener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// ��gps
		option.setCoorType("bd09ll"); // ������������
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();
    }

    /**
     * ����·�߹滮����ʾ��
     *
     * @param v
     */
    public void SearchButtonProcess(View v) {
        //��������ڵ��·������
        route = null;
        mBtnPre.setVisibility(View.INVISIBLE);
        mBtnNext.setVisibility(View.INVISIBLE);
        mBaidumap.clear();
        
        //�������յ���Ϣ������tranist search ��˵��������������
//        PlanNode stNode = PlanNode.withCityNameAndPlaceName("�Ϻ�", textSt.getText().toString());
        PlanNode stNode = PlanNode.withCityNameAndPlaceName("�Ϻ�",address);
//        Log.e("SearchButtonProcess", address);
        String ss ="�Ϻ���"+editEn.getText().toString();
        
//        g.geocode(new GeoCodeOption().city("�Ϻ�").address(address));
//        Log.e("SearchButtonProcess ss", ss);
        PlanNode enNode = PlanNode.withCityNameAndPlaceName("�Ϻ�", editEn.getText().toString());
        Log.e("SearchButtonProcess", ss);

        // ʵ��ʹ�����������յ���н�����ȷ���趨
        if (v.getId() == R.id.transit) {
            mSearch.transitSearch((new TransitRoutePlanOption())
                    .from(stNode)
                    .city("�Ϻ�")
                    .to(enNode));
        } else if (v.getId() == R.id.walk) {
//        	Log.e("walk search","st:"+stNode.+",en:"+enNode.toString());
            mSearch.walkingSearch((new WalkingRoutePlanOption())
                    .from(stNode)
                    .to(enNode));

        }
    }

    /**
     * �ڵ����ʾ��
     *
     * @param v
     */
    public void nodeClick(View v) {
        if (route == null ||
                route.getAllStep() == null) {
            return;
        }
        if (nodeIndex == -1 && v.getId() == R.id.pre) {
        	return;
        }
        //���ýڵ�����
        if (v.getId() == R.id.next) {
            if (nodeIndex < route.getAllStep().size() - 1) {
            	nodeIndex++;
            } else {
            	return;
            }
        } else if (v.getId() == R.id.pre) {
        	if (nodeIndex > 0) {
        		nodeIndex--;
        	} else {
            	return;
            }
        }
        //��ȡ�ڽ����Ϣ
        LatLng nodeLocation = null;
        String nodeTitle = null;
        Object step = route.getAllStep().get(nodeIndex);
        if (step instanceof WalkingRouteLine.WalkingStep) {
            nodeLocation = ((WalkingRouteLine.WalkingStep) step).getEntrace().getLocation();
            nodeTitle = ((WalkingRouteLine.WalkingStep) step).getInstructions();
        } else if (step instanceof TransitRouteLine.TransitStep) {
            nodeLocation = ((TransitRouteLine.TransitStep) step).getEntrace().getLocation();
            nodeTitle = ((TransitRouteLine.TransitStep) step).getInstructions();
        }

        if (nodeLocation == null || nodeTitle == null) {
            return;
        }
        //�ƶ��ڵ�������
        mBaidumap.setMapStatus(MapStatusUpdateFactory.newLatLng(nodeLocation));
        // show popup
        popupText = new TextView(MainActivity.this);
        popupText.setBackgroundResource(R.drawable.popup);
        popupText.setTextColor(0xFF000000);
        popupText.setText(nodeTitle);
        mBaidumap.showInfoWindow(new InfoWindow(popupText, nodeLocation, 0));

    }




    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    
    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult result) {
    	Log.e("result", "onGetWalkingRouteResult");
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(MainActivity.this, "��Ǹ��δ�ҵ����", Toast.LENGTH_SHORT).show();
//            end_build.show();
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
        	Log.e("result", "no_error");
            nodeIndex = -1;
            mBtnPre.setVisibility(View.VISIBLE);
            mBtnNext.setVisibility(View.VISIBLE);
            route = result.getRouteLines().get(0);
            WalkingRouteOverlay overlay = new MyWalkingRouteOverlay(mBaidumap);
            mBaidumap.setOnMarkerClickListener(overlay);
            routeOverlay = overlay;
            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
            return;
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            //���յ��;�����ַ����壬ͨ�����½ӿڻ�ȡ�����ѯ��Ϣ
        	Log.e("ss","���յ��;�����ַ����壬ͨ�����½ӿڻ�ȡ�����ѯ��Ϣ");
            end_info = result.getSuggestAddrInfo().getSuggestEndNode();
            String[] s = new String[16];
            for(int i =0;i<15 && i< end_info.size();i++)
            {
            	s[i] = end_info.get(i).address;
//            	Log.e("xxx",s[i]);
            }
            end_build.setItems(s, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					editEn.setText(end_info.get(which).address);
				}
			});
            end_build.show();
            return;
        }
        

    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult result) {

        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(MainActivity.this, "��Ǹ��δ�ҵ����", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            //���յ��;�����ַ����壬ͨ�����½ӿڻ�ȡ�����ѯ��Ϣ
        	Log.e("ss","���յ��;�����ַ����壬ͨ�����½ӿڻ�ȡ�����ѯ��Ϣ");
            end_info = result.getSuggestAddrInfo().getSuggestEndNode();
            String[] s = new String[16];
            for(int i =0;i<15 && i< end_info.size();i++)
            {
            	s[i] = end_info.get(i).address;
//            	Log.e("xxx",s[i]);
            }
            end_build.setItems(s, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					editEn.setText(end_info.get(which).address);
				}
			});
            end_build.show();
//            return;
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;
            mBtnPre.setVisibility(View.VISIBLE);
            mBtnNext.setVisibility(View.VISIBLE);
            route = result.getRouteLines().get(0);
            TransitRouteOverlay overlay = new MyTransitRouteOverlay(mBaidumap);
            mBaidumap.setOnMarkerClickListener(overlay);
            routeOverlay = overlay;
            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        }
    }

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult result) {
        
    }
    


    private class MyWalkingRouteOverlay extends WalkingRouteOverlay {

        public MyWalkingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

    private class MyTransitRouteOverlay extends TransitRouteOverlay {

        public MyTransitRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

    @Override
    public void onMapClick(LatLng point) {
        mBaidumap.hideInfoWindow();
    }

    @Override
    public boolean onMapPoiClick(MapPoi poi) {
    	return false;
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
        mSearch.destroy();
		mBaidumap.setMyLocationEnabled(false);
		mBaidumap.clear();
        mMapView.onDestroy();
		mLocClient.stop();
		// �رն�λͼ��
        super.onDestroy();
    }
    class MyHandler extends Handler{  
        
        public MyHandler(Looper looper){  
            super(looper);  
        }  
          
        public void handleMessage(Message msg){  
            super.handleMessage(msg);  
            textSt.setText(address);  
        }  
    }  

}
