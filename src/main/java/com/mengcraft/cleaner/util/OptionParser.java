package com.mengcraft.cleaner.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OptionParser {

    private class ParserBox {

        private final SimpleOption option;
        private final ArrayVector<String> ittor;
        private OptionDefine[] defines;

        public Option parser() {
            while (ittor.remain() > 0) {
                checksArguments();
            }
            return option;
        }

        private OptionDefine select(String curr) {
            int it = defines.length;
            OptionDefine def = null;
            while (def == null && it-- != 0) {
                def = defines[it].option().equals(curr) ? defines[it] : null;
            }
            return def;
        }

        private void checksArguments() {
            String curr = ittor.next();
            if (isOptionnal(curr)) {
                String key = curr.substring(2);
                OptionDefine define = select(key);
                int type = define != null ? define.define() : 3;
                if (type == 0) {
                    checkAdd(key, OptionValue.NULL);
                } else if (type == 1 && isArgumentNext(ittor)) {
                    checkAdd(key, ittor.next());
                } else if (type == 2 && isArgumentNext(ittor)) {
                    checkAdd(key, ittor.next());
                } else if (type == 2) {
                    checkAdd(key, OptionValue.NULL);
                } else if (type == 3) {
                    option.other.add(key);
                }
            } else {
                option.alone.add(curr);
            }
        }

        private void checkAdd(String key, OptionValue empty) {
            OptionValue object = option.map().get(key);
            if (object == null) {
                option.map().put(key, empty);
            }
        }

        private void checkAdd(String key, String next) {
            OptionValue object = option.map().get(key);
            if (object != null) {
                object.addArgument(next);
            } else {
                option.map().put(key, new OptionValue(next));
            }
        }

        private boolean isArgumentNext(ArrayVector<String> ittor) {
            String next = ittor.get();
            if (next != null && !isOptionnal(next)) {
                return true;
            }
            return false;
        }

        private boolean isOptionnal(String input) {
            return input.startsWith("--") && input.length() > 2;
        }

        public ParserBox(OptionDefine[] defines, String... arguments) {
            this.defines = defines;
            this.ittor = new ArrayVector<String>(arguments);
            this.option = new SimpleOption();
        }
    }

    private class SimpleOption implements Option {

        private final List<String> alone;
        private final List<String> other;
        private final Map<String, OptionValue> map;

        @Override
        public List<String> alones() {
            return new ArrayList<String>(alone);
        }

        @Override
        public List<String> others() {
            return new ArrayList<String>(other);
        }

        @Override
        public boolean has(String key) {
            return map.get(key) != null;
        }

        @Override
        public OptionValue get(String key) {
            return has(key) ? map.get(key) : OptionValue.NULL;
        }

        protected Map<String, OptionValue> map() {
            return map;
        }

        public SimpleOption() {
            this.map = new HashMap<String, OptionValue>();
            this.alone = new ArrayList<String>();
            this.other = new ArrayList<String>();
        }

    }

    private final OptionDefine[] defines;

    public Option parse(String... arguments) {
        if (arguments == null) {
            throw new NullPointerException();
        }
        return new ParserBox(defines, arguments).parser();
    }

    public OptionParser(OptionDefine... defines) {
        this.defines = defines;
    }

}