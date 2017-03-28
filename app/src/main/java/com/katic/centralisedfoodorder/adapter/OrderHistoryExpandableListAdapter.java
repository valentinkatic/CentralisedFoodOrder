package com.katic.centralisedfoodorder.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.katic.centralisedfoodorder.OrderHistoryActivity;
import com.katic.centralisedfoodorder.R;
import com.katic.centralisedfoodorder.classes.ChildHolder;
import com.katic.centralisedfoodorder.classes.ChildItem;
import com.katic.centralisedfoodorder.classes.GroupHolder;
import com.katic.centralisedfoodorder.classes.GroupItem;

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
        return items.get(groupPosition).getItems().get(childPosition);
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

        holder.title.setText(item.getTitle());
        holder.type.setText(item.getType());
        holder.price.setText(String.format(Locale.getDefault(),"%.2f kn", item.getPrice()));
        holder.ingredients.setText(item.getIngredients());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!item.isClicked()) {
                    item.setClicked(true);
                    holder.ingredients.setVisibility(View.VISIBLE);
                } else {
                    item.setClicked(false);
                    holder.ingredients.setVisibility(View.GONE);
                }
            }
        });

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return items.get(groupPosition).getItems().size();
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

    int pos;

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
            holder.navigationView = (ImageView) convertView.findViewById(R.id.order_history_navigation);
            holder.date = (TextView) convertView.findViewById(R.id.order_history_date);
            convertView.setTag(holder);
        } else {
            holder = (GroupHolder) convertView.getTag();
        }

        holder.title.setText(item.getTitle());
        holder.date.setText(item.getOrderTime());

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

        holder.navigationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pos = groupPosition;
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.
                        setMessage(context.getText(R.string.instructions))
                        .setNegativeButton(context.getText(R.string.no), dialogClickListener)
                        .setPositiveButton(context.getText(R.string.yes), dialogClickListener)
                        .show();

            }
        });

        return convertView;
    }

    private DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    MapMethod();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        }
    };

    public void MapMethod() {
        Uri gmmIntentUri = Uri.parse("google.navigation:q="+ items.get(pos).getAddress()+" "+items.get(pos).getCity() +"&mode=d");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(mapIntent);
        }
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
