package com.katic.centralisedfoodorder.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.katic.centralisedfoodorder.R;
import com.katic.centralisedfoodorder.RestaurantActivity;
import com.katic.centralisedfoodorder.classes.ChildHolder;
import com.katic.centralisedfoodorder.classes.ChildItem;
import com.katic.centralisedfoodorder.classes.GroupHolder;
import com.katic.centralisedfoodorder.classes.GroupItem;

import java.util.List;
import java.util.Locale;

public class AnimatedListAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private LayoutInflater inflater;

    private List<GroupItem> items;
    private String resturantName;
    private List<GroupItem> cart = RestaurantActivity.cart;
    private Context context;
    private boolean anon;

    public AnimatedListAdapter(Context context, String resturantName) {
        inflater = LayoutInflater.from(context);
        this.resturantName = resturantName;
        this.context = context;

        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                anon = user == null || user.isAnonymous();
            }
        });
    }

    public void setData(List<GroupItem> items) {
        this.items = items;
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
    public View getRealChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final ChildHolder holder;
        final ChildItem item = getChild(groupPosition, childPosition);
        if (convertView == null) {
            holder = new ChildHolder();
            convertView = inflater.inflate(R.layout.list_item, parent, false);
            holder.title = (TextView) convertView.findViewById(R.id.textTitle);
            holder.price = (TextView) convertView.findViewById(R.id.textPrice);
            holder.ingredients = (TextView) convertView.findViewById(R.id.ingredients);
            holder.addToCart = (ImageView) convertView.findViewById(R.id.addToCart) ;
            if (anon) holder.addToCart.setVisibility(View.GONE);
            holder.ingredients.setVisibility(View.GONE);
            convertView.setTag(holder);
        } else {
            holder = (ChildHolder) convertView.getTag();
        }

        holder.title.setText(item.getTitle());
        if(item.getPrice()!=0)
        holder.price.setText(String.format(Locale.getDefault(), "%.2f kn", item.getPrice()));
        else {
            holder.price.setText(R.string.choose_size);

            View.OnClickListener pizzaDialog = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Dialog dialog = new Dialog(context);
                    View dialogView =inflater.inflate(R.layout.dialog_main, null);
                    ListView lv = (ListView) dialogView.findViewById(R.id.custom_list);

                    CustomListAdapterDialog clad = new CustomListAdapterDialog(context, item.getPizza(), resturantName, item.getTitle(), item.getIngredients());

                    lv.setAdapter(clad);

                    dialog.setTitle(item.getTitle());
                    dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_box);
                    dialog.setContentView(dialogView);

                    dialog.show();
                }
            };
            holder.addToCart.setOnClickListener(pizzaDialog);
        }
        holder.ingredients.setText(item.getIngredients());

        for (int i=0; i<cart.size(); i++){
            GroupItem current = cart.get(i);
            if(current.getTitle().equals(resturantName))
                for (int j=0; j<current.getItems().size(); j++)
                    if(current.getItems().get(j).getTitle().equals(item.getTitle()) && current.getItems().get(j).getIngredients().equals(item.getIngredients()))
                        item.setAddedToCart(true);
        }

        if (item.isAddedToCart()) {
            holder.addToCart.setImageResource(R.drawable.checkout);
        } else {
            holder.addToCart.setImageResource(R.drawable.add_to_cart);
        }

        if(item.getPrice()!=0)
        holder.addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (item.isAddedToCart()) {
                    item.setAddedToCart(false);
                    for (int i = 0; i < cart.size(); i++) {
                        GroupItem current = cart.get(i);
                        if (current.getTitle().equals(resturantName))
                            for (int j = 0; j < current.getItems().size(); j++)
                                if (current.getItems().get(j).getTitle().equals(item.getTitle()) && current.getItems().get(j).getIngredients().equals(item.getIngredients())) {
                                    cart.get(i).getItems().remove(j);
                                    holder.addToCart.setImageResource(R.drawable.add_to_cart);
                                }
                     }
                } else {
                    item.setAddedToCart(true);
                    GroupItem groupItem = new GroupItem();
                    groupItem.setTitle(resturantName);
                    ChildItem cartItem = new ChildItem(item.getTitle(), item.getIngredients(), item.getPrice(), getGroup(groupPosition).getTitle(), 1);
                    groupItem.getItems().add(cartItem);
                    cart.add(groupItem);
                    holder.addToCart.setImageResource(R.drawable.checkout);
                }
                ((RestaurantActivity) context).addToCart(resturantName, cart);
            }
        });

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
    public int getRealChildrenCount(int groupPosition) {
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

        holder.title.setText(item.getTitle());

        if(!item.isClickedGroup()) holder.groupImageView.setImageResource(R.drawable.right);
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
