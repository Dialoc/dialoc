package com.example.owner.dialoc;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class UserReportAdapter extends RecyclerView.Adapter<UserReportAdapter.MyViewHolder> {

    private List<UserReport> userReportList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView reportType;
        public TextView reportNumber;

        public MyViewHolder(View view) {
            super(view);

            reportType = (TextView) view.findViewById(R.id.user_report_type);
            reportNumber = (TextView) view.findViewById(R.id.user_report_number);
        }
    }

    public UserReportAdapter(List<UserReport> userReportList) {
        this.userReportList = userReportList;
    }

    @Override
    public UserReportAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_report_row, parent, false);
        return new UserReportAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(UserReportAdapter.MyViewHolder holder, int position) {
        UserReport result = userReportList.get(position);
        holder.reportType.setText(result.getReportType());
        String numberOfReports = "Number of users: " + result.getNumberOfReports();
        holder.reportNumber.setText(numberOfReports);
    }

    @Override
    public int getItemCount() {
        return userReportList.size();
    }
}
