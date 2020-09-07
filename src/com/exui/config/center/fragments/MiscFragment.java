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

    public static final String TAG = "MiscFragment";
    private static final String KEY_ASPECT_RATIO_APPS_ENABLED = "aspect_ratio_apps_enabled";
    private static final String KEY_ASPECT_RATIO_APPS_LIST = "aspect_ratio_apps_list";
    private static final String KEY_ASPECT_RATIO_CATEGORY = "aspect_ratio_category";
    private static final String KEY_ASPECT_RATIO_APPS_LIST_SCROLLER = "aspect_ratio_apps_list_scroller";
    private Handler mHandler = new Handler();
    private ContentResolver mResolver;

    private AppMultiSelectListPreference mAspectRatioAppsSelect;
    private ScrollAppsViewPreference mAspectRatioApps;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final PreferenceScreen prefSet = getPreferenceScreen();
        addPreferencesFromResource(R.xml.config_center_misc_category);

        PreferenceCategory overallPreferences = (PreferenceCategory) findPreference("misc_overall_cat");

        boolean enableSmartPixels = getContext().getResources().
                getBoolean(com.android.internal.R.bool.config_enableSmartPixels);
        Preference smartPixelsPref = (Preference) findPreference("smart_pixels");

        if (!enableSmartPixels){
            overallPreferences.removePreference(smartPixelsPref);
        }

        final PreferenceCategory aspectRatioCategory =
            (PreferenceCategory) getPreferenceScreen().findPreference(KEY_ASPECT_RATIO_CATEGORY);
        final boolean supportMaxAspectRatio =
            getResources().getBoolean(com.android.internal.R.bool.config_haveHigherAspectRatioScreen);
        if (!supportMaxAspectRatio) {
            getPreferenceScreen().removePreference(aspectRatioCategory);
        } else {
            mAspectRatioAppsSelect =
                (AppMultiSelectListPreference) findPreference(KEY_ASPECT_RATIO_APPS_LIST);
            mAspectRatioApps =
                (ScrollAppsViewPreference) findPreference(KEY_ASPECT_RATIO_APPS_LIST_SCROLLER);
            final String valuesString = Settings.System.getString(getContentResolver(),
                Settings.System.ASPECT_RATIO_APPS_LIST);
            List<String> valuesList = new ArrayList<String>();
            if (!TextUtils.isEmpty(valuesString)) {
                valuesList.addAll(Arrays.asList(valuesString.split(":")));
                mAspectRatioApps.setVisible(true);
                mAspectRatioApps.setValues(valuesList);
            } else {
                mAspectRatioApps.setVisible(false);
            }
            mAspectRatioAppsSelect.setValues(valuesList);
            mAspectRatioAppsSelect.setOnPreferenceChangeListener(this);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mAspectRatioAppsSelect) {
            Collection<String> valueList = (Collection<String>) newValue;
            mAspectRatioApps.setVisible(false);
            if (valueList != null) {
                Settings.System.putString(getContentResolver(),
                    Settings.System.ASPECT_RATIO_APPS_LIST, TextUtils.join(":", valueList));
                mAspectRatioApps.setVisible(true);
                mAspectRatioApps.setValues(valueList);
            } else {
                Settings.System.putString(getContentResolver(),
                    Settings.System.ASPECT_RATIO_APPS_LIST, "");
            }
            return true;
        }
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.CUSTOM_SETTINGS;
    }
}
