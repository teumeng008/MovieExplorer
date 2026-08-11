package com.rupp.movieexplorer;

import android.app.Application;
import com.cloudinary.android.MediaManager;
import java.util.HashMap;
import java.util.Map;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Initialize Cloudinary
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "ep3rqorw"); // Replace with your cloud name
        // Optional: If you use signed uploads, you might need api_key and api_secret
        // config.put("api_key", "YOUR_API_KEY");
        // config.put("api_secret", "YOUR_API_SECRET");
        
        MediaManager.init(this, config);
    }
}
