package com.jhordyabonia.ag;

import android.os.Bundle;
import android.widget.TextView;

import util.NavigationDrawerFragment;

public class MainActivity extends HomeActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    public MainActivity()
    {super();DROP_MODE=true;}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.dropMode(true);
        //this.onNavigationDrawerItemSelected(2);
    }
}
