package com.example.gaopj.class1test1;


import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.Poi;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import gaopj.app.MyApplication;
import gaopj.bean.City;
import gaopj.service.LocationService;

/***
 * 单点定位示例，用来展示基本的定位结果，配置在LocationService.java中
 * 默认配置也可以在LocationService中修改
 * 默认配置的内容自于开发者论坛中对开发者长期提出的疑问内容
 *
 * @author baidu
 *
 */
public class LocationActivity extends Activity {
	private LocationService locationService;
	private TextView LocationResult;
	private Button fanhui;
	private Button startLocation;
	private String dingweidiqu="";
	private String selectcode="";
	List<City> cityList;
	private MyApplication myApplication;

	private ImageView back;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// -----------demo view config ------------
		setContentView(R.layout.location);
		LocationResult = (TextView) findViewById(R.id.textView1);
		LocationResult.setMovementMethod(ScrollingMovementMethod.getInstance());
		startLocation = (Button) findViewById(R.id.addfence);
		fanhui = (Button) findViewById(R.id.fanhui);
		//startLocation = (ImageView) findViewById(R.id.addfence);
		back= (ImageView) findViewById(R.id.title_back);
		myApplication= (MyApplication)getApplication();
		cityList=myApplication.getCityList();

		//back.setOnClickListener(this);

//		back.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View view) {
//				//locationService.stop();
//				Intent i = new Intent();
//				if(!selectcode.equals(""))
//					i.putExtra("cityCode",selectcode);
//				int j=0;
//				for (;j<cityList.size();j++)
//				{
//					String ct=cityList.get(j).getCity().toString();
//					if(ct.equals(dingweidiqu))
//					{
//						Log.i("baidu","对上了"+cityList.get(j).getNumber().toString());
//						selectcode=cityList.get(j).getNumber().toString();
//						break;
//					}
//				}
//				if(j==cityList.size())
//				{
//					Log.i("baidu","没对上");
//					i.putExtra("cityCode", "101010100");
//				}else {
//					i.putExtra("cityCode", selectcode);
//				}
//				setResult(RESULT_OK,i);
//				finish();
//			}
//		});
	}

	/**
	 * 显示请求字符串
	 *
	 * @param str
	 */
	public void logMsg(String str) {
		try {
			if (LocationResult != null)
				LocationResult.setText(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void jiarurizhi(String str)
	{
		dingweidiqu=str.substring(0,str.length()-1);
	    Log.i("baidu","dingweidiqu:"+dingweidiqu);

	}

	/***
	 * Stop location service
	 */
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		locationService.unregisterListener(mListener); //注销掉监听
		locationService.stop(); //停止定位服务
		super.onStop();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		// -----------location config ------------
		locationService = ((MyApplication) getApplication()).locationService;
		//获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
		locationService.registerListener(mListener);
		//注册监听
		int type = getIntent().getIntExtra("from", 0);
		if (type == 0) {
			locationService.setLocationOption(locationService.getDefaultLocationClientOption());
		} else if (type == 1) {
			locationService.setLocationOption(locationService.getOption());
		}
		locationService.start();
		fanhui.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				//Toast.makeText(LocationActivity.this,"点击了返回",Toast.LENGTH_LONG).show();
				Log.i("fanhui","点击了返回");

				locationService.stop();
				Intent i = new Intent();
				if(!selectcode.equals(""))
					i.putExtra("cityCode",selectcode);
				int j=0;
				for (;j<cityList.size();j++)
				{
					String ct=cityList.get(j).getCity().toString();
					if(ct.equals(dingweidiqu))
					{
						Log.i("baidu","对上了"+cityList.get(j).getNumber().toString());
						selectcode=cityList.get(j).getNumber().toString();
						break;
					}
				}
				if(j==cityList.size())
				{
					Log.i("baidu","没对上");
					i.putExtra("cityCode", "101010100");
				}else {
					i.putExtra("cityCode", selectcode);
				}
				setResult(RESULT_OK,i);
				finish();


			}
		});

		startLocation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i("fanhui","点击了定位按钮");
				if (startLocation.getText().toString().equals(getString(R.string.startlocation))) {
					locationService.start();// 定位SDK
					// start之后会默认发起一次定位请求，开发者无须判断isstart并主动调用request
					startLocation.setText(getString(R.string.stoplocation));
				} else {
					locationService.stop();
					startLocation.setText(getString(R.string.startlocation));
				}
			}
		});
	}


	/*****
	 * @see copy funtion to you project
	 * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
	 *
	 */
	private BDLocationListener mListener = new BDLocationListener() {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// TODO Auto-generated method stub
			Log.i("baidu","onReceiveLocation");
			if (null != location && location.getLocType() != BDLocation.TypeServerError) {
				StringBuffer sb = new StringBuffer(256);
				StringBuffer diqu=new StringBuffer(64);
				sb.append("time : ");
				/**
				 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
				 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
				 */
				sb.append(location.getTime());
				sb.append("\nlocType : ");// 定位类型
				sb.append(location.getLocType());
				sb.append("\nlocType description : ");// *****对应的定位类型说明*****
				sb.append(location.getLocTypeDescription());
				sb.append("\nlatitude : ");// 纬度
				sb.append(location.getLatitude());
				sb.append("\nlontitude : ");// 经度
				sb.append(location.getLongitude());
				sb.append("\nradius : ");// 半径
				sb.append(location.getRadius());
				sb.append("\nCountryCode : ");// 国家码
				sb.append(location.getCountryCode());
				sb.append("\nCountry : ");// 国家名称
				sb.append(location.getCountry());
				sb.append("\ncitycode : ");// 城市编码
				sb.append(location.getCityCode());
				sb.append("\ncity : ");// 城市
				sb.append(location.getCity());
				sb.append("\nDistrict : ");// 区
				diqu.append(location.getDistrict());
				sb.append(location.getDistrict());
				sb.append("\nStreet : ");// 街道
				sb.append(location.getStreet());
				sb.append("\naddr : ");// 地址信息
				sb.append(location.getAddrStr());
				sb.append("\nUserIndoorState: ");// *****返回用户室内外判断结果*****
				sb.append(location.getUserIndoorState());
				sb.append("\nDirection(not all devices have value): ");
				sb.append(location.getDirection());// 方向
				sb.append("\nlocationdescribe: ");
				sb.append(location.getLocationDescribe());// 位置语义化信息
				sb.append("\nPoi: ");// POI信息
				if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
					for (int i = 0; i < location.getPoiList().size(); i++) {
						Poi poi = (Poi) location.getPoiList().get(i);
						sb.append(poi.getName() + ";");
					}
				}
				if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
					sb.append("\nspeed : ");
					sb.append(location.getSpeed());// 速度 单位：km/h
					sb.append("\nsatellite : ");
					sb.append(location.getSatelliteNumber());// 卫星数目
					sb.append("\nheight : ");
					sb.append(location.getAltitude());// 海拔高度 单位：米
					sb.append("\ngps status : ");
					sb.append(location.getGpsAccuracyStatus());// *****gps质量判断*****
					sb.append("\ndescribe : ");
					sb.append("gps定位成功");
				} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
					// 运营商信息
					if (location.hasAltitude()) {// *****如果有海拔高度*****
						sb.append("\nheight : ");
						sb.append(location.getAltitude());// 单位：米
					}
					sb.append("\noperationers : ");// 运营商信息
					sb.append(location.getOperators());
					sb.append("\ndescribe : ");
					sb.append("网络定位成功");
				} else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
					sb.append("\ndescribe : ");
					sb.append("离线定位成功，离线定位结果也是有效的");
				} else if (location.getLocType() == BDLocation.TypeServerError) {
					sb.append("\ndescribe : ");
					sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
				} else if (location.getLocType() == BDLocation.TypeNetWorkException) {
					sb.append("\ndescribe : ");
					sb.append("网络不同导致定位失败，请检查网络是否通畅");
				} else if (location.getLocType() == BDLocation.TypeCriteriaException) {
					sb.append("\ndescribe : ");
					sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
				}
				logMsg(sb.toString());
				jiarurizhi(diqu.toString());

				Log.i("baidu",sb.toString());
				Log.i("baidu",diqu.toString());
			}
		}

	};

//	@Override
//	public void onClick(View view) {
//		switch (view.getId()){
//			case  R.id.title_back:
//				locationService.stop();
//				Intent i = new Intent();
//				if(!selectcode.equals(""))
//					i.putExtra("cityCode",selectcode);
//				setResult(RESULT_OK,i);
//
//				int j=0;
//				for (;j<cityList.size();j++)
//				{
//					String ct=cityList.get(j).getCity().toString();
//					if(ct.equals(dingweidiqu))
//					{
//						Log.i("baidu","对上了"+cityList.get(j).getNumber().toString());
//						selectcode=cityList.get(j).getNumber().toString();
//						SharedPreferences mySharedPreferences = getSharedPreferences("config", Activity.MODE_PRIVATE);
//						SharedPreferences.Editor editor = mySharedPreferences.edit();
//						editor.putString("main_city_code",selectcode );
//						editor.commit();
//						break;
//					}
//				}
//				if(j==cityList.size())
//				{
//					Log.i("baidu","没对上");
//					i.putExtra("cityCode", "101010100");
//				}else {
//					i.putExtra("cityCode", selectcode);
//				}
//				setResult(RESULT_OK,i);
//				finish();
//				break;
//			default:
//				break;
//		}
//	}
}
