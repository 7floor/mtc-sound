package com.sevenfloor.mtcsound.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import com.sevenfloor.mtcsound.R;

public class PatchInfoDialogFragment extends DialogFragment {

    static PatchInfoDialogFragment newInstance(String reason) {
        PatchInfoDialogFragment f = new PatchInfoDialogFragment();

        Bundle args = new Bundle();
        args.putString("reason", reason);
        f.setArguments(args);

        return f;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String reason = getArguments().getString("reason");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(String.format(getString(R.string.patch_info), reason))
                .setPositiveButton(android.R.string.ok, null);

        return builder.create();
    }
}
