package com.xiaomi.parts.kcal;

import android.os.Bundle;
import android.provider.Settings;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceFragment;
import androidx.preference.Preference;
import androidx.viewpager.widget.ViewPager;

import com.xiaomi.parts.R;
import com.xiaomi.parts.preferences.SecureSettingSeekBarPreference;
import com.xiaomi.parts.preferences.SecureSettingSwitchPreference;

import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class KcalSettings extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    ViewPager viewPager;
    LinearLayout DotsKcal;
    private int dotscount;
    private ImageView[] dots;
    private KcalUtils mKcalUtils;

    private SecureSettingSwitchPreference mEnabled;
    private SecureSettingSwitchPreference mSetOnBoot;
    private SecureSettingSeekBarPreference mRed;
    private SecureSettingSeekBarPreference mGreen;
    private SecureSettingSeekBarPreference mBlue;
    private SecureSettingSeekBarPreference mSaturation;
    private SecureSettingSeekBarPreference mValue;
    private SecureSettingSeekBarPreference mContrast;
    private SecureSettingSeekBarPreference mHue;
    private SecureSettingSeekBarPreference mMin;
    private SecureSettingSwitchPreference mGrayscale;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.kcal_preferences, rootKey);

        mKcalUtils = new KcalUtils(getContext());

        mEnabled = (SecureSettingSwitchPreference) findPreference(KcalUtils.PREF_ENABLED);
        mEnabled.setOnPreferenceChangeListener(this);
        mEnabled.setTitle(mKcalUtils.getInt(KcalUtils.PREF_ENABLED, 0) == 1 ? R.string.kcal_enabled : R.string.kcal_disabled);

        mMin = (SecureSettingSeekBarPreference) findPreference(KcalUtils.PREF_MINIMUM);
        mMin.setOnPreferenceChangeListener(this);

        mRed = (SecureSettingSeekBarPreference) findPreference(KcalUtils.PREF_RED);
        mRed.setOnPreferenceChangeListener(this);

        mGreen = (SecureSettingSeekBarPreference) findPreference(KcalUtils.PREF_GREEN);
        mGreen.setOnPreferenceChangeListener(this);

        mBlue = (SecureSettingSeekBarPreference) findPreference(KcalUtils.PREF_BLUE);
        mBlue.setOnPreferenceChangeListener(this);

        mSaturation = (SecureSettingSeekBarPreference) findPreference(KcalUtils.PREF_SATURATION);
        mSaturation.setEnabled(mKcalUtils.getInt(KcalUtils.PREF_GRAYSCALE, 0) == 0);
        mSaturation.setOnPreferenceChangeListener(this);

        mValue = (SecureSettingSeekBarPreference) findPreference(KcalUtils.PREF_VALUE);
        mValue.setOnPreferenceChangeListener(this);

        mContrast = (SecureSettingSeekBarPreference) findPreference(KcalUtils.PREF_CONTRAST);
        mContrast.setOnPreferenceChangeListener(this);

        mHue = (SecureSettingSeekBarPreference) findPreference(KcalUtils.PREF_HUE);
        mHue.setOnPreferenceChangeListener(this);

        mGrayscale = (SecureSettingSwitchPreference) findPreference(KcalUtils.PREF_GRAYSCALE);
        mGrayscale.setOnPreferenceChangeListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.kcal_layout, container, false);
        ((ViewGroup) view).addView(super.onCreateView(inflater, container, savedInstanceState));
        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getActivity());
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.setPageTransformer(true, new FadeOutTransformation());
        DotsKcal = (LinearLayout) view.findViewById(R.id.dots);
        dotscount = viewPagerAdapter.getCount();
        dots = new ImageView[dotscount];

        for(int i = 0; i < dotscount; i++){
            dots[i] = new ImageView(getActivity());
            dots[i].setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.inactive_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);
            DotsKcal.addView(dots[i], params);
        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.active_dot));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                for(int i = 0; i< dotscount; i++){
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.inactive_dot));
                }
                dots[position].setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.active_dot));
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        final String key = preference.getKey();
        String rgb = null;

        switch (key) {
            case KcalUtils.PREF_ENABLED:
                mEnabled.setTitle((boolean) value ? R.string.kcal_enabled : R.string.kcal_disabled);
                break;
            case KcalUtils.PREF_RED:
                rgb = value + " " + mGreen.getValue() + " " + mBlue.getValue();
                break;
            case KcalUtils.PREF_GREEN:
                rgb = mRed.getValue() + " " + value + " " + mBlue.getValue();
                break;
            case KcalUtils.PREF_BLUE:
                rgb = mRed.getValue() + " " + mGreen.getValue() + " " + value;
                break;
            case KcalUtils.PREF_GRAYSCALE:
                setGrayscale((boolean) value);
                // setGrayscale already set kcal data
                return true;
            default:
                break;
        }

        if (key.contains("kcal")) {
            mKcalUtils.setKcalData(key, rgb != null ? rgb : value);
        }
        return true;
    }

    void resetKcalData() {
        applyValues(KcalUtils.DEFAULT_RED + " " +
                    KcalUtils.DEFAULT_GREEN + " " +
                    KcalUtils.DEFAULT_BLUE + " " +
                    KcalUtils.DEFAULT_MINIMUM + " " +
                    KcalUtils.DEFAULT_SATURATION + " " +
                    KcalUtils.DEFAULT_VALUE + " " +
                    KcalUtils.DEFAULT_CONTRAST + " " +
                    KcalUtils.DEFAULT_HUE);
        setGrayscale(false);
    }

    void applyValues(String preset) {
        String[] values = preset.split(" ");
        int red = Integer.parseInt(values[0]);
        int green = Integer.parseInt(values[1]);
        int blue = Integer.parseInt(values[2]);
        int min = Integer.parseInt(values[3]);
        int sat = Integer.parseInt(values[4]);
        int value = Integer.parseInt(values[5]);
        int contrast = Integer.parseInt(values[6]);
        int hue = Integer.parseInt(values[7]);

        mRed.refresh(red);
        mGreen.refresh(green);
        mBlue.refresh(blue);
        mMin.refresh(min);
        mSaturation.refresh(sat);
        mValue.refresh(value);
        mContrast.refresh(contrast);
        mHue.refresh(hue);
    }

    void setGrayscale(boolean checked) {
        mGrayscale.setChecked(checked);
        mSaturation.setEnabled(!checked);
        mKcalUtils.setKcalData(KcalUtils.PREF_GRAYSCALE, false);
    }
}
