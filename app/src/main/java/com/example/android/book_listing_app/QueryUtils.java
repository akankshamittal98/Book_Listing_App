package com.example.android.book_listing_app;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
public class QueryUtils {
    private QueryUtils() {
    }
    public static String authorsList(JSONArray authorsList) throws JSONException {
        String stringAuthorsList = null;
        if (authorsList.length() == 0) {
            return null;
        }
        for (int i = 0; i < authorsList.length(); i++) {
            if (i == 0) {
                stringAuthorsList = authorsList.getString(0);
            } else {
                stringAuthorsList = stringAuthorsList + " " + authorsList.getString(i);
            }
        }
        return stringAuthorsList;
    }
    public static List<Book> bookExtract(String json) {
        List<Book> books = new ArrayList<>();
        try {
            JSONObject jsonResponse = new JSONObject(json);
            if (jsonResponse.getInt("totalItems") == 0) {
                return books;
            }
            JSONArray jsonArray = jsonResponse.getJSONArray("items");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject book = jsonArray.getJSONObject(i);
                JSONObject informationOfBook = book.getJSONObject("volumeInfo");
                String name = informationOfBook.getString("title");
                JSONArray arrayOfAuthors = informationOfBook.getJSONArray("authors");
                String authors = authorsList(arrayOfAuthors);
                Book bookss = new Book(authors, name);
                books.add(bookss);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return books;
    }
}
