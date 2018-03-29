package br.com.actia.controller;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import br.com.actia.Globals;
import br.com.actia.communication.CanMSG;
import br.com.actia.dualzoneinterface.R;

/**
 * Created by Armani andersonaramni@gmail.com on 20/02/17.
 * Edited by Gabriel Fernandes  gab.frosa@gmail.com
 */

public class MicFragment extends Fragment{
    private static final String TAG = "MicFragment";
    private View view;
    private boolean isDriver = false;
    //GAFR
    private Globals globals;
    private ImageButton btnMicBack;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_mic, container, false);

        globals = Globals.getInstance(getActivity().getApplication().getApplicationContext());
        //globals.getEventBus().register(this);
        Log.v(TAG, "onCreateView");

        btnMicBack = (ImageButton) view.findViewById(R.id.btnMicBack);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated");
        initializeActions();

    }

    private void initializeActions() {
        btnMicBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CanMSG canMSG = new CanMSG();
                canMSG.setId(CanMSG.MSGID_EQUIPAMENT_CTRL);
                canMSG.setLength((byte) 8);
                canMSG.setType(CanMSG.MSGTYPE_EXTENDED);
                //even PSG
                canMSG.setData("008400ffffffffff");
                globals.sendCanCMDEvent(canMSG);

                globals.setMicStatus(false);
            }
        });
    }

    public void sendChangeMode(Globals globals) {
        CanMSG canMSG = new CanMSG();
        canMSG.setId(CanMSG.MSGID_EQUIPAMENT_CTRL);
        canMSG.setLength((byte) 8);
        canMSG.setType(CanMSG.MSGTYPE_EXTENDED);

        //if(isDriver) {
        //    canMSG.setData("004000ffffffffff");
        //}else {
            canMSG.setData("008400ffffffffff");
        //}
        globals.sendCanCMDEvent(canMSG);
    }

    public void setIsDriver(boolean val) {
        isDriver = val;
    }
}
