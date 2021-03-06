package com.example.coen268project.View;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ArrayAdapter;

import com.example.coen268project.Presentation.Utility;
import com.example.coen268project.R;

public class SellFragment extends Fragment {
    private static final String[] titles = Utility.Category.toArray();
    private ListView listView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_sell, container, false);
        listView = (ListView) view.findViewById(R.id.list_view);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(getActivity(), R.layout.activity_buyer_list,R.id.text_id,titles);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), LocationFragment.class);
                intent.putExtra("Item", (CharSequence) listView.getItemAtPosition(i));
                startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }
}


