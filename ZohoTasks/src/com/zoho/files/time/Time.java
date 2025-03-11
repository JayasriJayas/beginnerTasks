package com.zoho.files.time;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Set;


public class Time {
		public LocalDateTime getCurrentTime() {
			return LocalDateTime.now();
		}
		public LocalDate getDates(int year,int month,int date) {
			return LocalDate.of(year, month, date);
		}
		public LocalTime getLocalTime(int hour,int minute) {
			return LocalTime.of(hour, minute);
		}
		public long getInMillis() {
			return System.currentTimeMillis();
		}
		public ZonedDateTime getZoneTime(String zoneid) {
			return ZonedDateTime.now(ZoneId.of(zoneid));
		}
		public ZonedDateTime getZonesTime(LocalDate date,LocalTime time,ZoneId zoneid) {
			return ZonedDateTime.of(date,time,zoneid);
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
		public ZoneOffset getZoneOffset(ZonedDateTime zonetime) {
			return zonetime.getOffset();
		}
		public ZoneId getId(String zoneid) {
			return ZoneId.of(zoneid);
		}
		
}
