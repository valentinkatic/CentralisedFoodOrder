package com.katic.centralisedfoodorder.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.katic.centralisedfoodorder.R;
import com.katic.centralisedfoodorder.RestaurantActivity;
import com.katic.centralisedfoodorder.classes.ChildHolder;
import com.katic.centralisedfoodorder.classes.ChildItem;
import com.katic.centralisedfoodorder.classes.GroupHolder;
import com.katic.centralisedfoodorder.classes.GroupItem;

import java.util.List;

public class AnimatedListAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {
    private LayoutInflater inflater;

    private List<GroupItem> items;
    private String resturantName;
    private List<GroupItem> cart = RestaurantActivity.cart;
    Context context;

    public AnimatedListAdapter(Context context, String resturantName) {
        inflater = LayoutInflater.from(context);
        this.resturantName = resturantName;
        this.context = context;
    }

    public void setData(List<GroupItem> items) {
        this.items = items;
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
    public View getRealChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final ChildHolder holder;
        final ChildItem item = getChild(groupPosition, childPosition);
        if (convertView == null) {
            holder = new ChildHolder();
            convertView = inflater.inflate(R.layout.list_item, parent, false);
            holder.title = (TextView) convertView.findViewById(R.id.textTitle);
            holder.price = (TextView) convertView.findViewById(R.id.textHint);
            holder.ingredients = (TextView) convertView.findViewById(R.id.invisible);
            holder.addToCart = (ImageView) convertView.findViewById(R.id.addToCart) ;
            holder.ingredients.setVisibility(View.GONE);
            convertView.setTag(holder);
        } else {
            holder = (ChildHolder) convertView.getTag();
        }

        holder.title.setText(item.title);
        if(item.price!=0)
        holder.price.setText(String.format("%.2f", item.price) + " kn");
        else {
            holder.price.setText(R.string.choose_size);
            holder.addToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Dialog dialog = new Dialog(context);
                    View dialogView =inflater.inflate(R.layout.dialog_main, null);
                    ListView lv = (ListView) dialogView.findViewById(R.id.custom_list);

                    CustomListAdapterDialog clad = new CustomListAdapterDialog(context, item.pizza, resturantName, item.title, item.ingredients);

                    lv.setAdapter(clad);

                    dialog.setTitle(item.title);
                    dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_box);
                    dialog.setContentView(dialogView);

                    dialog.show();
                }
            });
        }
        holder.ingredients.setText(item.ingredients);

        for (int i=0; i<cart.size(); i++){
            GroupItem current = cart.get(i);
            if(current.title.equals(resturantName))
                for (int j=0; j<current.items.size(); j++)
                    if(current.items.get(j).title.equals(item.title) && current.items.get(j).ingredients.equals(item.ingredients))
                        item.addedToCart=true;
        }

        if (item.addedToCart) {
            holder.addToCart.setImageResource(R.drawable.checkout);
        } else {
            holder.addToCart.setImageResource(R.drawable.add_to_cart);
        }

        if(item.price!=0)
        holder.addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (item.addedToCart) {
                    item.addedToCart = false;
                    for (int i = 0; i < cart.size(); i++) {
                        GroupItem current = cart.get(i);
                        if (current.title.equals(resturantName))
                            for (int j = 0; j < current.items.size(); j++)
                                if (current.items.get(j).title.equals(item.title) && current.items.get(j).ingredients.equals(item.ingredients)) {
                                    cart.get(i).items.remove(j);
                                    holder.addToCart.setImageResource(R.drawable.add_to_cart);
                                }
                     }
                } else {
                    item.addedToCart = true;
                    GroupItem groupItem = new GroupItem();
                    groupItem.title = resturantName;
                    ChildItem cartItem = new ChildItem(item.title, item.ingredients, item.price, getGroup(groupPosition).title);
                    groupItem.items.add(cartItem);
                    cart.add(groupItem);
                    holder.addToCart.setImageResource(R.drawable.checkout);
                }
                ((RestaurantActivity) context).addToCart(resturantName, cart);
            }
        });

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
    public int getRealChildrenCount(int groupPosition) {
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
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final GroupHolder holder;
        GroupItem item = getGroup(groupPosition);
        if (convertView == null) {
            holder = new GroupHolder();
            convertView = inflater.inflate(R.layout.group_item, parent, false);
            holder.title = (TextView) convertView.findViewById(R.id.textTitle);
            holder.groupImageView = (ImageView) convertView.findViewById(R.id.groupImageView);
            convertView.setTag(holder);
        } else {
            holder = (GroupHolder) convertView.getTag();
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
