package com.mengcraft.cleaner.util;

import java.util.List;

public interface Option {

    public boolean has(String key);

    public List<String> alones();

    public List<String> others();

    public OptionValue get(String kay);

}
