package br.com.actia.communication.DeviceCOM;

import android.util.Log;

/**
 * Created by Armani on 04/12/2015.
 */
public class DeviceConnectionEvent
{
    private static final String TAG = "CONNECTION THREAD";
    private boolean connected;
    private boolean error;
    private String message;

    public DeviceConnectionEvent()
    {
    }

    public DeviceConnectionEvent(boolean connected, boolean error, String message)
    {
        this.connected = connected;
        this.error = error;
        this.message = message;
        Log.d(TAG, "message on connect: "+this.message);
    }

    public boolean isConnected()
    {
        return connected;
    }

    public void setConnected(boolean connected)
    {
        this.connected = connected;
    }

    public boolean isError()
    {
        return error;
    }

    public void setError(boolean error)
    {
        this.error = error;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
}
