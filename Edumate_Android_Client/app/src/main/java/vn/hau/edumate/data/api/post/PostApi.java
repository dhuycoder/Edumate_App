package vn.hau.edumate.data.api.post;

import java.util.LinkedHashSet;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import vn.hau.edumate.data.enums.TagType;
import vn.hau.edumate.data.model.request.CreatePostRequest;
import vn.hau.edumate.data.model.request.ReportRequest;
import vn.hau.edumate.data.model.response.DataResponse;
import vn.hau.edumate.data.model.response.PagedResponse;
import vn.hau.edumate.data.model.response.PostResponse;

public interface PostApi {
    @GET("post/tag/type/{tagType}")
    Call<DataResponse<PagedResponse<PostResponse>>> getPostByTagType(
            @Path("tagType") TagType tagType,
            @Query("page") int page,
            @Query("limit") int limit
    );


    @PUT("post/like/{postId}")
    Call<DataResponse<Integer>> likePost(@Path("postId") Long postId);

    @GET("post/tag/{id}")
    Call<DataResponse<LinkedHashSet<PostResponse>>> getPostByTag(@Path("id") Long tagId);

    @GET("post/{id}")
    Call<DataResponse<PostResponse>> getPostById(@Path("id") Long postId);
    @GET("post/by-user/{userId}")
    Call<DataResponse<LinkedHashSet<PostResponse>>> getPostByUserId(@Path("userId") String userId);
    @GET("post/by-user-like")
    Call<DataResponse<LinkedHashSet<PostResponse>>> getPostByCurrentUserLike();
    @POST("post")
    Call<DataResponse<PostResponse>> createPost(@Body CreatePostRequest createPostRequest);
    @DELETE("post/{id}")
    Call<DataResponse<PostResponse>> deletePost(@Path("id") Long postId);

    @POST("report/{postId}")
    Call<DataResponse<Void>> reportPost(@Path("postId") Long postId, @Body ReportRequest reason);
    @POST("post/hide/{postId}")
    Call<DataResponse<Void>> hidePost(@Path("postId") Long postId);

    @GET("post/by-image/{id}")
    Call<DataResponse<Long>> getPostIdByImageId(@Path("id") Long imageId);
}
