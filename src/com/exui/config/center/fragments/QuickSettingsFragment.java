/*
 * Copyright (C) 2019 ExtendedUI
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.TextUtils;
import androidx.preference.*;

import com.android.internal.logging.nano.MetricsProto; 

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.exui.config.center.preferences.*;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class QuickSettingsFragment extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    public static final String TAG = "QuickSettingsFragment";
    private static final String QS_BLUR_INTENSITY = "qs_blur_intensity";

    private ContentResolver mResolver;
    private SystemSettingSeekBarPreference mQsBlurIntensity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.quicksettings);

        PreferenceScreen prefScreen = getPreferenceScreen();
        Context mContext = getContext();
        mResolver = getActivity().getContentResolver();

        mQsBlurIntensity = (SystemSettingSeekBarPreference) findPreference(QS_BLUR_INTENSITY);
        int qsBlurIntensity = Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.QS_BLUR_INTENSITY, 100);
        mQsBlurIntensity.setValue(qsBlurIntensity);
        mQsBlurIntensity.setOnPreferenceChangeListener(this);

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mQsBlurIntensity) {
            Context mContext = getContext();
            int value = (Integer) newValue;
            Settings.System.putInt(mContext.getContentResolver(),
                    Settings.System.QS_BLUR_INTENSITY, value);
            return true;
        } 
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.CUSTOM_SETTINGS;
    }

} 
