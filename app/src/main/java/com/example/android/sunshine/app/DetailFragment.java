package com.example.android.sunshine.app;

/**
 * Created by Jeff Rhodes on 5/13/16.
 */

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.app.data.WeatherContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int DETAILS_LOADER_ID = 0;

    private static final String SUNSHINE_HASHTAG = " #SunshineApp";

    private static final String[] DETAIL_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
    };

    // these constants correspond to the projection defined above, and must change if the
    // projection changes
    private static final int COL_WEATHER_ID = 0;
    private static final int COL_WEATHER_DATE = 1;
    private static final int COL_WEATHER_DESC = 2;
    private static final int COL_WEATHER_MAX_TEMP = 3;
    private static final int COL_WEATHER_MIN_TEMP = 4;
    private static final int COL_WEATHER_HUMIDITY = 5;
    private static final int COL_WEATHER_PRESSURE = 6;
    private static final int COL_WEATHER_WIND_SPEED = 7;
    private static final int COL_WEATHER_DEGREES = 8;
    private static final int COL_WEATHER_CONDITION_ID = 9;

    public static final String DETAIL_URI_KEY = "detailUri";

    /**
     * ViewHolder class
     */
    public class ViewHolder {
        TextView mTvDay;
        TextView mTvDate;
        TextView mTvForecast;
        TextView mTvHigh;
        TextView mTvLow;
        ImageView mIvIcon;
        TextView mTvWind;
        TextView mTvHumidity;
        TextView mTvPressure;

        public ViewHolder(View view) {
            mTvDay = (TextView)view.findViewById(R.id.day_tv);
            mTvDate = (TextView)view.findViewById(R.id.date_tv);
            mTvForecast = (TextView)view.findViewById(R.id.forecast_tv);
            mTvHigh = (TextView)view.findViewById(R.id.high_tv);
            mTvLow = (TextView)view.findViewById(R.id.low_tv);
            mIvIcon = (ImageView)view.findViewById(R.id.icon_iv);
            mTvWind = (TextView)view.findViewById(R.id.wind_tv);
            mTvHumidity = (TextView)view.findViewById(R.id.humidity_tv);
            mTvPressure = (TextView)view.findViewById(R.id.pressure_tv);
        }
    }

    private Uri mDetailUri;

    private ShareActionProvider mShareActionProvider;

    private ViewHolder mHolder;

    private String mDetailsStr;

    /**
     * Constrcutor.
     */
    public DetailFragment() {

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detailfragment, menu);
        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.action_share);
        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareIntent());
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(DETAILS_LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //mDetailUri = getActivity().getIntent().getDataString();

        Bundle args = getArguments();
        if (args != null) {
            mDetailUri = args.getParcelable(DETAIL_URI_KEY);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mHolder = new ViewHolder(rootView);
        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (mDetailUri != null) {
            return new CursorLoader(getActivity(), mDetailUri, DETAIL_COLUMNS, null, null, null);
        } else {
            return null;
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) { return; }

        long date = data.getLong(COL_WEATHER_DATE);
        String dayString = Utility.getDayName(getActivity(),date);
        String monthDayString = Utility.getFormattedMonthDay(getActivity(), date);
        String weatherDescription = data.getString(COL_WEATHER_DESC);

        double maxTemp = data.getDouble(COL_WEATHER_MAX_TEMP);
        double minTemp = data.getDouble(COL_WEATHER_MIN_TEMP);
        boolean isMetric = Utility.isMetric(getActivity());

        String high = Utility.formatTemperature(getActivity(), maxTemp, isMetric);
        String highContent = Utility.getTemperatureDescription(getActivity(), high, true, isMetric);

        String low = Utility.formatTemperature(getActivity(),minTemp, isMetric);
        String lowContent = Utility.getTemperatureDescription(getActivity(), low, false, isMetric);

        float windSpeed = data.getFloat(COL_WEATHER_WIND_SPEED);
        float degrees = data.getFloat(COL_WEATHER_DEGREES);
        float humidity = data.getFloat(COL_WEATHER_HUMIDITY);
        float pressure = data.getFloat(COL_WEATHER_PRESSURE);

        mHolder.mTvDay.setText(dayString);
        mHolder.mTvDate.setText(monthDayString);
        mHolder.mTvHigh.setText(high);
        mHolder.mTvHigh.setContentDescription(highContent);
        mHolder.mTvLow.setText(low);
        mHolder.mTvLow.setContentDescription(lowContent);
        mHolder.mTvForecast.setText(weatherDescription);
        mHolder.mTvForecast.setContentDescription(getString(R.string.forecast_desc) +
                weatherDescription);

        int weatherCondId = data.getInt(COL_WEATHER_CONDITION_ID);
        mHolder.mIvIcon.setImageResource(Utility.getArtResourceForWeatherCondition(weatherCondId));

        mHolder.mTvPressure.setText(getString(R.string.format_pressure, pressure));
        mHolder.mTvHumidity.setText(getString(R.string.format_humidity, humidity));
        mHolder.mTvWind.setText(Utility.getFormattedWind(getActivity(), windSpeed, degrees));
        mHolder.mTvWind.setContentDescription(Utility.getWindDescription(getActivity(),
                windSpeed, degrees));

        //For share intent
        String dateString = Utility.formatDate(date);
        mDetailsStr = String.format("%s - %s - %s/%s", dateString, weatherDescription, high, low);

        // If onCreateOptionsMenu has already happened, we need to update the share intent now.
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareIntent());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    void onLocationChanged( String newLocation ) {
        // replace the uri, since the location has changed
        Uri uri = mDetailUri;
        if (null != uri) {
            long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
            Uri updatedUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(newLocation, date);
            mDetailUri = updatedUri;
            getLoaderManager().restartLoader(DETAILS_LOADER_ID, null, this);
        }
    }

    private Intent createShareIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_TEXT, mDetailsStr + SUNSHINE_HASHTAG);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        return intent;
    }


}
