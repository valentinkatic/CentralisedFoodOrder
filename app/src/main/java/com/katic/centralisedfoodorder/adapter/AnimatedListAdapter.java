package com.katic.centralisedfoodorder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.katic.centralisedfoodorder.R;
import com.katic.centralisedfoodorder.RestaurantActivity;
import com.katic.centralisedfoodorder.classes.Cart;
import com.katic.centralisedfoodorder.classes.ChildHolder;
import com.katic.centralisedfoodorder.classes.ChildItem;
import com.katic.centralisedfoodorder.classes.GroupHolder;
import com.katic.centralisedfoodorder.classes.GroupItem;
import com.katic.centralisedfoodorder.classes.Restaurant;

import java.util.ArrayList;
import java.util.List;

public class AnimatedListAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {
    private LayoutInflater inflater;

    private List<GroupItem> items;
    private Long id;
    private List<Cart> cart = RestaurantActivity.cart;
    Context context;

    public AnimatedListAdapter(Context context, Long id) {
        inflater = LayoutInflater.from(context);
        this.id = id;
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
            holder.hint = (TextView) convertView.findViewById(R.id.textHint);
            holder.invisible = (TextView) convertView.findViewById(R.id.invisible);
            holder.addToCart = (ImageView) convertView.findViewById(R.id.addToCart) ;
            holder.invisible.setVisibility(View.GONE);
            convertView.setTag(holder);
        } else {
            holder = (ChildHolder) convertView.getTag();
        }

        holder.title.setText(item.title);
        holder.hint.setText(item.hint);
        holder.invisible.setText(item.invisible);

        for (int i=0; i<cart.size(); i++){
            Cart current = cart.get(i);
            if(current.ID==id && current.markedFoodChild == childPosition && current.markedFoodGroup == groupPosition)
                item.addedToCart=true;
        }

        if (item.addedToCart) {
            holder.addToCart.setImageResource(R.drawable.checkout);
        } else {
            holder.addToCart.setImageResource(R.drawable.add_to_cart);
        }

        holder.addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(item.addedToCart){
                    item.addedToCart = false;
                    for (int i=0; i<cart.size(); i++){
                        Cart current = cart.get(i);
                        if(current.ID==id && current.markedFoodChild == childPosition && current.markedFoodGroup == groupPosition) {
                            cart.remove(i);
                            holder.addToCart.setImageResource(R.drawable.add_to_cart);
                        }
                    }
                }
                else {
                    item.addedToCart = true;
                    Cart cartItem = new Cart(id, groupPosition, childPosition);
                    cart.add(cartItem);
                    holder.addToCart.setImageResource(R.drawable.checkout);
                }
                ((RestaurantActivity)context).addToCart(cart);
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!item.clicked) {
                    item.clicked=true;
                    holder.invisible.setVisibility(View.VISIBLE);
                } else {
                    item.clicked=false;
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
