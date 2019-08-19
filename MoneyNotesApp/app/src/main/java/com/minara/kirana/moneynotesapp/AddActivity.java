package com.minara.kirana.moneynotesapp;

import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.minara.kirana.moneynotesapp.helper.SqliteHelper;

public class AddActivity extends AppCompatActivity {

    RadioGroup radio_status;
    RadioButton radio_masuk, radio_keluar;
    EditText edt_jumlah, edt_keterangan;
    Button btn_simpan;

    String status;
    RippleView rip_simpan;
    SqliteHelper sqliteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        getSupportActionBar().setTitle("tambah");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        status = "";

        sqliteHelper = new SqliteHelper(this);

        radio_status = findViewById(R.id.radio_status);
        radio_masuk = findViewById(R.id.radio_masuk);
        radio_keluar = findViewById(R.id.radio_keluar);
        edt_jumlah = findViewById(R.id.edit_jumlah);
        edt_keterangan = findViewById(R.id.edit_keterangan);
        btn_simpan = findViewById(R.id.btn_simpan);
        rip_simpan = findViewById(R.id.rip_simpan);

        radio_status.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.radio_masuk:
                        status = "MASUK";
                        break;
                    case R.id.radio_keluar:
                        status = "KELUAR";
                        break;
                }
            }
        });

        rip_simpan.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {

                //kenapa getApplicationContext bukannya AddActivity.this
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
                    simpanData();
                }
            }
        });
    }

    private void simpanData(){
        SQLiteDatabase database = sqliteHelper.getWritableDatabase();
        database.execSQL(
                "INSERT INTO transaksi (status, jumlah, keterangan) VALUES ('"+status +
                        "', '"+ edt_jumlah.getText().toString()+ "','"+edt_keterangan.getText().toString()+ "')");

        Toast.makeText(getApplicationContext(), "data keuangan sudah di simpan", Toast.LENGTH_SHORT).show();
        edt_keterangan.requestFocus();
        finish();
    }

    //method untuk memberikan action pada panah di actionbar
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;

    }
}
