package com.jagteshwar.sqldatatoexcel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jagteshwar.sqldatatoexcel.adapter.RecyclerViewAdapter;
import com.jagteshwar.sqldatatoexcel.db.DBHelper;
import com.jagteshwar.sqldatatoexcel.db.DBQueries;
import com.jagteshwar.sqldatatoexcel.db.SQLiteToExcelConversion;
import com.jagteshwar.sqldatatoexcel.model.Employees;
import com.jagteshwar.sqldatatoexcel.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    EditText editEmp;
    Button btnSaveUser, btnExport, btnSendMail;
    List<Employees> empList;

    DBHelper dbHelper;
    DBQueries dbQueries;
    String directory_path = Environment.getExternalStorageDirectory().getPath() + "/Backup/";
    SQLiteToExcelConversion sqliteToExcel;
    String fileAndLocation = "///sdcard/backup/employees.xls";
    RecyclerView recyclerView;
    String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        btnSaveUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate(editEmp)) {
                    dbQueries.open();

                    Employees employees = new Employees(editEmp.getText().toString());
                    dbQueries.insertEmployees(employees);
                    empList = dbQueries.readEmployees();
                    setAdapter();
                    dbQueries.close();
                    Utils.showSnackBar(view, "Successfully Inserted");
                }
            }
        });

        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (isWriteStoragePermissionGranted()) {
                    final File file = new File(directory_path);
                    if (!file.exists()) {
                        Log.v("File Created", String.valueOf(file.mkdirs()));
                    }
                    createExcel();
                } else {
                    Utils.showSnackBar(view, "Permission not granted");
                }

            }
        });

        btnSendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                emailIntent.setType("application/excel");
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"jagteshwars3@gmail.com"});
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Excel Sheet");
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Hello Jag");

                File file = new File(fileAndLocation);
                Uri xls_file = Uri.parse("file:/" + file);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    xls_file = FileProvider.getUriForFile(MainActivity.this, BuildConfig.APPLICATION_ID + ".provider", file);
                }

                if (file.exists()) {
                    Log.v("MyTag", "Email file_exists!");
                } else {
                    Log.v("MyTag", "Email file does not exist!");
                }

                Log.v("MyTag", "SEND EMAIL FileUri=" + xls_file);
                emailIntent.putExtra(Intent.EXTRA_STREAM, xls_file);

                MainActivity.this.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                Utils.showSnackBar(v, "File sent via mail");
            }
        });

    }

    public boolean isWriteStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted21");
                return true;
            } else {

                Log.v(TAG, "Permission is revoked2");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        } else {
            Log.v(TAG, "Permission is granted2");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(TAG, "Permission: " + permissions[0] + "was " + grantResults[0]);
            createExcel();

        }
    }

    public void createExcel() {
        sqliteToExcel = new SQLiteToExcelConversion(this, DBHelper.DB_NAME, directory_path);
        Log.v(TAG, directory_path);
        sqliteToExcel.exportAllTables("employees.xls", new SQLiteToExcelConversion.ExportListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onCompleted(String filePath) {
                Toast.makeText(MainActivity.this, "Export successful", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(MainActivity.this, "Export not successful", Toast.LENGTH_SHORT).show();

            }
        });

    }

    boolean validate(EditText editText) {
        if (editText.getText().toString().length() == 0) {
            editText.setError("Field Required");
            editText.requestFocus();
        }
        return editText.getText().toString().length() > 0;
    }

    void initViews() {
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = new DBHelper(getApplicationContext());
        dbQueries = new DBQueries(getApplicationContext());

        editEmp = (EditText) findViewById(R.id.edt_emp);
        btnSaveUser = (Button) findViewById(R.id.btn_save_emp);
        btnExport = (Button) findViewById(R.id.btn_export);
        btnSendMail = (Button) findViewById(R.id.btn_sendMail);
        recyclerView = findViewById(R.id.recyclerview_employees);
        empList = new ArrayList<>();

        dbQueries.open();
        empList = dbQueries.readEmployees();
        setAdapter();
        dbQueries.close();
    }

    private void setAdapter() {
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(empList);
        RecyclerView.LayoutManager layoutManager =
                new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return true;
    }
}
