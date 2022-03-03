package com.example.pst;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by Setu on 2/28/22
 * An implementation of the Converter
 */
public class JsonLineConverter implements Converter{

    private final String delimeter;
    private final List<DateTimeFormatter> dateFormatters;
    private static final String[] supportedDateFormats = {"yyyy-MM-dd", "yyyy/MM/dd", "dd-MM-yyyy"};
    private static final String splitterPostfix = "(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)";
    private static final String regexToClean = "^\"|\\||\"$|,$";

    public JsonLineConverter(String delimeter) {
        this.delimeter = delimeter;
        dateFormatters = Arrays.stream(supportedDateFormats)
                .map(DateTimeFormatter::ofPattern)
                .collect(Collectors.toList());
    }

    /**
     * Converts a line of text to Person object
     * @param input - text to convert
     * @return - Person object
     */
    @Override
    public Person convert( String input) {
        String splitter = delimeter + splitterPostfix;
        String[] attributes = input.split(splitter, -1);

        Person person = new Person();
        person.setFirstName(trim(attributes[0]));
        person.setMiddleName(trim(attributes[1]));
        person.setLastName(trim(attributes[2]));
        person.setGender(trim(attributes[3]));
        person.setDateOfBirth(getDateOfBirth(attributes[4]));
        person.setSalary(Integer.parseInt(attributes[5]));
        return person;
    }

    /**
     * Cleans the data
     * @param data - Text to clean
     * @return - Cleaned text
     */
    private String trim(String data){
        //Trim double quotes
        data = data.replaceAll(regexToClean, "");

        //Trim white space
        data = data.trim();

        return data.isEmpty() ? null : data;
    }

    /**
     * Parse date of birth
     * @param dateStr - String to parse
     * @return - Parsed date
     */
    private LocalDate getDateOfBirth(String dateStr){
        return dateFormatters.stream()
                .map(formatter -> parseDate(dateStr, formatter))
                .filter(Objects::nonNull)
                .findAny()
                .orElse(null);
    }

    /**
     * Parse date based on date time formatter
     * @param dateStr - String to parse
     * @param formatter - DatetimeFormatter to parse with
     * @return - Parsed date
     */
    private LocalDate parseDate(String dateStr, DateTimeFormatter formatter){
        try{
            return LocalDate.parse(dateStr, formatter);
        }catch (DateTimeParseException ex){
            //Could not parse
            return null;
        }
    }
}
