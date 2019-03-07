package com.example.android.book_listing_app;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    TextView notFoundData;
    EditText text;
    ImageButton image;
    BookAdapter adapter;
    static final String results = "searchResults";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        adapter = new BookAdapter(this, -1);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        notFoundData = (TextView) findViewById(R.id.data_not_found);
        text = (EditText) findViewById(R.id.edit);
        image = (ImageButton) findViewById(R.id.image);

        image.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isInternetAvailable()){
                    BookAsyncTask task = new BookAsyncTask();
                    task.execute();
                } else {
                    Toast.makeText(MainActivity.this, R.string.error_no_internet,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (savedInstanceState != null) {
            Book[] books = (Book[]) savedInstanceState.getParcelableArray(results);
            adapter.addAll(books);
        }
    }
    private boolean isInternetAvailable(){
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
       return  activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
    private void updateUi(List<Book> books){
        if (books.isEmpty()){
            notFoundData.setVisibility(View.VISIBLE);
        } else {
            notFoundData.setVisibility(View.GONE);
        }
        adapter.clear();
        adapter.addAll(books);
    }
    private String getInput() {
        return text.getText().toString();
    }

    private String getUrl() {
        final String mainUrl = "https://www.googleapis.com/books/v1/volumes?q=search+";
        String userInput = getInput().trim().replaceAll("\\s+","+");
        String url = mainUrl + userInput;
        return url;
    }
    private class BookAsyncTask extends AsyncTask<URL, Void, List<Book>> {

        @Override
        protected List<Book> doInBackground(URL... urls) {
            URL url = createURL(getUrl());
            String jsonResponse = "";

            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                e.printStackTrace();
            }

            List<Book> books = parseJson(jsonResponse);
            return books;
        }

        @Override
        protected void onPostExecute(List<Book> books) {
            if (books == null) {
                return;
            }
            updateUi(books);
        }

        private URL createURL(String stringUrl) {
            try {
                return new URL(stringUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            }
        }

        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";

            if (url == null){
                return jsonResponse;
            }

            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;

            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(10000 /* milliseconds */);
                urlConnection.setConnectTimeout(15000 /* milliseconds */);
                urlConnection.connect();
                if (urlConnection.getResponseCode() == 200){
                    inputStream = urlConnection.getInputStream();
                    jsonResponse = readFromStream(inputStream);
                } else {
                    Log.e("mainActivity", "Response code: " + urlConnection.getResponseCode());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            return jsonResponse;
        }

        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder result = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
            }
            return result.toString();
        }

        private List<Book> parseJson(String json) {

            if (json == null) {
                return null;
            }
            List<Book> books =  QueryUtils.bookExtract(json);
            return books;
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Book[] books = new Book[adapter.getCount()];
        for (int i = 0; i < books.length; i++) {
            books[i] = adapter.getItem(i);
        }
        outState.putParcelableArray(results, (Parcelable[]) books);
    }

}
