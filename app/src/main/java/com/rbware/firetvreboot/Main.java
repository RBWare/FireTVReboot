package com.rbware.androiddatetime;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Date;


public class Main extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Run Root Command to set date/time
        new RequestTask().execute("http://www.timeapi.org/cdt/now"); // CDT
    }

    private class RequestTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... uri) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;
            try {
                response = httpclient.execute(new HttpGet(uri[0]));
                StatusLine statusLine = response.getStatusLine();
                if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    responseString = out.toString();
                } else{
                    //Closes the connection.
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (ClientProtocolException e) {
                //TODO Handle problems..
            } catch (IOException e) {
                //TODO Handle problems..
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // Parse
            String properFormat = getSuFormat(result);

            // Set date properly using SU
            // adb shell 'su 0 date -s 20140825.134241'
            String suCommand = "su 0 date -s " + properFormat;
            try {
                Process process = Runtime.getRuntime().exec("su");
                DataOutputStream os = new DataOutputStream(process.getOutputStream());
                os.writeBytes("date -s " + properFormat + "; \n");
            } catch (Exception ex) {
                Log.i("Settings", "Could not reboot", ex);
            }

            // Quit everything
            Toast.makeText(Main.this, "Date and Time Updated", Toast.LENGTH_SHORT).show();
            Main.this.finish();
        }

        private String getSuFormat(String input){
            String output = "";
            // 2014-08-25T14:11:34-05:00
            output = input.substring(0, input.indexOf("T"));
            output = output.replace("-", "");
            output += ".";
            output += input.substring(input.indexOf("T") + 1, input.lastIndexOf("-"));
            output = output.replace(":", "");


            return output;
        }
    }
}
