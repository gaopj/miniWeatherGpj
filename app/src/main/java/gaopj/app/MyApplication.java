package gaopj.app;

import android.app.Application;
import android.app.Service;
import android.os.Environment;
import android.os.Vibrator;
import android.util.Log;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import gaopj.bean.City;
import gaopj.bean.CityDB;
import gaopj.service.LocationService;




/**
 * Created by gpj on 2016/10/18.
 */

public class MyApplication extends Application {
    public LocationService locationService;
    public Vibrator mVibrator;
    private static final String TAG = "MyAPP";
    private static MyApplication mApplication;
    private CityDB mCityDB;
    private List<City> mCityList;

    @Override
    public void onCreate() {
        locationService = new LocationService(getApplicationContext());
        mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);

        super.onCreate();
        Log.d(TAG, "MyAppLication->Oncreate");
        mApplication = this;
        mCityDB = openCityDB();
        initCityList();
    }

    public static MyApplication getInstance() {
        return mApplication;
    }

    private void initCityList() {
        mCityList = new ArrayList<City>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                prepareCityList();
            }
        }).start();
    }

    private boolean prepareCityList() {
        mCityList = mCityDB.getAllCity();
        int i = 0;
        for (City city : mCityList) {
            i++;
            String cityName = city.getCity();
            String cityCode = city.getNumber();
            Log.d(TAG, cityCode + ":" + cityName);
        }
        Log.d(TAG, "i=" + i);
        return true;
    }

    public List<City> getCityList() {
        return mCityList;
    }


    private CityDB openCityDB() {
        String path = "/data"
                + Environment.getDataDirectory().getAbsolutePath()
                + File.separator + getPackageName()
                + File.separator + "database1"
                + File.separator
                + CityDB.CITY_DB_NAME;
        File db = new File(path);
        Log.d(TAG, path);

        if (!db.exists()) {
            String pathfolder = "/data"
                    + Environment.getDataDirectory().getAbsolutePath()
                    + File.separator + getPackageName()
                    + File.separator + "database1"
                    + File.separator;
            File dirFirstFolder = new File(pathfolder);
            if (!dirFirstFolder.exists()) {
                dirFirstFolder.mkdirs();
                Log.i("MyApp", "mkdirs.");
            }
            Log.i("MyApp", "db is not exists");
            try {
                InputStream is = getAssets().open("city.db");
                FileOutputStream fos = new FileOutputStream(db);
                int len = -1;
                byte[] buffer = new byte[1024];
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                   fos.flush();
                }
                fos.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
        return new CityDB(this, path);
    }
}
