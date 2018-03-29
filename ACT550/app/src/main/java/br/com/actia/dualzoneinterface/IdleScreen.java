package br.com.actia.dualzoneinterface;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class IdleScreen extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "IDLE SCREEN";
    private ImageButton btBackToHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idle_screen);

        btBackToHome = (ImageButton) findViewById(R.id.vipIdle);
        btBackToHome.setClickable(true);
        btBackToHome.setOnClickListener(this);
        //Intent intent = new Intent(IdleScreen.this, Interface.class);
        //startActivity(intent);

    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(IdleScreen.this, Interface.class);
        startActivity(intent);
    }
}
