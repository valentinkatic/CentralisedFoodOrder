package com.katic.centralisedfoodorder.ui.confirm;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.katic.centralisedfoodorder.R;
import com.katic.centralisedfoodorder.data.models.DeliveryAddress;
import com.katic.centralisedfoodorder.ui.PresenterInjector;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConfirmActivity extends AppCompatActivity implements ConfirmContract.View, ChooseAddressAdapter.AddressListener, DialogInterface.OnClickListener {

    private ConfirmContract.Presenter mPresenter;

    @BindView(R.id.rg_order_type) RadioGroup mOrderTypeRadioGroup;
    @BindView(R.id.rb_delivery) RadioButton mDeliveryRadioButton;
    @BindView(R.id.rb_pickup) RadioButton mPickupRadioButton;
    @BindView(R.id.rl_delivery) RelativeLayout mDeliveryLayout;
    @BindView(R.id.rl_pickup) RelativeLayout mPickupLayout;
    @BindView(R.id.et_last_name) EditText mEtLastName;
    @BindView(R.id.et_street) EditText mEtStreet;
    @BindView(R.id.et_street_number) EditText mEtStreetNumber;
    @BindView(R.id.et_city) EditText mEtCity;
    @BindView(R.id.et_phone_number) EditText mEtPhoneNumber;
    @BindView(R.id.et_floor) EditText mEtFloor;
    @BindView(R.id.et_last_name_pickup) EditText mEtLastNamePickup;

    private AlertDialog mDialogChooseAddress = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);
        ButterKnife.bind(this);

        initializeUi();

        PresenterInjector.injectConfirmPresenter(this);

        mOrderTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_delivery:
                        onDeliveryChecked();
                        break;
                    case R.id.rb_pickup:
                        onPickupChecked();
                        break;
                    default:
                        break;
                }
            }
        });

        mPresenter.start(getIntent().getExtras());
    }

    private void initializeUi(){
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.confirm);
        }
    }

    @Override
    public void loadDefaultAddress(DeliveryAddress address) {
        mEtLastName.setText(address.getLastName());
        mEtStreet.setText(address.getStreet());
        mEtStreetNumber.setText(address.getStreetNumber());
        mEtCity.setText(address.getCity());
        mEtFloor.setText(address.getFloor());
        mEtPhoneNumber.setText(address.getPhoneNumber());
    }

    @Override
    public void loadLastNamePickup(String lastName) {
        mEtLastNamePickup.setText(lastName);
    }

    @OnClick(R.id.bt_select_address) void selectAddress(){
        mPresenter.onAddressSelectClicked();
    }

    @OnClick(R.id.bt_add_address) void saveAddress(){
        if (!validateInputForm()){
            return;
        }
        DeliveryAddress address = new DeliveryAddress();
        address.setLastName(mEtLastName.getText().toString());
        address.setStreet(mEtStreet.getText().toString());
        address.setStreetNumber(mEtStreetNumber.getText().toString());
        address.setCity(mEtCity.getText().toString());
        address.setFloor(mEtFloor.getText().toString());
        address.setPhoneNumber(mEtPhoneNumber.getText().toString());
        mPresenter.onAddressSaveClicked(address);
    }

    @OnClick(R.id.bt_reset) void reset(){
        mEtLastName.setText("");
        mEtStreet.setText("");
        mEtStreetNumber.setText("");
        mEtCity.setText("");
        mEtFloor.setText("");
        mEtPhoneNumber.setText("");
    }

    @OnClick(R.id.bt_confirm) void confirm(){
        if (mOrderTypeRadioGroup.getCheckedRadioButtonId() == -1){
            showToastMessage(ConfirmContract.KEY_MESSAGE_CHOOSE_ORDER_TYPE);
        } else {
            if (!validateInputForm()){
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getText(R.string.orderConfirm))
                    .setPositiveButton(getText(R.string.yes), this)
                    .setNegativeButton(getText(R.string.no), null)
                    .show();
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (mOrderTypeRadioGroup.getCheckedRadioButtonId()){
            case R.id.rb_delivery:
                DeliveryAddress address = new DeliveryAddress();
                address.setLastName(mEtLastName.getText().toString());
                address.setStreet(mEtStreet.getText().toString());
                address.setStreetNumber(mEtStreetNumber.getText().toString());
                address.setCity(mEtCity.getText().toString());
                address.setFloor(mEtFloor.getText().toString());
                address.setPhoneNumber(mEtPhoneNumber.getText().toString());
                mPresenter.confirmDelivery(address);
                break;
            case R.id.rb_pickup:
                mPresenter.confirmPickup(mEtLastNamePickup.getText().toString());
                break;
            default:
                break;
        }
    }

    @Override
    public void onDeliveryChecked() {
        mDeliveryLayout.setVisibility(View.VISIBLE);
        mPickupLayout.setVisibility(View.GONE);
        mEtLastNamePickup.setError(null);
    }

    @Override
    public void onPickupChecked() {
        mDeliveryLayout.setVisibility(View.GONE);
        mPickupLayout.setVisibility(View.VISIBLE);
        mEtLastName.setError(null);
        mEtStreet.setError(null);
        mEtStreetNumber.setError(null);
        mEtCity.setError(null);
        mEtPhoneNumber.setError(null);
    }

    @Override
    public boolean validateInputForm() {
        boolean valid = true;

        if (mDeliveryRadioButton.isChecked()){
            String lastName = mEtLastName.getText().toString();
            if (TextUtils.isEmpty(lastName)) {
                mEtLastName.setError(getString(R.string.required));
                valid = false;
            } else {
                mEtLastName.setError(null);
            }

            String street = mEtStreet.getText().toString();
            if (TextUtils.isEmpty(street)) {
                mEtStreet.setError(getString(R.string.required));
                valid = false;
            } else {
                mEtStreet.setError(null);
            }

            String streetNum = mEtStreetNumber.getText().toString();
            if (TextUtils.isEmpty(streetNum)) {
                mEtStreetNumber.setError(getString(R.string.required));
                valid = false;
            } else {
                mEtStreetNumber.setError(null);
            }

            String city = mEtCity.getText().toString();
            if (TextUtils.isEmpty(city)) {
                mEtCity.setError(getString(R.string.required));
                valid = false;
            } else {
                mEtCity.setError(null);
            }

            String phoneNum = mEtPhoneNumber.getText().toString();
            if (TextUtils.isEmpty(phoneNum)) {
                mEtPhoneNumber.setError(getString(R.string.required));
                valid = false;
            } else {
                mEtPhoneNumber.setError(null);
            }
        } else if (mPickupRadioButton.isChecked()){
            String lastName = mEtLastNamePickup.getText().toString();
            if (TextUtils.isEmpty(lastName)) {
                mEtLastNamePickup.setError(getString(R.string.required));
                valid = false;
            } else {
                mEtLastNamePickup.setError(null);
            }
        } else {
            valid = false;
        }

        return valid;
    }

    @Override
    public void setPresenter(ConfirmContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void startSelectAddressDialog(List<DeliveryAddress> addresses) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.ChooseAddressDialog);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_choose_address, null);
        RecyclerView addressesRecyclerView = dialogView.findViewById(R.id.rv_addresses);
        addressesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        ChooseAddressAdapter addressAdapter = new ChooseAddressAdapter(this, addresses);
        addressesRecyclerView.setAdapter(addressAdapter);

        TextView title = new TextView(this);
        title.setText(R.string.choose_address);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);
        title.setPadding(20, 20, 20, 20);

        builder.setView(dialogView);
        builder.setCustomTitle(title);

        mDialogChooseAddress = builder.create();
        mDialogChooseAddress.show();
    }

    @Override
    public void onAddressSelected(DeliveryAddress deliveryAddress) {
        if (mDialogChooseAddress != null && mDialogChooseAddress.isShowing()){
            mDialogChooseAddress.dismiss();
        }
        mPresenter.onAddressSelected(deliveryAddress);
    }

    @Override
    public void onAddressRemoved(DeliveryAddress deliveryAddress) {
        mPresenter.removeAddress(deliveryAddress);
    }

    @Override
    public void showToastMessage(int messageID) {
        String message = null;
        switch (messageID){
            case ConfirmContract.KEY_MESSAGE_ADDRESS_SAVED:
                message = getString(R.string.address_save);
                break;
            case ConfirmContract.KEY_MESSAGE_ADDRESS_REMOVED:
                message = getString(R.string.address_delete);
                break;
            case ConfirmContract.KEY_MESSAGE_ADDRESS_SET:
                message = getString(R.string.address_set);
                break;
            case ConfirmContract.KEY_MESSAGE_CHOOSE_ORDER_TYPE:
                message = getString(R.string.pickMethod);
                break;
                case ConfirmContract.KEY_MESSAGE_ORDER_SENT:
                message = getString(R.string.order_sent);
                break;
            default:
                break;
        }
        if (message != null){
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void navigateToActivity(Class activity, boolean orderSent) {
        Intent intent = new Intent(this, activity);
        startActivity(intent);
        if (orderSent){
            finishAffinity();
        }
    }

    @Override
    public void dismissView() {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_order_history:
                mPresenter.onOrderHistoryClicked();
                break;
            case R.id.menu_logout:
                mPresenter.signOut(this);
                break;
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        dismissView();
    }
}
