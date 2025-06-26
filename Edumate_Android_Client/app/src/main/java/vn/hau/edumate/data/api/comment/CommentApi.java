package vn.hau.edumate.data.api.comment;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import vn.hau.edumate.data.model.request.CreateCommentRequest;
import vn.hau.edumate.data.model.response.CommentResponse;
import vn.hau.edumate.data.model.response.DataResponse;

public interface CommentApi {
    @GET("comment/post/{postId}")
    Call<DataResponse<List<CommentResponse>>> getCommentsByPostId(@Path("postId") Long postId);
    @POST("comment")
    Call<DataResponse<CommentResponse>> createComment(@Body CreateCommentRequest commentRequest,
                                                      @Query("postId") Long postId);
    @PUT("comment/like/{commentId}")
    Call<DataResponse<Integer>> likeComment(@Path("commentId") Long commentId);

    @GET("comment/{commentId}")
    Call<DataResponse<CommentResponse>> getCommentById(@Path("commentId") Long commentId);
    @GET("comment/{parentId}/replies")
    Call<DataResponse<List<CommentResponse>>> getRepliesByParentId(@Path("parentId") Long parentId);
    @GET("comment/by-current-user")
    Call<DataResponse<List<CommentResponse>>> getCommentsAndRepliesByCurrentUser();

    @POST("comment/{parentId}/replies")
    Call<DataResponse<CommentResponse>> createReply(@Body CreateCommentRequest commentRequest,
                                                     @Path("parentId") Long parentId);

}
