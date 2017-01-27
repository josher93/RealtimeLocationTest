package com.globalpaysolutions.realtimelocationtest.models;

/**
 * Created by Josué Chávez on 21/01/2017.
 */

public class User
{
    private int id;
    private String key;
    private long latitude;
    private long longitude;

    public int getId()
    {
        return id;
    }

    public String getKey()
    {
        return key;
    }

    public long getLatitude()
    {
        return latitude;
    }

    public long getLongitude()
    {
        return longitude;
    }

    public void setId(int pid)
    {
        id = pid;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public void setLatitude(long platitude)
    {
        latitude = platitude;
    }

    public void setLongitude(long plongitude)
    {
        longitude = plongitude;
    }

    public User()
    {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }


    public User(int id, String key, long latitude, long longitude)
    {
        this.setId(id);
        this.setKey(key);
        this.setLatitude(latitude);
        this.setLongitude(longitude);
    }
}
