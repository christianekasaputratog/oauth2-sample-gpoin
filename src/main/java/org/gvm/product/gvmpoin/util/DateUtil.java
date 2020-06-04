package org.gvm.product.gvmpoin.util;

import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.LocalDate;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateUtil {

  public static Date getTimeNow() {
    Calendar calendar = Calendar.getInstance(Locale.getDefault());

    return calendar.getTime();
  }

  public static int hoursBetween(Date d1, Date d2) {
    return Hours.hoursBetween(new LocalDate(d1.getTime()), new LocalDate(d2.getTime())).getHours();
  }

  public static int daysBetween(Date d1, Date d2) {
    return Days.daysBetween(new LocalDate(d1.getTime()), new LocalDate(d2.getTime())).getDays();
  }

  public static Date getTimeByAddDays(Integer day) {
    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+07"));
    calendar.add(Calendar.DATE, day);
    calendar.set(Calendar.HOUR_OF_DAY, 23);
    calendar.set(Calendar.MINUTE, 59);
    calendar.set(Calendar.SECOND, 59);
    return calendar.getTime();
  }

  public static String getParsedSimpleDateFormat() {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy, HH:mm",
        Locale.US);
    return simpleDateFormat.format(new Date());
  }

}
