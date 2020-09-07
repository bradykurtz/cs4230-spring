package com.weber.cms.spring.formatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

@Component
public class DateFormatter implements Formatter<Date> {

    SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");

    @Override
    public Date parse(String text, Locale locale) throws ParseException {

        return formatter.parse(text);
    }

    @Override
    public String print(Date object, Locale locale) {
        return formatter.format(object);
    }
}
