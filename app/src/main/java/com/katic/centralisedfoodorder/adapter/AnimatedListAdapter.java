package com.katic.centralisedfoodorder.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.katic.centralisedfoodorder.R;
import com.katic.centralisedfoodorder.RestaurantActivity;

import java.util.List;

public class AnimatedListAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {
    private LayoutInflater inflater;

    private List<RestaurantActivity.GroupItem> items;
    boolean clickedChild = false;

    public AnimatedListAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    public void setData(List<RestaurantActivity.GroupItem> items) {
        this.items = items;
    }

    @Override
    public RestaurantActivity.ChildItem getChild(int groupPosition, int childPosition) {
        return items.get(groupPosition).items.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final RestaurantActivity.ChildHolder holder;
       RestaurantActivity.ChildItem item = getChild(groupPosition, childPosition);
        if (convertView == null) {
            holder = new RestaurantActivity.ChildHolder();
            convertView = inflater.inflate(R.layout.list_item, parent, false);
            holder.title = (TextView) convertView.findViewById(R.id.textTitle);
            holder.hint = (TextView) convertView.findViewById(R.id.textHint);
            holder.invisible = (TextView) convertView.findViewById(R.id.invisible);
            holder.invisible.setVisibility(View.GONE);
            convertView.setTag(holder);
        } else {
            holder = (RestaurantActivity.ChildHolder) convertView.getTag();
        }

        holder.title.setText(item.title);
        holder.hint.setText(item.hint);
        holder.invisible.setText(item.invisible);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!clickedChild) {
                    clickedChild=true;
                    holder.invisible.setVisibility(View.VISIBLE);
                } else {
                    clickedChild=false;
                    holder.invisible.setVisibility(View.GONE);
                }
            }
        });

        return convertView;
    }

    @Override
    public int getRealChildrenCount(int groupPosition) {
        return items.get(groupPosition).items.size();
    }

    @Override
    public RestaurantActivity.GroupItem getGroup(int groupPosition) {
        return items.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return items.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final RestaurantActivity.GroupHolder holder;
        RestaurantActivity.GroupItem item = getGroup(groupPosition);
        if (convertView == null) {
            holder = new RestaurantActivity.GroupHolder();
            convertView = inflater.inflate(R.layout.group_item, parent, false);
            holder.title = (TextView) convertView.findViewById(R.id.textTitle);
            holder.groupImageView = (ImageView) convertView.findViewById(R.id.groupImageView);
            convertView.setTag(holder);
        } else {
            holder = (RestaurantActivity.GroupHolder) convertView.getTag();
        }

        holder.title.setText(item.title);

        if(!item.clickedGroup) holder.groupImageView.setImageResource(R.drawable.right);
        else holder.groupImageView.setImageResource(R.drawable.down);


        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int arg0, int arg1) {
        return true;
    }

}
