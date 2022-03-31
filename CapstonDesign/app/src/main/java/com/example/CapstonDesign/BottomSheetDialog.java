package com.example.CapstonDesign;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetDialog extends BottomSheetDialogFragment {

    final private Context mContext;
    final private BottomSheetAdapter bottomSheetAdapter;

    public BottomSheetDialog(Context mContext, BottomSheetAdapter bottomSheetAdapter) {
        this.mContext = mContext;
        this.bottomSheetAdapter = bottomSheetAdapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable
            ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.modal_bottom_sheet,
                container, false);

        ListView runningFinishedList = v.findViewById(R.id.runningFinishedList);
        runningFinishedList.setAdapter(bottomSheetAdapter);

        return v;
    }
}
