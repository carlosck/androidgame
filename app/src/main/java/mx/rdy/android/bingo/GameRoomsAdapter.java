package mx.rdy.android.bingo;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Seca on 3/21/16.
 */


public class GameRoomsAdapter extends BaseAdapter {

    private Context mContext;
    private JSONArray mThumbIds;
    private View.OnClickListener gameRoomsListener;
    private LayoutInflater inflater;

    public GameRoomsAdapter(Context c,JSONArray items, View.OnClickListener listenner,LayoutInflater inflater) {
        mContext = c;
        mThumbIds = items;
        gameRoomsListener = listenner;
        this.inflater= inflater;
    }
    public void setItems(JSONArray items)
    {
        mThumbIds = items;
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

            grid = inflater.inflate(R.layout.mygrid_layout, parent, false);
            ImageView imageView = (ImageView) grid.findViewById(R.id.image);
            GridView.LayoutParams vp = new GridView.LayoutParams(Toolbar.LayoutParams.WRAP_CONTENT, Toolbar.LayoutParams.WRAP_CONTENT);
            int id = 0;
            try {
                JSONObject object = null;
                object = mThumbIds.getJSONObject(position);
                id = parent.getResources().getIdentifier(object.getString("name").toLowerCase(), "drawable", MainActivity.PACKAGE_NAME);
                imageView.setOnClickListener(gameRoomsListener);
                imageView.setTag(object.getInt("value"));
                //id= getResources().getIdentifier("bingo", "drawable", getPackageName());

            } catch (JSONException e) {
                e.printStackTrace();
            }
            imageView.setLayoutParams(vp);
            imageView.setImageResource(id);
        } else {
            grid = (View) convertView;

        }

        return grid;
    }
}