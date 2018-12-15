package com.example.taoxiao;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.taoxiao.Adapter.PublishDataAdapter;
import com.example.taoxiao.bin.PublishData;

import java.util.ArrayList;
import java.util.List;

public class MyPublishActivity extends AppCompatActivity implements View.OnClickListener {


                        /*适配器*/
    private List<PublishData> publishDataList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_publish);
                /*初始化标题*/
        getSupportActionBar().hide();
        /*对标题进行赋值*/
        TextView title = (TextView)findViewById(R.id.title_text);
        title.setText("我的发布");
        Button bt_edit = (Button)findViewById(R.id.title_edit);
        bt_edit.setVisibility(View.INVISIBLE);
        Button bt_back = (Button)findViewById(R.id.title_back);
        bt_back.setOnClickListener(this);

        init();

        PublishDataAdapter adapter = new PublishDataAdapter(MyPublishActivity.this,R.layout.publish_item, publishDataList);
        ListView listView = (ListView)findViewById(R.id.publish_ls);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?>parent, View view, int position, long id){
                PublishData publishData = publishDataList.get(position);

                /*Log.d("ChatActivity","ID is " + data);*/

                Intent intent = new Intent(MyPublishActivity.this,PublishDetailActivity.class);   //跳转到聊天界面
                                /*将id传给下个发送聊天信息的界面*/
                intent.putExtra("title", publishData.getTitle());
                intent.putExtra("price", publishData.getPrice());
                intent.putExtra("description", publishData.getDescription());
                intent.putExtra("userid",publishData.getUserid());
                startActivity(intent);
            }
        });

    }

    @Override
    public void onClick(View v) {
        finish();
    }
    public void init(){
        PublishData publishData = new PublishData("充电宝","90","用了挺久的充电宝，很耐用，能虫3次苹果6.","Zzhiwen");
        publishDataList.add(publishData);
    }
}
