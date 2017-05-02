package edu.stevens.cs522.bookstore.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import edu.stevens.cs522.bookstore.contracts.BookContract;

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

    public Book(Cursor c) {
        this.title = BookContract.getTitle(c);
        this.id = Integer.parseInt(BookContract.getId(c));
        System.out.println("Book.java line 119 id= " + this.id);
        String[] authorsName = BookContract.getAuthors(c);
        System.out.println("Book.java line 120 " + authorsName);
        Author[] authors = Author.CREATOR.newArray(authorsName.length);
        int index = 0;
        for (String i : authorsName) {
            Author a = null;
            String[] authorFullName = i.split("\\s+");
            if (authorFullName.length == 1) {
                a = new Author(authorFullName[0]);
            } else if (authorFullName.length == 2) {
                a = new Author(authorFullName[0], authorFullName[1]);
            } else if (authorFullName.length == 3) {
                a = new Author(authorFullName[0], authorFullName[1], authorFullName[2]);
            } else {

            }
            authors[index] = a;
            index++;
        }
        this.authors = authors;
        this.price = BookContract.getPrice(c);
        this.isbn = BookContract.getIsbn(c);
    }

    public void writeToProvider(ContentValues values) {
        BookContract.putTitle(values, this.title);
        String authorsName = "";
        for (int i = 0; i < this.authors.length; i++) {
            if (authors[i].middleInitial != null) {
                authorsName += authors[i].firstName + " " + authors[i].middleInitial + " " + authors[i].lastName;
            } else {
                authorsName += authors[i].firstName + " " + authors[i].lastName;
            }
            authorsName += "\n";
        }
        BookContract.putAuthors(values, authorsName);
        BookContract.putIsbn(values, this.isbn);
        BookContract.putPrice(values, this.price);
    }

    @Override
    public String toString() {
        return title + " " + getFirstAuthor();
    }

    public String authorObjectToString(Author[] authors) {
        String authorsName = "";
        for(Author a : authors){
            if(a.middleInitial != null){
                authorsName += a.firstName+" "+a.middleInitial+" "+a.lastName;
            }else{
                authorsName += a.firstName+" "+a.lastName;
            }
            authorsName += "\n";
        }
        return authorsName;
    }


}