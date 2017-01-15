package com.example.tho.daa_moblie_client.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.tho.daa_moblie_client.CheckBoxView.library.SmoothCheckBox;
import com.example.tho.daa_moblie_client.Controller.Singleton;
import com.example.tho.daa_moblie_client.Models.RequestModels.Init.Bean;
import com.example.tho.daa_moblie_client.R;

import java.util.ArrayList;

public class LogActivity extends AppCompatActivity {

    private ArrayList<Bean> mList = new ArrayList<>();
    Singleton singleton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        singleton = Singleton.getInstance();

        mList = singleton.getmList();





//        mList.add(new Bean("tho","20",true));
//        mList.add(new Bean("my","20",true));


        ListView lv = (ListView) findViewById(R.id.lv);
        TextView emptyText = (TextView)findViewById(android.R.id.empty);
        lv.setEmptyView(emptyText);
        lv.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return mList.size();
            }

            @Override
            public Object getItem(int position) {
                return mList.get(position);
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                ViewHolder holder;
                if (convertView == null) {
                    holder = new ViewHolder();
                    convertView = View.inflate(LogActivity.this, R.layout.item, null);
                    holder.time = (TextView) convertView.findViewById(R.id.time);
                    holder.tv = (TextView) convertView.findViewById(R.id.tv);
                    holder.cb = (SmoothCheckBox) convertView.findViewById(R.id.scb);
                    holder.cb.setClickable(false);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                final Bean bean = mList.get(position);
                holder.cb.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
                        bean.isChecked = isChecked;
                    }
                });


                holder.cb.setChecked(true);
                String text = bean.getName();
                String time = bean.getTime();
                holder.time.setText(time);
                holder.tv.setText(text);


                return convertView;
            }

            class ViewHolder {
                SmoothCheckBox cb;
                TextView tv;
                TextView time;
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bean bean = (Bean) parent.getAdapter().getItem(position);
                bean.isChecked = !bean.isChecked;
                SmoothCheckBox checkBox = (SmoothCheckBox) view.findViewById(R.id.scb);
//                checkBox.setChecked(bean.isChecked, true);
               // Toast.makeText(LogActivity.this, bean.getTen(),Toast.LENGTH_SHORT).show();
            }
        });
    }



}
