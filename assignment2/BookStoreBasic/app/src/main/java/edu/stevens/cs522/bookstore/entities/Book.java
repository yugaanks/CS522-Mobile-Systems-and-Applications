package edu.stevens.cs522.bookstore.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Book implements Parcelable {

    // TODO Modify this to implement the Parcelable interface. done

    public int id;

    public String title;

    public Author[] authors;

    public String isbn;

    public String price;

    //generated getter and setter methods for book details class
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Author[] getAuthors() {
        return authors;
    }

    public void setAuthors(Author[] authors) {
        this.authors = authors;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Book(int id, String title, Author[] author, String isbn, String price) {
        this.id = id;
        this.title = title;

        this.authors = author;
        this.isbn = isbn;
        this.price = price;
    }

    public String getFirstAuthor() {
        if (authors != null && authors.length > 0) {
            return authors[0].toString();
        } else {
            return "";
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);                      //1
        dest.writeString(title);                //2
        dest.writeTypedArray(authors, flags);   //3
        dest.writeString(isbn);                 //4
        dest.writeString(price);                //5
    }

    public Book(Parcel in) {
        //authors=in.readParcelable(Author.class.getClassLoader());
        //id=bundle.getInt();
        id = in.readInt();
        title = in.readString();
        authors = in.createTypedArray(Author.CREATOR);
        isbn = in.readString();
        price = in.readString();
    }

    public static final Parcelable.Creator<Book> CREATOR = new Parcelable.Creator<Book>() {
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public String toString() {
        return title + " " + getFirstAuthor();
    }

}