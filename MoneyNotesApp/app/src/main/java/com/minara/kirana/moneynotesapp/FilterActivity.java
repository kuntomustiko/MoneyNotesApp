package com.minara.kirana.moneynotesapp;

import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.minara.kirana.moneynotesapp.helper.CurrentDate;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class FilterActivity extends AppCompatActivity {

    EditText edt_dari, edt_ke;
    Button btn_filter;
    RippleView rip_filter;
    DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        getSupportActionBar().setTitle("Filter");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edt_dari = (EditText) findViewById(R.id.edit_dari);
        edt_ke = (EditText) findViewById(R.id.edit_ke);
        rip_filter = (RippleView) findViewById(R.id.rip_filter);

        edt_dari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog = new DatePickerDialog(FilterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        NumberFormat numberFormat = new DecimalFormat("00");

                        MainActivity.tgl_dari = year + "-" + numberFormat.format(month+1) + "-" + numberFormat.format(dayOfMonth);

                        Log.e("_tanggal_dari", MainActivity.tgl_dari);

                        edt_dari.setText(numberFormat.format(dayOfMonth) + "/"+ numberFormat.format(month+1) +
                                "/" + year);
                    }
                }, CurrentDate.year, CurrentDate.month, CurrentDate.day);

                datePickerDialog.show();
            }
        });

        edt_ke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog = new DatePickerDialog(FilterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        NumberFormat numberFormat = new DecimalFormat("00");

                        MainActivity.tgl_ke = year + "-" + numberFormat.format(month+1) + "-" + numberFormat.format(dayOfMonth);

                        Log.e("_tanggal_ke", MainActivity.tgl_ke);

                        edt_ke.setText(numberFormat.format(dayOfMonth) + "/"+ numberFormat.format(month+1) +
                                "/" + year);
                    }

                    //data tanggal yang ingin di munculkan di datepickerdialog
                }, CurrentDate.year, CurrentDate.month, CurrentDate.day);

                datePickerDialog.show();
            }
        });

        rip_filter.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                if (edt_dari.getText().toString().equals("") || edt_ke.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "Isi data dengan benar", Toast.LENGTH_SHORT).show();
                } else {

                    MainActivity.filter = true;
                    MainActivity.text_filter.setText(edt_dari.getText().toString());
                    MainActivity.text_filter.setVisibility(View.VISIBLE);

                    finish();

                }

            }
        });
    }

    //method untuk memberikan action pada panah di actionbar
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;

    }
}
