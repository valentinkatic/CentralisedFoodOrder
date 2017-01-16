package com.katic.centralisedfoodorder.adapter;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.katic.centralisedfoodorder.ConfirmActivity;
import com.katic.centralisedfoodorder.R;
import com.katic.centralisedfoodorder.RestaurantActivity;
import com.katic.centralisedfoodorder.classes.ChildItem;
import com.katic.centralisedfoodorder.classes.DelieveryAddress;
import com.katic.centralisedfoodorder.classes.GroupItem;
import com.katic.centralisedfoodorder.classes.Pizza;

import java.util.List;

public class ChooseDelieveryAddressAdapter extends BaseAdapter {

    private List<DelieveryAddress> listData;

    private LayoutInflater layoutInflater;
    Context context;

    public ChooseDelieveryAddressAdapter(Context context, List<DelieveryAddress> listData) {
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
        final DelieveryAddress item = listData.get(position);
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.dialog_addresses, null);
            holder = new ViewHolder();
            holder.addressDialogView = (RelativeLayout) convertView.findViewById(R.id.addressDialog);
            holder.lastNameView = (TextView) convertView.findViewById(R.id.lastNameDialog);
            holder.streetView = (TextView) convertView.findViewById(R.id.streetDialog);
            holder.streetNumView = (TextView) convertView.findViewById(R.id.streetNumberDialog);
            holder.cityView = (TextView) convertView.findViewById(R.id.cityDialog);
            holder.phoneNumView = (TextView) convertView.findViewById(R.id.phoneNumberDialog);
            holder.removeView = (ImageView) convertView.findViewById(R.id.removeDialog);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.addressDialogView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i=0; i<listData.size(); i++)
                    listData.get(i).defaultAddress=false;
                listData.get(position).defaultAddress=true;
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

        holder.lastNameView.setText(item.lastName);
        holder.streetView.setText(item.street);
        holder.streetNumView.setText(item.streetNumber);
        holder.cityView.setText(item.city);
        holder.phoneNumView.setText(item.phoneNumber);

        return convertView;
    }

    static class ViewHolder {
        RelativeLayout addressDialogView;
        TextView lastNameView;
        TextView streetView;
        TextView streetNumView;
        TextView cityView;
        TextView phoneNumView;
        ImageView removeView;
    }

}