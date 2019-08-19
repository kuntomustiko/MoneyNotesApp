package com.minara.kirana.moneynotesapp;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.minara.kirana.moneynotesapp.helper.CurrentDate;
import com.minara.kirana.moneynotesapp.helper.SqliteHelper;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class EditActivity extends AppCompatActivity {

    RadioGroup radio_status;
    RadioButton radio_masuk, radio_keluar;
    EditText edt_jumlah , edt_keterangan, edt_tanggal;
    Button btn_simpan;
    DatePickerDialog datePickerDialog;

    Cursor cursor;

    String status, tanggal;
    RippleView rip_simpan;
    SqliteHelper sqliteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        getSupportActionBar().setTitle("Edit");
        //untuk menambahkan panah ke activity sebelumnya (hanya panah no action)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sqliteHelper = new SqliteHelper(this);

        radio_status = findViewById(R.id.radio_status);
        radio_masuk = findViewById(R.id.radio_masuk);
        radio_keluar = findViewById(R.id.radio_keluar);
        edt_jumlah = findViewById(R.id.edit_jumlah);
        edt_keterangan = findViewById(R.id.edit_keterangan);
        edt_tanggal = findViewById(R.id.edit_tanggal);
        btn_simpan = findViewById(R.id.btn_simpan);
        rip_simpan = findViewById(R.id.rip_simpan);

        radio_status.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.radio_masuk:
                        status = "MASUK";
                        break;
                    case R.id.radio_keluar:
                        status = "KELUAR";
                        break;
                }
            }
        });

        SQLiteDatabase database = sqliteHelper.getReadableDatabase();
        cursor = database.rawQuery( "SELECT *, strftime('%d/%m/%Y', tanggal) AS tgl FROM transaksi WHERE transaksi_id= '"
                        + MainActivity.transaksi_id + "'"
                , null);
        cursor.moveToFirst();

        status = cursor.getString(1);

        switch (status){
            case "MASUK":
                radio_masuk.setChecked(true);
                break;
            case "KELUAR":
                radio_keluar.setChecked(true);
                break;
        }

        edt_jumlah.setText(cursor.getString(2));
        edt_keterangan.setText(cursor.getString(3));

        tanggal = cursor.getString(4);
        edt_tanggal.setText(cursor.getString(5));
        edt_tanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog = new DatePickerDialog(EditActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        NumberFormat numberFormat = new DecimalFormat("00");
                        tanggal = year + "-" + numberFormat.format(month+1) + "-" + numberFormat.format(dayOfMonth);

                        Log.e("_tanggal", tanggal);

                        edt_tanggal.setText(numberFormat.format(dayOfMonth) + "/"+ numberFormat.format((month+1)) +
                                "/" + year);
                    }
                }, CurrentDate.year, CurrentDate.month, CurrentDate.day );

                datePickerDialog.show();

            }
        });
        rip_simpan.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if (status.equals("") || edt_jumlah.getText().toString().equals("") || edt_keterangan.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "isi data dengan benar",
                            Toast.LENGTH_SHORT).show();
                } else if (status.equals("")){
                    Toast.makeText(getApplicationContext(), "status harus di lengkapi",
                            Toast.LENGTH_SHORT).show();
                    radio_status.requestFocus();
                } else if (edt_jumlah.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "jumlah harus diisi", Toast.LENGTH_SHORT).show();
                    edt_jumlah.requestFocus();
                } else if (edt_keterangan.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "keterangan harus diisi", Toast.LENGTH_SHORT).show();
                    edt_keterangan.requestFocus();
                } else {
                    simpanEdit();
                }

            }
        });
    }

    private void simpanEdit() {
        SQLiteDatabase  database = sqliteHelper.getWritableDatabase();
        database.execSQL("UPDATE transaksi SET status = '" + status + "', jumlah = '" + edt_jumlah.getText().toString() +
                "', keterangan= '" + edt_keterangan.getText().toString()+"', tanggal='" + tanggal +
                "' WHERE transaksi_id='" + MainActivity.transaksi_id + "' ");

        Toast.makeText(getApplicationContext(), "perubahan sudah di simpan", Toast.LENGTH_SHORT).show();
        finish();

    }

    //method untuk memberikan action pada panah di actionbar
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;

    }
}
