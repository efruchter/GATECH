package project.phase2.structs;

import java.util.ArrayList;

public class StringMatchList extends ArrayList<StringMatchList.StringMatchTuple> {

    public StringMatchList() {
        // Default
    }

    public StringMatchList(StringMatchList a) {
        add(a);
    }

    public StringMatchList(String... a) {
        add(a);
    }

    /**
     * Union of this set and another
     *
     * @param b
     * @return
     */
    public StringMatchList union(final StringMatchList b) {

        StringMatchList l = new StringMatchList();

        l.addIfNotContains(this);
        l.addIfNotContains(b);

        return l;
    }

    public StringMatchList intersection(final StringMatchList b) {
        StringMatchList n = new StringMatchList();

        for (StringMatchTuple string : this) {
            if (b.contains(string)) {
                n.add(string);
            }
        }

        return n;
    }

    /**
     * This minus second.
     *
     * @param second other list.
     * @return the difference of the two lists.
     */
    public StringMatchList difference(final StringMatchList second) {
        StringMatchList n = new StringMatchList();

        for (StringMatchTuple string : this) {
            if (!second.contains(string)) {
                n.add(string);
            }
        }

        return n;
    }

    public void addIfNotContains(final StringMatchTuple s) {
        if (!contains(s)) {
            super.add(s);
        }
    }

    public void addIfNotContains(final StringMatchList s) {
        for (StringMatchTuple a : s) {
            addIfNotContains(a);
        }
    }

    public void add(String... s) {
        for (String r : s)
            super.add(new StringMatchTuple(r));
    }

    public void add(StringMatchList s) {
        for (StringMatchTuple r : s)
            super.add(new StringMatchTuple(r));
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof StringMatchList)) {
            return false;
        }

        StringMatchList a = (StringMatchList) o;

        if (a.size() != this.size()) {
            return false;
        }

        for (int i = 0; i < a.size(); i++) {
            if (!this.get(i).equals(a.get(i))) {
                return false;
            }
        }


        return true;
    }

    @Override
    public String toString() {
        StringBuffer be = new StringBuffer();
        be.append("[");
        for (StringMatchTuple s : this) {
            be.append(", ").append(s.toString());
        }
        be.append("]");
        return be.toString().replaceFirst(", ", "");
    }

    public static class StringMatchTuple {

        public String string;
        public String fileName = "";
        public int line = 0, startIndex = 0, endIndex = 0;

        public StringMatchTuple() {

        }

        public StringMatchTuple(String s) {
            string = s;
        }

        public StringMatchTuple(StringMatchTuple s) {
            string = s.string;
            fileName = s.fileName;
            line = s.line;
            startIndex = s.startIndex;
            endIndex = s.endIndex;
        }

        public String toString() {
            // "cba"<"file1.txt", 40, 50>}
            return "\"" + string + "\" <\"" + fileName + "\", " + line + ", " + startIndex + ", " + endIndex + ">";
        }

        public boolean equals(Object o) {

            if (!(o instanceof StringMatchTuple)) {
                return false;
            }

            StringMatchTuple a = (StringMatchTuple) o;

            return string.equals(a.string) && fileName.equals(a.fileName) && line == a.line
                    && startIndex == a.startIndex && endIndex == a.endIndex;
        }
    }
}
