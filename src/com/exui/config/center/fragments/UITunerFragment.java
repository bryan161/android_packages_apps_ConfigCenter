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
import android.app.UiModeManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemProperties;
import android.provider.Settings;
import android.text.TextUtils;
import androidx.preference.*;
import static com.exui.config.center.utils.Utils.handleOverlays;

import com.android.internal.util.exui.Utils;
import com.android.internal.util.exui.ThemesUtils;

import android.content.om.IOverlayManager;
import android.content.om.OverlayInfo;
import android.os.RemoteException;
import android.os.ServiceManager;

import com.android.internal.logging.nano.MetricsProto;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class UITunerFragment extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener {

    public static final String TAG = "UITunerFragment";

    private ContentResolver mResolver;
    private static final String SWITCH_STYLE = "switch_style";
    private ListPreference mSStyle;

    private IOverlayManager mOverlayManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.config_center_uituner_category);

        mOverlayManager = IOverlayManager.Stub.asInterface(
                ServiceManager.getService(Context.OVERLAY_SERVICE));

        PreferenceScreen prefScreen = getPreferenceScreen();
        Context mContext = getContext();

        mResolver = getActivity().getContentResolver();

        mSStyle = (ListPreference) findPreference(SWITCH_STYLE);
        int SStyle = Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.SWITCH_STYLE, 0);
        int SStyleValue = getOverlayPosition(ThemesUtils.SWITCH_THEMES);
        if (SStyleValue != 0) {
            mSStyle.setValue(String.valueOf(SStyle));
        }
        mSStyle.setSummary(mSStyle.getEntry());
        mSStyle.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (preference == mSStyle) {
                    String value = (String) newValue;
                    Settings.System.putInt(getActivity().getContentResolver(), Settings.System.SWITCH_STYLE, Integer.valueOf(value));
                    int valueIndex = mSStyle.findIndexOfValue(value);
                    mSStyle.setSummary(mSStyle.getEntries()[valueIndex]);
                    String overlayName = getOverlayName(ThemesUtils.SWITCH_THEMES);
                    if (overlayName != null) {
                    handleOverlays(overlayName, false, mOverlayManager);
                    }
                    if (valueIndex > 0) {
                        handleOverlays(ThemesUtils.SWITCH_THEMES[valueIndex],
                                true, mOverlayManager);
                    }
                    return true;
                }
                return false;
            }
       });
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        return false;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsProto.MetricsEvent.CUSTOM_SETTINGS;
    }

    private String getOverlayName(String[] overlays) {
            String overlayName = null;
            for (int i = 0; i < overlays.length; i++) {
                String overlay = overlays[i];
                if (Utils.isThemeEnabled(overlay)) {
                    overlayName = overlay;
                }
            }
            return overlayName;
        }

    private int getOverlayPosition(String[] overlays) {
            int position = -1;
            for (int i = 0; i < overlays.length; i++) {
                String overlay = overlays[i];
                if (Utils.isThemeEnabled(overlay)) {
                    position = i;
                }
            }
            return position;
        }
}
