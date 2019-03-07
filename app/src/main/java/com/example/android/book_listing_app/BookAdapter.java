package com.example.android.book_listing_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
public class BookAdapter extends ArrayAdapter<Book> {
    public BookAdapter(Context context, int resource) {
        super(context, resource);
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Book book = getItem(position);
        if (view == null){
            view = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }
        TextView name = (TextView) view.findViewById(R.id.name);
        name.setText(book.getName());
        TextView author = (TextView) view.findViewById(R.id.author);
        author.setText(book.getAuthor());
        return view;
    }
}

