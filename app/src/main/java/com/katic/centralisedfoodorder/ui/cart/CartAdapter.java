package com.katic.centralisedfoodorder.ui.cart;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.katic.centralisedfoodorder.R;
import com.katic.centralisedfoodorder.data.models.CartItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private List<CartItem> mCartItems;
    private CartListener mCartListener;

    public CartAdapter(CartListener cartListener) {
        this.mCartListener = cartListener;
        mCartItems = new ArrayList<>();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_food_title) TextView tvFoodTitle;
        @BindView(R.id.tv_food_type) TextView tvFoodType;
        @BindView(R.id.tv_food_price) TextView tvFoodPrice;
        @BindView(R.id.btn_amount) Button btnAmount;
        Context context;
        CartItem cartItem;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            context = itemView.getContext();
        }

        private void bind(int position){
            cartItem = mCartItems.get(position);

            String foodTitle = cartItem.getTitle();
            if (cartItem.getSize() != null && cartItem.getSize().trim().length() > 0){
                foodTitle += "(" + cartItem.getSize() + ")";
            }
            tvFoodTitle.setText(foodTitle);
            tvFoodType.setText(cartItem.getType());
            setAmountText();
        }

        @OnClick(R.id.btn_remove) void removeCartItem(){
            new AlertDialog.Builder(context)
                .setMessage(context.getText(R.string.are_you_sure))
                    .setPositiveButton(context.getText(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            notifyItemRemoved(getCartItemIndex(cartItem));
                            mCartItems.remove(cartItem);
                            mCartListener.onCartItemRemoved(cartItem);
                        }
                    })
                    .setNegativeButton(context.getText(R.string.no), null)
                    .show();
        }

        @OnClick(R.id.btn_amount) void changeAmount(){
            final Dialog dialog = new Dialog(context);
            View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_quantity_picker, null);

            Button b1 = dialogView.findViewById(R.id.setBtn);
            Button b2 = dialogView.findViewById(R.id.cancelBtn);
            final NumberPicker np = dialogView.findViewById(R.id.numberPicker);
            np.setMaxValue(10);
            np.setMinValue(1);
            np.setValue(cartItem.getAmount());
            np.setWrapSelectorWheel(false);

            b1.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    cartItem.setAmount(np.getValue());
                    setAmountText();
                    mCartListener.onAmountChanged(cartItem);
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

            dialog.setTitle(context.getText(R.string.quantity));
            dialog.setContentView(dialogView);
            dialog.show();

        }

        private void setAmountText(){
            tvFoodPrice.setText(String.format(Locale.getDefault(),"(%d x %.2f) %.2f kn", cartItem.getAmount(), cartItem.getPrice(), cartItem.getPrice() * cartItem.getAmount()));
            btnAmount.setText(String.format(Locale.getDefault(), "%s %d", context.getString(R.string.quantity), cartItem.getAmount()));
        }

        private int getCartItemIndex(CartItem cartItem){
            for (int i = 0; i < getItemCount(); i++){
                if (mCartItems.get(i).equals(cartItem)){
                    return i;
                }
            }
            return -1;
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mCartItems == null ? 0 : mCartItems.size();
    }

    public void setCartItems(List<CartItem> cartItems){
        this.mCartItems = cartItems;
        notifyDataSetChanged();
    }

    public interface CartListener {

        void onCartItemRemoved(CartItem cartItem);

        void onAmountChanged(CartItem cartItem);

    }

}
