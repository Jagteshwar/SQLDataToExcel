package com.jagteshwar.sqldatatoexcel.db;

class DBConstants {

    static final String EMP_TABLE= "employees";
    static final String EMP_ID = "emp_id";
    static final String EMP_NAME = "emp_name";

    static final String CREATE_EMP_TABLE = "CREATE TABLE IF NOT EXISTS " + EMP_TABLE + " ("
            + EMP_ID + " INTEGER PRIMARY KEY,"
            + EMP_NAME + " TEXT)";

    static final String SELECT_QUERY = "SELECT * FROM " + EMP_TABLE;

}