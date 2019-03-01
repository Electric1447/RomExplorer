package eparon.romexplorer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Objects;

public class DeviceActivity extends AppCompatActivity {

    public String PREFS_RE = "REPrefsFile";
    SharedPreferences prefs;

    boolean stableFilter = false, betsFilter = false, alphaFilter = false;
    boolean officialFilter = false, unofficialFilter = false, naFilter = false;

    Device cDevice;
    ImageView Image;

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        if (!isNetworkAvailable())
            startActivity(new Intent(DeviceActivity.this, InternetCheck.class));

        prefs = getSharedPreferences(PREFS_RE, Context.MODE_PRIVATE);

        stableFilter = prefs.getBoolean("stableFilter", stableFilter);
        betsFilter = prefs.getBoolean("betsFilter", betsFilter);
        alphaFilter = prefs.getBoolean("alphaFilter", alphaFilter);

        officialFilter = prefs.getBoolean("officialFilter", officialFilter);
        unofficialFilter = prefs.getBoolean("unofficialFilter", unofficialFilter);
        naFilter = prefs.getBoolean("naFilter", naFilter);

        cDevice = (Device) getIntent().getSerializableExtra("DEVICE");

        TextView Title = findViewById(R.id.title);
        Title.setText(cDevice.getName());

        TextView Text[] = new TextView[4];
        SpannableStringBuilder[] sb = new SpannableStringBuilder[Text.length];
        String[] temp = new String[] {Objects.requireNonNull(cDevice).getName(), cDevice.getCodename(), cDevice.getManufacturer(), cDevice.getYear()};

        for (int i = 0; i < Text.length; i++) {
            int resID = getResources().getIdentifier("text" + (i + 1), "id", getPackageName());
            Text[i] = findViewById(resID);
            sb[i] = new SpannableStringBuilder(String.format("%s %s", getResources().getStringArray(R.array.deviceBoldText)[i], temp[i]));
            sb[i].setSpan(new StyleSpan(Typeface.BOLD), 0, getResources().getStringArray(R.array.deviceBoldText)[i].length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            Text[i].setText(sb[i]);
        }

        Image = findViewById(R.id.image);
        int resID = getResources().getIdentifier(cDevice.getCodename(), "drawable", getPackageName());
        Image.setImageResource(resID);
        if (Image.getDrawable() == null)
            Image.setImageResource(R.drawable.device_default);

        TableLayout tLayout, top;
        TableRow table1;
        TextView rName, rVersion, rStatus, rType, rUrl;
        ImageView rImage;

        View v = View.inflate(this, R.layout.romtable, null);
        table1 = v.findViewById(R.id.tableRow1);

        tLayout = findViewById(R.id.tl);
        top = findViewById(R.id.topTable);

        if (table1.getParent() != null)
            ((ViewGroup)table1.getParent()).removeView(table1);
        tLayout.addView(table1);

        if (table1.getParent() != null)
            ((ViewGroup)table1.getParent()).removeView(table1);
        top.addView(table1);
        int[] tabledimms = new int[5];

        TextView tText1 = findViewById(R.id.top1);
        LinearLayout tText2 = findViewById(R.id.top2);
        TextView tText3 = findViewById(R.id.top3);
        TextView tText4 = findViewById(R.id.top4);
        TextView tText5 = findViewById(R.id.top5);

        for (int i = 0; i < cDevice.getRoms().length; i++) {

            View v2 = View.inflate(this, R.layout.romtable, null);
            TableRow table2 = v2.findViewById(R.id.tableRow2);

            rImage = v2.findViewById(R.id.rImage);
            int resID2 = getResources().getIdentifier(cDevice.getRoms()[i].getName().toLowerCase().replaceAll("\\s+", "").replaceAll("\\d", "").replaceAll("\\.", ""), "drawable", getPackageName());
            rImage.setImageResource(resID2);
            if (rImage.getDrawable() == null)
                rImage.setImageResource(R.drawable.rom_default);

            rName = v2.findViewById(R.id.textView1);
            rName.setText(cDevice.getRoms()[i].getName());
            if (cDevice.getRoms()[i].getName().length() > 10)
                rName.setTextSize((float)(cDevice.getRoms()[i].getName().length()) / 2 - 1);

            rVersion = v2.findViewById(R.id.textView2);
            rVersion.setText(cDevice.getRoms()[i].getVersion());

            rStatus = v2.findViewById(R.id.textView3);
            rStatus.setText(cDevice.getRoms()[i].getStatus());
            switch (cDevice.getRoms()[i].getStatus().replaceAll("\\s+", "")) {
                case "Stable":
                    if (stableFilter)
                        continue;
                    rStatus.setTextColor(getResources().getColor(R.color.colorGreen2));
                    break;
                case "Beta":
                    if (betsFilter)
                        continue;
                    rStatus.setTextColor(getResources().getColor(R.color.colorOrange2));
                    break;
                case "Alpha":
                    if (alphaFilter)
                        continue;
                    rStatus.setTextColor(getResources().getColor(R.color.colorRed));
                    break;
            }

            rType = v2.findViewById(R.id.textView4);
            rType.setText(cDevice.getRoms()[i].getType());
            switch (cDevice.getRoms()[i].getType().replaceAll("\\s+", "")) {
                case "Official":
                    if (officialFilter)
                        continue;
                    rType.setTextColor(getResources().getColor(R.color.colorDGreen2));
                    break;
                case "Unofficial":
                    if (unofficialFilter)
                        continue;
                    rType.setTextColor(getResources().getColor(R.color.colorDBlue2));
                    rType.setTextSize(15f);
                    break;
                case "N/A":
                    if (naFilter)
                        continue;
                    rType.setTextColor(getResources().getColor(R.color.colorGrey));
                    break;
            }

            rUrl = v2.findViewById(R.id.textView5);
            rUrl.setTag(i);

            if (table2.getParent() != null)
                ((ViewGroup)table2.getParent()).removeView(table2);
            tLayout.addView(table2);

            rUrl.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    String url = cDevice.getRoms()[(int)v.getTag()].getUrl();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                }
            });

            rName.measure(0, 0);
            if (tabledimms[0] < rName.getMeasuredWidth())
                tabledimms[0] = rName.getMeasuredWidth();
            else {
                tText1.measure(0, 0);
                rName.setWidth(tText1.getMeasuredWidth());
            }
            rVersion.measure(0, 0);
            if (tabledimms[1] < rVersion.getMeasuredWidth())
                tabledimms[1] = rVersion.getMeasuredWidth();
            else {
                tText2.measure(0, 0);
                rVersion.setWidth(tText2.getMeasuredWidth());
            }
            rStatus.measure(0, 0);
            if (tabledimms[2] < rStatus.getMeasuredWidth())
                tabledimms[2] = rStatus.getMeasuredWidth();
            else {
                tText3.measure(0, 0);
                rStatus.setWidth(tText3.getMeasuredWidth());
            }
            rType.measure(0, 0);
            if (tabledimms[3] < rType.getMeasuredWidth())
                tabledimms[3] = rType.getMeasuredWidth();
            else {
                tText4.measure(0, 0);
                rType.setWidth(tText4.getMeasuredWidth());
            }
            rUrl.measure(0, 0);
            if (tabledimms[4] < rUrl.getMeasuredWidth())
                tabledimms[4] = rUrl.getMeasuredWidth();
            else {
                tText5.measure(0, 0);
                rUrl.setWidth(tText5.getMeasuredWidth());
            }
        }

        if (tabledimms[0] > tText1.getMeasuredWidth())
            tText1.setWidth(tabledimms[0]);
        if (tabledimms[1] > tText2.getMeasuredWidth())
            tText2.setLayoutParams(new TableRow.LayoutParams(tabledimms[1], TableRow.LayoutParams.WRAP_CONTENT));
        if (tabledimms[2] > tText3.getMeasuredWidth())
            tText3.setWidth(tabledimms[2]);
        if (tabledimms[3] > tText4.getMeasuredWidth())
            tText4.setWidth(tabledimms[3]);
        if (tabledimms[4] > tText5.getMeasuredWidth())
            tText5.setWidth(tabledimms[4]);
    }

}
