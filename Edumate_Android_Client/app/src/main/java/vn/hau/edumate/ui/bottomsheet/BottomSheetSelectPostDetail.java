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

public class BottomSheetSelectPostDetail  extends BottomSheetDialogFragment {
    private BottomSheetListener listener;

    public interface BottomSheetListener {
        void onEditClicked();
        void onDeleteClicked();
    }
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement BottomSheetListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_layout_post_detail, container, false);

        view.findViewById(R.id.btnEdit).setOnClickListener(v -> {
            listener.onEditClicked();
            dismiss();
        });

        view.findViewById(R.id.btnDelete).setOnClickListener(v -> {
            listener.onDeleteClicked();
            dismiss();
        });

        view.findViewById(R.id.btnCancel).setOnClickListener(v -> {
            dismiss();
        });

        return view;
    }
}
