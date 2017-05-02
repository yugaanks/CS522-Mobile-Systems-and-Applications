package edu.stevens.cs522.chatserver.entities;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.common.net.InetAddresses;

import java.net.InetAddress;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import edu.stevens.cs522.chatserver.contracts.PeerContract;
import edu.stevens.cs522.chatserver.util.InetAddressUtils;

/**
 * Created by dduggan.
 */

public class Peer {

    public long id;

    public String name;
    // Last time we heard from this peer.
    public Date timestamp;

    public InetAddress address;

    public int port;

    // TODO add operations for parcels (Parcelable), cursors and contentvalues

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public InetAddress getAddress() {
        return address;
    }

    public void setAddress(InetAddress address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Peer(String name, InetAddress address, int port, Date timestamp) {
        this.name = name;
        this.address = address;
        this.port = port;
        this.timestamp = timestamp;
    }

    public Peer(Cursor cursor) {
        DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);
        this.id = Long.parseLong(PeerContract.getId(cursor));
        Log.i(Peer.class.getCanonicalName(), "id at peer class " + id);
        this.name = PeerContract.getName(cursor);
        Log.i(Peer.class.getCanonicalName(), "name at peer class " + name);
        String temp = PeerContract.getAddress(cursor).substring(1);
        Log.i(Peer.class.getCanonicalName(), "temp value at peer class " + temp);
        this.address = InetAddresses.forString(temp);
        Log.i(Peer.class.getCanonicalName(), "temp value after conversion at peer class " + temp);
        //this.address = InetAddressUtils.getAddress(cursor, 1);
        this.port = Integer.parseInt(PeerContract.getPort(cursor));
        try {
            this.timestamp = df.parse(PeerContract.getTimestamp(cursor));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public Peer() {
    }

    public int describeContents() {
        return 0;
    }

    public Peer(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.address = (InetAddress) in.readSerializable();
        this.address = InetAddressUtils.readAddress(in);
        this.port = in.readInt();
        this.timestamp = new Date(in.readLong());
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeSerializable(this.address);
        InetAddressUtils.writeAddress(dest, this.address);
        dest.writeInt(this.port);
        dest.writeLong(this.timestamp.getTime());
    }

    public static final Parcelable.Creator<Peer> CREATOR = new Parcelable.Creator<Peer>() {
        public Peer createFromParcel(Parcel source) {
            return new Peer(source);
        }

        public Peer[] newArray(int size) {
            return new Peer[size];
        }
    };

}
