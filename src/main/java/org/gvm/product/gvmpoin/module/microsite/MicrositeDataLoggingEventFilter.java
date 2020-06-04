package org.gvm.product.gvmpoin.module.microsite;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.core.spi.FilterReply;

public class MicrositeDataLoggingEventFilter
        extends ch.qos.logback.core.filter.AbstractMatcherFilter {
  @Override
  public FilterReply decide(Object event) {
    if (!isStarted()) {
      return FilterReply.NEUTRAL;
    }

    LoggingEvent loggingEvent = (LoggingEvent) event;

    if (loggingEvent.getLevel().equals(Level.INFO)) {
      return FilterReply.NEUTRAL;
    } else {
      return FilterReply.DENY;
    }
  }
}
