package com.example.coen268project.View;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.coen268project.Firebase.CallBack;
import com.example.coen268project.Model.ItemDao;
import com.example.coen268project.Presentation.Item;
import com.example.coen268project.Presentation.Utility;
import com.example.coen268project.R;

import java.util.ArrayList;

public class MyOrdersActivity extends AppCompatActivity {
    private Item item;
    private ArrayList<ItemDao> itemList = new ArrayList<>();
    private ListView myOrdersListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);
        item = new Item();
        myOrdersListView = findViewById(R.id.orders_list_view);
        getAllItems();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


    private void getAllItems(){
        item.getMyOrders(Utility.getCurrentUserId(), new CallBack() {
            @Override
            public void onSuccess(Object object) {
                if(object == null)
                {
                    Toast.makeText(MyOrdersActivity.this,"No items to display", Toast.LENGTH_SHORT).show();
                    return;
                }
                Object[] objectArray = (Object[]) object;
                itemList.clear();
                for (Object itemElement : objectArray
                ) {
                    itemList.add((ItemDao) itemElement);
                }
                BindItems();
            }

            @Override
            public void onError(Object object) {
            }
        });
    }

    public void BindItems() {
        final MainAdapter_my_orders adapter= new MainAdapter_my_orders(itemList);
        myOrdersListView.setAdapter(adapter);
        myOrdersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Toast.makeText(MyOrdersActivity.this,adapter.getItem(i).getItemName(),Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MyOrdersActivity.this, ViewOrderActivity.class);
                intent.putExtra("ItemId", (CharSequence) adapter.getItem(i).getItemId());
                startActivity(intent);
            }
        });
    }

    public class MainAdapter_my_orders extends BaseAdapter {
        LayoutInflater inflater;
        ArrayList<ItemDao> items;

        public MainAdapter_my_orders(ArrayList<ItemDao> items) {
            this.items = items;
        }

        @Override
        public int getCount() {
            return this.items.size();
        }

        @Override
        public ItemDao getItem(int position) {
            return this.items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            inflater = getLayoutInflater();
            convertView=inflater.inflate(R.layout.activity_myorders_row, null);
            ImageView imageView = convertView.findViewById(R.id.imageMyOrders);
            TextView nameText = convertView.findViewById(R.id.nameMyOrders);
            TextView categoryText = convertView.findViewById(R.id.categoryMyOrders);
            Glide.with(MyOrdersActivity.this).load(this.items.get(position).getPictureName()).into(imageView);
            nameText.setText(this.items.get(position).getItemName());
            categoryText.setText(this.items.get(position).getCategory());
            return convertView;
        }
    }
}
