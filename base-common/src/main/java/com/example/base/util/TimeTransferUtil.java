package com.example.base.util;

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

/**
 * 时间转换工具类  转换localDate 与 Date
 * 使用jdk1.8日期类，线程安全、便捷
 *
 * @author lh
 * @date 2017年10月27日
 * @version 1.0
 */

/**
 * @author Administrator
 * @date 2017年12月8日
 */
public class TimeTransferUtil {
	/**
	 * 时区
	 */
	private static final ZoneId zoneId = ZoneId.systemDefault();

	/**
	 * 2017-10-27 的日期格式
	 */
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd",
			Locale.SIMPLIFIED_CHINESE);
	/**
	 * 2017-10-27 11:11 精确到分钟的日期格式
	 */
	private static final DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm",
			Locale.SIMPLIFIED_CHINESE);

	/**
	 * 把字符串日期转换成 Date对象
	 *
	 * @param date
	 * @return Date
	 */
	public static Date stringToDate(String date) {
		return localDateToDate(stringToLocalDate(date));
	}

	/**
	 * LocalDate 转换成Date
	 *
	 * @param date
	 * @return LocalDate
	 */
	public static Date localDateToDate(LocalDate date) {
		ZonedDateTime zdt = date.atStartOfDay(zoneId);
		return Date.from(zdt.toInstant());
	}

	/**
	 * Date 转换成LocalDate
	 *
	 * @param date
	 * @return
	 */
	public static LocalDate dateToLocalDate(Date date) {
		if (date instanceof java.sql.Date) {
			date = new java.util.Date(date.getTime());
		}
		Instant instant = date.toInstant();
		return instant.atZone(zoneId).toLocalDate();

	}

	/**
	 * 把date 转换成精确到分钟的时间格式字符串s
	 *
	 * @param date
	 * @return
	 */
	public static LocalDateTime dateToLocalDateTime(Date date) {
		if (date instanceof java.sql.Date) {
			date = new java.util.Date(date.getTime());
		}
		Instant instant = date.toInstant();
		return instant.atZone(zoneId).toLocalDateTime();

	}

	public static String LocalDateTimeString(Date date) {
		LocalDateTime localDate = dateToLocalDateTime(date);
		return localDate.format(formatter2);

	}

	/**
	 * 把字符串日期转换成 LocalDate对象
	 *
	 * @param date
	 * @return
	 */
	public static LocalDate stringToLocalDate(String date) {
		return LocalDate.parse(date, formatter);
	}

	/**
	 * 获取当前时间
	 *
	 * @return Timestamp
	 */
	public static Timestamp getCurrentTimestamp() {
		Timestamp timestamp = new Timestamp(new Date().getTime());
		return timestamp;
	}

	public static String getToday() {
		LocalDate date = LocalDate.now();
		return date.toString();
	}

}
