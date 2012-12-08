package project.phase2.structs;

import java.util.ArrayList;

public class StringMatchList extends ArrayList<String> {

    public StringMatchList() {
        // Default
    }

    public StringMatchList(StringMatchList a) {
        add(a);
    }

    public StringMatchList(String ... a) {
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

        for (String string : this) {
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

        for (String string : this) {
            if (!second.contains(string)) {
                n.add(string);
            }
        }

        return n;
    }

    public void addIfNotContains(final String s) {
        if (!contains(s)) {
            super.add(s);
        }
    }

    public void addIfNotContains(final StringMatchList s) {
        for (String a : s) {
            addIfNotContains(a);
        }
    }

    public void add(String... s) {
        for (String r : s)
            super.add(r);
    }

    public void add(StringMatchList s) {
        for (String r : s)
            super.add(r);
    }

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
        for (String s : this) {
            be.append(", ").append(s);
        }
        return be.toString().replaceFirst(",", "");
    }
}
