package com.scottmcclellan.lockereatsapp.GCM;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by abuchmann on 21.10.2015.
 */
public class LockerEatsIDListenerService extends InstanceIDListenerService {
    @Override
    public void onTokenRefresh() {
        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
        Intent intent = new Intent(this, LockerEatsRegistrationIntentService.class);
        startService(intent);
    }
}
