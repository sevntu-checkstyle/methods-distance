package com.github.sevntu.checkstyle.analysis;

import java.io.File;
import java.util.List;
import java.util.Map;

public class InputOverloadedMethods {

    public static class Base {

        public void format() {}

        public void format(String s) {}
    }

    public static class Derived extends Base {

        public void method() {
            format();
            this.format();
            format("");
            this.format("");
            format(new File(""), "");
            this.format(new File(""), "");
            Derived.this.format();
            Derived.this.format("");
            Derived.super.format();
            Derived.super.format("");
            String.format("");

        }

        public void format() {
            format("");
        }

        public void format(String s) {
            format(new File(""), "");
        }

        public void format(final File file, final String name) {

        }

        public void format(List<String> list) {

        }

        public void format(java.util.Set<String> list) {

        }

        public void format(Map<String, List<String>> list) {

        }
    }
}
