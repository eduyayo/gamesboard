package com.pigdroid.gameboard.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;
import com.mikepenz.aboutlibraries.ui.LibsFragment;
import com.pigdroid.gameboard.R;

/**
 * Created by eduyayo on 20/06/2015.
 */
public class AboutActivity extends KillableActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        LibsFragment fragment = new LibsBuilder()
                .withFields(R.string.class.getFields())
                .withLibraries("picasso", "androidbootstrap", "snackbar", "aboutlibraries")
                .withVersionShown(false)
                .withLicenseShown(false)
                .withLibraryModification("aboutlibraries", Libs.LibraryFields.LIBRARY_NAME, "_AboutLibraries")
                .fragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();

        findViewById(R.id.game_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse("http://pigdroid.bogspot.com"));
                startActivity(i);
            }
        });

    }
}
