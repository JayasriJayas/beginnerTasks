package com.zoho.files.time;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;


public class Time {
		public LocalDateTime getCurrentTime() {
			return LocalDateTime.now();
		}
		public long getInMillis() {
			return System.currentTimeMillis();
		}
		public ZonedDateTime getZoneTime(String place) {
			return ZonedDateTime.now(ZoneId.of(place));
		}
		public String getWeekDay() {
			Instant instant = Instant.ofEpochMilli(getInMillis());
			ZonedDateTime zone = instant.atZone(ZoneId.systemDefault());
			return zone.getDayOfWeek().toString();
			
		}
		public String getCurrentMonth() {
			Instant instant = Instant.ofEpochMilli(getInMillis());
			ZonedDateTime zone = instant.atZone(ZoneId.systemDefault());
			return zone.getMonth().toString();
			
		}
		public int getCurrentYear() {
			Instant instant = Instant.ofEpochMilli(getInMillis());
			ZonedDateTime zone = instant.atZone(ZoneId.systemDefault());
			return zone.getYear();
			
		}

}
