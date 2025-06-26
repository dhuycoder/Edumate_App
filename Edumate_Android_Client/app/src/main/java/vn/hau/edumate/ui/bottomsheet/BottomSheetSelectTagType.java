package vn.hau.edumate.ui.bottomsheet;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

import javax.annotation.Nullable;

import vn.hau.edumate.R;
import vn.hau.edumate.data.model.response.TagResponse;
import vn.hau.edumate.ui.community.postcreate.PostCreateActivity;

public class BottomSheetSelectTagType extends BottomSheetDialogFragment {
    private final OnTagNameSelectedListener listener;
    private final List<TagResponse> tagResponses;
    public BottomSheetSelectTagType(OnTagNameSelectedListener listener, List<TagResponse> tagResponses) {
        this.listener = listener;
        this.tagResponses = tagResponses;
    }
    public interface OnTagNameSelectedListener {
        void onTagNameSelected(String name,int id);
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_select_tag_type, container, false);

        RadioGroup radioGroup = view.findViewById(R.id.radioGroupTalkType);
        Button btnSelect = view.findViewById(R.id.btnSelect);
        for (TagResponse tag : tagResponses) {
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setText(tag.getName());
            radioButton.setId(Math.toIntExact(tag.getId()));

            // Tuỳ chọn: thiết lập padding, margin, style nếu cần
            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(
                    RadioGroup.LayoutParams.WRAP_CONTENT,
                    RadioGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 16, 0, 16);
            radioGroup.addView(radioButton, params);
        }

        btnSelect.setOnClickListener(v -> {
            Intent intent = new Intent(view.getContext(), PostCreateActivity.class);
            // Lấy ID của RadioButton được chọn
            int selectedId = radioGroup.getCheckedRadioButtonId();
            if (selectedId != -1) {

                // Lấy RadioButton theo ID
                RadioButton selectedRadioButton = view.findViewById(selectedId);

                // Lấy nội dung (text) của RadioButton
                String selectedText = selectedRadioButton.getText().toString();
                listener.onTagNameSelected(selectedText,selectedId);
                dismiss();

            } else {
                Toast.makeText(view.getContext(), "Bạn chưa chọn mục nào!", Toast.LENGTH_SHORT).show();
            }

        });



        return view;
    }
}
