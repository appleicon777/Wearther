package com.example.wearther;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

import java.io.IOException;

public class CloudVisionService {

    private static final String BASE_URL = "https://vision.googleapis.com/v1/";
    private static final String API_KEY = "YOUR_API_KEY"; // Replace with your actual API key

    private final VisionApi visionApi;

    public CloudVisionService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        visionApi = retrofit.create(VisionApi.class);
    }

    public Call<JsonObject> analyzeImage(String imageBase64) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json"),
                createRequestBody(imageBase64));

        return visionApi.analyzeImage(API_KEY, body);
    }

    private String createRequestBody(String imageBase64) {
        JsonObject requestBody = new JsonObject();
        JsonObject image = new JsonObject();
        image.addProperty("content", imageBase64);
        requestBody.add("image", image);
        requestBody.add("features", createFeatureArray());
        return new Gson().toJson(requestBody);
    }

    private JsonObject createFeatureArray() {
        JsonObject feature = new JsonObject();
        feature.addProperty("type", "LABEL_DETECTION");
        feature.addProperty("maxResults", 10);
        return feature;
    }

    interface VisionApi {
        @POST("images:annotate")
        Call<JsonObject> analyzeImage(@retrofit2.http.Query("key") String apiKey, @Body RequestBody body);
    }
}