package com.suhel.photosphere.service.rest;

import com.suhel.photosphere.base.model.ApiResponse;
import com.suhel.photosphere.base.model.ProgressRequestBody;
import com.suhel.photosphere.model.request.CreateComment;
import com.suhel.photosphere.model.request.CreatePost;
import com.suhel.photosphere.model.request.FCM;
import com.suhel.photosphere.model.request.RegisterSocial;
import com.suhel.photosphere.model.response.Comment;
import com.suhel.photosphere.model.response.Post;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RestInterface {

    //region Auth
    @PUT("auth/socialLogin")
    Single<ApiResponse<Void>> socialLogin(@Body RegisterSocial registerSocial);

    @PUT("auth/silentLogin")
    Single<ApiResponse<Void>> silentLogin();

    @PUT("auth/fcm")
    Single<ApiResponse<Void>> fcm(@Body FCM fcm);
    //endregion

    //region Posts
    @Multipart
    @POST("posts")
    Single<ApiResponse<Void>> createPost(@Part("photo") ProgressRequestBody photo, @Body CreatePost createPost);

    @GET("posts")
    Single<ApiResponse<List<Post>>> getAllPosts(@Query("skip") int skip, @Query("limit") int limit);

    @GET("posts/by/me")
    Single<ApiResponse<List<Post>>> getMyPosts(@Query("skip") int skip, @Query("limit") int limit);

    @GET("posts/by/{userId}")
    Single<ApiResponse<List<Post>>> getUsersPosts(@Path("userId") String userId, @Query("skip") int skip, @Query("limit") int limit);
    //endregion

    //region Comments
    @POST("posts/{postId}/comments")
    Single<ApiResponse<Comment>> createComment(@Path("postId") String postId, @Body CreateComment createComment);

    @GET("posts/{postId}/comments")
    Single<ApiResponse<List<Comment>>> getAllComments(@Path("postId") String postId, @Query("skip") int skip, @Query("limit") int limit);

    @PUT("posts/{postId}/comments/{commentId}")
    Single<ApiResponse<Void>> editComment(@Path("postId") String postId, @Path("commentId") String commentId, @Query("skip") int skip, @Query("limit") int limit);

    @DELETE("posts/{postId}/comments/{commentId}")
    Single<ApiResponse<Void>> deleteComment(@Path("postId") String postId, @Path("commentId") String commentId);
    //endregion

    //region Likes
    @POST("posts/{postId}/like")
    Single<ApiResponse<Void>> likePost(@Path("postId") String postId);

    @DELETE("posts/{postId}/unlike")
    Single<ApiResponse<Void>> unlikePost(@Path("postId") String postId);
    //endregion

}
