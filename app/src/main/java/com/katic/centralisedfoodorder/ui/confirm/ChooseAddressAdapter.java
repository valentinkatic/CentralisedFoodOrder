package com.katic.centralisedfoodorder.ui.confirm;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.katic.centralisedfoodorder.R;
import com.katic.centralisedfoodorder.data.models.DeliveryAddress;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChooseAddressAdapter extends RecyclerView.Adapter<ChooseAddressAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tv_last_name) TextView tvLastName;
        @BindView(R.id.tv_address) TextView tvAddress;
        @BindView(R.id.tv_city) TextView tvCity;
        @BindView(R.id.tv_phone_number) TextView tvPhoneNumber;

        private DeliveryAddress address;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        void bind (int position){
            address = mAddresses.get(position);

            tvLastName.setText(address.getLastName());
            tvAddress.setText(String.format(Locale.getDefault(), "%s %s", address.getStreet(), address.getStreetNumber()));
            tvCity.setText(address.getCity());
            tvPhoneNumber.setText(address.getPhoneNumber());
        }

        @OnClick(R.id.iv_remove) void removeAddress(){
            mAddresses.remove(address);
            notifyItemRemoved(getAdapterPosition());
            mAddressListener.onAddressRemoved(address);
        }

        @Override
        public void onClick(View v) {
            mAddressListener.onAddressSelected(address);
        }
    }

    private AddressListener mAddressListener;
    private List<DeliveryAddress> mAddresses;

    public ChooseAddressAdapter(AddressListener mAddressListener, List<DeliveryAddress> mAddresses) {
        this.mAddressListener = mAddressListener;
        this.mAddresses = mAddresses;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mAddresses == null ? 0 : mAddresses.size();
    }

    public interface AddressListener {

        void onAddressSelected(DeliveryAddress deliveryAddress);

        void onAddressRemoved(DeliveryAddress deliveryAddress);

    }
}
