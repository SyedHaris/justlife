package com.example.justlife.util;

import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.Objects;

@Component
public class Helper {

    public LocalTime calculateEndTimeFromDurationAndStartTime(LocalTime startTime, short duration) {
        Objects.requireNonNull(startTime);
        return startTime.plusHours(duration);
    }

    public LocalTime convertStringToLocalTime(String time) {
        Objects.requireNonNull(time);
        var timeParts = time.split(":");
        if (timeParts.length == 2)
            return LocalTime.of(Integer.parseInt(timeParts[0]), Integer.parseInt(timeParts[1]));
        return null;
    }

}
