package edu.stevens.cs522.bookstore.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Author implements Parcelable {

	// NOTE: middleInitial may be NULL!

	public String firstName;

	public String middleInitial;

	public String lastName;

	public Author(String firstName, String middleInitial, String lastName) {
		this.firstName = firstName;
		this.middleInitial = middleInitial;
		this.lastName = lastName;
	}

	public Author(String firstName,String lastName) {
		this.lastName = lastName;
		this.firstName = firstName;
		this.middleInitial="";
	}

	public Author(String firstName) {
		this.firstName = firstName;
		this.middleInitial="";
		this.lastName="";
	}

	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(firstName);
		dest.writeString(middleInitial);
		dest.writeString(lastName);
	}

	public Author(Parcel in) {
		firstName=in.readString();
		middleInitial=in.readString();
		lastName=in.readString();
	}

    /*public Author(String authorName) {

    }*/

	public static final Parcelable.Creator<Author> CREATOR= new Parcelable.Creator<Author>() {
		public Author createFromParcel(Parcel in) {
			return new Author(in);
		}

		public Author[] newArray(int size) {
			return new Author[size];
		}
	};
}
