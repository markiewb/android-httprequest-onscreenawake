/**
 * Copyright 2017 markiewb
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.markiewb.onscreenawake;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Main2Activity extends Activity {


    private String url = "https://www.qwregoogle.de";
    private BroadcastReceiver mybroadcast = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("BroadcastReceiver", "Receiver");

            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                Log.e("BroadcastReceiver", "Screen ON");
                makeHTTPRequest();
            }
        }
    };

    private void makeHTTPRequest() {
        new GetUrlContentTask(this, url).execute();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        registerReceiver(mybroadcast, new IntentFilter(Intent.ACTION_SCREEN_ON));
    }

    /**
     * https://stackoverflow.com/a/38593586
     */
    private class GetUrlContentTask extends AsyncTask<String, Integer, String> {
        private Context context;
        private String url;
        private int timeoutMillis = 5000;
        private IOException exception = null;

        private GetUrlContentTask(Context context, String url) {
            this.context = context;
            this.url = url;
        }

        protected String doInBackground(String... params) {
            String content = null, line;
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("GET");
                // do not setDoOutput or it will be a POST request
                // connection.setDoOutput(true);
                connection.setConnectTimeout(timeoutMillis);
                connection.setReadTimeout(timeoutMillis);
                connection.connect();

                BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                content = "";
                while ((line = rd.readLine()) != null) {
                    content += line + "\n";
                }
            } catch (IOException e) {
                content = e.getLocalizedMessage();
                e.printStackTrace();
                this.exception = e;
            }
            return content;
        }

        protected void onProgressUpdate(Integer... progress) {
        }


        protected void onPostExecute(String result) {
            // this is executed on the main thread after the process is over
            // update your UI here
            Log.i(getClass().getSimpleName(), "" + result);
            if (null == exception) {
                Toast.makeText(context, "Called " + url + " successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Called " + url + " with error " + exception.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }


}
