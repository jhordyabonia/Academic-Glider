package com.jhordyabonia.ag;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;

import util.InformacionFragment;
import util.Style;

import static com.jhordyabonia.ag.HomeActivity.ON_DISPLAY;

public class NotificacionesActivity extends FragmentActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Style.bar(this);

        setContentView(R.layout.lienzo);
        Notificaciones noti= new Notificaciones();

        noti.paint(findViewById(R.id.FrameLayout1));
        ON_DISPLAY=HomeActivity.NOTIFICATION;
        getActionBar().setTitle(R.string.notifications);

        getActionBar().setHomeButtonEnabled(true);
    }
    @Override
    public final boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}