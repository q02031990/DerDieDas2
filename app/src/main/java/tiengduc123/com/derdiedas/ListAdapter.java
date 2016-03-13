package tiengduc123.com.derdiedas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by qadmin on 23.12.15.
 */
public class ListAdapter extends ArrayAdapter<woeter> {
    public ListAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }


    public ListAdapter(Context context, int resource, List<woeter> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.item_of_listview, null);
        }

        woeter p = getItem(position);

        if (p != null) {
            // Anh xa + Gan gia tri
            TextView nameVideo = (TextView) v.findViewById(R.id.txtArtikel);
            nameVideo.setText(p.artikel);

            TextView timeVideo = (TextView) v.findViewById(R.id.txtWoeter);
            timeVideo.setText(" " + p.woeter);

            TextView definition = (TextView) v.findViewById(R.id.txtDict);
            if(p.definition.length()>14){
                definition.setText(" " + p.definition.substring(0,13) + "...");
            }else{
                definition.setText(" " + p.definition);
            }



            TextView id = (TextView) v.findViewById(R.id.txtID);
            id.setText(" " + p.ID);
        }

        return v;
    }
}