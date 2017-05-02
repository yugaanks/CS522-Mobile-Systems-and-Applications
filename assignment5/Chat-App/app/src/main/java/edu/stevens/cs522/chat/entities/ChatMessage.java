package edu.stevens.cs522.chat.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import edu.stevens.cs522.chat.contracts.MessageContract;

/**
 * Created by dduggan.
 */

public class ChatMessage implements Parcelable {

    public long id;

    public String messageText;

    public Date timestamp;

    public String sender;

    public long senderId;

    public ChatMessage() {
    }

    //  add operations for parcels (Parcelable), cursors and contentvalues
    protected ChatMessage(Parcel in) {
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
    public ChatMessage(Cursor cursor) {
        //
        this.id = Long.parseLong(MessageContract.getId(cursor));
        this.messageText = MessageContract.getMessageText(cursor);
        this.sender = MessageContract.getSender(cursor);
        this.timestamp=MessageContract.getTimestamp(cursor);
    }
    public static final Parcelable.Creator<ChatMessage> CREATOR = new Parcelable.Creator<ChatMessage>() {
        public ChatMessage createFromParcel(Parcel source) {
            return new ChatMessage(source);
        }

        public ChatMessage[] newArray(int size) {
            return new ChatMessage[size];
        }
    };

    public void writeToProvider(ContentValues values) {
        MessageContract.putSender(values,sender);
        MessageContract.putTimestamp(values,timestamp);
        MessageContract.putMessageText(values,messageText);
    }

}
