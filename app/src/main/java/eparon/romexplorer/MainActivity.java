package eparon.romexplorer;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
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

    LinearLayout lLayout[] = new LinearLayout[3];
    int layoutInt = 0;
    Button b;

    int layer = 0;
    boolean disableAM = false;

    String[] SamsungTags = new String[] {"Galaxy A Series", "Galaxy S Series", "Galaxy Note Series"};

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onBackPressed() {
        if (disableAM || layer == 0)
            this.finishAffinity();
        else if (layer == 1) {
            layer = 0;
            ViewDevices(devices);
        } else if (layer == 2) {
            layer = 1;
            SamsungCategoryMode();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences(PREFS_RE, Context.MODE_PRIVATE);

        disableAM = prefs.getBoolean("disableAM", disableAM);


        if (!isNetworkAvailable())
            startActivity(new Intent(MainActivity.this, InternetCheck.class));

        lLayout[0] = findViewById(R.id.layout1);
        lLayout[1] = findViewById(R.id.layout2);
        lLayout[2] = findViewById(R.id.layout3);
        TextView version = findViewById(R.id.version);
        version.setText(String.format("Version BETA %s", BuildConfig.VERSION_NAME.substring(1)));

        context = this;

        new DownloadRomXML().execute(URL);
    }

    private void ViewDevices(Device[] dev){

        layoutInt = 0;
        lLayout[0].removeAllViews();
        lLayout[1].removeAllViews();
        lLayout[2].removeAllViews();

        String[] manufacturers = new String[Objects.requireNonNull(Device.getAllManufacturersNames(devices)).length + 1];
        manufacturers[0] = "ALL DEVICES";
        System.arraycopy(Objects.requireNonNull(Device.getAllManufacturersNames(devices)), 0, manufacturers, 1, Objects.requireNonNull(Device.getAllManufacturersNames(devices)).length);

        int length = dev.length;

        if (!disableAM && layer == 0)
            length = manufacturers.length;

        for (int i = 0; i < length; i++) {
            View v = View.inflate(context, R.layout.buttons, null);
            b = v.findViewById(R.id.button);
            b.setTag(i);

            if (b.getParent() != null)
                ((ViewGroup) b.getParent()).removeView(b);

            if (!disableAM && layer != 0) {
                layoutInt = i % 2 + 1;
                b.getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
            }

            lLayout[layoutInt].addView(b);

            if (!disableAM && layer == 0) {
                b.setText(manufacturers[i]);
                int resID = getResources().getIdentifier(manufacturers[i].toLowerCase().replaceAll("\\s+", "") + "_logo", "drawable", getPackageName());
                b.setBackgroundResource(resID);
                if (b.getBackground() == null) {
                    b.setBackgroundResource(R.drawable.px400_empty);
                    b.setTextSize(40f);
                    b.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                } else
                    b.setTextColor(0x00000000);
                b.getLayoutParams().height = 240;
                b.getLayoutParams().width = 960;
            } else {
                b.setText(dev[i].getName());
                if (dev[i].getName().length() > 15)
                    b.setTextSize(15f);
                if (dev[i].getName().length() > 20)
                    b.setTextSize(14f);
            }

            final Button finalB = b;
            b.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if (!disableAM && layer == 0) {
                        layer = 1;
                        switch (finalB.getText().toString()) {
                            case "ALL DEVICES":
                                ViewDevices(devices);
                                break;
                            case "Samsung":
                                SamsungCategoryMode();
                                break;
                            default:
                                ViewDevices(Objects.requireNonNull(Device.findDevicesByManufacturer(devices, finalB.getText().toString())));
                                break;
                        }
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
    }

    private static String getValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = nodeList.item(0);
        return node.getNodeValue();
    }

    public void goSettings(View view) {
        startActivity(new Intent(MainActivity.this, Settings.class));
    }

    private void SamsungCategoryMode() {

        layoutInt = 0;
        lLayout[0].removeAllViews();
        lLayout[1].removeAllViews();
        lLayout[2].removeAllViews();

        for (int i = 0; i < SamsungTags.length; i++) {
            View v = View.inflate(context, R.layout.buttons, null);
            b = v.findViewById(R.id.button);
            b.setTag(i);

            if (b.getParent() != null)
                ((ViewGroup) b.getParent()).removeView(b);

            lLayout[0].addView(b);

            b.setText(SamsungTags[i]);
            b.setAllCaps(true);

            final Button finalB = b;
            b.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    layer = 2;
                    String s = finalB.getText().toString();
                    ViewDevices(Objects.requireNonNull(Device.findDevicesByTag(Objects.requireNonNull(Device.findDevicesByManufacturer(devices, "Samsung")), s.substring(0, s.length() - 7))));
                }
            });
        }
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

