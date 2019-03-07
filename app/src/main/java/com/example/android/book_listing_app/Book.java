package com.example.android.book_listing_app;
import android.os.Parcel;
import android.os.Parcelable;
public class Book implements Parcelable {
    String author;
    String name;
    public Book(String author, String title) {
        this.author = author;
        this.name = title;
    }
    protected Book(Parcel in) {
        author = in.readString();
        name = in.readString();
    }
    public String getAuthor() {
        return author;
    }
    public String getName() {
        return name;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(author);
        parcel.writeString(name);
    }
    @Override
    public int describeContents() {
        return 0;
    }
    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }
        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };
}
