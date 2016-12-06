package com.example.gaopj.class1test1;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import gaopj.app.MyApplication;
import gaopj.bean.City;

import static android.R.attr.data;

/**
 * Created by gpj on 2016/10/18.
 */

public class SelectCity extends Activity implements View.OnClickListener{
    private ImageView mBackBtn;
    private ArrayList<String> dataCity ;
    private ArrayList<String> dataprovince;
    private ArrayList<String> dataCode;
    private ArrayList<String> xianshi ;
    private ArrayList<String> mData ;

    private ListView mlistView ;
    private String selectcode;
    private TextView selectct_tv;
  private   ArrayAdapter<String> adapter;
    List<City> cityList;
   private MyApplication myApplication;

    private EditText eSearch;
    Handler myhandler = new Handler();
    @Override
    protected void  onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_city);

        set_eSearch_TextChanged();

        selectct_tv=(TextView)findViewById(R.id.title_name1);
        mBackBtn = (ImageView) findViewById(R.id.title_back);
        mBackBtn.setOnClickListener(this);
        dataprovince = new ArrayList<String>();
        dataCity = new ArrayList<String>();
        dataCode=new ArrayList<String>();
        xianshi=new ArrayList<String>();
        mData = new ArrayList<String>();
       myApplication= (MyApplication)getApplication();
        cityList=myApplication.getCityList();
       // Log.d("info",cityList.get(0).getNumber());
       // Log.d("info",dataCity.get(0));
       for (int i=0;i<cityList.size();i++)
     {
          dataCity.add(cityList.get(i).getCity().toString());
         dataCode.add(cityList.get(i).getNumber().toString());
         dataprovince.add(cityList.get(i).getProvince().toString());
         String s=cityList.get(i).getNumber().toString()+"-"+cityList.get(i).getProvince().toString()+"-"+cityList.get(i).getCity().toString();
         xianshi.add(s);
         mData.add(s);
     }
       // Log.d("info",cityList.get(0).getNumber());
       mlistView = (ListView)findViewById(R.id.list_view);
        adapter = new ArrayAdapter<String>(SelectCity.this,android.R.layout.simple_list_item_1,mData);
       mlistView.setAdapter(adapter);
        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
        @Override
          public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
           Toast.makeText(SelectCity.this,"你单击了："+dataCode.get(i),Toast.LENGTH_SHORT).show();
           // selectcode=dataCode.get(i);
            selectcode=mData.get(i).substring(0,9);
           // selectct_tv.setText("你选择了："+dataCity.get(i));
            selectct_tv.setText("你选择了："+mData.get(i).substring(10));

           }
      });
      Log.d("info",cityList.get(0).getNumber());
    }
    @Override
    public  void onClick(View v)
    {
        switch (v.getId()){
            case  R.id.title_back:
                Intent i = new Intent();
                i.putExtra("cityCode",selectcode);
                setResult(RESULT_OK,i);

                SharedPreferences mySharedPreferences = getSharedPreferences("config", Activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = mySharedPreferences.edit();
                editor.putString("main_city_code",selectcode );
                editor.commit();

                finish();
                break;
            default:
                break;
        }
    }

    private void set_eSearch_TextChanged()
    {
        eSearch = (EditText) findViewById(R.id.search_edit);

        eSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub
                //这个应该是在改变的时候会做的动作吧，具体还没用到过。
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub
                //这是文本框改变之前会执行的动作
            }

            @Override
            public void afterTextChanged(Editable s) {
                /**这是文本框改变之后 会执行的动作
                 * 因为我们要做的就是，在文本框改变的同时，我们的listview的数据也进行相应的变动，并且如一的显示在界面上。
                 * 所以这里我们就需要加上数据的修改的动作了。
                 */
                    myhandler.post(eChanged);
            }
        });

    }
    Runnable eChanged = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            String data = eSearch.getText().toString();

            mData.clear();

            getmDataSub(mData, data);

            adapter.notifyDataSetChanged();

        }
    };

    private void getmDataSub(ArrayList<String> mDataSubs, String data)
    {
        int length = xianshi.size();
        for(int i = 0; i < length; ++i){
            if(xianshi.get(i).contains(data) ){
                String   item=xianshi.get(i);
                mDataSubs.add(item);
            }
        }
    }
}
