package com.example.pst;

import java.util.List;

/**
 * Created by Setu on 2/28/22
 */
public interface Converter {

    /**
     * Converts a line of text to Person object
     * @param input - text to convert
     * @return - Person object
     */
    Person convert(String input);

    default String intelligentConvert(String input){
        return input;
    };
}
