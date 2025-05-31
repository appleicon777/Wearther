package com.example.wearther;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Base64;
import androidx.annotation.WorkerThread;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.HashMap;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;

public class VisionApiHelper {
    public interface MetaDataCallback {
        void onResult(Map<String, String> metaData);
    }

    public static void extractMetaData(Context context, Bitmap bitmap, MetaDataCallback callback) {
        new Thread(() -> {
            try {
                // 1. Bitmap을 Base64로 변환
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
                String base64 = Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP);

                // 2. Vision API 요청 JSON 생성
                JSONObject request = new JSONObject();
                JSONArray requests = new JSONArray();
                JSONObject image = new JSONObject();
                image.put("content", base64);
                JSONObject feature = new JSONObject();
                feature.put("type", "LABEL_DETECTION");
                feature.put("maxResults", 5);
                JSONArray features = new JSONArray();
                features.put(feature);
                JSONObject req = new JSONObject();
                req.put("image", image);
                req.put("features", features);
                requests.put(req);
                request.put("requests", requests);

                // 3. Vision API 호출
                URL url = new URL("https://vision.googleapis.com/v1/images:annotate?key=YOUR_API_KEY");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);
                OutputStream os = conn.getOutputStream();
                os.write(request.toString().getBytes("UTF-8"));
                os.close();

                // 4. 응답 파싱
                InputStream is = conn.getInputStream();
                ByteArrayOutputStream respBaos = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];
                int len;
                while ((len = is.read(buf)) != -1) respBaos.write(buf, 0, len);
                String resp = new String(respBaos.toByteArray(), "UTF-8");
                JSONObject respJson = new JSONObject(resp);
                JSONArray labels = respJson.getJSONArray("responses")
                        .getJSONObject(0)
                        .getJSONArray("labelAnnotations");

                Map<String, String> meta = new HashMap<>();
                if (labels.length() > 0) {
                    meta.put("label", labels.getJSONObject(0).getString("description"));
                }
                // 필요에 따라 color 등 추가 추출

                // 5. 콜백
                callback.onResult(meta);

            } catch (Exception e) {
                e.printStackTrace();
                callback.onResult(null);
            }
        }).start();
    }
}