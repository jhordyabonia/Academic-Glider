package com.jhordyabonia.ag;

import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;

import static com.jhordyabonia.ag.PlaceholderFragment.newInstance;

public class MainActivity extends HomeActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {
    public MainActivity()
    {super();DROP_MODE=true;}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.dropMode(true);
    }

}
