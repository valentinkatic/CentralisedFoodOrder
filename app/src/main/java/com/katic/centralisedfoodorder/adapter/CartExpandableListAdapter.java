package com.katic.centralisedfoodorder.adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.katic.centralisedfoodorder.CartActivity;
import com.katic.centralisedfoodorder.R;
import com.katic.centralisedfoodorder.RestaurantActivity;
import com.katic.centralisedfoodorder.classes.ChildHolder;
import com.katic.centralisedfoodorder.classes.ChildItem;
import com.katic.centralisedfoodorder.classes.GroupHolder;
import com.katic.centralisedfoodorder.classes.GroupItem;

import java.util.List;
import java.util.Locale;

public class CartExpandableListAdapter extends BaseExpandableListAdapter {

    private LayoutInflater inflater;

    private static List<GroupItem> items;
    private List<GroupItem> cart = CartActivity.cart;
    Context context;

    public CartExpandableListAdapter(Context context, List<GroupItem> items) {
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
            convertView = inflater.inflate(R.layout.cart_list, parent, false);
            holder.title = (TextView) convertView.findViewById(R.id.cartTitle);
            holder.type = (TextView) convertView.findViewById(R.id.cartType);
            holder.price = (TextView) convertView.findViewById(R.id.cartPrice);
            holder.ingredients = (TextView) convertView.findViewById(R.id.cartIngredients);
            holder.remove = (Button) convertView.findViewById(R.id.removeBtn);
            holder.quantity = (Button) convertView.findViewById(R.id.quantityBtn);
            holder.ingredients.setVisibility(View.GONE);
            convertView.setTag(holder);
        } else {
            holder = (ChildHolder) convertView.getTag();
        }

        holder.title.setText(item.getTitle());
        holder.type.setText(item.getType());
        holder.price.setText(String.format(Locale.getDefault(),"%.2f kn", item.getPrice()*item.getQuantity()));
        holder.ingredients.setText(item.getIngredients());
        holder.quantity.setText(context.getText(R.string.quantity)+" "+Integer.toString(item.getQuantity()));

        final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        String restaurantTitle = getGroup(groupPosition).getTitle();
                        item.setAddedToCart(false);
                        for (int i = 0; i < cart.size(); i++) {
                            GroupItem current = cart.get(i);
                            if (current.getTitle().equals(restaurantTitle))
                                for (int j = 0; j < current.getItems().size(); j++)
                                    if (current.getItems().get(j).getTitle().equals(item.getTitle())) {
                                        cart.get(i).getItems().remove(j);
                                    }
                        }
                        ((CartActivity) context).addToCart(restaurantTitle, cart);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(context.getText(R.string.are_you_sure)).setPositiveButton(context.getText(R.string.yes), dialogClickListener)
                        .setNegativeButton(context.getText(R.string.no), dialogClickListener).show();
            }
        });

        holder.quantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                View dialogView = inflater.inflate(R.layout.dialog_quantity_picker, null);
                dialog.setTitle(context.getText(R.string.quantity));
                Button b1 = (Button) dialogView.findViewById(R.id.setBtn);
                Button b2 = (Button) dialogView.findViewById(R.id.cancelBtn);
                final NumberPicker np = (NumberPicker) dialogView.findViewById(R.id.numberPicker);

                np.setMaxValue(10);
                np.setMinValue(1);
                np.setValue(item.getQuantity());
                np.setWrapSelectorWheel(false);
                b1.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        item.setQuantity(np.getValue());
                        holder.quantity.setText(context.getText(R.string.quantity)+" "+Integer.toString(item.getQuantity()));

                        String restaurantTitle = getGroup(groupPosition).getTitle();
                        item.setAddedToCart(false);
                        for (int i = 0; i < cart.size(); i++) {
                            GroupItem current = cart.get(i);
                            if (current.getTitle().equals(restaurantTitle))
                                for (int j = 0; j < current.getItems().size(); j++)
                                    if (current.getItems().get(j).getTitle().equals(item.getTitle())) {
                                        cart.get(i).getItems().get(j).setQuantity(item.getQuantity());
                                    }
                        }
                        ((CartActivity) context).addToCart(restaurantTitle, cart);
                        holder.price.setText(String.format(Locale.getDefault(),"%.2f kn", item.getPrice()*item.getQuantity()));

                        dialog.dismiss();
                    }
                });
                b2.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.setContentView(dialogView);
                dialog.show();

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

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        final GroupHolder holder;
        GroupItem item = getGroup(groupPosition);
        if (convertView == null) {
            holder = new GroupHolder();
            convertView = inflater.inflate(R.layout.cart_group, parent, false);
            holder.title = (TextView) convertView.findViewById(R.id.cartGroup);
            convertView.setTag(holder);
        } else {
            holder = (GroupHolder) convertView.getTag();
        }

        holder.title.setText(item.getTitle());
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
