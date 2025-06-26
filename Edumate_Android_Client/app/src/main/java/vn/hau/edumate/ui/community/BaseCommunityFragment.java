package vn.hau.edumate.ui.community;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import vn.hau.edumate.R;
import vn.hau.edumate.data.model.response.PostResponse;
import vn.hau.edumate.ui.adapter.PostAdapter;
import vn.hau.edumate.ui.community.sharingknowledge.SharingKnowLedgeViewModel;

public abstract class BaseCommunityFragment<T extends AndroidViewModel> extends Fragment {
    public LinearLayout layoutBtnTag;
    protected RecyclerView rcvPost;
    protected PostAdapter postAdapter;

    protected T viewModel;
    private final Class<T> viewModelClass;
    protected LinearLayout layoutNoPost;


    protected FrameLayout loadingPageLayout;

    protected BaseCommunityFragment(Class<T> viewModel) {
        this.viewModelClass = viewModel;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(viewModelClass);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.base_fragment_community, container, false);

        loadingPageLayout = view.findViewById(R.id.loading_page);
        layoutBtnTag = view.findViewById(R.id.layout_btn_tag);
        rcvPost = view.findViewById(R.id.rcv_post);
        postAdapter = new PostAdapter(view.getContext(), (BaseCommunityViewModel) viewModel);
        layoutNoPost = view.findViewById(R.id.layout_no_posts);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(),RecyclerView.VERTICAL,false);
        rcvPost.setLayoutManager(linearLayoutManager);
        rcvPost.setAdapter(postAdapter);


        loadTagButton();
        loadPostByTagType();
        setLoadingPage();


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    protected abstract void setLoadingPage();
    protected abstract void loadTagButton();
    protected abstract void loadPostByTagType();
}
