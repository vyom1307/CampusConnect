package com.example.movement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class Custom_adapter_gridview_complaint extends BaseAdapter {



        private Context context;
        private List<String> roomTypes;
        private int[] imageIds;  // If you have icons for each complaint type

        public Custom_adapter_gridview_complaint(Context context, List<String> roomTypes, int[] imageIds) {
            this.context = context;
            this.roomTypes = roomTypes;
            this.imageIds = imageIds;
        }

        @Override
        public int getCount() {
            return roomTypes.size();
        }

        @Override
        public Object getItem(int position) {
            return roomTypes.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                // Inflate the grid item layout
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.grid_list, parent, false);
            }

            // Set data to the views
            ImageView icon = convertView.findViewById(R.id.type_logo);
            TextView label = convertView.findViewById(R.id.type_name);

            icon.setImageResource(imageIds[position]);  // Set the appropriate icon
            label.setText(roomTypes.get(position));     // Set the complaint type

            return convertView;
        }
    }


