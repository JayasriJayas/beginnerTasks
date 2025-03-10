package com.zoho.files.time;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Set;


public class Time {
		public LocalDateTime getCurrentTime() {
			return LocalDateTime.now();
		}
		public long getInMillis() {
			return System.currentTimeMillis();
		}
		public ZonedDateTime getZoneTime(String zoneid) {
			return ZonedDateTime.now(ZoneId.of(zoneid));
		}
		public String getWeekDay(String zoneid) {
			Instant instant = Instant.ofEpochMilli(getInMillis());
			ZonedDateTime zone = instant.atZone(ZoneId.of(zoneid));
			return zone.getDayOfWeek().toString();
			
		}
		public String getCurrentMonth(String zoneid) {
			Instant instant = Instant.ofEpochMilli(getInMillis());
			ZonedDateTime zone = instant.atZone(ZoneId.of(zoneid));
			return zone.getMonth().toString();
			
		}
		public int getCurrentYear(String zoneid) {
			Instant instant = Instant.ofEpochMilli(getInMillis());
			ZonedDateTime zone = instant.atZone(ZoneId.of(zoneid));
			return zone.getYear();
			
		}
		public Set<String> getZoneIds(){
			return ZoneId.getAvailableZoneIds();		
		}
		
}
