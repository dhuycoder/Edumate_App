package vn.hau.edumate.ui.bottomsheet;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import vn.hau.edumate.R;

public class BottomSheetReportOrHidePost extends BottomSheetDialogFragment {
    private BottomSheetReportOrHidePostListener listener;

    public interface BottomSheetReportOrHidePostListener {
        void onReportClicked();
        void onHideClicked();
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (BottomSheetReportOrHidePostListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement BottomSheetListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_report_hide_post, container, false);

        view.findViewById(R.id.btnReport).setOnClickListener(v -> {
            listener.onReportClicked();
            dismiss();
        });

        view.findViewById(R.id.btnHide).setOnClickListener(v -> {
            listener.onHideClicked();
            dismiss();
        });

        view.findViewById(R.id.btnCancel).setOnClickListener(v -> {
            dismiss();
        });

        return view;
    }
}
