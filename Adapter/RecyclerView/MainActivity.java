package com.example.recyleviewdemo;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;
import com.example.recyleviewdemo.BaseRecyclerViewAdapter.*;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private List<String> mDatas;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        mRecyclerView = (RecyclerView) findViewById(R.id.recylerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, mDatas,R.layout.v_item);
        mRecyclerView.setAdapter(adapter);
        //绑定点击Item事件
        adapter.setOnItemClickListener(new OnRVItemClickListener<String>() {
            @Override
            public void onClick(View view, String item, int position) {
                Toast.makeText(MainActivity.this, item + "  " + position, Toast.LENGTH_LONG).show();
            }
        });
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.HORIZONTAL_LIST));
    }

    protected void initData()
    {
        mDatas = new ArrayList<String>();
        for (int i = 'A'; i < 'z'; i++)
        {
            mDatas.add("" + (char) i);
        }
    }

    private class RecyclerViewAdapter extends BaseRecyclerViewAdapter<String,
            BaseRecyclerViewAdapter.SparseArrayViewHolder>{

        public RecyclerViewAdapter(Context context,List<String> list,int viewId){
            super(context,list,viewId);
        }

        @Override
        protected void bindDataToItemView(SparseArrayViewHolder holder, String item){
            holder.setText(R.id.id_num,item);
            holder.setOnClickListener(R.id.id_btn, new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this,"My Id is"+v.getId(),Toast.LENGTH_LONG).show();
                }
            });

        }
       /* @Override
        public SparseArrayViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
            View v = LayoutInflater.from(mContext).inflate(mViewId,parent,false);
            SparseArrayViewHolder holder = new SparseArrayViewHolder(v);
            return holder;
        }*/
    }
}
