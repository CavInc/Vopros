package cav.vopros.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import cav.vopros.R;

/**
 * Created by Kotov Alexander on 28.02.17.
 *
 */
public class StatisticAdapter extends ArrayAdapter<RecordModel>{

    private LayoutInflater mInflater;
    private int resLayout;

    public StatisticAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
        resLayout = resource;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        View row=convertView;
        if (row == null) {
            row = mInflater.inflate(resLayout,parent,false);
            holder = new ViewHolder();
            holder.dateRec = (TextView) row.findViewById(R.id.dateRec);
            holder.countRec = (TextView) row.findViewById(R.id.countRec);
            row.setTag(holder);
        }else {
            holder = (ViewHolder) row.getTag();
        }

        RecordModel item = getItem(position);
        holder.dateRec.setText(item.getDateRec());
        holder.countRec.setText(item.getCountRect());

        return row;
    }

    protected class ViewHolder {
        public TextView dateRec;
        public TextView countRec;
    }
}
