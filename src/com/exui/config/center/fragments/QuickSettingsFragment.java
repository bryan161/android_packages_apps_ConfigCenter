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
    private static final String BG_COLOR = "notif_bg_color";
    private static final String ICON_COLOR = "notif_icon_color";
    private static final String BG_MODE = "notif_bg_color_mode";
    private static final String ICON_MODE = "notif_icon_color_mode";

    private ContentResolver mResolver;
    private SystemSettingSeekBarPreference mQsBlurIntensity;
    private SystemSettingListPreference mBgMode;
    private SystemSettingListPreference mIconMode;
    private SystemSettingColorPickerPreference mBgColor;
    private SystemSettingColorPickerPreference mIconColor;

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

        int color = Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.NOTIF_CLEAR_ALL_BG_COLOR, 0x3980FF) ;

        int iconColor = Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.NOTIF_CLEAR_ALL_ICON_COLOR, 0x3980FF);

        int mode = Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.NOTIF_DISMISALL_COLOR_MODE, 0);

        int iconmode = Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.NOTIF_DISMISALL_ICON_COLOR_MODE, 0);

        mBgMode = (SystemSettingListPreference) findPreference(BG_MODE);
        mBgMode.setOnPreferenceChangeListener(this);

        mBgColor = (SystemSettingColorPickerPreference) findPreference(BG_COLOR);
        mBgColor.setNewPreviewColor(color);
        mBgColor.setAlphaSliderEnabled(false);
        String Hex = convertToRGB(color);
        mBgColor.setSummary(Hex);
        mBgColor.setOnPreferenceChangeListener(this);

        mIconMode = (SystemSettingListPreference) findPreference(ICON_MODE);
        mIconMode.setOnPreferenceChangeListener(this);

        mIconColor = (SystemSettingColorPickerPreference) findPreference(ICON_COLOR);
        mIconColor.setNewPreviewColor(iconColor);
        String Hex2 = convertToRGB(iconColor);
        mIconColor.setAlphaSliderEnabled(false);
        mIconColor.setSummary(Hex2);
        mIconColor.setOnPreferenceChangeListener(this);

        updateprefs(mode);
        updateIconprefs(iconmode);

    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mQsBlurIntensity) {
            Context mContext = getContext();
            int value = (Integer) newValue;
            Settings.System.putInt(mContext.getContentResolver(),
                    Settings.System.QS_BLUR_INTENSITY, value);
            return true;
        } else if (preference == mBgMode) {
             int value = Integer.parseInt((String) newValue);
             updateprefs(value);
             return true;
        } else if (preference == mIconMode) {
             int value = Integer.parseInt((String) newValue);
             updateIconprefs(value);
             return true;
        } else if (preference == mBgColor) {
             String hex = convertToRGB(
                    Integer.valueOf(String.valueOf(newValue)));
             preference.setSummary(hex);
             return true;
        } else if (preference == mIconColor) {
             String hex = convertToRGB(
                    Integer.valueOf(String.valueOf(newValue)));
             preference.setSummary(hex);
             return true;
        } 
        return false;
    }

    private void updateprefs(int mode) {
        if (mode == 2)
            mBgColor.setEnabled(true);
        else 
            mBgColor.setEnabled(false);
    }

    private void updateIconprefs(int mode) {
        if (mode == 2)
            mIconColor.setEnabled(true);
        else 
            mIconColor.setEnabled(false);
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.CUSTOM_SETTINGS;
    }

    public static String convertToRGB(int color) {
        String red = Integer.toHexString(Color.red(color));
        String green = Integer.toHexString(Color.green(color));
        String blue = Integer.toHexString(Color.blue(color));

        if (red.length() == 1) {
            red = "0" + red;
        }

        if (green.length() == 1) {
            green = "0" + green;
        }

        if (blue.length() == 1) {
            blue = "0" + blue;
        }

        return "#" + red + green + blue;
    }
} 
