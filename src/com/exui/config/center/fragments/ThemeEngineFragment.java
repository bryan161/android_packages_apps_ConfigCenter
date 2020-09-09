/*
 * Copyright (C) 2020 ShapeShiftOS
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

import android.content.ContentResolver;
import android.content.Context;
import android.os.SystemProperties;
import androidx.preference.*;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.UserHandle;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.provider.SearchIndexableResource;
import android.provider.Settings;

import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.settings.display.NightModePreferenceController;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.display.OverlayCategoryPreferenceController;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import android.util.Log;

import com.exui.config.center.display.AccentColorPreferenceController;
import com.android.settings.R;
import com.android.internal.util.exui.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

public class ThemeEngineFragment extends DashboardFragment {
    private static final String TAG = "ThemeEngineFragment";

    private ContentResolver mResolver;

    private static final String PREF_PREVIEW = "switch_preview";

    ListPreference mPreview;

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.CUSTOM_SETTINGS;
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        final ContentResolver resolver = getActivity().getContentResolver();

        mPreview = findPreference(PREF_PREVIEW);

        if (Utils.isThemeEnabled("com.android.system.switch.stock")) {
            mPreview.setLayoutResource(R.layout.accents_shapes_base_preview_stock);
        } else if (Utils.isThemeEnabled("com.android.system.switch.oneplus")) {
            mPreview.setLayoutResource(R.layout.accents_shapes_base_preview_oos);
        } else if (Utils.isThemeEnabled("com.android.system.switch.fluid")) {
            mPreview.setLayoutResource(R.layout.accents_shapes_base_preview);
        } else {
            mPreview.setLayoutResource(R.layout.accents_shapes_base_preview_stock);
        }
    }

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.config_center_themer_category;
    }

    @Override
    protected List<AbstractPreferenceController> createPreferenceControllers(Context context) {
        return buildPreferenceControllers(context, getSettingsLifecycle(), this);
    }

    private static List<AbstractPreferenceController> buildPreferenceControllers(
            Context context, Lifecycle lifecycle, Fragment fragment) {
        final List<AbstractPreferenceController> controllers = new ArrayList<>();
        controllers.add(new OverlayCategoryPreferenceController(context,
                "android.theme.customization.accent_color"));
        controllers.add(new AccentColorPreferenceController(context));
        controllers.add(new OverlayCategoryPreferenceController(context,
                "android.theme.customization.font"));
        controllers.add(new OverlayCategoryPreferenceController(context,
                "android.theme.customization.adaptive_icon_shape"));
        controllers.add(new OverlayCategoryPreferenceController(context,
                "android.theme.customization.icon_pack.android"));
        controllers.add(new OverlayCategoryPreferenceController(context,
                "android.theme.customization.primary_color"));
        controllers.add(new NightModePreferenceController(context));
        return controllers;
    }
} 
