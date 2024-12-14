package com.example.movement;



import static java.security.AccessController.getContext;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.movement.model.Complaint;

import java.util.List;

public class ComplaintAdapter extends ArrayAdapter<Complaint> {

    private Context context;
    private List<Complaint> complaints;

    public ComplaintAdapter(Context context, List<Complaint> complaints) {
        super(context, 0, complaints);
        this.context = context;
        this.complaints = complaints;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Complaint complaint = getItem(position);
        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_complaint_item, parent, false);
        }


        TextView complaintType = listItemView.findViewById(R.id.complaintType);
        TextView complaintText = listItemView.findViewById(R.id.complaintText);

        Complaint currentComplaint = complaints.get(position);

        if (currentComplaint.getStatus().equals("Ongoing")) {
            listItemView.setBackgroundColor(ContextCompat.getColor(context, R.color.holo_green_dark));  // Define this color in res/values/colors.xml
        } else if (currentComplaint.getStatus().equals("Completed")) {
            listItemView.setBackgroundColor(ContextCompat.getColor(context, R.color.blue));  // Define this color in res/values/colors.xml
        }



        // Populate the data into the template view using the complaint object
        complaintType.setText(complaint.getComplaintType());
        complaintText.setText(complaint.getComplaintText());


        return listItemView;
    }

}
