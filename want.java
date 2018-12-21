package com.example.a51167.mywork;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.a51167.mywork.Adapter.WantAdapter;
import com.example.a51167.mywork.bin.GoodsData;
import com.example.a51167.mywork.bin.user;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class want extends AppCompatActivity {
    private List<GoodsData> publishDataList = new ArrayList<>();
    private Integer sort3=0;
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_want);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        Button sortUp=(Button)this.findViewById(R.id.sortUp3);
        Button sortDown=(Button)this.findViewById(R.id.sortDown3);
        sortUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sort3= 1;
                sendRequestWithHttpURLConnection();
                new Thread(){
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
        sortDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sort3= 0;
                sendRequestWithHttpURLConnection();
                new Thread(){
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });
        /*对标题进行赋值*/
        init();

        WantAdapter adapter = new WantAdapter(want.this,R.layout.want_item, publishDataList);
        ListView listView = (ListView)findViewById(R.id.listView33);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?>parent, View view, int position, long id){
                GoodsData publishData = publishDataList.get(position);

                /*Log.d("ChatActivity","ID is " + data);*/

                Intent intent = new Intent(want.this,PublishDetailActivity.class);   //跳转到详细界面
                /*将详细信息传到界面*/
                intent.putExtra("want_title", publishData.getTitle());
                intent.putExtra("want_price", publishData.getPrice());
                intent.putExtra("want_description", publishData.getDescription());
                intent.putExtra("userid",publishData.getUserid());
                startActivity(intent);
            }
        });

    }

    public void init(){

        GoodsData publishData = new GoodsData("充电宝",90.6,"用了挺久的充电宝，很耐用，能虫3次苹果6.","002");
        publishDataList.add(publishData);
        //sendRequestWithHttpURLConnection();
    }
    private void sendRequestWithHttpURLConnection(){

        new Thread(new Runnable() {
            @Override

            public void run(){
                try{

                    OkHttpClient client = new OkHttpClient();
                    RequestBody requestBody = new FormBody.Builder()    /*发送账号密码*/
                            .add("sort", String.valueOf(sort3))
                            .build();
                    Request request = new Request.Builder()
                            .url("http://172.24.8.43:8080/goods/getgoodsINFsellid")  /*登录url  http://172.24.7.31:8080/user/login   */
                            .post(requestBody)
                            .build();
                    Response response = null;
                    String responseData = null;

                    response = client.newCall(request).execute();
                    responseData = response.body().string();

                    parseJSONWithJSONObject(responseData);              /*解析Json数据*/

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void parseJSONWithJSONObject(String jsonData){
        try{
            /*解析json数据*/
            JSONArray jsonArray = new JSONArray(jsonData);
            for(int i = 0; i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String title = jsonObject.getString("goodname");
                String description = jsonObject.getString("description");
                String date = jsonObject.getString("uptime");
                Double price = jsonObject.getDouble("price");
                GoodsData publishData = new GoodsData(title,price,description,user.username);
                publishDataList.add(publishData);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.search,menu);
        return super.onCreateOptionsMenu(menu);
    }
}
