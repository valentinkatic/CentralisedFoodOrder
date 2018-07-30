package com.katic.centralisedfoodorder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.katic.centralisedfoodorder.ConfirmActivity;
import com.katic.centralisedfoodorder.R;
import com.katic.centralisedfoodorder.classes.DeliveryAddress;

import java.util.List;

public class ChooseDelieveryAddressAdapter extends BaseAdapter {

    private List<DeliveryAddress> listData;

    private LayoutInflater layoutInflater;
    private Context context;

    public ChooseDelieveryAddressAdapter(Context context, List<DeliveryAddress> listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        final DeliveryAddress item = listData.get(position);
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_address, null);
            holder = new ViewHolder();
            holder.addressDialogView = (RelativeLayout) convertView.findViewById(R.id.parent_view);
            holder.lastNameView = (TextView) convertView.findViewById(R.id.tv_last_name);
            holder.streetView = (TextView) convertView.findViewById(R.id.tv_address);
            holder.streetNumView = (TextView) convertView.findViewById(R.id.tv_street_number);
            holder.cityView = (TextView) convertView.findViewById(R.id.tv_city);
            holder.phoneNumView = (TextView) convertView.findViewById(R.id.tv_phone_number);
            holder.removeView = (ImageView) convertView.findViewById(R.id.iv_remove);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.addressDialogView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i=0; i<listData.size(); i++)
                    listData.get(i).setDefaultAddress(false);
                listData.get(position).setDefaultAddress(true);
                ((ConfirmActivity) context).setAddress(listData, position);
            }
        });

        holder.removeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listData.remove(position);
                ((ConfirmActivity) context).addAddress(listData, false);
                notifyDataSetChanged();
            }
        });

        holder.lastNameView.setText(item.getLastName());
        holder.streetView.setText(item.getStreet());
        holder.streetNumView.setText(item.getStreetNumber());
        holder.cityView.setText(item.getCity());
        holder.phoneNumView.setText(item.getPhoneNumber());

        return convertView;
    }

    private static class ViewHolder {
        RelativeLayout addressDialogView;
        TextView lastNameView;
        TextView streetView;
        TextView streetNumView;
        TextView cityView;
        TextView phoneNumView;
        ImageView removeView;
    }

}