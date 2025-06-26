package vn.hau.edumate.ui.community.postcreate;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;
import okhttp3.MultipartBody;
import vn.hau.edumate.data.api.image.ImageApi;
import vn.hau.edumate.data.api.post.PostApi;
import vn.hau.edumate.data.api.tag.TagApi;
import vn.hau.edumate.data.api.user.UserApi;
import vn.hau.edumate.data.enums.TagType;
import vn.hau.edumate.data.model.request.CreatePostRequest;
import vn.hau.edumate.data.model.request.CreateUserStatusHistory;
import vn.hau.edumate.data.model.response.DataResponse;
import vn.hau.edumate.data.model.response.ImageResponse;
import vn.hau.edumate.data.model.response.PostResponse;
import vn.hau.edumate.data.model.response.TagResponse;
import vn.hau.edumate.data.network.ApiClient;
import vn.hau.edumate.data.repository.ImageRepository;
import vn.hau.edumate.data.repository.PostRepository;
import vn.hau.edumate.data.repository.TagRepository;
import vn.hau.edumate.data.repository.UserRepository;
import vn.hau.edumate.helper.GeminiHelper;
import vn.hau.edumate.util.ResultCallback;

@Getter
@Setter
public class PostCreateViewModel extends AndroidViewModel {
    public final TagRepository tagRepository;
    private final ImageRepository imageRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final MutableLiveData<List<Long>> imageIds = new MutableLiveData<>();
    private final MutableLiveData<List<TagResponse>> tagListLiveData = new MutableLiveData<>();
    private final  MutableLiveData<String> _answer = new MutableLiveData<>();
    private final LiveData<String> answer = _answer;
    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    private final LiveData<Boolean> isLoading = _isLoading;

    public PostCreateViewModel(@NonNull Application application) {
        super(application);
        this.tagRepository = new TagRepository(ApiClient.createService(TagApi.class,application));
        this.imageRepository = new ImageRepository(ApiClient.createService(ImageApi.class,application));
        this.postRepository = new PostRepository(ApiClient.createService(PostApi.class,application));
        this.userRepository = new UserRepository(ApiClient.createService(UserApi.class,application));
    }

    public void getTagByType(TagType tagType,Context context) {
        tagRepository.getTagsByTagType(tagType, context,new TagRepository.getTagByType() {
            @Override
            public void onSuccess(List<TagResponse> tagResponses) {
                tagListLiveData.postValue(tagResponses);
            }
            @Override
            public void onError(String message) {
                Log.e("PostCreateViewModel", "Lỗi: " + message);
            }
        });
    }
    public void saveImage(List<MultipartBody.Part> multipartFiles, Context context) {

        imageRepository.saveImage(multipartFiles, context ,new ResultCallback<>() {

            @Override
            public void onSuccess(DataResponse<List<ImageResponse>> result) {
                imageIds.setValue(result.getData().stream()
                        .map(ImageResponse::getId)
                        .collect(Collectors.toList()));
            }

            @Override
            public void onError(Throwable throwable) {
                Log.d("PostDetailViewModel", "Lỗi: " + throwable.getMessage());
                _isLoading.setValue(false);
            }
        });
    }
    public void createPost(CreatePostRequest createPostRequest, Context context, ResultCallback<DataResponse<PostResponse>> callback){
        postRepository.createPost(createPostRequest,  context,new ResultCallback<>(){
            @Override
            public void onSuccess(DataResponse<PostResponse> result) {
                _isLoading.setValue(false);
                callback.onSuccess(result);
            }

            @Override
            public void onError(Throwable throwable) {
                callback.onError(throwable);
                _isLoading.setValue(false);
            }
        });
    }
    public void createUserStatusHistory(String userId, CreateUserStatusHistory createUserStatusHistory, ResultCallback<DataResponse<Integer>> callback){
        _isLoading.setValue(false);
        userRepository.createUserStatusHistory(userId,createUserStatusHistory,callback);
    }
    public void askGemini(String prompt, List<Bitmap> images , Context context,LifecycleOwner owner){
        GeminiHelper helper = GeminiHelper.getInstance(context);
        _isLoading.setValue(true);
        helper.getAnswer(images,prompt).observe(owner, answer -> {
            Log.d("PostCreateViewModel", "answer: " + _answer.getValue());
            _answer.setValue(answer);
        });

    }
}
