package com.katic.centralisedfoodorder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import com.google.firebase.auth.FirebaseAuth;
import com.katic.centralisedfoodorder.adapter.CartExpandableListAdapter;
import com.katic.centralisedfoodorder.classes.GroupItem;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends BaseActivity {

    CartExpandableListAdapter adapter;
    ExpandableListView expListView;


    private List<GroupItem> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Cart");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setElevation(4);
        actionBar.collapseActionView();

        items = RestaurantActivity.items;

        expListView = (ExpandableListView) findViewById(R.id.cartView);

        adapter = new CartExpandableListAdapter(this, items);

        expListView.setAdapter(adapter);

        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int groupPosition, long l) {
                if (expListView.isGroupExpanded(groupPosition)) {
                    items.get(groupPosition).clickedGroup = false;
                    expListView.collapseGroup(groupPosition);
                } else {
                    items.get(groupPosition).clickedGroup = true;
                    expListView.expandGroup(groupPosition);
                }
                return true;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu, menu);
        menu.getItem(0).setIcon(R.drawable.checkout);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.cart:
                //Intent checkout = new Intent(RestaurantActivity.this, CartActivity.class);
                //startActivity(checkout);
                return true;
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(CartActivity.this, MainActivity.class);
                startActivity(intent);
                finishAffinity();
                return true;
            default:
                finish();
                return true;
        }
    }

}
