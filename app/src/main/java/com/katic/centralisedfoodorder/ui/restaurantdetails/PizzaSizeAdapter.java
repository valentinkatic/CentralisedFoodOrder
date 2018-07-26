package com.katic.centralisedfoodorder.ui.restaurantdetails;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.katic.centralisedfoodorder.R;
import com.katic.centralisedfoodorder.data.models.Pizza;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PizzaSizeAdapter extends RecyclerView.Adapter<PizzaSizeAdapter.ViewHolder> {

    private List<Pizza> mPizzaList;
    private PizzaSizeListener mPizzaSizeListener;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.iv_check) ImageView ivCheck;
        @BindView(R.id.tv_size) TextView tvSize;
        @BindView(R.id.tv_price) TextView tvPrice;
        private Context context;
        private Pizza pizza;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
            context = itemView.getContext();
        }

        void bind(int position){
            pizza = mPizzaList.get(position);

            tvSize.setText(pizza.getSize());
            tvPrice.setText(String.format(Locale.getDefault(), "%.2f kn", pizza.getPrice()));

            if (pizza.isChecked()) {
                imageViewAnimatedChange(context, ivCheck, true);
            } else {
                imageViewAnimatedChange(context, ivCheck, false);
            }
        }

        @Override
        public void onClick(View v) {
            if (pizza.isChecked()){
                pizza.setChecked(false);
            } else {
                for (int i = 0; i < getItemCount(); i++) {
                    mPizzaList.get(i).setChecked(false);
                }
                pizza.setChecked(true);
                mPizzaSizeListener.onSizeClicked(pizza);
            }
            notifyDataSetChanged();
        }
    }

    public PizzaSizeAdapter(List<Pizza> pizzaList, PizzaSizeListener pizzaSizeListener) {
        this.mPizzaList = pizzaList;
        this.mPizzaSizeListener = pizzaSizeListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pizza_size, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mPizzaList.size();
    }

    private void imageViewAnimatedChange(Context c, final ImageView v, boolean adding) {
        final Animation anim;
        if (adding){
            anim = AnimationUtils.loadAnimation(c, android.R.anim.fade_in);
            v.setImageResource(R.drawable.ic_check_circle_black_24dp);
        } else {
            anim = AnimationUtils.loadAnimation(c, android.R.anim.fade_out);
            v.setImageDrawable(null);
        }

        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation) {}
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override public void onAnimationEnd(Animation animation) {}
        });
        v.startAnimation(anim);

    }

    public interface PizzaSizeListener {
        void onSizeClicked(Pizza pizza);
    }
}
