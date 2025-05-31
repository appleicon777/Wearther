package com.example.wearther;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CloudVisionService {

    private static final String TAG = "CloudVisionService";
    private static final String CLOUD_VISION_API_URL = "https://vision.googleapis.com/v1/images:annotate?key=YOUR_API_KEY";
    private final OkHttpClient client;
    private final Gson gson;

    public CloudVisionService() {
        client = new OkHttpClient();
        gson = new Gson();
    }

    public void analyzeImage(String base64Image, final CloudVisionCallback callback) {
        JsonObject requestBody = new JsonObject();
        JsonObject image = new JsonObject();
        image.addProperty("content", base64Image);

        JsonObject feature = new JsonObject();
        feature.addProperty("type", "LABEL_DETECTION");
        feature.addProperty("maxResults", 10);

        requestBody.add("requests", gson.toJsonTree(Collections.singletonList(new JsonObject() {{
            add("image", image);
            add("features", gson.toJsonTree(Collections.singletonList(feature)));
        }})));

        Request request = new Request.Builder()
                .url(CLOUD_VISION_API_URL)
                .post(RequestBody.create(MediaType.parse("application/json"), gson.toJson(requestBody)))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "API call failed: " + e.getMessage());
                callback.onFailure(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    callback.onSuccess(responseData);
                } else {
                    Log.e(TAG, "API call failed: " + response.message());
                    callback.onFailure(new IOException("Unexpected code " + response));
                }
            }
        });
    }

    public interface CloudVisionCallback {
        void onSuccess(String response);
        void onFailure(Exception e);
    }
}