package eparon.romexplorer;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class InternetCheck extends AppCompatActivity {

    //Checking if network is available.
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_netcheck);

        CheckConnection(); //Checking if the device has Internet Connectivity.
    }

    private void CheckConnection() {
        if (isNetworkAvailable()) {
            TextView t = findViewById(R.id.text);
            Button b = findViewById(R.id.button);
            t.setVisibility(View.GONE);
            b.setVisibility(View.GONE);
            startActivity(new Intent(InternetCheck.this, MainActivity.class));
        } else
            Toast.makeText(this, R.string.icError, Toast.LENGTH_SHORT).show();
    }

    public void CheckConnectionButton (View view) { CheckConnection(); } //Rechecking Internet Connectivity on button click.

}