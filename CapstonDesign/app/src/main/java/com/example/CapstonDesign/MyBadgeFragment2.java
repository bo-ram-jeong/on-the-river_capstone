package com.example.CapstonDesign;


import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

//import androidx.fragment.app.Fragment;

@SuppressLint("ValidFragment")
public class MyBadgeFragment2 extends Fragment {
    FragmentManager fragmentManager;

    public MyBadgeFragment2(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);

        View mView = inflater.inflate(R.layout.fragment_badge2, container, false);

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
                fragmentManager.beginTransaction().remove(MyBadgeFragment2.this).commitAllowingStateLoss();

                FragmentManager fm = getFragmentManager();
                fm.popBackStackImmediate(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });
        return mView;
    }

    private void init(View view, String badgeCode, String badgeTitle, String badgeDetail) {
        if (badgeCode != null) {
            ImageView badge_img = (ImageView) view.findViewById(R.id.badge_img);
            TextView  badge_title = (TextView) view.findViewById(R.id.badge_title);
            TextView  badge_detail = (TextView) view.findViewById(R.id.badge_detail);
            badge_img.setImageResource(Integer.parseInt(badgeCode));
            badge_title.setText(badgeTitle);
            badge_detail.setText(badgeDetail);
        }

    }
}
