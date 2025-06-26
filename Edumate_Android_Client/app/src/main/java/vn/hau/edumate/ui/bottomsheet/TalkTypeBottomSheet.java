package vn.hau.edumate.ui.bottomsheet;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import javax.annotation.Nullable;

import vn.hau.edumate.R;
import vn.hau.edumate.data.enums.TagType;

public class TalkTypeBottomSheet extends BottomSheetDialogFragment {
    private OnTalkTypeSelectedListener listener;
    public interface OnTalkTypeSelectedListener {
        void onTalkTypeSelected(TagType type);
    }
    public TalkTypeBottomSheet(OnTalkTypeSelectedListener listener) {
        this.listener = listener;
    }
    private Button btnSharingKnowledge, btnHomeWorkSupport;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(vn.hau.edumate.R.layout.bottom_sheet_talk_type, container, false);
        btnSharingKnowledge = view.findViewById(R.id.Sharing_knowledge);
        btnHomeWorkSupport = view.findViewById(R.id.home_work_support);
        btnHomeWorkSupport.setOnClickListener(v -> {
            listener.onTalkTypeSelected(TagType.HOMEWORK_SUPPORT);
            dismiss();
        });
        btnSharingKnowledge.setOnClickListener(v -> {
            listener.onTalkTypeSelected(TagType.SHARING_KNOWLEDGE);
            dismiss();
        });
        return view;
    }

}
