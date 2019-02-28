package eparon.romexplorer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;

public class Settings extends AppCompatActivity {

    public String PREFS_RE = "REPrefsFile";
    SharedPreferences prefs;

    boolean stableFilter = false, betsFilter = false, alphaFilter = false;
    boolean officialFilter = false, unofficialFilter = false, naFilter = false;
    boolean disableAM = false;

    CheckBox stableCB, betaCB, alphaCB;
    CheckBox officialCB, unofficialCB, naCB;
    CheckBox damCB;

    @Override
    public void onBackPressed() {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("stableFilter", stableFilter);
        editor.putBoolean("betsFilter", betsFilter);
        editor.putBoolean("alphaFilter", alphaFilter);
        editor.putBoolean("officialFilter", officialFilter);
        editor.putBoolean("unofficialFilter", unofficialFilter);
        editor.putBoolean("naFilter", naFilter);
        editor.putBoolean("disableAM", disableAM);
        editor.apply();

        startActivity(new Intent(Settings.this, MainActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = getSharedPreferences(PREFS_RE, Context.MODE_PRIVATE);

        stableFilter = prefs.getBoolean("stableFilter", stableFilter);
        betsFilter = prefs.getBoolean("betsFilter", betsFilter);
        alphaFilter = prefs.getBoolean("alphaFilter", alphaFilter);

        officialFilter = prefs.getBoolean("officialFilter", officialFilter);
        unofficialFilter = prefs.getBoolean("unofficialFilter", unofficialFilter);
        naFilter = prefs.getBoolean("naFilter", naFilter);

        disableAM = prefs.getBoolean("disableAM", disableAM);

        stableCB = findViewById(R.id.cbStable);
        betaCB = findViewById(R.id.cbBeta);
        alphaCB = findViewById(R.id.cbAlpha);

        officialCB = findViewById(R.id.cbOfficial);
        unofficialCB = findViewById(R.id.cbUnofficial);
        naCB = findViewById(R.id.cbNA);

        damCB = findViewById(R.id.cbDam);

        stableCB.setChecked(!stableFilter);
        betaCB.setChecked(!betsFilter);
        alphaCB.setChecked(!alphaFilter);

        officialCB.setChecked(!officialFilter);
        unofficialCB.setChecked(!unofficialFilter);
        naCB.setChecked(!naFilter);

        damCB.setChecked(disableAM);
    }

    public void StableCB (View view) {
        stableFilter = !stableCB.isChecked();
    }

    public void BetaCB (View view) {
        betsFilter = !betaCB.isChecked();
    }

    public void AlphaCB (View view) {
        alphaFilter = !alphaCB.isChecked();
    }

    public void OfficialCB (View view) {
        officialFilter = !officialCB.isChecked();
    }

    public void UnofficialCB (View view) {
        unofficialFilter = !unofficialCB.isChecked();
    }

    public void NACB (View view) {
        naFilter = !naCB.isChecked();
    }

    public void DamCB (View view) {
        disableAM = damCB.isChecked();
    }

}
