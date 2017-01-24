package com.katic.centralisedfoodorder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.katic.centralisedfoodorder.CartActivity;
import com.katic.centralisedfoodorder.OrderHistoryActivity;
import com.katic.centralisedfoodorder.R;
import com.katic.centralisedfoodorder.classes.ChildHolder;
import com.katic.centralisedfoodorder.classes.ChildItem;
import com.katic.centralisedfoodorder.classes.GroupHolder;
import com.katic.centralisedfoodorder.classes.GroupItem;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class OrderHistoryExpandableListAdapter extends BaseExpandableListAdapter {

    private LayoutInflater inflater;

    private static List<GroupItem> items;
    private Context context;

    public OrderHistoryExpandableListAdapter(Context context, List<GroupItem> items) {
        inflater = LayoutInflater.from(context);
        this.items = items;
        this.context = context;
    }

    @Override
    public ChildItem getChild(int groupPosition, int childPosition) {
        return items.get(groupPosition).items.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final ChildHolder holder;
        final ChildItem item = getChild(groupPosition, childPosition);

        if (convertView == null) {
            holder = new ChildHolder();
            convertView = inflater.inflate(R.layout.order_history_list, parent, false);
            holder.title = (TextView) convertView.findViewById(R.id.cartTitle);
            holder.type = (TextView) convertView.findViewById(R.id.cartType);
            holder.price = (TextView) convertView.findViewById(R.id.cartPrice);
            holder.ingredients = (TextView) convertView.findViewById(R.id.cartIngredients);
            holder.ingredients.setVisibility(View.GONE);
            convertView.setTag(holder);
        } else {
            holder = (ChildHolder) convertView.getTag();
        }

        holder.title.setText(item.title);
        holder.type.setText(item.type);
        holder.price.setText(String.format(Locale.getDefault(),"%.2f kn", item.price));
        holder.ingredients.setText(item.ingredients);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!item.clicked) {
                    item.clicked=true;
                    holder.ingredients.setVisibility(View.VISIBLE);
                } else {
                    item.clicked=false;
                    holder.ingredients.setVisibility(View.GONE);
                }
            }
        });

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return items.get(groupPosition).items.size();
    }

    @Override
    public GroupItem getGroup(int groupPosition) {
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
    public View getGroupView(final int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        final GroupHolder holder;
        GroupItem item = getGroup(groupPosition);
        if (convertView == null) {
            holder = new GroupHolder();
            convertView = inflater.inflate(R.layout.order_history_group, parent, false);
            holder.title = (TextView) convertView.findViewById(R.id.order_history_group);
            holder.groupImageView = (ImageView) convertView.findViewById(R.id.order_history_remove);
            holder.groupImageAdditionalView = (ImageView) convertView.findViewById(R.id.order_history_addToCart);
            holder.date = (TextView) convertView.findViewById(R.id.order_history_date);
            convertView.setTag(holder);
        } else {
            holder = (GroupHolder) convertView.getTag();
        }

        holder.title.setText(item.title);
        holder.date.setText(item.orderTime);

        holder.groupImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                items.remove(groupPosition);
                ((OrderHistoryActivity) context).removeFromHistory(items);
            }
        });

        holder.groupImageAdditionalView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((OrderHistoryActivity) context).addToCart(items.get(groupPosition));
            }
        });

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
