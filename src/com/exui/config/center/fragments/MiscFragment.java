/*
 * Copyright (C) 2017 AICP
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.exui.config.center.fragments;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemProperties;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import androidx.preference.PreferenceCategory;
import androidx.preference.*;
import com.exui.config.center.preferences.SystemPropStore;
import com.exui.config.center.preferences.SystemPropSwitchPreference;

import com.android.internal.logging.nano.MetricsProto; 

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.exui.config.center.preferences.AppMultiSelectListPreference;
import com.exui.config.center.preferences.ScrollAppsViewPreference;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class MiscFragment extends SettingsPreferenceFragment
            implements Preference.OnPreferenceChangeListener {

    private static final String PREF_ADBLOCK = "persist.aicp.hosts_block";

    private Handler mHandler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final PreferenceScreen prefSet = getPreferenceScreen();
        addPreferencesFromResource(R.xml.config_center_misc_category);

        PreferenceCategory overallPreferences = (PreferenceCategory) findPreference("misc_overall_cat");

        findPreference(PREF_ADBLOCK).setOnPreferenceChangeListener(this);

        boolean enableSmartPixels = getContext().getResources().
                getBoolean(com.android.internal.R.bool.config_enableSmartPixels);
        Preference smartPixelsPref = (Preference) findPreference("smart_pixels");

        if (!enableSmartPixels){
            overallPreferences.removePreference(smartPixelsPref);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (PREF_ADBLOCK.equals(preference.getKey())) {
            // Flush the java VM DNS cache to re-read the hosts file.
            // Delay to ensure the value is persisted before we refresh
            mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        InetAddress.clearDnsCache();
                    }
            }, 1000);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.CUSTOM_SETTINGS;
    }
}
