package br.com.actia.controller;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.actia.Globals;
import br.com.actia.communication.CanMSG;
import br.com.actia.dualzoneinterface.R;

/**
 * Created by Armani andersonaramni@gmail.com on 20/02/17.
 */

public class AuxFragment extends Fragment {
    private static String TAG = "AuxFragment";
    private View    view;
    private boolean isDriver = false;
    private Globals globals;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_aux, container, false);

        globals = Globals.getInstance(getActivity().getApplication().getApplicationContext());
        //globals.getEventBus().register(this);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void sendChangeMode(Globals globals) {
        CanMSG canMSG = new CanMSG();
        canMSG.setId(CanMSG.MSGID_EQUIPAMENT_CTRL);
        canMSG.setLength((byte) 8);
        canMSG.setType(CanMSG.MSGTYPE_EXTENDED);

        if(isDriver) {
            canMSG.setData("0060000000000000");
            Log.d(TAG, "isDriver::send 0060000000000000");
        }else {
            canMSG.setData("0086000000000000");
            Log.d(TAG, "isPsg::send 0086000000000000");
        }
        globals.sendCanCMDEvent(canMSG);
    }

    public void setIsDriver(boolean val) {
        isDriver = val;
    }
}
