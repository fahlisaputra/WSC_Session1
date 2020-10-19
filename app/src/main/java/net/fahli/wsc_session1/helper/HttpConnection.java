package net.fahli.wsc_session1.helper;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;

public class HttpConnection {
    private InputStream OpenHttpConnection(String url) throws IOException {
        InputStream inputStream = null;
        int response = -1;

        URL path = new URL(url);
        URLConnection connection = path.openConnection();

        if (!(connection instanceof HttpURLConnection)) {
            throw new IOException("Not an HTTP Connection");
        } else {
            try {
                HttpURLConnection httpURLConnection = (HttpURLConnection) connection;
                httpURLConnection.setAllowUserInteraction(false);
                httpURLConnection.setInstanceFollowRedirects(true);
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                response = httpURLConnection.getResponseCode();
                if (response == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
            } catch (Exception e) {
                throw new IOException("Error connecting to the server");
            }
            return inputStream;
        }
    }

    public String Connect(String url) {
        int bufferSize = 2000;
        InputStream inputStream = null;
        try {
            inputStream = OpenHttpConnection(url);
        } catch (Exception ignored) {

        }

        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        int charRead;
        String str = "";
        char[] inputBuffer = new char[bufferSize];

        try {
            while ((charRead = inputStreamReader.read(inputBuffer)) > 0) {
                String readString = String.copyValueOf(inputBuffer, 0, charRead);
                str += readString;
                inputBuffer = new char[bufferSize];
            }
            inputStream.close();
        } catch (Exception ignored) {

        }
        return str;
    }

    public static class DownloadData extends AsyncTask<String, Void, String> {
        WeakReference<AsyncCallback> listener;
        int type = 0;

        public DownloadData(AsyncCallback listener, int type) {
            this.listener = new WeakReference<>(listener);
            this.type = type;
        }

        @Override
        protected void onPostExecute(String s) {
            AsyncCallback myListener = this.listener.get();
            if (myListener != null) {
                myListener.onPostExecute(s, type);
            }
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                HttpConnection connection = new HttpConnection();
                return connection.Connect(strings[0]);
            } catch (Exception e) {
                return e.getMessage();
            }
        }
    }
}
