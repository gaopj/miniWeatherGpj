package com.example.gaopj.class1test1;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import gaopj.bean.NextWeather;
import gaopj.bean.TodayWeather;
import gaopj.util.NetUtil;

/**
 * Created by lenovo on 2016/9/20.
 */
public class MainActivity extends Activity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private final int SDK_PERMISSION_REQUEST = 127;
    private String permissionInfo;

    private ViewPageAdapter vpAdapter;
    private ViewPager vp;
    private List<View> views;

    private TextView tvriqi1;
    private TextView tvwendu1;
    private TextView tvtianqi1;
    private TextView tvfengli1;
    private TextView tvriqi2;
    private TextView tvwendu2;
    private TextView tvtianqi2;
    private TextView tvfengli2;
    private TextView tvriqi3;
    private TextView tvwendu3;
    private TextView tvtianqi3;
    private TextView tvfengli3;
    private TextView tvriqi4;
    private TextView tvwendu4;
    private TextView tvtianqi4;
    private TextView tvfengli4;
    private TextView tvriqi5;
    private TextView tvwendu5;
    private TextView tvtianqi5;
    private TextView tvfengli5;
    private TextView tvriqi6;
    private TextView tvwendu6;
    private TextView tvtianqi6;
    private TextView tvfengli6;


    private ImageView rotete_update_btn;
    private Animation operatingAnim;
    private ImageView mCitySelect;
    private ImageView mUpdateBtn;
    private ImageView location;
    private TextView cityTv, timeTv, humidityTv, weekTv, pmDataTv, pmQualityTv,
            temperatureTv, climateTv, windTv, city_name_Tv, wenduTv;
    private ImageView weatherImg, pmImg;
    private static final int UPDATE_TODAY_WEATHER = 1;

    private TodayWeather serviceweather = null;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case UPDATE_TODAY_WEATHER:
                    updateTodayWeather((TodayWeather) msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    void initView() {
        weatherImg = (ImageView) findViewById(R.id.weather_img);
        pmImg = (ImageView) findViewById(R.id.pm2_5_img);
        city_name_Tv = (TextView) findViewById(R.id.title_city_name);
        cityTv = (TextView) findViewById(R.id.city);
        timeTv = (TextView) findViewById(R.id.time);
        humidityTv = (TextView) findViewById(R.id.humidity);
        weekTv = (TextView) findViewById(R.id.week_today);
        pmDataTv = (TextView) findViewById(R.id.pm_data);
        pmQualityTv = (TextView) findViewById(R.id.pm2_5_quality);
        pmImg = (ImageView) findViewById(R.id.pm2_5_img);
        temperatureTv = (TextView) findViewById(R.id.temperature);
        climateTv = (TextView) findViewById(R.id.climate);
        windTv = (TextView) findViewById(R.id.wind);
        wenduTv = (TextView) findViewById(R.id.wendu);

        SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
        String responseStr = sharedPreferences.getString("upweather", "");
        Log.i("init", "??" + responseStr + "??");
        if (responseStr != "") {
            Log.d("myWeather", responseStr);
            serviceweather = parseXML(responseStr);
            if (serviceweather != null) {
                Log.d("serviceweather", serviceweather.toString());
                Message msg = new Message();
                msg.what = UPDATE_TODAY_WEATHER;
                msg.obj = serviceweather;
                mHandler.sendMessage(msg);
            }
        } else {
            city_name_Tv.setText("N/A");
            cityTv.setText("N/A");
            timeTv.setText("N/A");
            humidityTv.setText("N/A");
            pmDataTv.setText("N/A");
            pmQualityTv.setText("N/A");
            weekTv.setText("N/A");
            temperatureTv.setText("N/A");
            climateTv.setText("N/A");
            windTv.setText("N/A");
            wenduTv.setText("N/A");
            weatherImg.setImageDrawable(getResources().getDrawable(R.drawable.biz_plugin_weather_qing));
            pmImg.setImageDrawable(getResources().getDrawable(R.drawable.biz_plugin_weather_0_50));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_info);

        mUpdateBtn = (ImageView) findViewById(R.id.title_update_btn);
        mUpdateBtn.setOnClickListener(this);
        location= (ImageView) findViewById(R.id.title_location);
        location.setOnClickListener(this);
        rotete_update_btn = (ImageView) findViewById(R.id.title_update_zhuan_btn);
        operatingAnim = AnimationUtils.loadAnimation(this, R.anim.update_btn_rotate);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);

        if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
            Log.d("myWeather", "网络OK");
            Toast.makeText(MainActivity.this, "网络OK！", Toast.LENGTH_LONG).show();
        } else {
            Log.d("myWeather", "网络挂了");
            Toast.makeText(MainActivity.this, "网络挂了！", Toast.LENGTH_LONG).show();
        }
        mCitySelect = (ImageView) findViewById(R.id.title_city_manager);
        mCitySelect.setOnClickListener(this);


        startService(new Intent(getBaseContext(), Myservice.class));
        Log.i("init", "要进入init");
        initView();
        initViews();
        inittv();

    }

    //@param cityCode
    public void queryWeatherCode(String cityCode) {
        final String address = "http://wthrcdn.etouch.cn/WeatherApi?citykey=" + cityCode;
        Log.d("myWeather", address);
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection con = null;
                TodayWeather todayWeather = null;
                try {
                    URL url = new URL(address);
                    con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setConnectTimeout(8000);
                    con.setReadTimeout(8000);
                    InputStream in = con.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();

                    String str;
                    while ((str = reader.readLine()) != null) {
                        response.append(str);
                        Log.d("myWeather", str);
                    }
                    String responseStr = response.toString();
                    Log.d("myWeather", responseStr);
                    todayWeather = parseXML(responseStr);
                    if (todayWeather != null) {

                        SharedPreferences mySharedPreferences = getSharedPreferences("config", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = mySharedPreferences.edit();
                        editor.putString("upweather", responseStr);
                        editor.commit();

                        Log.d("myWeather", todayWeather.toString());
                        Message msg = new Message();
                        msg.what = UPDATE_TODAY_WEATHER;
                        msg.obj = todayWeather;
                        mHandler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (con != null) {
                        con.disconnect();
                    }
                }
            }
        }).start();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.title_city_manager) {
            Intent i = new Intent(this, SelectCity.class);
            // startActivity(i);
            startActivityForResult(i, 1);
        }
        if (view.getId() == R.id.title_update_btn) {
            SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
            String cityCode = sharedPreferences.getString("main_city_code", "101010100");
            Log.d("MyWeather", cityCode);
            if (operatingAnim != null) {
                mUpdateBtn.setVisibility(View.GONE);
                rotete_update_btn.setVisibility(View.VISIBLE);
                rotete_update_btn.startAnimation(operatingAnim);
                // mUpdateBtn .startAnimation(operatingAnim);
            }
            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                Log.d("myWeather", "网络OK");
                queryWeatherCode(cityCode);

            } else {
                Log.d("myWeather", "网络挂了");
                Toast.makeText(MainActivity.this, "网络挂了！", Toast.LENGTH_LONG).show();
            }
        }
        if(view.getId()==R.id.title_location)
        {
            Log.d("dingwei", "你点击了定位按钮");
            getPersimmions();
            startActivity(new Intent(MainActivity.this, LocationActivity.class));
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String newCityCode = data.getStringExtra("cityCode");
            Log.d("myWeather", "选择的城市代码为：" + newCityCode);
            if (NetUtil.getNetworkState(this) != NetUtil.NETWORN_NONE) {
                Log.d("myWeather", "网络OK");
                queryWeatherCode(newCityCode);
            } else {
                Log.d("myWeather", "网络挂了");
                Toast.makeText(MainActivity.this, "网络挂了！", Toast.LENGTH_LONG).show();
            }
        }
    }

    private TodayWeather parseXML(String xmldata) {
        TodayWeather todayWeather = null;


        int fengxiangCount = 0;
        int fengliCount = 0;
        int dateCount = 0;
        int highCount = 0;
        int lowCount = 0;
        int typeCount = 0;
        try {
            XmlPullParserFactory fac = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = fac.newPullParser();
            xmlPullParser.setInput(new StringReader(xmldata));
            int eventType = xmlPullParser.getEventType();
            Log.d("myWeather", "parseXML");
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
// 判断当前事件是否为文档开始事件
                    case XmlPullParser.START_DOCUMENT:
                        break;
// 判断当前事件是否为标签元素开始事件
                    case XmlPullParser.START_TAG:
                        if (xmlPullParser.getName().equals("resp"
                        )) {
                            todayWeather = new TodayWeather();
                            todayWeather.setNext1(new NextWeather());
                            todayWeather.setNext2(new NextWeather());
                            todayWeather.setNext3(new NextWeather());
                            todayWeather.setNext4(new NextWeather());
                            todayWeather.setNext5(new NextWeather());
                            todayWeather.setNext6(new NextWeather());

                        }
                        if (todayWeather != null) {
                            if (xmlPullParser.getName().equals("city")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setCity(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("updatetime")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setUpdatetime(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("shidu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setShidu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("wendu")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setWendu(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("pm25")) {
                                eventType = xmlPullParser.next();
                                todayWeather.setPm25(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("quality")) {
                                eventType = xmlPullParser.next()
                                ;
                                todayWeather.setQuality(xmlPullParser.getText());
                            } else if (xmlPullParser.getName().equals("fengxiang")) {
                                switch (fengxiangCount) {
                                    case 0:
                                        eventType = xmlPullParser.next();
                                        todayWeather.setFengxiang(xmlPullParser.getText());
                                        fengxiangCount++;
                                        break;
                                    case 1:
                                        eventType = xmlPullParser.next();
                                        todayWeather.getNext1().setFengxiang(xmlPullParser.getText());
                                        fengxiangCount++;
                                        break;
                                    case 2:
                                        eventType = xmlPullParser.next();
                                        todayWeather.getNext2().setFengxiang(xmlPullParser.getText());
                                        fengxiangCount++;
                                        break;
                                    case 3:
                                        eventType = xmlPullParser.next();
                                        todayWeather.getNext3().setFengxiang(xmlPullParser.getText());
                                        fengxiangCount++;
                                        break;
                                    case 4:
                                        eventType = xmlPullParser.next();
                                        todayWeather.getNext4().setFengxiang(xmlPullParser.getText());
                                        fengxiangCount++;
                                        break;
                                    case 5:
                                        eventType = xmlPullParser.next();
                                        todayWeather.getNext5().setFengxiang(xmlPullParser.getText());
                                        fengxiangCount++;
                                        break;
                                    case 6:
                                        eventType = xmlPullParser.next();
                                        todayWeather.getNext6().setFengxiang(xmlPullParser.getText());
                                        fengxiangCount++;
                                        break;
                                }

                            } else if (xmlPullParser.getName().equals("fengli") && fengliCount == 0) {
                                eventType = xmlPullParser.next();
                                todayWeather.setFengli(xmlPullParser.getText());
                                fengliCount++;
                            } else if (xmlPullParser.getName().equals("date")) {
                                switch (dateCount) {
                                    case 0:
                                        eventType = xmlPullParser.next();
                                        todayWeather.setDate(xmlPullParser.getText());
                                        dateCount++;
                                        break;
                                    case 1:
                                        eventType = xmlPullParser.next();
                                        todayWeather.getNext1().setDate(xmlPullParser.getText());
                                        dateCount++;
                                        break;
                                    case 2:
                                        eventType = xmlPullParser.next();
                                        todayWeather.getNext2().setDate(xmlPullParser.getText());
                                        dateCount++;
                                        break;
                                    case 3:
                                        eventType = xmlPullParser.next();
                                        todayWeather.getNext3().setDate(xmlPullParser.getText());
                                        dateCount++;
                                        break;
                                    case 4:
                                        eventType = xmlPullParser.next();
                                        todayWeather.getNext4().setDate(xmlPullParser.getText());
                                        dateCount++;
                                        break;
                                    case 5:
                                        eventType = xmlPullParser.next();
                                        todayWeather.getNext5().setDate(xmlPullParser.getText());
                                        dateCount++;
                                        break;
                                    case 6:
                                        eventType = xmlPullParser.next();
                                        todayWeather.getNext6().setDate(xmlPullParser.getText());
                                        dateCount++;
                                        break;
                                }
                            } else if (xmlPullParser.getName().equals("high")) {
                                switch (highCount) {
                                    case 0:
                                        eventType = xmlPullParser.next();
                                        todayWeather.setHigh(xmlPullParser.getText().substring(2).trim());
                                        highCount++;
                                        break;
                                    case 1:
                                        eventType = xmlPullParser.next();
                                        todayWeather.getNext1().setHigh(xmlPullParser.getText().substring(2).trim());
                                        highCount++;
                                        break;
                                    case 2:
                                        eventType = xmlPullParser.next();
                                        todayWeather.getNext2().setHigh(xmlPullParser.getText().substring(2).trim());
                                        highCount++;
                                        break;
                                    case 3:
                                        eventType = xmlPullParser.next();
                                        todayWeather.getNext3().setHigh(xmlPullParser.getText().substring(2).trim());
                                        highCount++;
                                        break;
                                    case 4:
                                        eventType = xmlPullParser.next();
                                        todayWeather.getNext4().setHigh(xmlPullParser.getText().substring(2).trim());
                                        highCount++;
                                        break;
                                    case 5:
                                        eventType = xmlPullParser.next();
                                        todayWeather.getNext5().setHigh(xmlPullParser.getText().substring(2).trim());
                                        highCount++;
                                        break;
                                    case 6:
                                        eventType = xmlPullParser.next();
                                        todayWeather.getNext6().setHigh(xmlPullParser.getText().substring(2).trim());
                                        highCount++;
                                        break;
                                }

                            } else if (xmlPullParser.getName().equals("low")) {
                                switch (lowCount) {
                                    case 0:
                                        eventType = xmlPullParser.next();
                                        todayWeather.setLow(xmlPullParser.getText().substring(2).trim());
                                        lowCount++;
                                        break;
                                    case 1:
                                        eventType = xmlPullParser.next();
                                        todayWeather.getNext1().setLow(xmlPullParser.getText().substring(2).trim());
                                        lowCount++;
                                        break;
                                    case 2:
                                        eventType = xmlPullParser.next();
                                        todayWeather.getNext2().setLow(xmlPullParser.getText().substring(2).trim());
                                        lowCount++;
                                        break;
                                    case 3:
                                        eventType = xmlPullParser.next();
                                        todayWeather.getNext3().setLow(xmlPullParser.getText().substring(2).trim());
                                        lowCount++;
                                        break;
                                    case 4:
                                        eventType = xmlPullParser.next();
                                        todayWeather.getNext4().setLow(xmlPullParser.getText().substring(2).trim());
                                        lowCount++;
                                        break;
                                    case 5:
                                        eventType = xmlPullParser.next();
                                        todayWeather.getNext5().setLow(xmlPullParser.getText().substring(2).trim());
                                        lowCount++;
                                        break;
                                    case 6:
                                        eventType = xmlPullParser.next();
                                        todayWeather.getNext6().setLow(xmlPullParser.getText().substring(2).trim());
                                        lowCount++;
                                        break;
                                }
                            } else if (xmlPullParser.getName().equals("type")) {
                                switch (typeCount) {
                                    case 0:
                                        eventType = xmlPullParser.next();
                                        todayWeather.setType(xmlPullParser.getText());
                                        typeCount++;
                                        break;
                                    case 1:
                                        eventType = xmlPullParser.next();
                                        todayWeather.getNext1().setType(xmlPullParser.getText());
                                        typeCount++;
                                        break;
                                    case 2:
                                        eventType = xmlPullParser.next();
                                        todayWeather.getNext2().setType(xmlPullParser.getText());
                                        typeCount++;
                                        break;
                                    case 3:
                                        eventType = xmlPullParser.next();
                                        todayWeather.getNext3().setType(xmlPullParser.getText());
                                        typeCount++;
                                        break;
                                    case 4:
                                        eventType = xmlPullParser.next();
                                        todayWeather.getNext4().setType(xmlPullParser.getText());
                                        typeCount++;
                                        break;
                                    case 5:
                                        eventType = xmlPullParser.next();
                                        todayWeather.getNext5().setType(xmlPullParser.getText());
                                        typeCount++;
                                        break;
                                    case 6:
                                        eventType = xmlPullParser.next();
                                        todayWeather.getNext6().setType(xmlPullParser.getText());
                                        typeCount++;
                                        break;
                                }
                            }
                        }
                        break;
// 判断当前事件是否为标签元素结束事件
                    case XmlPullParser.END_TAG:
                        break;
                }
// 进入下一个元素并触发相应事件
                eventType = xmlPullParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return todayWeather;
    }

    void updateTodayWeather(TodayWeather todayWeather) {

        tvriqi1.setText(todayWeather.getNext1().getDate() + "");
        tvriqi2.setText(todayWeather.getNext2().getDate() + "");
        tvriqi3.setText(todayWeather.getNext3().getDate() + "");
        tvriqi4.setText(todayWeather.getNext4().getDate() + "");
        tvriqi5.setText(todayWeather.getNext5().getDate() + "");
        tvriqi6.setText(todayWeather.getNext6().getDate() + "");

        tvwendu1.setText(todayWeather.getNext1().getLow() + "~" + todayWeather.getNext1().getHigh());
        tvwendu2.setText(todayWeather.getNext2().getLow() + "~" + todayWeather.getNext2().getHigh());
        tvwendu3.setText(todayWeather.getNext3().getLow() + "~" + todayWeather.getNext3().getHigh());
        tvwendu4.setText(todayWeather.getNext4().getLow() + "~" + todayWeather.getNext4().getHigh());
        tvwendu5.setText(todayWeather.getNext5().getLow() + "~" + todayWeather.getNext5().getHigh());
        tvwendu6.setText(todayWeather.getNext6().getLow() + "~" + todayWeather.getNext6().getHigh());

        tvtianqi1.setText(todayWeather.getNext1().getType());
        tvtianqi2.setText(todayWeather.getNext2().getType());
        tvtianqi3.setText(todayWeather.getNext3().getType());
        tvtianqi4.setText(todayWeather.getNext4().getType());
        tvtianqi5.setText(todayWeather.getNext5().getType());
        tvtianqi6.setText(todayWeather.getNext6().getType());

        tvfengli1.setText(todayWeather.getNext1().getFengxiang());
        tvfengli2.setText(todayWeather.getNext2().getFengxiang());
        tvfengli3.setText(todayWeather.getNext3().getFengxiang());
        tvfengli4.setText(todayWeather.getNext4().getFengxiang());
        tvfengli5.setText(todayWeather.getNext5().getFengxiang());
        tvfengli6.setText(todayWeather.getNext6().getFengxiang());


        city_name_Tv.setText(todayWeather.getCity() + "天气");
        cityTv.setText(todayWeather.getCity());
        timeTv.setText(todayWeather.getUpdatetime() + "发布");
        humidityTv.setText("湿度：" + todayWeather.getShidu());
        pmDataTv.setText(todayWeather.getPm25());
        pmQualityTv.setText(todayWeather.getQuality());
        weekTv.setText(todayWeather.getDate());
        temperatureTv.setText(todayWeather.getHigh() + "~" + todayWeather.getLow());
        climateTv.setText(todayWeather.getType());
        windTv.setText("风力:" + todayWeather.getFengli());
        wenduTv.setText(todayWeather.getWendu());
        if (pmQualityTv.getText().toString().equals("优"))
            pmImg.setImageDrawable(getResources().getDrawable(R.drawable.biz_plugin_weather_0_50));
        else if (pmQualityTv.getText().toString().equals("良"))
            pmImg.setImageDrawable(getResources().getDrawable(R.drawable.biz_plugin_weather_51_100));
        else if (pmQualityTv.getText().toString().equals("轻度污染"))
            pmImg.setImageDrawable(getResources().getDrawable(R.drawable.biz_plugin_weather_101_150));
        else if (pmQualityTv.getText().toString().equals("中度污染"))
            pmImg.setImageDrawable(getResources().getDrawable(R.drawable.biz_plugin_weather_151_200));
        else if (pmQualityTv.getText().toString().equals("重度污染"))
            pmImg.setImageDrawable(getResources().getDrawable(R.drawable.biz_plugin_weather_201_300));
        else
            pmImg.setImageDrawable(getResources().getDrawable(R.drawable.biz_plugin_weather_greater_300));

        if (climateTv.getText().equals("暴雪"))
            weatherImg.setImageDrawable(getResources().getDrawable(R.drawable.biz_plugin_weather_baoxue));
        else if (climateTv.getText().equals("暴雨"))
            weatherImg.setImageDrawable(getResources().getDrawable(R.drawable.biz_plugin_weather_baoyu));
        else if (climateTv.getText().equals("大暴雨"))
            weatherImg.setImageDrawable(getResources().getDrawable(R.drawable.biz_plugin_weather_dabaoyu));
        else if (climateTv.getText().equals("大雪"))
            weatherImg.setImageDrawable(getResources().getDrawable(R.drawable.biz_plugin_weather_daxue));
        else if (climateTv.getText().equals("大雨"))
            weatherImg.setImageDrawable(getResources().getDrawable(R.drawable.biz_plugin_weather_dayu));
        else if (climateTv.getText().equals("多云"))
            weatherImg.setImageDrawable(getResources().getDrawable(R.drawable.biz_plugin_weather_duoyun));
        else if (climateTv.getText().equals("雷阵雨"))
            weatherImg.setImageDrawable(getResources().getDrawable(R.drawable.biz_plugin_weather_leizhenyu));
        else if (climateTv.getText().equals("雷阵雨冰雹"))
            weatherImg.setImageDrawable(getResources().getDrawable(R.drawable.biz_plugin_weather_leizhenyubingbao));
        else if (climateTv.getText().equals("特大暴雨"))
            weatherImg.setImageDrawable(getResources().getDrawable(R.drawable.biz_plugin_weather_tedabaoyu));
        else if (climateTv.getText().equals("雾"))
            weatherImg.setImageDrawable(getResources().getDrawable(R.drawable.biz_plugin_weather_wu));
        else if (climateTv.getText().equals("小雪"))
            weatherImg.setImageDrawable(getResources().getDrawable(R.drawable.biz_plugin_weather_xiaoxue));
        else if (climateTv.getText().equals("阴"))
            weatherImg.setImageDrawable(getResources().getDrawable(R.drawable.biz_plugin_weather_yin));
        else if (climateTv.getText().equals("雨加雪"))
            weatherImg.setImageDrawable(getResources().getDrawable(R.drawable.biz_plugin_weather_yujiaxue));
        else if (climateTv.getText().equals("阵雪"))
            weatherImg.setImageDrawable(getResources().getDrawable(R.drawable.biz_plugin_weather_zhenxue));
        else if (climateTv.getText().equals("阵雨"))
            weatherImg.setImageDrawable(getResources().getDrawable(R.drawable.biz_plugin_weather_zhenyu));
        else if (climateTv.getText().equals("中雪"))
            weatherImg.setImageDrawable(getResources().getDrawable(R.drawable.biz_plugin_weather_zhongxue));
        else if (climateTv.getText().equals("中雨"))
            weatherImg.setImageDrawable(getResources().getDrawable(R.drawable.biz_plugin_weather_zhongyu));
        else
            weatherImg.setImageDrawable(getResources().getDrawable(R.drawable.biz_plugin_weather_qing));

        Toast.makeText(MainActivity.this, "更新成功！", Toast.LENGTH_SHORT).show();
        mUpdateBtn.setVisibility(View.VISIBLE);
        rotete_update_btn.setVisibility(View.INVISIBLE);
        rotete_update_btn.clearAnimation();
    }

    private void inittv() {
        tvriqi1 = (TextView) views.get(0).findViewById(R.id.nextday1);
        tvwendu1 = (TextView) views.get(0).findViewById(R.id.nextwendu1);
        tvtianqi1 = (TextView) views.get(0).findViewById(R.id.nexttianqi1);
        tvfengli1 = (TextView) views.get(0).findViewById(R.id.nextfengli1);

        tvriqi2 = (TextView) views.get(0).findViewById(R.id.nextday2);
        tvwendu2 = (TextView) views.get(0).findViewById(R.id.nextwendu2);
        tvtianqi2 = (TextView) views.get(0).findViewById(R.id.nexttianqi2);
        tvfengli2 = (TextView) views.get(0).findViewById(R.id.nextfengli2);

        tvriqi3 = (TextView) views.get(0).findViewById(R.id.nextday3);
        tvwendu3 = (TextView) views.get(0).findViewById(R.id.nextwendu3);
        tvtianqi3 = (TextView) views.get(0).findViewById(R.id.nexttianqi3);
        tvfengli3 = (TextView) views.get(0).findViewById(R.id.nextfengli3);

        tvriqi4 = (TextView) views.get(1).findViewById(R.id.nextday4);
        tvwendu4 = (TextView) views.get(1).findViewById(R.id.nextwendu4);
        tvtianqi4 = (TextView) views.get(1).findViewById(R.id.nexttianqi4);
        tvfengli4 = (TextView) views.get(1).findViewById(R.id.nextfengli4);

        tvriqi5 = (TextView) views.get(1).findViewById(R.id.nextday5);
        tvwendu5 = (TextView) views.get(1).findViewById(R.id.nextwendu5);
        tvtianqi5 = (TextView) views.get(1).findViewById(R.id.nexttianqi5);
        tvfengli5 = (TextView) views.get(1).findViewById(R.id.nextfengli5);

        tvriqi6 = (TextView) views.get(1).findViewById(R.id.nextday6);
        tvwendu6 = (TextView) views.get(1).findViewById(R.id.nextwendu6);
        tvtianqi6 = (TextView) views.get(1).findViewById(R.id.nexttianqi6);
        tvfengli6 = (TextView) views.get(1).findViewById(R.id.nextfengli6);
    }

    private void initViews() {
        LayoutInflater inflater = LayoutInflater.from(this);
        views = new ArrayList<View>();
        views.add(inflater.inflate(R.layout.page1, null));
        views.add(inflater.inflate(R.layout.page2, null));
        vpAdapter = new ViewPageAdapter(this, views);
        vp = (ViewPager) findViewById(R.id.viewpager);
        vp.setAdapter(vpAdapter);
        vp.setOnPageChangeListener(this);

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    @TargetApi(23)
    private void getPersimmions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();
            /***
             * 定位权限为必须权限，用户如果禁止，则每次进入都会申请
             */
            // 定位精确位置
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
			/*
			 * 读写权限和电话状态权限非必要权限(建议授予)只会申请一次，用户同意或者禁止，只会弹一次
			 */
            // 读写权限
            if (addPermission(permissions, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
            }
            // 读取电话状态权限
            if (addPermission(permissions, Manifest.permission.READ_PHONE_STATE)) {
                permissionInfo += "Manifest.permission.READ_PHONE_STATE Deny \n";
            }

            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
            }
        }
    }

    @TargetApi(23)
    private boolean addPermission(ArrayList<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) { // 如果应用没有获得对应权限,则添加到列表中,准备批量申请
            if (shouldShowRequestPermissionRationale(permission)){
                return true;
            }else{
                permissionsList.add(permission);
                return false;
            }

        }else{
            return true;
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // TODO Auto-generated method stub
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }
}