package br.com.actia.dualzoneinterface;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import br.com.actia.Globals;
import br.com.actia.communication.CanMSG;
import br.com.actia.communication.DeviceCOM.DeviceCom;
import br.com.actia.controller.AuxFragment;
import br.com.actia.controller.ConfigFragment;
import br.com.actia.controller.LoadConfigurations;
import br.com.actia.controller.MicFragment;
import br.com.actia.controller.RadioFragment;
import br.com.actia.controller.SDFragment;
import br.com.actia.controller.UsbFragment;
import br.com.actia.event.DVDStatusEvent;
import br.com.actia.event.PlaylistSDChangedEvent;
import br.com.actia.event.PlaylistUSBChangedEvent;
import br.com.actia.event.SelectConfigEvent;
import br.com.actia.model.CONFIG.AppBottomArea;
import br.com.actia.model.CONFIG.AppTopBar;
import br.com.actia.model.CONFIG.FileStructure;
import br.com.actia.model.EquipmentStatusFrame;
import br.com.actia.model.DVD_S_MODEL.DVDSource;
import br.com.actia.model.UsbMediaFile;
import br.com.actia.service.CanComService;

public class Interface extends Activity implements View.OnClickListener {
    private static final boolean DRIVER = true;
    private static final boolean PASSENGER = false;
    private static final String  DRIVER_TAG = "DRIVER_TAG";
    private static final String  PASSENGER_TAG = "PASSENGER_TAG";
    private static final String TAG = "DUAL ZONE MAIN";
    private ImageButton ibVipUsb;
    private ImageButton ibVipSd;
    private ImageButton ibVipRadio;
    private ImageButton ibVipAux;
    private ImageButton ibVipConfig;
    private ImageButton ibExecUsb;
    private ImageButton ibExecSd;
    private ImageButton ibExecRadio;
    private ImageButton ibExecAux;
    private ImageButton ibExecConfig;

    private ImageButton ibVipMic;
    private ImageButton ibExecMic;

    private Intent      intentService = null;
    private Globals     globals = null;

    private UsbMediaFile usbMediaFile;
    private UsbMediaFile sdMediaFile;

    private boolean isClick = false;
    private boolean micOn = false;

    private FragmentManager fragmentManager = getFragmentManager();

    private int tIdle = 0;
    private boolean currentSide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interface);

        usbMediaFile = new UsbMediaFile();
        sdMediaFile  = new UsbMediaFile();

        menuInitialize();

        Log.v(TAG, "Starting LoadConfigurations()");
        LoadConfigurations loader = new LoadConfigurations();
        FileStructure fileStructure = loader.getFileStructure();

        if(fileStructure != null) {
            Log.v(TAG, "fileStructure FOUND");
            setConfigurations(fileStructure);
        }
        else {
            Log.v(TAG, "fileStructure == NULL");
        }

        globals = Globals.getInstance(this);
        globals.getEventBus().register(this);

        globals.setMicStatus(false);
        this.tIdle = 0;

        //Enable Bluetooth Automatically
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
            Toast.makeText(getApplicationContext(), "Bluetooth switched ON", Toast.LENGTH_LONG).show();
        }else{
            Log.d(TAG, "mBluetoothAdapter isNotEnabled.");
            //Toast.makeText(getApplicationContext(), "Error during Bluetooth switing ON", Toast.LENGTH_LONG).show();
        }


        /*byte bt[] = new byte[8];
        bt[0] = (byte)0x21;
        bt[1] = (byte)0x00;
        bt[2] = (byte)0x00;
        bt[3] = (byte)0x00; //39
        bt[4] = (byte)0x00;
        bt[5] = (byte)0x00;
        bt[6] = (byte)0x00;
        bt[7] = (byte)0x00;
        globals.getEventBus().post(new DVDStatusEvent(bt));*/
    }

    @Override
    protected void onStart() {
        super.onStart();

        //// Transfered to onCreate to follow the MUX implementations.
        /*
        //Enable Bluetooth Automatically
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            mBluetoothAdapter.enable();
            Toast.makeText(getApplicationContext(), "Bluetooth switched ON", Toast.LENGTH_LONG).show();
        }else{
            Log.d(TAG, "mBluetoothAdapter isNotEnabled.");
            //Toast.makeText(getApplicationContext(), "Error during Bluetooth switing ON", Toast.LENGTH_LONG).show();
        }*/

        this.tIdle = 0;
    }

    private void keepAlive() {
        //Log.d(TAG, "KeepAlive running...");
        CanMSG canMSG = new CanMSG();
        canMSG.setId(CanMSG.MSGID_EQUIPAMENT_ALIVE);
        canMSG.setLength((byte) 8);
        canMSG.setType(CanMSG.MSGTYPE_EXTENDED);
        canMSG.setData("0000000000000000");
        globals.sendCanCMDEvent(canMSG);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.tIdle = 0;

        //Start service
        intentService = new Intent(this, CanComService.class);
        intentService.putExtra("COM_TYPE", DeviceCom.DEVICE_BT_C2BT);
        startService(intentService);

        new Thread(new Runnable() {
            public void run(){
                while(true){
                    try {
                        Thread.sleep(2000);
                        //Log.d(TAG,"TESTE");
                        keepAlive();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        //Stop the Communication Service
        stopService(new Intent(this, CanComService.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void menuInitialize() {
        ibVipUsb = (ImageButton) findViewById(R.id.vipUsb);
        ibVipSd = (ImageButton) findViewById(R.id.vipSd);
        ibVipRadio = (ImageButton) findViewById(R.id.vipRadio);
        ibVipAux = (ImageButton) findViewById(R.id.vipAux);
        ibVipConfig = (ImageButton) findViewById(R.id.vipConfig);
        ibVipMic = (ImageButton) findViewById(R.id.vipMIC);

        ibVipUsb.setOnClickListener(this);
        ibVipSd.setOnClickListener(this);
        ibVipRadio.setOnClickListener(this);
        ibVipAux.setOnClickListener(this);
        ibVipConfig.setOnClickListener(this);
        //ibVipMic.setOnClickListener(this);

        ibVipMic.setClickable(false);

        ibExecUsb = (ImageButton) findViewById(R.id.execUsb);
        ibExecSd = (ImageButton) findViewById(R.id.execSd);
        ibExecRadio = (ImageButton) findViewById(R.id.execRadio);
        ibExecAux = (ImageButton) findViewById(R.id.execAux);
        ibExecConfig = (ImageButton) findViewById(R.id.execConfig);
        ibExecMic = (ImageButton) findViewById(R.id.execMIC);

        ibExecUsb.setOnClickListener(this);
        ibExecSd.setOnClickListener(this);
        ibExecRadio.setOnClickListener(this);
        ibExecAux.setOnClickListener(this);
        ibExecConfig.setOnClickListener(this);
        ibExecMic.setOnClickListener(this);
    }

    //ivate void setCurrentSources(byte frmDriverSource, byte frmPassSource) {
    private void setCurrentSources(byte frmDriverSource, byte frmPassSource, DVDStatusEvent event) {
        Fragment vipFrag = fragmentManager.findFragmentById(R.id.vipTabContent);
        if(vipFrag == null || !(vipFrag instanceof ConfigFragment)) {
            setDriverSource(frmDriverSource);
        }

        Fragment execFrag = fragmentManager.findFragmentById(R.id.execTabContent);
        if(execFrag == null || !(execFrag instanceof ConfigFragment)) {
            setPassengerSource(frmPassSource);
        }
    }

    private void setDriverSource(byte frmDriverSource) {

        Log.d(TAG,"setDriverSource running...");

        //anyone vip btn for clear the correct side menu
        resetMenu(ibVipAux);
        //resetMenu(ibVipMic);

        switch (frmDriverSource) {
            case DVDSource.DVD_SOURCE_AUX:
                Log.d(TAG,"DVD_SOURCE_AUX...");
                setFragmentAux(R.id.vipTabContent, DRIVER);
                ibVipAux.setSelected(true);
                break;
            case DVDSource.DVD_SOURCE_SDCARD:
                Log.d(TAG,"DVD_SOURCE_SDCARD...");
                setFragmentSD(R.id.vipTabContent, DRIVER);
                ibVipSd.setSelected(true);
                break;
            case DVDSource.DVD_SOURCE_USB:
                Log.d(TAG,"DVD_SOURCE_USB...");
                setFragmentUSB(R.id.vipTabContent, DRIVER);
                ibVipUsb.setSelected(true);
                break;
            case DVDSource.DVD_SOURCE_RADIO:
                Log.d(TAG,"DVD_SOURCE_RADIO...");
                setFragmentRadio(R.id.vipTabContent, DRIVER);
                ibVipRadio.setSelected(true);
                break;
            case DVDSource.DVD_SOURCE_MIC:
                Log.d(TAG,"DVD_SOURCE_MIC...");
                setFragmentMIC(R.id.vipTabContent, DRIVER);
                ibVipMic.setSelected(false);
                break;
            case DVDSource.DVD_SOURCE_CONFIG:
                Log.d(TAG,"DVD_SOURCE_CONFIG...");
                //setFragmentConfig(R.id.vipTabContent, DRIVER);
                //ibVipConfig.setSelected(true);
                break;
        }
    }

    private void setPassengerSource(byte frmPassSource) {
        Log.d(TAG,"setPassengerSource running...");
        //anyone exec btn for clear the correct side menu
        resetMenu(ibExecAux);
        //resetMenu(ibExecMic);

        switch (frmPassSource) {
            case DVDSource.DVD_SOURCE_AUX:
                Log.d(TAG,"Source = DVD_SOURCE_AUX");
                setFragmentAux(R.id.execTabContent, PASSENGER);
                ibExecAux.setSelected(true);
                break;
            case DVDSource.DVD_SOURCE_SDCARD:
                setFragmentSD(R.id.execTabContent, PASSENGER);
                ibExecSd.setSelected(true);
                break;
            case DVDSource.DVD_SOURCE_USB:
                setFragmentUSB(R.id.execTabContent, PASSENGER);
                ibExecUsb.setSelected(true);
                break;
            case DVDSource.DVD_SOURCE_RADIO:
                setFragmentRadio(R.id.execTabContent, PASSENGER);
                ibExecRadio.setSelected(true);
                break;
            case DVDSource.DVD_SOURCE_MIC:
                setFragmentMIC(R.id.execTabContent, PASSENGER);
                ibExecMic.setSelected(true);
                break;
            case DVDSource.DVD_SOURCE_CONFIG:
                //setFragmentConfig(R.id.execTabContent, PASSENGER);
                //ibExecConfig.setSelected(true);
                break;
        }
    }

    //Clear all menu selections
    public void resetMenu(ImageButton button) {
        if( button == ibVipSd || button == ibVipUsb || button == ibVipRadio ||
                button == ibVipAux || button == ibVipConfig || button == ibVipMic ) {
            ibVipSd.setSelected(false);
            ibVipUsb.setSelected(false);
            ibVipRadio.setSelected(false);
            ibVipAux.setSelected(false);
            ibVipConfig.setSelected(false);
            ibVipMic.setSelected(false);
        }
        else if( button == ibExecSd || button == ibExecUsb || button == ibExecRadio ||
                button == ibExecAux || button == ibExecConfig || button == ibExecMic) {
            ibExecSd.setSelected(false);
            ibExecUsb.setSelected(false);
            ibExecRadio.setSelected(false);
            ibExecAux.setSelected(false);
            ibExecConfig.setSelected(false);
            ibExecMic.setSelected(false);
        }
    }

    @Override
    public void onClick(View view) {
        int R_content;

        ImageButton ib = (ImageButton) view;
        resetMenu(ib);
        ib.setSelected(true);
        isClick = true;
        //micOn = false;

        if(view == ibVipAux || view == ibVipConfig || view == ibVipSd || view == ibVipRadio || view == ibVipUsb || view == ibVipMic) {
        //if(view == ibVipAux || view == ibVipConfig || view == ibVipSd || view == ibVipRadio || view == ibVipUsb || view == ibExecMic) {
            R_content = R.id.vipTabContent;
        }
        else {
            R_content = R.id.execTabContent;
        }

        if(view == ibVipUsb || view == ibExecUsb) {
            Log.d(TAG,"any USB button pressed...");
            if (!globals.getMicStatus()){
                Log.d(TAG,"setFragmentUSB::MIC is ON::Let's disable...");
                //sendMicDisable(false);
                setFragmentUSB(R_content, (view == ibVipUsb));
                //micOn = false;
            }else{
                Log.d(TAG,"setFragmentUSB::MIC is OFF::noting to do!!!");
            }
        }
        else if(view == ibVipSd || view == ibExecSd) {
            Log.d(TAG,"any SD button pressed...");
            if (!globals.getMicStatus()){
                Log.d(TAG,"setFragmentSD::MIC is ON::Let's disable...");
                //sendMicDisable(false);
                setFragmentSD(R_content, (view == ibVipSd));
                //micOn = false;
            }else{
                Log.d(TAG,"setFragmentSD::MIC is OFF::noting to do!!!");
            }
        }
        else if(view == ibVipRadio || view == ibExecRadio) {
            Log.d(TAG,"any RADIO button pressed...");
            if (!globals.getMicStatus()){
                Log.d(TAG,"setFragmentRADIO::MIC is ON::Let's disable...");
                //sendMicDisable(false);
                setFragmentRadio(R_content, (view == ibVipRadio));
                //micOn = false;
            }else{
                Log.d(TAG,"setFragmentRADIO::MIC is OFF::noting to do!!!");
            }
        }
        else if(view == ibVipAux || view == ibExecAux) {
            Log.d(TAG,"any AUX button pressed...");
            if (!globals.getMicStatus()){
                Log.d(TAG,"setFragmentAUX::MIC is ON ::Let's disable...");
                //sendMicDisable(false);
                setFragmentAux(R_content, (view == ibVipAux));
                //micOn = false;
            }else{
                Log.d(TAG,"setFragmentAUX::MIC is OFF::noting to do!!!");
            }
        }
        else if(view == ibVipConfig || view == ibExecConfig) {
            if (!globals.getMicStatus()){
                Log.d(TAG,"setFragmentUSB::MIC is ON::Let's disable...");
                //sendMicDisable(false);
                setFragmentConfig(R_content, (view == ibVipConfig));
                //micOn = false;
            }
        }
        else if(view == ibVipMic || view == ibExecMic) {
            setFragmentMIC(R_content, (view == ibExecMic));
            /*
            if (micOn){
                Log.d(TAG,"setFragmentUSB::MIC is ON::Let's disable...");
                sendMicDisable(false);
                micOn = false;
            }*/
        }
    }

    //The MIC source needs click again to exit, is like physical button that is pull-up.
    private void sendMicDisable(boolean isDrive){
        Log.d(TAG,"sendMicDisable running");

        CanMSG canMSG = new CanMSG();
        canMSG.setId(CanMSG.MSGID_EQUIPAMENT_CTRL);
        canMSG.setLength((byte) 8);
        canMSG.setType(CanMSG.MSGTYPE_EXTENDED);

        if(isDrive) {
            Log.d(TAG,"is Drv");
            canMSG.setData("004000ffffffffff");
        }else {
            Log.d(TAG,"is PSG");
            canMSG.setData("008400ffffffffff");
        }
        globals.sendCanCMDEvent(canMSG);
    }

    private void setFragmentUSB(int R_fragmentContainer, boolean isDrive) {
        Fragment fragment = fragmentManager.findFragmentById(R_fragmentContainer);
        if(fragment != null && (fragment instanceof UsbFragment)) {
            return;
        }
        Log.d(TAG, "CHANGE USB fragment | IsDriver : " + isDrive);

        UsbFragment usbFragment = new UsbFragment();
        usbFragment.setIsDriver(isDrive);
        if(isClick){
            Log.d(TAG, "by click...");
            usbFragment.sendChangeMode(globals);
            isClick =false;
        }else{
            Log.d(TAG, "setFragmentUSB called by CAN");
        }
        usbFragment.setUsbMediaFile(this.usbMediaFile);
        changeFragment(R_fragmentContainer, usbFragment, isDrive);
    }

    private void setFragmentSD(int R_fragmentContainer, boolean isDrive) {
        Fragment fragment = fragmentManager.findFragmentById(R_fragmentContainer);
        if(fragment != null && (fragment instanceof SDFragment)) {
            return;
        }
        Log.d(TAG, "CHANGE SD fragment | IsDriver : " + isDrive);

        SDFragment sdFragment = new SDFragment();
        sdFragment.setIsDriver(isDrive);
        sdFragment.setCurrentSide(this.currentSide);

        if(isClick) {
            Log.d(TAG,"by click...");
            sdFragment.sendChangeMode(globals);
            isClick = false;
        }else{
            Log.d(TAG,"setFragmentSD called by CAN...");
        }
        sdFragment.setSdMediaFile(this.sdMediaFile);

        changeFragment(R_fragmentContainer, sdFragment, isDrive);
    }

    private void setFragmentRadio(int R_fragmentContainer, boolean isDrive) {

        /*
        if (micOn){
            Log.d(TAG,"setFragmentRadio::MIC is ON::Let's disable...");
            sendMicDisable(isDrive);
            micOn = false;
        }*/

        Fragment fragment = fragmentManager.findFragmentById(R_fragmentContainer);
        if(fragment != null && (fragment instanceof RadioFragment)) {
            return;
        }
        Log.d(TAG, "CHANGE RADIO fragment | IsDriver : " + isDrive);

        RadioFragment radioFragment = new RadioFragment();
        radioFragment.setIsDriver(isDrive);
        if(isClick) {
            Log.d(TAG,"by click");
            radioFragment.sendChangeMode(globals);
            isClick = false;
        }else{
            Log.d(TAG, "setFragmentRadio called by CAN...");
        }
        changeFragment(R_fragmentContainer, radioFragment, isDrive);
    }

    private void setFragmentAux(int R_fragmentContainer, boolean isDrive) {
        /*
        if (micOn){
            Log.d(TAG,"setFragmentAux::MIC is ON::Let's disable...");
            sendMicDisable(isDrive);
            micOn = false;
        }*/

        Fragment fragment = fragmentManager.findFragmentById(R_fragmentContainer);
        if(fragment != null && (fragment instanceof AuxFragment)) {
            return;
        }
        Log.d(TAG, "CHANGE AUX fragment | IsDriver : " + isDrive);

        AuxFragment auxFragment = new AuxFragment();
        auxFragment.setIsDriver(isDrive);
        if(isClick) {
            Log.d(TAG, "by click...");
            auxFragment.sendChangeMode(globals);
            isClick = false;
        }else{
            Log.d(TAG, "setFragmentAux called by CAN...");
        }
        changeFragment(R_fragmentContainer, auxFragment, isDrive);
    }

    private void setFragmentConfig(int R_fragmentContainer, boolean isDrive) {

        /*
        if (micOn){
            Log.d(TAG,"setFragmentConfig::MIC is ON::Let's disable...");
            sendMicDisable(isDrive);
            micOn = false;
        }*/

        Fragment fragment = fragmentManager.findFragmentById(R_fragmentContainer);
        if(fragment != null && (fragment instanceof ConfigFragment)) {
            return;
        }
        Log.d(TAG, "CHANGE CONFIG fragment | IsDriver : " + isDrive);

        ConfigFragment configFragment = new ConfigFragment();
        configFragment.setIsDriver(isDrive);
        if(isClick) {
            configFragment.sendChangeMode(globals);
            isClick = false;
        }

        changeFragment(R_fragmentContainer, configFragment, isDrive);
    }

    private void setFragmentMIC(int R_fragmentContainer, boolean isDrive) {

        int R_cntnt = R.id.execTabContent;

        Fragment fragment = fragmentManager.findFragmentById(R_fragmentContainer);
        if(fragment != null && (fragment instanceof MicFragment)) {
            return;
        }
        Log.d(TAG, "CHANGE MIC fragment | IsDriver : " + isDrive);

        MicFragment micFragment = new MicFragment();
        micFragment.setIsDriver(isDrive);
        if(isClick) {
            micFragment.sendChangeMode(globals);
            isClick = false;
        }
        changeFragment(R_fragmentContainer, micFragment, isDrive);
        micOn = true;
        globals.setMicStatus(micOn);
    }

    /**
     * Change the current fragment (Destroy it) for a new fragment
     * @param R_fragmentContainer - Fragment Container ID
     * @param newFragment - New fragment instance
     * @param isDriver - boolean indicating if is driver or not
     */
    private void changeFragment(int R_fragmentContainer, Fragment newFragment, boolean isDriver) {
        Fragment currentFragment;
        String tag;

        tag = isDriver ? DRIVER_TAG : PASSENGER_TAG;

        currentFragment = fragmentManager.findFragmentByTag(tag);

        if(currentFragment != null) {
            FragmentTransaction trans = fragmentManager.beginTransaction();
            trans.remove(currentFragment).commit();
        }

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R_fragmentContainer, newFragment, tag);
        fragmentTransaction.commit();
    }

    //##############################################################################################
    //EVENTS
    //##############################################################################################
    public void onEventMainThread(final DVDStatusEvent event) {

        //Log.d(TAG,"GAAAFR:: onEventMainThread running...");

        EquipmentStatusFrame equipmentStatusFrame = event.getEquipmentStatusFrame();
        this.currentSide = equipmentStatusFrame.getZone();

        byte frmDriverSource = equipmentStatusFrame.getDvdSourceDriver().getValue();
        byte frmPassSource = equipmentStatusFrame.getDvdSourcePassenger().getValue();
        this.tIdle = 0;

        //setCurrentSources(frmDriverSource, frmPassSource);
        setCurrentSources(frmDriverSource, frmPassSource, event);
    }

    public void onEventMainThread(final SelectConfigEvent event) {
        Log.d(TAG,"onEventMainThread (eventBus) running...");
        if(event.isDriver()) {
            Log.d(TAG,"event.isDrive");
            setDriverSource(DVDSource.DVD_SOURCE_CONFIG);
        }else {
            Log.d(TAG,"event.isPSG");
            setPassengerSource(DVDSource.DVD_SOURCE_CONFIG);
        }
    }

    /**
     * EventBus callback
     * @param playlistUSBChangedEvent
     */
    public void onEventMainThread(final PlaylistUSBChangedEvent playlistUSBChangedEvent) {
        UsbMediaFile usbMediaFile = playlistUSBChangedEvent.getUsbMediaFile();

        if(! usbMediaFile.getPlaylistPath().equalsIgnoreCase(this.usbMediaFile.getPlaylistPath())) {
            this.usbMediaFile = usbMediaFile;
        }
    }

    public void onEventMainThread(final PlaylistSDChangedEvent playlistSDChangedEvent) {
        UsbMediaFile sdMediaFile = playlistSDChangedEvent.getSdMediaFile();

        if(! sdMediaFile.getPlaylistPath().equalsIgnoreCase(this.sdMediaFile.getPlaylistPath())) {
            this.sdMediaFile = sdMediaFile;
        }
    }

    //##############################################################################################
    //CONFIGURATIONS
    //##############################################################################################
    private void setConfigurations(FileStructure fileStructure) {
        Log.v(TAG, "###" + fileStructure.toString());

        try {
            //Set TOP BAR CONFIGURATIONS
            AppTopBar topBar = fileStructure.getTopBar();

            if (topBar != null) {
                TextView tvArea1 = (TextView) findViewById(R.id.area1Text);
                TextView tvArea2 = (TextView) findViewById(R.id.area2Text);
                LinearLayout area1 = (LinearLayout) findViewById(R.id.area1Header);
                LinearLayout area2 = (LinearLayout) findViewById(R.id.area2Header);
                LinearLayout brandArea = (LinearLayout) findViewById(R.id.topBrandArea);
                View separator = (View) findViewById(R.id.separator);

                //Remove all default views
                brandArea.removeAllViews();

                //TopBar background color
                brandArea.setBackgroundColor(Color.parseColor(topBar.getBgColor()));
                tvArea1.setBackgroundColor(Color.parseColor(topBar.getBgColor()));
                tvArea2.setBackgroundColor(Color.parseColor(topBar.getBgColor()));
                separator.setBackgroundColor(Color.parseColor(topBar.getBgColor()));

                tvArea1.setText(topBar.getArea1Text());
                tvArea2.setText(topBar.getArea2Text());

                List<String> logoList = topBar.getLogoList();

                for (int i = 0; i < logoList.size(); i++) {
                    ImageView imageView = new ImageView(getApplicationContext());
                    Bitmap bmp = BitmapFactory.decodeFile(logoList.get(i));

                    if(bmp != null)
                        imageView.setImageBitmap(bmp);

                    brandArea.addView(imageView);
                }
            }

            //Set Buttons configurations
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ibVipSd.setBackground(makeSelector(Color.parseColor(fileStructure.getMenuBar().getBtnPressed()),
                        Color.parseColor(fileStructure.getMenuBar().getBtnSelected()), Color.parseColor(fileStructure.getMenuBar().getBtnNormal())));
                ibVipUsb.setBackground(makeSelector(Color.parseColor(fileStructure.getMenuBar().getBtnPressed()),
                        Color.parseColor(fileStructure.getMenuBar().getBtnSelected()), Color.parseColor(fileStructure.getMenuBar().getBtnNormal())));
                ibVipRadio.setBackground(makeSelector(Color.parseColor(fileStructure.getMenuBar().getBtnPressed()),
                        Color.parseColor(fileStructure.getMenuBar().getBtnSelected()), Color.parseColor(fileStructure.getMenuBar().getBtnNormal())));
                ibVipAux.setBackground(makeSelector(Color.parseColor(fileStructure.getMenuBar().getBtnPressed()),
                        Color.parseColor(fileStructure.getMenuBar().getBtnSelected()), Color.parseColor(fileStructure.getMenuBar().getBtnNormal())));
                ibVipConfig.setBackground(makeSelector(Color.parseColor(fileStructure.getMenuBar().getBtnPressed()),
                        Color.parseColor(fileStructure.getMenuBar().getBtnSelected()), Color.parseColor(fileStructure.getMenuBar().getBtnNormal())));
                ibVipMic.setBackground(makeSelector(Color.parseColor(fileStructure.getMenuBar().getBtnPressed()),
                        Color.parseColor(fileStructure.getMenuBar().getBtnSelected()), Color.parseColor(fileStructure.getMenuBar().getBtnNormal())));

                ibExecSd.setBackground(makeSelector(Color.parseColor(fileStructure.getMenuBar().getBtnPressed()),
                        Color.parseColor(fileStructure.getMenuBar().getBtnSelected()), Color.parseColor(fileStructure.getMenuBar().getBtnNormal())));
                ibExecSd.setBackground(makeSelector(Color.parseColor(fileStructure.getMenuBar().getBtnPressed()),
                        Color.parseColor(fileStructure.getMenuBar().getBtnSelected()), Color.parseColor(fileStructure.getMenuBar().getBtnNormal())));
                ibExecRadio.setBackground(makeSelector(Color.parseColor(fileStructure.getMenuBar().getBtnPressed()),
                        Color.parseColor(fileStructure.getMenuBar().getBtnSelected()), Color.parseColor(fileStructure.getMenuBar().getBtnNormal())));
                ibExecAux.setBackground(makeSelector(Color.parseColor(fileStructure.getMenuBar().getBtnPressed()),
                        Color.parseColor(fileStructure.getMenuBar().getBtnSelected()), Color.parseColor(fileStructure.getMenuBar().getBtnNormal())));
                ibExecConfig.setBackground(makeSelector(Color.parseColor(fileStructure.getMenuBar().getBtnPressed()),
                        Color.parseColor(fileStructure.getMenuBar().getBtnSelected()), Color.parseColor(fileStructure.getMenuBar().getBtnNormal())));
                ibExecMic.setBackground(makeSelector(Color.parseColor(fileStructure.getMenuBar().getBtnPressed()),
                        Color.parseColor(fileStructure.getMenuBar().getBtnSelected()), Color.parseColor(fileStructure.getMenuBar().getBtnNormal())));
            }

            //Set bottom Bar configurations
            AppBottomArea bottomBar = fileStructure.getBottomArea();

            if(bottomBar != null) {
                LinearLayout poweredArea = (LinearLayout) findViewById(R.id.bottomPoweredBy);

                //Remove all default views
                poweredArea.removeAllViews();

                List<String> logoList = bottomBar.getLogoList();
                for(int i = 0; i < logoList.size(); i++) {
                    ImageView imageView = new ImageView(getApplicationContext());
                    Bitmap bmp = BitmapFactory.decodeFile(logoList.get(i));

                    if(bmp != null)
                        imageView.setImageBitmap(bmp);

                    poweredArea.addView(imageView);
                }
            }

            //Set Controllers colors

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public StateListDrawable makeSelector(int pressedColor, int selectedColor, int normalColor) {
        StateListDrawable res = new StateListDrawable();

        res.addState(new int[]{android.R.attr.state_pressed}, new ColorDrawable(pressedColor));
        res.addState(new int[]{android.R.attr.state_selected}, new ColorDrawable(selectedColor));
        res.addState(new int[]{}, new ColorDrawable(normalColor));
        return res;
    }
}
