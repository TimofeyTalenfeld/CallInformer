package ru.yandex.yamblz.callinformer.ui.fragment;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import java.io.File;
import java.io.IOException;

import ru.yandex.yamblz.callinformer.R;
import ru.yandex.yamblz.callinformer.receiver.CallReceiver;

/**
 * Created by root on 7/18/16.
 */
public class SettingsFragment extends Fragment {

    private File infoFile;

    private Switch enabledReceiver;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.settings_fragment, container, false);

        infoFile = new File(getContext().getFilesDir() + "/disabled");

        enabledReceiver = (Switch) v.findViewById(R.id.enable_widget);
        enabledReceiver.setChecked(!infoFile.exists());
        enabledReceiver.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b) {
                if(infoFile.exists()) {
                    infoFile.delete();
                }
            } else {
                if(!infoFile.exists()) {
                    try {
                        infoFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        return v;
    }
}
