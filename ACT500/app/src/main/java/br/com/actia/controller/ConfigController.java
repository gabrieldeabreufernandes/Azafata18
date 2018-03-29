package br.com.actia.controller;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.ToggleButton;

import br.com.actia.Globals;
import br.com.actia.communication.CanMSG;
import br.com.actia.dualzoneinterface.Interface;
import br.com.actia.dualzoneinterface.R;
import br.com.actia.event.DVDStatusEvent;
import br.com.actia.event.DriverStatusEvent;
import br.com.actia.event.PassengerStatusEvent;
import br.com.actia.event.SelectConfigEvent;
import br.com.actia.model.CONFIG.AppMenuBar;
import br.com.actia.model.CONFIG.AppWorkArea;
import br.com.actia.model.DVDStatusFrame;
import br.com.actia.model.DVD_S_MODEL.DVDEqualizer;
import br.com.actia.model.DVD_S_MODEL.DVDSource;
import br.com.actia.model.StatusFrame;

/**
 * Created by Armani(anderson.armani@actia.com.br) on 08/10/2015.
 */
public class ConfigController extends DefaultController {
    private ToggleButton tgBtnFlat = null;
    private ToggleButton tgBtnRock = null;
    private ToggleButton tgBtnOpera = null;
    private ToggleButton tgBtnPop = null;
    private ToggleButton tgBtnVoice = null;
    private ToggleButton tbInterfaceBlock = null;

    private ImageButton btnVolDown = null;
    private ImageButton btnVolUp = null;
    private ImageButton btnBalanceDown = null;
    private ImageButton btnBalanceUp = null;
    private ImageButton btnBassDown = null;
    private ImageButton btnBassUp = null;
    private ImageButton btnTrebleDown = null;
    private ImageButton btnTrebleUp = null;

    private SeekBar sbVolume = null;
    private SeekBar sbBalance = null;
    private SeekBar sbBass = null;
    private SeekBar sbTreble = null;

    Globals globals = null;
    private boolean isDriver;
    private StatusFrame statusFrame;
    private DVDSource actualSource;

    public ConfigController(Interface mainActivity, FrameLayout frameLayout, ImageButton button, @LayoutRes int resource, boolean isDriver) {
        super(mainActivity, frameLayout, button, resource);
        this.isDriver = isDriver;
        this.statusFrame = new StatusFrame();

        initializeView();
        initializeActions();
        globals = Globals.getInstance(mainActivity.getApplicationContext());
        globals.getEventBus().register(this);
    }

    @Override
    public void initializeView() {
        tgBtnFlat = (ToggleButton) view.findViewById(R.id.tbFlat);
        tgBtnRock = (ToggleButton) view.findViewById(R.id.tbRock);
        tgBtnOpera = (ToggleButton) view.findViewById(R.id.tbOpera);
        tgBtnPop = (ToggleButton) view.findViewById(R.id.tbPop);
        tgBtnVoice = (ToggleButton) view.findViewById(R.id.tbVoice);
        tbInterfaceBlock = (ToggleButton) view.findViewById((R.id.tbInterfaceBlock));

        btnVolDown = (ImageButton) view.findViewById(R.id.btnVolumeDown);
        btnVolUp = (ImageButton) view.findViewById(R.id.btnVolumeUp);
        btnBalanceDown = (ImageButton) view.findViewById(R.id.btnBalanceDown);
        btnBalanceUp = (ImageButton) view.findViewById(R.id.btnBalanceUp);
        btnBassDown = (ImageButton) view.findViewById(R.id.btnBassDown);
        btnBassUp = (ImageButton) view.findViewById(R.id.btnBassUp);
        btnTrebleDown = (ImageButton) view.findViewById(R.id.btnTrebleDown);
        btnTrebleUp = (ImageButton) view.findViewById(R.id.btnTrebleUp);

        sbVolume = (SeekBar) view.findViewById(R.id.skVolumeBar);
        sbBalance = (SeekBar) view.findViewById(R.id.sbBalanceBar);
        sbBass = (SeekBar) view.findViewById(R.id.sbBassBar);
        sbTreble = (SeekBar) view.findViewById(R.id.sbTrebleBar);
    }

    @Override
    public void initializeActions() {
        tgBtnFlat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEqualizer(DVDEqualizer.DVD_EQU_FLAT, true);
            }
        });
        tgBtnRock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEqualizer(DVDEqualizer.DVD_EQU_ROCK, true);
            }
        });
        tgBtnOpera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEqualizer(DVDEqualizer.DVD_EQU_OPERA, true);
            }
        });
        tgBtnPop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEqualizer(DVDEqualizer.DVD_EQU_POP, true);
            }
        });
        tgBtnVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEqualizer(DVDEqualizer.DVD_EQU_VOICE, true);
            }
        });
        tbInterfaceBlock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tbInterfaceBlock.isChecked()) {
                    globals.sendCanCtrlCMDEvent(CanMSG.CAN_CMD_ENABLE_BLOCKED_STATE);
                }
                else {
                    globals.sendCanCtrlCMDEvent(CanMSG.CAN_CMD_DISABLE_BLOCKED_STATE);
                }
            }
        });

        btnVolDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusFrame.volumeDown();
                sendStatusCMD(statusFrame);
            }
        });

        btnVolUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusFrame.volumeUp();
                sendStatusCMD(statusFrame);
            }
        });

        btnBalanceDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusFrame.balanceDown();
                sendStatusCMD(statusFrame);
            }
        });

        btnBalanceUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusFrame.balanceUp();
                sendStatusCMD(statusFrame);
            }
        });

        btnBassDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusFrame.bassDown();
                sendStatusCMD(statusFrame);
            }
        });

        btnBassUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusFrame.bassUp();
                sendStatusCMD(statusFrame);
            }
        });

        btnTrebleDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusFrame.trebleDown();
                sendStatusCMD(statusFrame);
            }
        });

        btnTrebleUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusFrame.trebleUp();
                sendStatusCMD(statusFrame);
            }
        });


        sbBalance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                statusFrame.setBalance((byte) sbBalance.getProgress());
                sendStatusCMD(statusFrame);
            }
        });
        sbBass.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                statusFrame.setBass((byte) sbBass.getProgress());
                sendStatusCMD(statusFrame);
            }
        });
        sbTreble.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                statusFrame.setTreble((byte) sbTreble.getProgress());
                sendStatusCMD(statusFrame);
            }
        });
        sbVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                statusFrame.setVolume((byte) sbVolume.getProgress());
                sendStatusCMD(statusFrame);
            }
        });
    }

    @Override
    public void sendChangeMode() {
        globals.getEventBus().post(new SelectConfigEvent(isDriver));
    }

    @Override
    public void configView(AppWorkArea appWorkArea, AppMenuBar appMenuBar) {
        //Change the looking fields here
    }

    private void sendStatusCMD(StatusFrame statusFrame){
        setSeekBars(statusFrame);
        CanMSG canMSG = new CanMSG();
        canMSG.setLength(CanMSG.DEFAULT_MSG_LEN);
        canMSG.setType(CanMSG.MSGTYPE_EXTENDED);

        if(isDriver) {
            //Setted the last data byte, like Javier Garate's e-mail
            byte btSend[] = statusFrame.getBytes();
            btSend[7] = actualSource.getValue();
            canMSG.setId(CanMSG.MSGID_DRIVER_CTRL);
            canMSG.setData(btSend, (byte) 8);
        }
        else {
            //Setted the last data byte, like Javier Garate's e-mail
            byte btSend[] = statusFrame.getBytes();
            btSend[7] = actualSource.getValue();
            canMSG.setId(CanMSG.MSGID_PASSENGER_CTRL);
            canMSG.setData(btSend, (byte)8);
        }

        globals.sendCanCMDEvent(canMSG);
        //Set time to show config view
        globals.getEventBus().post(new SelectConfigEvent(isDriver));
    }

    private void setSeekBars(StatusFrame statusFrame){
        this.statusFrame = statusFrame;
        sbVolume.setProgress(statusFrame.getVolume());
        sbBalance.setProgress(statusFrame.getBalance());
        sbBass.setProgress(statusFrame.getBass());
        sbTreble.setProgress(statusFrame.getTreble());
    }

    private void setEqualizer(byte type, boolean sendCMD) {
        byte cmdToSend = CanMSG.CAN_NO_CMD;

        tgBtnFlat.setChecked(false);
        tgBtnOpera.setChecked(false);
        tgBtnPop.setChecked(false);
        tgBtnRock.setChecked(false);
        tgBtnVoice.setChecked(false);

        switch (type){
           case DVDEqualizer.DVD_EQU_FLAT:
               tgBtnFlat.setChecked(true);
               cmdToSend = isDriver ? CanMSG.CAN_CMD_EQU_DRV_FLAT : CanMSG.CAN_CMD_EQU_PAS_FLAT;
               break;
           case DVDEqualizer.DVD_EQU_ROCK:
                tgBtnRock.setChecked(true);
                cmdToSend = isDriver ? CanMSG.CAN_CMD_EQU_DRV_ROCK : CanMSG.CAN_CMD_EQU_PAS_ROCK;
                break;
           case DVDEqualizer.DVD_EQU_OPERA:
               tgBtnOpera.setChecked(true);
               cmdToSend = isDriver ? CanMSG.CAN_CMD_EQU_DRV_OPERA : CanMSG.CAN_CMD_EQU_PAS_OPERA;
               break;
           case DVDEqualizer.DVD_EQU_POP:
               tgBtnPop.setChecked(true);
               cmdToSend = isDriver ? CanMSG.CAN_CMD_EQU_DRV_POP : CanMSG.CAN_CMD_EQU_PAS_POP;
               break;
           case DVDEqualizer.DVD_EQU_VOICE:
               tgBtnVoice.setChecked(true);
               cmdToSend = isDriver ? CanMSG.CAN_CMD_EQU_DRV_VOICE : CanMSG.CAN_CMD_EQU_PAS_VOICE;
               break;
        }

        if(sendCMD) {
            globals.sendCanCtrlCMDEvent(cmdToSend);

            //Set time to show config view
            globals.getEventBus().post(new SelectConfigEvent(isDriver));
        }
    }

    public void onEventMainThread(final PassengerStatusEvent event) {
        if(isDriver == false) {
            setSeekBars(event.getPassengerStatusFrame());
        }
    }

    public void onEventMainThread(final DriverStatusEvent event) {
        if(isDriver == true) {
            setSeekBars(event.getDriverStatusFrame());
        }
    }

    public void onEventMainThread(final DVDStatusEvent event) {
        DVDStatusFrame dvdStatusFrame = event.getDvdStatusFrame();
        if(isDriver == true) {
            setEqualizer(dvdStatusFrame.getDvdEqualizerDriver().getValue(), false);
            actualSource = dvdStatusFrame.getDvdSourceDriver();
        }
        else {
            setEqualizer(dvdStatusFrame.getDvdEqualizerPassenger().getValue(), false);
            actualSource = dvdStatusFrame.getDvdSourcePassenger();
        }
    }
}
