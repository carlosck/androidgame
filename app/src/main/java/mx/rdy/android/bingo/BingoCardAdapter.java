package mx.rdy.android.bingo;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Seca on 3/28/16.
 */
public class BingoCardAdapter extends BaseAdapter{private Context mContext;
    private JSONArray mThumbIds;
    private View.OnClickListener clickListener;
    private LayoutInflater inflater;


    public BingoCardAdapter(Context c,JSONArray items, View.OnClickListener listenner,LayoutInflater inflater) {
        mContext = c;
        mThumbIds = items;
        clickListener = listenner;
        this.inflater= inflater;
    }
    @Override
    public int getCount() {
        return mThumbIds.length();
    }

    @Override
    public JSONObject getItem(int arg0) {
        JSONObject item=null;
        try {
            item= mThumbIds.getJSONObject(arg0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return item;
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        View grid;
        if (convertView == null) {
            //grid = new View(mContext);
            //LayoutInflater inflater = getLayoutInflater();

            grid = inflater.inflate(R.layout.bingo_card, parent, false);
            TextView textView = (TextView) grid.findViewById(R.id.bingo_item);
            GridView.LayoutParams vp = new GridView.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT);
            int id = 0;
            try {

                Integer value = mThumbIds.getInt(position);
                //object.getString("name").toLowerCase();
                //textView.setText(object.getString("name").toLowerCase());
                textView.setText(value.toString());
                textView.setOnClickListener(clickListener);
                textView.setTag(value);
                //id= getResources().getIdentifier("bingo", "drawable", getPackageName());

            } catch (JSONException e) {
                e.printStackTrace();
            }
            textView.setLayoutParams(vp);

        } else {
            grid = (View) convertView;

        }

        return grid;
    }


}