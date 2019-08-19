package com.minara.kirana.moneynotesapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.andexert.library.RippleView;
import com.minara.kirana.moneynotesapp.helper.SqliteHelper;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView tv_masuk, tv_keluar, tv_total;
    ListView list_kas;
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<HashMap<String, String>> aruskas;

    public static TextView text_filter;
    public static String transaksi_id, tgl_dari, tgl_ke;
    public static boolean filter;

    Cursor cursor;

    String query_kas, query_total;
    SqliteHelper sqliteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /* Belajar LOG*/
        Log.d("contoh", "misal");
        Log.e("contoh", "misal error");

        text_filter = findViewById(R.id.text_filter);
        tv_masuk = findViewById(R.id.text_masuk);
        tv_keluar = findViewById(R.id.text_keluar);
        tv_total = findViewById(R.id.text_total);
        list_kas = findViewById(R.id.list_kas);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);

        aruskas = new ArrayList<>();
        sqliteHelper = new SqliteHelper(this);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                query_kas =
                        "SELECT *, strftime('%d/%m/%Y', tanggal) AS tgl FROM transaksi ORDER BY transaksi_id DESC";
                query_total =
                        "SELECT SUM(jumlah) AS total, " +
                                "(SELECT SUM(jumlah) FROM transaksi WHERE status='MASUK') AS masuk," +
                                "(SELECT SUM(jumlah) FROM transaksi WHERE status='KELUAR') AS keluar " +
                                "FROM transaksi";

                kasAdapter();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddActivity.class));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        query_kas =
                "SELECT *, strftime('%d/%m/%Y', tanggal) AS tgl FROM transaksi ORDER BY transaksi_id DESC";
        query_total =
                "SELECT SUM(jumlah) AS total," +
                        "(SELECT SUM(jumlah) FROM transaksi WHERE status='MASUK') AS masuk," +
                        "(SELECT SUM(jumlah) FROM transaksi WHERE status='KELUAR') AS keluar FROM transaksi";

        if (filter){
            query_kas =
                    "SELECT *, strftime('%d/%m/%Y', tanggal) AS tgl FROM transaksi "+
                            " WHERE (tanggal >= '" + tgl_dari +"') AND (tanggal <= '" + tgl_ke + "') ORDER BY transaksi_id ASC";

            query_total =  "SELECT SUM(jumlah) AS total, " +
                    "(SELECT SUM(jumlah) FROM transaksi WHERE status='MASUK' AND (tanggal >= '" + tgl_dari +"') AND (tanggal <= '" + tgl_ke + "' )) , " +
                    "(SELECT SUM(jumlah) FROM transaksi WHERE status='KELUAR' AND (tanggal >= '" + tgl_dari +"') AND (tanggal <= '" + tgl_ke + "' )) " +
                    "FROM transaksi "+
                    " WHERE (tanggal >= '" + tgl_dari +"') AND (tanggal <= '" + tgl_ke + "')" ;

        }

        kasAdapter();
    }

    private void kasAdapter(){

        aruskas.clear();
        list_kas.setAdapter(null);

        SQLiteDatabase database = sqliteHelper.getReadableDatabase();
        cursor = database.rawQuery(
                query_kas, null
        );
        cursor.moveToFirst();

        for (int i = 0; i < cursor.getCount(); i++){
            cursor.moveToPosition(i);

            HashMap<String, String> map = new HashMap<>();
            map.put("transaksi_id", cursor.getString(0));
            map.put("status", cursor.getString(1));
            map.put("jumlah", cursor.getString(2));
            map.put("keterangan", cursor.getString(3));
            map.put("tanggal", cursor.getString(5));

            aruskas.add(map);
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(this, aruskas, R.layout.list_content_main,
                new String[]{"transaksi_id", "status", "jumlah", "keterangan", "tanggal"},
                //kita masukkan kedalam view masing-masing
                new int[]{R.id.text_transaksi_id, R.id.text_status, R.id.text_jumlah, R.id.text_keterangan,
                        R.id.text_tanggal});

        list_kas.setAdapter(simpleAdapter);
        list_kas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                transaksi_id = ((TextView) view.findViewById(R.id.text_transaksi_id)).getText().toString();

                Log.e("_transaksi_id", transaksi_id);

                listMenu();
            }
        });

        kasTotal();
    }

    private void kasTotal(){
        NumberFormat rupiah = NumberFormat.getInstance(Locale.GERMANY);

        SQLiteDatabase database = sqliteHelper.getReadableDatabase();
        cursor = database.rawQuery(query_total, null);

        cursor.moveToFirst();
        tv_masuk.setText(rupiah.format(cursor.getDouble(1)));
        tv_keluar.setText(rupiah.format(cursor.getDouble(2)));
        tv_total.setText(
                rupiah.format(cursor.getDouble(1) - cursor.getDouble(2))
        );

        swipeRefreshLayout.setRefreshing(false);
        if (!filter){
            text_filter.setVisibility(View.GONE);
        }
        filter = false;
    }

    private void listMenu(){
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.list_content_main);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        RippleView ripple_hapus = dialog.findViewById(R.id.ripple_hapus);
        RippleView ripple_edit = dialog.findViewById(R.id.ripple_edit);

        ripple_hapus.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                dialog.dismiss();
                hapus();
            }
        });

        ripple_edit.setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
            @Override
            public void onComplete(RippleView rippleView) {
                dialog.dismiss();

                startActivity(new Intent(MainActivity.this, EditActivity.class));
            }
        });

        dialog.show();
    }

    private void hapus(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Konfirmasi");
        builder.setMessage("Yakin untuk menghapus data ini ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                SQLiteDatabase database = sqliteHelper.getWritableDatabase();
                database.execSQL("DELETE FROM transaksi WHERE transaksi_id = '" + transaksi_id + "'");

                Toast.makeText(getApplicationContext(), "Data berhasil di hapus", Toast.LENGTH_LONG).show();
                kasAdapter();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_filter) {
            startActivity(new Intent(MainActivity.this, FilterActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
