package tools;

import java.lang.reflect.Array;
import java.sql.Time;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

public final class TimeTool {
    // Converts seconds to HH:MM:SS
    public static String secToString(Long s) {

        long hours = s / 3600;
        String hoursString = String.valueOf(hours);
        if (hoursString.length() == 1) {
            hoursString = "0" + hoursString;
        }
        long mins = (s % 3600) / 60;
        String minsString = String.valueOf(mins);
        if (minsString.length() == 1) {
            minsString = "0" + minsString;
        }
        long secs = ((s % 3600) % 60);
        String secsString = String.valueOf(secs);
        if (secsString.length() == 1) {
            secsString = "0" + secsString;
        }

        return hoursString + ":" + minsString + ":" + secsString;
    }

    public static String stripHours(String s) {
        if (s.substring(0, 2).equals("00")) {
            return s.substring(3);
        }
        return s;
    }

    public static void printTime() {
        System.out.println(ZonedDateTime.now(
                // Specify time zone.
                ZoneId.of( "Pacific/Auckland" )
        )  );
    }

    public static ZonedDateTime getTime() {
        return(ZonedDateTime.now(
                // Specify time zone.
                ZoneId.of( "Pacific/Auckland" )
        ));
    }
}
