package com.jagteshwar.sqldatatoexcel.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.jagteshwar.sqldatatoexcel.model.Employees;

import java.util.ArrayList;

public class DBQueries {

    private Context context;
    private SQLiteDatabase database;
    private DBHelper dbHelper;

    public DBQueries(Context context) {
        this.context = context;
    }

    public DBQueries open() throws SQLException {
        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbHelper.close();
    }

    // Users
    public boolean insertEmployees(Employees employees) {
        ContentValues values = new ContentValues();
        values.put(DBConstants.EMP_NAME, employees.getEmpName());
        return database.insert(DBConstants.EMP_TABLE, null, values) > -1;
    }

    public ArrayList<Employees> readEmployees() {
        ArrayList<Employees> list = new ArrayList<>();
        try {
            Cursor cursor;
            database = dbHelper.getReadableDatabase();
            cursor = database.rawQuery(DBConstants.SELECT_QUERY, null);
            list.clear();
            if (cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    do {

                        int empId = cursor.getInt(cursor.getColumnIndexOrThrow(DBConstants.EMP_ID));
                        String empName = cursor.getString(cursor.getColumnIndexOrThrow(DBConstants.EMP_NAME));
                        Employees employees = new Employees(empId, empName);
                        list.add(employees);
                    } while (cursor.moveToNext());
                }
            }
            cursor.close();
        } catch (Exception e) {
            Log.v("Exception", e.getMessage());
        }
        return list;
    }

}

