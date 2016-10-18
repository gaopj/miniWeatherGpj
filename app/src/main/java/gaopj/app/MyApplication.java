package gaopj.app;

import android.app.Application;
import android.util.Log;

/**
 * Created by gpj on 2016/10/18.
 */

public class MyApplication extends Application{
    private  static  final  String TAG ="MyAPP";
    @Override
    public void onCreate()
    {
        super.onCreate();
        Log.d(TAG,"MyAppLication->Oncreate");
    }
}
