package ba.tim14.nwt.nwt_android.adapters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ba.tim14.nwt.nwt_android.R;
import ba.tim14.nwt.nwt_android.classes.RowItem;

public class CustomSpinnerAdapter extends ArrayAdapter<RowItem> {

    LayoutInflater flater;

    protected CustomSpinnerAdapter(Activity context, int resouceId, int textviewId, List<RowItem> list){

        super(context,resouceId,textviewId, list);
        flater = context.getLayoutInflater();
    }

   /* @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        RowItem rowItem = getItem(position);

        View rowview = flater.inflate(R.layout.spinner_item,null,true);

        ImageView imageView = rowview.findViewById(R.id.image_item);
        imageView.setImageResource(rowItem.getImageId());

        TextView txtTitle = rowview.findViewById(R.id.tv_item);
        txtTitle.setText(rowItem.getTitle());

        return rowview;
    }*/

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        /*if(convertView == null){
            convertView = flater.inflate(R.layout.spinner_item,parent, false);
        }
        RowItem rowItem = getItem(position);
        ImageView imageView = convertView.findViewById(R.id.image_item);
        TextView txtTitle = convertView.findViewById(R.id.tv_item);

        parent.setVisibility(View.VISIBLE);
        imageView.setImageResource(rowItem.getImageId());
        txtTitle.setText(rowItem.getTitle());

        return convertView;*/

        convertView = null;
        if (position == 0) {
            convertView = flater.inflate(R.layout.spinner_item,parent, false);
            convertView.setVisibility(View.GONE);
        } else {
            convertView = super.getDropDownView(position, null, parent);
        }
        return convertView;
    }

}
