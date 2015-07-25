package com.mengcraft.cleaner.util;

import java.util.ArrayList;
import java.util.List;

public class OptionValue {

    public static final OptionValue NULL = new OptionValue();
    public static final int DEFAULT_SIZE = 8;

    private String[] values;
    private int cursor;

    public int size() {
        return cursor;
    }

    public boolean hasArgumrnt() {
        return size() > 0;
    }

    /**
     * Return the last parsed String in this option.
     * 
     * @return
     */
    public String argument() {
        if (this != NULL) {
            return values[cursor - 1];
        }
        return new String();
    }

    public String argument(int cursor) {
        return values[cursor];
    }

    public List<String> arguments() {
        List<String> list = new ArrayList<String>();
        for (int i = 0; i != cursor;) {
            list.add(values[i++]);
        }
        return list;
    }

    protected void addArgument(String value) {
        if (cursor == values.length) {
            growArray();
        }
        values[cursor++] = value;
    }

    private void growArray() {
        final String[] bigger;
        if (values.length < 1) {
            bigger = new String[DEFAULT_SIZE];
        } else {
            bigger = new String[values.length * 2];
        }
        for (int i = 0; i != values.length;) {
            bigger[i] = values[i++];
        }
        this.values = bigger;
    }

    private OptionValue() {
        this.values = new String[] {};
    }

    public OptionValue(String next) {
        this.values = new String[] { next };
        this.cursor = 1;
    }

}
