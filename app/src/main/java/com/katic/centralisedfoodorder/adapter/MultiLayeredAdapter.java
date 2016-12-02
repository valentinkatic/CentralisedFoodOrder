package com.katic.centralisedfoodorder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.katic.centralisedfoodorder.R;
import com.katic.centralisedfoodorder.RestaurantActivity;

import java.util.ArrayList;
import java.util.List;

public class MultiLayeredAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {
    private LayoutInflater inflater;
    private CustExpListview SecondLevelexplv;

    public MultiLayeredAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        SecondLevelexplv = new CustExpListview(parent.getContext());
        SecondLevelexplv.setAdapter(new SecondAnimatedLevel());
        SecondLevelexplv.setGroupIndicator(null);
        return SecondLevelexplv;

    }

    @Override
    public int getRealChildrenCount(int groupPosition) {
        return 3;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupPosition;
    }

    @Override
    public int getGroupCount() {
        return 5;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }


    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        List<String> mJela = RestaurantActivity.jela;
        convertView = inflater.inflate(R.layout.first_level, parent, false);
        TextView tv = (TextView)  convertView.findViewById(R.id.firstLevel);
        tv.setText(mJela.get(groupPosition));
        ImageView iv = (ImageView) convertView.findViewById(R.id.imageFirst);
        if (isExpanded) {
            iv.setImageResource(R.drawable.down);
        }

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

    public class CustExpListview extends AnimatedExpandableListView {

        int intGroupPosition, intChildPosition, intGroupid;

        public CustExpListview(Context context) {
            super(context);
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            //widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(widthMeasureSpec, MeasureSpec.AT_MOST);
            heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(800, MeasureSpec.AT_MOST);

            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    public class SecondAnimatedLevel extends AnimatedExpandableListView.AnimatedExpandableListAdapter{

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getRealChildView(int groupPosition, int childPosition,
                                     boolean isLastChild, View convertView, ViewGroup parent) {

            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.third_level, parent, false);
            TextView tv = (TextView)  convertView.findViewById(R.id.thirdLevel);
            tv.setText("child");
            return convertView;
        }

        @Override
        public int getRealChildrenCount(int groupPosition) {
            return 1;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return groupPosition;
        }

        @Override
        public int getGroupCount() {
            return 1;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.second_level, parent, false);

            TextView tv = (TextView)  convertView.findViewById(R.id.secondLevel);
            tv.setText("->SecondLevel");
            ImageView iv = (ImageView) convertView.findViewById(R.id.imageSecond);
            if (isExpanded) {
                iv.setImageResource(R.drawable.down);
            }

            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            // TODO Auto-generated method stub
            return true;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            // TODO Auto-generated method stub
            return true;
        }

    }

}

