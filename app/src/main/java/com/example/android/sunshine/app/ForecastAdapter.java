package com.example.android.sunshine.app;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class ForecastAdapter extends CursorAdapter {

    // View types
    private final int VIEW_TYPE_TODAY = 0;
    private final int VIEW_TYPE_FUTURE_DAY = 1;

    private boolean mUseTodayView = true;

    /**
     * ViewHolder class
     */
    public class ViewHolder {
        TextView mTvDate;
        TextView mTvForecast;
        TextView mTvHigh;
        TextView mTvLow;
        ImageView mIvIcon;

        public ViewHolder(View view) {
            mTvDate = (TextView)view.findViewById(R.id.list_item_date_textview);
            mTvForecast = (TextView)view.findViewById(R.id.list_item_forecast_textview);
            mTvHigh = (TextView)view.findViewById(R.id.list_item_high_textview);
            mTvLow = (TextView)view.findViewById(R.id.list_item_low_textview);
            mIvIcon = (ImageView)view.findViewById(R.id.list_item_icon);
        }
    }

    /**
     * Constructor.
     * @param context
     * @param c
     * @param flags
     */
    public ForecastAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public void setUseTodayView(boolean flag) {
        mUseTodayView = flag;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 && mUseTodayView ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    /**
     * Prepare the weather high/lows for presentation.
     */
    private String formatHighLows(double high, double low) {
        boolean isMetric = Utility.isMetric(mContext);
        String highLowStr = Utility.formatTemperature(mContext, high, isMetric)
                + "/" + Utility.formatTemperature(mContext, low, isMetric);
        return highLowStr;
    }

    /*
        This is ported from FetchWeatherTask --- but now we go straight from the cursor to the
        string.
     */
    private String convertCursorRowToUXFormat(Cursor cursor) {
        String highAndLow = formatHighLows(
                cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP),
                cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP));

        return Utility.formatDate(cursor.getLong(ForecastFragment.COL_WEATHER_DATE)) +
                " - " + cursor.getString(ForecastFragment.COL_WEATHER_DESC) +
                " - " + highAndLow;
    }

    /*
        Remember that these views are reused as needed.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        int viewType = getItemViewType(cursor.getPosition());
        int layoutId;
        if (viewType == VIEW_TYPE_TODAY) {
            layoutId = R.layout.list_item_forecast_today;
        } else {
            layoutId = R.layout.list_item_forecast;
        }
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        view.setTag(new ViewHolder(view));

        return view;
    }

    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // our view is pretty simple here --- just a text view
        // we'll keep the UI functional with a simple (and slow!) binding.

        ViewHolder vh = (ViewHolder)view.getTag();

        String weatherDesc = cursor.getString(ForecastFragment.COL_WEATHER_DESC);
        vh.mTvForecast.setText(weatherDesc);
        vh.mTvForecast.setContentDescription(context.getString(R.string.forecast_desc) +
                weatherDesc);

        int high = cursor.getInt(ForecastFragment.COL_WEATHER_MAX_TEMP);
        int low = cursor.getInt(ForecastFragment.COL_WEATHER_MIN_TEMP);
        boolean isMetric = Utility.isMetric(context);

        vh.mTvHigh.setText(Utility.formatTemperature(mContext, high, isMetric));
        vh.mTvHigh.setContentDescription(
                Utility.getTemperatureDescription(mContext, vh.mTvHigh.getText().toString(), true, isMetric));

        vh.mTvLow.setText(Utility.formatTemperature(mContext, low, isMetric));
        vh.mTvLow.setContentDescription(
                Utility.getTemperatureDescription(mContext, vh.mTvLow.getText().toString(), false, isMetric));

        vh.mTvDate.setText(Utility.getFriendlyDayString(context, cursor.getLong(ForecastFragment.COL_WEATHER_DATE)));

        int weatherCondId = cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID);
        if (getItemViewType(cursor.getPosition()) == VIEW_TYPE_TODAY) {
            vh.mIvIcon.setImageResource(Utility.getArtResourceForWeatherCondition(weatherCondId));
        } else {
            vh.mIvIcon.setImageResource(Utility.getIconResourceForWeatherCondition(weatherCondId));
        }

    }


}