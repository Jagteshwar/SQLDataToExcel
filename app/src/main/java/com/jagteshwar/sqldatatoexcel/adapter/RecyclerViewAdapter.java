package com.jagteshwar.sqldatatoexcel.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jagteshwar.sqldatatoexcel.R;
import com.jagteshwar.sqldatatoexcel.model.Employees;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {

    List<Employees> employees;

    public RecyclerViewAdapter(List<Employees> employees) {
        this.employees = employees;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.RecyclerViewHolder holder, int position) {
        holder.empId.setText("EmpId: " + employees.get(position).getEmpId());
        holder.empName.setText("EmpName: " + employees.get(position).getEmpName());
    }

    @Override
    public int getItemCount() {
        return employees.size();
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView empName, empId;

        public RecyclerViewHolder(View view) {
            super(view);
            empName = (TextView) view.findViewById(R.id.empName);
            empId = (TextView) view.findViewById(R.id.empId);
        }
    }
}
