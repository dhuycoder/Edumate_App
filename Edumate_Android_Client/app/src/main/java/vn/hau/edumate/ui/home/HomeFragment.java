package vn.hau.edumate.ui.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import vn.hau.edumate.R;
import vn.hau.edumate.databinding.FragmentHomeBinding;
import vn.hau.edumate.ui.auth.LoginActivity;
import vn.hau.edumate.ui.intro.intro1;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private HomeViewModel viewModel;
    private ImageButton searchAiBtn;
    private CardView guildCard;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        guildCard = view.findViewById(R.id.gettingStartedCard);
        searchAiBtn = view.findViewById(R.id.searchAiBtn);
        viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);
        viewModel.getIsLogout().observe(getViewLifecycleOwner(), isLogout -> {
            if(isLogout) {
                startActivity(new Intent(requireContext(), LoginActivity.class));
                requireActivity().finish();
            }
        });
        guildCard.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), intro1.class);
            startActivity(intent);
        });
        viewModel.getUserName().observe(getViewLifecycleOwner(), userName -> {
            binding.greetingTextView.setText("Chào " + userName);
        });
        searchAiBtn.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), SearchAiActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}