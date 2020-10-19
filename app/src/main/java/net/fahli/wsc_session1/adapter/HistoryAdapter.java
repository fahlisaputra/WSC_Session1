package net.fahli.wsc_session1.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.fahli.wsc_session1.R;
import net.fahli.wsc_session1.entity.History;

import java.util.ArrayList;

public class HistoryAdapter extends BaseAdapter {
    private ArrayList<History> histories;
    private Context context;

    public HistoryAdapter(ArrayList<History> histories, Context context) {
        this.histories = histories;
        this.context = context;
    }

    @Override
    public int getCount() {
        return histories.size();
    }

    @Override
    public Object getItem(int position) {
        return histories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.transfer_item, parent, false);
        TextView txtOldDepartment, txtNewDepartment, txtOldAssetSN, txtNewAssetSN, txtRelocationDate;
        txtOldDepartment = convertView.findViewById(R.id.txtDepartment1);
        txtNewDepartment = convertView.findViewById(R.id.txtDepartment2);
        txtOldAssetSN = convertView.findViewById(R.id.txtAssetSN1);
        txtNewAssetSN = convertView.findViewById(R.id.txtAssetSN2);
        txtRelocationDate = convertView.findViewById(R.id.txtRelocationDate);
        History history = histories.get(position);
        txtOldDepartment.setText(history.getOldDepartment());
        txtNewDepartment.setText(history.getNewDepartment());
        txtOldAssetSN.setText(history.getOldAssetSN());
        txtNewAssetSN.setText(history.getNewAssetSN());
        txtRelocationDate.setText(history.getRelocationDate());
        return convertView;
    }
}
