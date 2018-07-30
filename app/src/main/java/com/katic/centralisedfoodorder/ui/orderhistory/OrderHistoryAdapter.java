package com.katic.centralisedfoodorder.ui.orderhistory;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.katic.centralisedfoodorder.R;
import com.katic.centralisedfoodorder.data.models.Cart;
import com.katic.centralisedfoodorder.data.models.CartItem;
import com.katic.centralisedfoodorder.utils.Utils;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OrderHistoryAdapter extends BaseExpandableListAdapter {

    private Activity mActivity;
    private List<Cart> mOrderHistory;
    private OrderHistoryListener mOrderHistoryListener;

    public OrderHistoryAdapter(Activity activity, List<Cart> mOrderHistory, OrderHistoryListener mOrderHistoryListener) {
        this.mActivity = activity;
        this.mOrderHistory = mOrderHistory;
        this.mOrderHistoryListener = mOrderHistoryListener;
    }

    @Override
    public int getGroupCount() {
        return mOrderHistory.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return mOrderHistory.get(getGroupPosition(groupPosition)).getCartItems().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mOrderHistory.get(getGroupPosition(groupPosition));
    }

    private int getGroupPosition(int groupPosition){
        return getGroupCount() - 1 - groupPosition;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mOrderHistory.get(getGroupPosition(groupPosition)).getCartItems().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return getGroupPosition(groupPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder viewHolder;
        if (convertView == null) {
            convertView = mActivity.getLayoutInflater().inflate(R.layout.order_history_group, null);
            viewHolder = new GroupViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (GroupViewHolder) convertView.getTag();
        }

        final Cart order = (Cart) getGroup(groupPosition);

        viewHolder.tvRestaurantName.setText(order.getRestaurantName());
        viewHolder.tvOrderDate.setText(Utils.getDisplayDate(order.getOrderDate()));

        viewHolder.ivAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOrderHistoryListener.addToCart(order);
            }
        });

        viewHolder.ivOrderRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOrderHistory.remove(order);
                notifyDataSetChanged();
                mOrderHistoryListener.removeOrder(order);
            }
        });

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder viewHolder;
        if (convertView == null) {
            convertView = mActivity.getLayoutInflater().inflate(R.layout.order_history_list, null);
            viewHolder = new ChildViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ChildViewHolder) convertView.getTag();
        }

        CartItem cartItem = (CartItem) getChild(groupPosition, childPosition);

        String foodTitle = cartItem.getTitle();
        if (cartItem.getSize() != null && cartItem.getSize().trim().length() > 0){
            foodTitle += "(" + cartItem.getSize() + ")";
        }
        viewHolder.tvFoodTitle.setText(foodTitle);
        viewHolder.tvFoodType.setText(cartItem.getType());
        viewHolder.tvFoodPrice.setText(String.format(Locale.getDefault(),"(%d x %.2f) %.2f kn", cartItem.getAmount(), cartItem.getPrice(), cartItem.getPrice() * cartItem.getAmount()));

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    class GroupViewHolder {
        @BindView(R.id.tv_restaurant_name) TextView tvRestaurantName;
        @BindView(R.id.tv_order_date) TextView tvOrderDate;
        @BindView(R.id.iv_order_remove) ImageView ivOrderRemove;
        @BindView(R.id.iv_add_to_cart) ImageView ivAddToCart;

        public GroupViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }

    class ChildViewHolder {
        @BindView(R.id.tv_food_title) TextView tvFoodTitle;
        @BindView(R.id.tv_food_type) TextView tvFoodType;
        @BindView(R.id.tv_food_price) TextView tvFoodPrice;

        public ChildViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }


    public interface OrderHistoryListener {

        void removeOrder(Cart order);

        void addToCart(Cart order);

    }
}
