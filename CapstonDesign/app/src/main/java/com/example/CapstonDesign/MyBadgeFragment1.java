package com.example.CapstonDesign;


import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

//import androidx.fragment.app.Fragment;


@SuppressLint("ValidFragment")
public class MyBadgeFragment1 extends Fragment {
    FragmentManager fragmentManager;
    View mView;
    public MyBadgeFragment1(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);

        mView = inflater.inflate(R.layout.fragment_badge1, container, false);

        setHasOptionsMenu(true);

        Bundle extra = this.getArguments();
        if (extra != null) {
            extra = getArguments();
            String tabId = extra.getString("tabId");
            String badgeCode = extra.getString("badgeCode");
            String badgeTitle = extra.getString("badgeTitle");
            String badgeDetail = extra.getString("badgeDetail");

            init(mView, badgeCode, badgeTitle, badgeDetail);

        }

        Button badge_close = mView.findViewById(R.id.badge_close);
        badge_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentManager.beginTransaction().remove(MyBadgeFragment1.this).commitAllowingStateLoss();

                FragmentManager fm = getFragmentManager();
                fm.popBackStackImmediate(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

        ImageButton btn_share = mView.findViewById(R.id.btn_share);

        btn_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = mView.findViewById(R.id.badge_img);
                view.setDrawingCacheEnabled(true);
                Bitmap screenBitmap = Bitmap.createBitmap(view.getDrawingCache());
                view.setDrawingCacheEnabled(false);

                try {
                    File cachePath = new File(getActivity().getApplicationContext().getCacheDir(), "images");
                    cachePath.mkdirs();
                    FileOutputStream stream = new FileOutputStream(cachePath + "/image.png");
                    screenBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    stream.close();

                    File newFile = new File(cachePath, "image.png");
                    Uri contentUri = FileProvider.getUriForFile(getActivity().getApplicationContext(),
                            "com.example.CapstonDesign.fileprovider", newFile);

                    Intent Sharing_intent = new Intent(Intent.ACTION_SEND);
                    Sharing_intent.setType("image/png");
                    Sharing_intent.putExtra(Intent.EXTRA_STREAM, contentUri);
                    startActivity(Intent.createChooser(Sharing_intent, "공유하기"));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        return mView;
    }

    private void init(View view, String badgeCode, String badgeTitle, String badgeDetail) {
        if (badgeCode != null) {
            ImageView badge_img = (ImageView) view.findViewById(R.id.badge_img);
            TextView badge_title = (TextView) view.findViewById(R.id.badge_title);
            TextView badge_detail = (TextView) view.findViewById(R.id.badge_detail);
            badge_img.setImageResource(Integer.parseInt(badgeCode));
            badge_title.setText(badgeTitle);
            badge_detail.setText(badgeDetail);
        }

    }
}
