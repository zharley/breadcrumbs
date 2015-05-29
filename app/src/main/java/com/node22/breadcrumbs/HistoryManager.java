package com.node22.breadcrumbs;

import android.net.Uri;
import android.os.AsyncTask;
import android.text.format.Time;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by zharley on 15-05-25.
 */
public class HistoryManager extends AsyncTask<String, Void, Location[]> {
    protected HistoryResponse response;

    public HistoryManager(HistoryResponse response) {
        this.response = response;
    }

    @Override
    protected Location[] doInBackground(String... params)  {
        // @see https://gist.github.com/anonymous/6b306e1f6a21b3718fa4

        if (params.length == 0) {
            return null;
        }

        String feed = params[0];

        Util.debug("Feed parameter: " + feed);

        Util.debug("Starting async task...");

        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        int numDays = 7;

        Location[] locations = null;

        // Will contain the raw JSON response as a string.
        String forecastJsonStr = null;
        try {
            Uri uri = Uri.parse("http://api.openweathermap.org/data/2.5/forecast/daily")
                .buildUpon()
                .appendQueryParameter("q", feed)
                .appendQueryParameter("mode", "json")
                .appendQueryParameter("units", "metric")
                .appendQueryParameter("cnt", Integer.toString(numDays))
                .build();

            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are avaiable at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            URL url = new URL(uri.toString());

            Util.debug("The constructed URI is: " + uri.toString());

            /*
            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty. No point in parsing.
                return null;
            }
            locationJSON = buffer.toString();
            */

            // Toronto, Hamilton, Kitchener, London, Windsor
            String locationJSON = " [ { \"latitude\": 43.653226, \"longitude\": -79.3831843, \"timestamp\": \"2012-04-23T18:25:43.511Z\" }, { \"latitude\": 43.243603, \"longitude\": -79.889075, \"timestamp\": \"2012-04-23T18:45:43.511Z\" }, { \"latitude\": 43.434311, \"longitude\": -80.4777469, \"timestamp\": \"2012-04-23T19:30:43.511Z\" }, { \"latitude\": 42.979398, \"longitude\": -81.246138, \"timestamp\": \"2012-04-23T21:00:43.511Z\" }, { \"latitude\": 42.292676, \"longitude\": -82.99333500000002, \"timestamp\": \"2012-04-23T22:33:00.511Z\" } ]";

            Util.debug("Full JSON returned: " + locationJSON);

            locations = getLocationDataFromJson(locationJSON);

            for (int i = 0; i < locations.length; i++) {
                Util.debug("Location " + Integer.toString(i) + " " +
                        Double.toString(locations[i].getLatitude()));
            }
        } catch (IOException e) {
            Util.error(e.toString());
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
        } catch (JSONException e) {
            Util.error("Malformed JSON: " + e.toString());
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Util.error("Error closing stream");
                }
            }
        }

        return locations;
    }

    // @see https://gist.github.com/anonymous/52b191849947d853a50e

    @Override
    protected void onPostExecute(Location[] locations) {
        this.response.processFinish(locations);
    }

    /* The date/time conversion code is going to be moved outside the asynctask later,
         * so for convenience we're breaking it out into its own method now.
         */
    private String getReadableDateString(long time){
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
        return shortenedDateFormat.format(time);
    }

    /**
     * Prepare the weather high/lows for presentation.
     */
    private String formatHighLows(double high, double low) {
        // For presentation, assume the user doesn't care about tenths of a degree.
        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);

        String highLowStr = roundedHigh + "/" + roundedLow;
        return highLowStr;
    }

    /**
     * Take the String representing location data in JSON Format and extract relevant
     * location data.
     */
    private Location[] getLocationDataFromJson(String input) throws JSONException {
        final String KEY_LATITUDE  = "latitude";
        final String KEY_LONGITUDE = "longitude";
        final String KEY_TIMESTAMP = "timestamp";

        JSONArray locations = new JSONArray(input);

        // Since this data is also sent in-order and the first day is always the
        // current day, we're going to take advantage of that to get a nice
        // normalized UTC date for all of our weather.
        Time dayTime = new Time();
        dayTime.setToNow();

        ArrayList<Location> mapLocations = new ArrayList<Location>();

        for(int i = 0; i < locations.length(); i++) {
            JSONObject location = locations.getJSONObject(i);

            double latitude = location.getDouble(KEY_LATITUDE);
            double longitude = location.getDouble(KEY_LONGITUDE);
            String timestamp = location.getString(KEY_TIMESTAMP);

            Util.debug(Integer.toString(i) + ") Timestamp=" + timestamp + ", Latitude=" + latitude + ", Longitude=" + longitude);

            mapLocations.add(new Location(latitude, longitude, dayTime));
        }

        return mapLocations.toArray(new Location[mapLocations.size()]);
    }
}
