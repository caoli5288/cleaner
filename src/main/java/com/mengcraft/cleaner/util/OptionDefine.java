package com.mengcraft.cleaner.util;

public class OptionDefine {

    public static final int NO_ARGUMENT = 0;
    public static final int REQUIRED_ARGUMENT = 1;
    public static final int OPTIONAL_ARGUMENT = 2;

    private final String option;
    private final int define;

    public String option() {
        return option;
    }

    public int define() {
        return define;
    }

    public OptionDefine(String option, int define) {
        if (define > 2 || define < 0) {
            throw new IllegalArgumentException();
        }
        if (option == null || option.length() < 1) {
            throw new NullPointerException();
        }
        this.option = option;
        this.define = define;
    }

    public OptionDefine(String option) {
        this(option, OPTIONAL_ARGUMENT);
    }

}
