package eparon.romexplorer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MainActivity extends AppCompatActivity {

    Context context;
    ProgressDialog pDialog;
    String URL[] = new String[] {"https://raw.githubusercontent.com/Electric1447/test/master/devices-test.xml", "https://raw.githubusercontent.com/Electric1447/test/master/roms-test.xml"};

    Device[] devices;
    Rom[][] roms;


    @Override
    public void onBackPressed() { this.finishAffinity(); }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView version = findViewById(R.id.version);
        version.setText(String.format("Version ALPHA %s", BuildConfig.VERSION_NAME.substring(1)));

        context = this;

        new DownloadRomXML().execute(URL);
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

            LinearLayout lLayout;
            Button b;

            for (int i = 0; i < devices.length; i++) {
                View v = View.inflate(context, R.layout.buttons, null);
                b = v.findViewById(R.id.button);
                b.setTag(i);

                if (b.getParent() != null)
                    ((ViewGroup) b.getParent()).removeView(b);

                lLayout = findViewById(R.id.layout1);
                lLayout.addView(b);

                b.setText(devices[i].getName());

                final Button finalB = b;
                b.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Device d = Device.findDeviceByName(devices, finalB.getText().toString());
                        assert d != null;
                        Intent a = new Intent(MainActivity.this, DeviceActivity.class);
                        a.putExtra("DEVICE", d);
                        startActivity(a);
                    }
                });
            }
        }
    }

//**********************************************************************************************************************************************************************************************************************************************************

}

