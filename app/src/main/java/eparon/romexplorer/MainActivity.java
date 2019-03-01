package eparon.romexplorer;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.net.URL;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MainActivity extends AppCompatActivity {

    public String PREFS_RE = "REPrefsFile";
    SharedPreferences prefs;

    Context context;
    ProgressDialog pDialog;
    String URL[] = new String[] {"https://raw.githubusercontent.com/Electric1447/RomExplorer/master/devices.xml", "https://raw.githubusercontent.com/Electric1447/RomExplorer/master/roms.xml"};

    Device[] devices;
    Rom[][] roms;

    LinearLayout lLayout;
    Button b;

    boolean state = true; // Manufacturers = true, Devices = false
    boolean disableAM = false;

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onBackPressed() {
        if (!state)
            this.finishAffinity();
        else
            ViewDevices(devices);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences(PREFS_RE, Context.MODE_PRIVATE);

        disableAM = prefs.getBoolean("disableAM", disableAM);
        state = !disableAM;

        if (!isNetworkAvailable())
            startActivity(new Intent(MainActivity.this, InternetCheck.class));

        lLayout = findViewById(R.id.layout1);
        TextView version = findViewById(R.id.version);
        version.setText(String.format("Version ALPHA %s", BuildConfig.VERSION_NAME.substring(1)));

        context = this;

        new DownloadRomXML().execute(URL);
    }

    private void ViewDevices(Device[] dev){

        lLayout.removeAllViews();

        String[] manufacturers = new String[Objects.requireNonNull(Device.getAllManufacturersNames(devices)).length + 1];
        manufacturers[0] = "ALL DEVICES";
        System.arraycopy(Objects.requireNonNull(Device.getAllManufacturersNames(devices)), 0, manufacturers, 1, Objects.requireNonNull(Device.getAllManufacturersNames(devices)).length);

        int length = dev.length;

        if (state)
            length = manufacturers.length;

        for (int i = 0; i < length; i++) {
            View v = View.inflate(context, R.layout.buttons, null);
            b = v.findViewById(R.id.button);
            b.setTag(i);

            if (b.getParent() != null)
                ((ViewGroup) b.getParent()).removeView(b);

            lLayout.addView(b);

            if (state) {
                b.setText(manufacturers[i]);
                b.setAllCaps(true);
            } else
                b.setText(dev[i].getName());

            final Button finalB = b;
            b.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (!state && !disableAM) {
                        if (finalB.getText().toString().equals("ALL DEVICES"))
                            ViewDevices(devices);
                        else
                            ViewDevices(Objects.requireNonNull(Device.findDevicesByManufacturer(devices, finalB.getText().toString())));
                    } else {
                        Device d = Device.findDeviceByName(devices, finalB.getText().toString());
                        assert d != null;
                        Intent a = new Intent(MainActivity.this, DeviceActivity.class);
                        a.putExtra("DEVICE", d);
                        startActivity(a);
                    }
                }
            });
        }

        if (!disableAM)
            state = !state;
    }

    private static String getValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = nodeList.item(0);
        return node.getNodeValue();
    }

    public void goSettings(View view) {
        startActivity(new Intent(MainActivity.this, Settings.class));
    }




//**********************************************************************************************************************************************************************************************************************************************************

    //
    // DownloadRomXML AsyncTask
    //
    @SuppressLint("StaticFieldLeak")
    private class DownloadRomXML extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Create a progressbar
            pDialog = new ProgressDialog(MainActivity.this);
            // Set progressbar title
            pDialog.setTitle("Please Wait");
            // Set progressbar message
            pDialog.setMessage("Loading...");
            pDialog.setIndeterminate(false);
            // Show progressbar
            pDialog.show();
        }

        @Override
        protected Void doInBackground(String[] Url) {
            try {

                    URL url = new URL(Url[1]);
                    DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                    Document doc = dBuilder.parse(new InputSource(url.openStream()));

                    Element element = doc.getDocumentElement();
                    element.normalize();

                    NodeList deviceList = doc.getElementsByTagName("rom-array");
                    roms = new Rom[deviceList.getLength()][];

                    for (int i = 0; i < deviceList.getLength(); i++) {
                        Node node = deviceList.item(i);
                        if (node.getNodeType() == Node.ELEMENT_NODE) {

                            Element element2 = (Element) node;
                            NodeList romList = element2.getElementsByTagName("rom");
                            roms[i] = new Rom[romList.getLength()];

                            for (int j = 0; j < romList.getLength(); j++) {
                                Node node2 = romList.item(j);
                                if (node2.getNodeType() == Node.ELEMENT_NODE) {
                                    Element element3 = (Element) node2;
                                    roms[i][j] = new Rom(getValue("name", element3),
                                            getValue("version", element3),
                                            getValue("status", element3),
                                            getValue("type", element3),
                                            getValue("url", element3));
                                }
                            }
                        }
                    }

            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            new DownloadDeviceXML().execute(URL);
        }
    }


//**********************************************************************************************************************************************************************************************************************************************************

    //
    // DownloadDeviceXML AsyncTask
    //
    @SuppressLint("StaticFieldLeak")
    private class DownloadDeviceXML extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String[] Url) {
            try {

                URL url = new URL(Url[0]);
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(new InputSource(url.openStream()));

                Element element = doc.getDocumentElement();
                element.normalize();

                NodeList nList = doc.getElementsByTagName("device");
                devices = new Device[nList.getLength()];

                for (int i = 0; i < nList.getLength(); i++) {
                    Node node = nList.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element2 = (Element) node;
                        devices[i] = new Device(getValue("name", element2),
                                getValue("codename", element2),
                                getValue("manufacturer", element2),
                                getValue("year", element2),
                                roms[i]);
                    }
                }

            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void args) {

            pDialog.dismiss();

            ViewDevices(devices);
        }
    }

//**********************************************************************************************************************************************************************************************************************************************************

}

