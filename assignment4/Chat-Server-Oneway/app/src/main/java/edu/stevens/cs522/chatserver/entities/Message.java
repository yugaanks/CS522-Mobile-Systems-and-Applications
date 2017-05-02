package edu.stevens.cs522.chatserver.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import edu.stevens.cs522.chatserver.contracts.MessageContract;

/**
 * Created by dduggan.
 */

public class Message {

    public long id;

    public String messageText;

    public Date timestamp;

    public String sender;

    public long senderId;

    public Message(){}

    public Message(long id, String messageText, String sender, long senderId, Date timestamp) {
        this.id = id;
        this.messageText = messageText;
        this.sender = sender;
        this.senderId=senderId;
        this.timestamp=timestamp;
    }
    protected Message(Parcel in) {
        this.id = in.readLong();
        this.messageText = in.readString();
        this.sender = in.readString();
        this.senderId=in.readLong();
        this.timestamp=new Date(in.readLong());
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.messageText);
        dest.writeString(this.sender);
        dest.writeLong(this.senderId);
        dest.writeLong(this.timestamp.getTime());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public long getSenderId() {
        return senderId;
    }

    public void setSenderId(long senderId) {
        this.senderId = senderId;
    }

    public Message(Cursor cursor){
        this.id = Long.parseLong(MessageContract.getId(cursor));
        this.messageText = MessageContract.getMessageText(cursor);
        this.sender = MessageContract.getSender(cursor);
        //this.senderId=
        this.timestamp=MessageContract.getTimestamp(cursor);
    }

    public static final Parcelable.Creator<Message> CREATOR = new Parcelable.Creator<Message>() {
        public Message createFromParcel(Parcel source) {
            return new Message(source);
        }

        public Message[] newArray(int size) {
            return new Message[size];
        }
    };

    public void writeToProvider(ContentValues values) {
        MessageContract.putSender(values,sender);
        MessageContract.putTimestamp(values,timestamp);
        MessageContract.putMessageText(values,messageText);
    }

}