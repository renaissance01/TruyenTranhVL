package truyentranh.vl.slideimages;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

import truyentranh.vl.R;

public class NumberPickerActivity extends Activity {

    private String trangcu;
    private String trangmoi;
    private Button btnThoat, btnDen;
    private String idtruyen, idchap, tenchap, tentruyen, vitri, path, sotrang, checktaitruyen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.billing_day_dialog);

        btnThoat = (Button)findViewById(R.id.btnThoat);
        btnDen = (Button)findViewById(R.id.btnDen);

        sotrang = getIntent().getBundleExtra("keychap").getString("sotrang");
        idtruyen = getIntent().getBundleExtra("keychap").getString("idtruyen");
        idchap = getIntent().getBundleExtra("keychap").getString("idchap");
        tentruyen = getIntent().getBundleExtra("keychap").getString("tentruyen");
        tenchap = getIntent().getBundleExtra("keychap").getString("tenchap");
        vitri = getIntent().getBundleExtra("keychap").getString("vitri");
        path = getIntent().getBundleExtra("keychap").getString("path");
        checktaitruyen = getIntent().getBundleExtra("keychap").getString("checktaitruyen");

        NumberPicker np = (NumberPicker) findViewById(R.id.number_picker);
        np.setMinValue(1);
        np.setMaxValue(Integer.valueOf(sotrang));
        np.setWrapSelectorWheel(true);

        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                trangcu = oldVal + "";
                trangmoi = newVal-1 + "";
            }
        });

        btnThoat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplication(), trangmoi + "--"+ tentruyen + " - Chap " + idchap + ": " + tenchap, Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        btnDen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(NumberPickerActivity.this, ShowImages.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("idtruyen", idtruyen);
                    bundle.putString("idchap", idchap);
                    bundle.putString("tentruyen", tentruyen);
                    bundle.putString("tenchap", tenchap);
                    bundle.putString("trang", trangmoi);
                    if (checktaitruyen.equals("checktaitruyen"))
                        bundle.putString("checktaitruyen", "checktaitruyen");
                    else if (checktaitruyen.equals("docfile")) {
                        bundle.putString("path", path);
                        bundle.putString("checktaitruyen", "docfile");
                    }
                    else
                        bundle.putString("checktaitruyen", "check");
                    intent.putExtra("keychap", bundle);
                    startActivity(intent);
                }catch (Exception e){}

            }
        });

    }

}