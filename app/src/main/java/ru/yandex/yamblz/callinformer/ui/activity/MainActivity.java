package ru.yandex.yamblz.callinformer.ui.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import ru.yandex.yamblz.callinformer.R;
import ru.yandex.yamblz.callinformer.ui.fragment.SettingsFragment;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment settingsFragment = fragmentManager.findFragmentById(R.id.fragment_container);

        if(settingsFragment == null) {
            settingsFragment = new SettingsFragment();
            fragmentManager.beginTransaction().add(R.id.fragment_container, settingsFragment).commit();
        }

    }
}
