package com.katic.centralisedfoodorder;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChooseActivity extends Activity {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        expListView = (ExpandableListView) findViewById(R.id.expandableListView);

        prepareListData();

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        expListView.setAdapter(listAdapter);

    }

    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("Izbor restorana");
        listDataHeader.add("Izbor kuhinje");

        // Adding child data
        List<String> izborRestorana = new ArrayList<String>();
        izborRestorana.add("Corner");
        izborRestorana.add("Galija");
        izborRestorana.add("Karaka");
        izborRestorana.add("Lipov Hlad");
        izborRestorana.add("Rustika");

        List<String> izborKuhinje = new ArrayList<String>();
        izborKuhinje.add("Kineska");
        izborKuhinje.add("Talijanska");
        izborKuhinje.add("Meksička");
        izborKuhinje.add("Veganska");

        listDataChild.put(listDataHeader.get(0), izborRestorana); // Header, Child data
        listDataChild.put(listDataHeader.get(1), izborKuhinje);
    }

}
