package org.bullock.barcode3;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomArrayAdapter  extends ArrayAdapter<Data> {
	private static final String FILE = "CustomAA";

    public CustomArrayAdapter(Context context, int textViewResourceId, List<Data> objects){
        super(context, textViewResourceId, objects );  
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent){
        View v = convertView;

        if(v==null) {
            LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v=vi.inflate(R.layout.list_item, null);
        }

        // get resources
        TextView tv = (TextView)v.findViewById(R.id.list_item);
        ImageView iv = (ImageView)v.findViewById(R.id.amazonIcon);
        Data data = getItem(pos);
        
        // set text
        tv.setText(data.getDetailString());
        Log.d (FILE, "pos: "+pos +", "+data.getDetailString());
        
        // make icon not visible if no url
        if (data.url != null && !data.url.startsWith("http:")) iv.setVisibility(View.GONE); else iv.setVisibility(View.VISIBLE);

        
        if (data.floatLowestUsedPrice() > 0.01) {
            tv.setTextColor(Color.GREEN);
        }
        else {
            tv.setTextColor(Color.LTGRAY);
        }

        return v;
    }
}