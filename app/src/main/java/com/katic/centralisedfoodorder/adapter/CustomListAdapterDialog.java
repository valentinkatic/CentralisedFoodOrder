package com.katic.centralisedfoodorder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.katic.centralisedfoodorder.R;
import com.katic.centralisedfoodorder.RestaurantActivity;
import com.katic.centralisedfoodorder.classes.ChildItem;
import com.katic.centralisedfoodorder.classes.GroupItem;
import com.katic.centralisedfoodorder.classes.Pizza;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CustomListAdapterDialog extends BaseAdapter {

    private List<Pizza> listData;
    private List<GroupItem> cart = RestaurantActivity.cart;
    private String restaurantTitle;
    private String title;
    private String ingredients;

    private LayoutInflater layoutInflater;
    Context context;

    public CustomListAdapterDialog(Context context, List<Pizza> listData, String restaurantTitle, String title, String ingredients) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(context);
        this.restaurantTitle = restaurantTitle;
        this.title = title;
        this.ingredients = ingredients;
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
        final Pizza item = listData.get(position);
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.dialog_item, null);
            holder = new ViewHolder();
            holder.sizeView = (TextView) convertView.findViewById(R.id.dialogSize);
            holder.priceView = (TextView) convertView.findViewById(R.id.dialogPrice);
            holder.cartView = (ImageView) convertView.findViewById(R.id.dialogImg);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.sizeView.setText(item.getSize());
        holder.priceView.setText(String.format(Locale.getDefault(), "%.2f kn", item.getPrice()));
        holder.cartView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (item.isAddedToCart()) {
                    item.setAddedToCart(false);
                    for (int i = 0; i < cart.size(); i++) {
                        GroupItem current = cart.get(i);
                        if (current.getTitle().equals(restaurantTitle))
                            for (int j = 0; j < current.getItems().size(); j++)
                                if (current.getItems().get(j).getTitle().equals(title+" ("+item.getSize()+")")) {
                                    cart.get(i).getItems().remove(j);
                                    holder.cartView.setImageResource(R.drawable.add_to_cart);
                                }
                    }
                } else {
                    item.setAddedToCart(true);
                    GroupItem groupItem = new GroupItem();
                    groupItem.setTitle(restaurantTitle);
                    ChildItem cartItem = new ChildItem(title+" ("+item.getSize()+")", ingredients, item.getPrice(), "Pizze", 1);
                    groupItem.getItems().add(cartItem);
                    cart.add(groupItem);
                    holder.cartView.setImageResource(R.drawable.checkout);
                }
                ((RestaurantActivity) context).addToCart(restaurantTitle, cart);
            }
        });

        for (int i=0; i<cart.size(); i++){
            GroupItem current = cart.get(i);
            if(current.getTitle().equals(restaurantTitle))
                for (int j=0; j<current.getItems().size(); j++)
                    if(current.getItems().get(j).getTitle().equals(title+" ("+item.getSize()+")"))
                        item.setAddedToCart(true);
        }

        if (item.isAddedToCart()) {
            holder.cartView.setImageResource(R.drawable.checkout);
        } else {
            holder.cartView.setImageResource(R.drawable.add_to_cart);
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView sizeView;
        TextView priceView;
        ImageView cartView;
    }

}