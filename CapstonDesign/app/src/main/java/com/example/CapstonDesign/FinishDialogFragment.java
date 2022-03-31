package com.example.CapstonDesign;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class FinishDialogFragment extends DialogFragment {


    public static FinishDialogFragment newInstance(int isClick) {
        FinishDialogFragment fragment = new FinishDialogFragment();
        Bundle args = new Bundle();
        args.putInt("isTime",isClick);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_finish_dialog, container, false);

        Button btn_yes = view.findViewById(R.id.btn_yes);
        btn_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity()!=null)
                ((MapActivity)getActivity()).goToFinishFromDialog();

                dismiss();
            }
        });
        return view;
    }
}