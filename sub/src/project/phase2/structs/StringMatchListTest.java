package project.phase2.structs;

import org.junit.Test;
import project.phase2.file.StringMatchOperations;

import java.io.File;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StringMatchListTest {
    @Test
    public void setActionsTest() {
        StringMatchList a = new StringMatchList("0", "1", "2");
        StringMatchList b = new StringMatchList("1", "2", "3");

        assertTrue(a.intersection(b).equals(new StringMatchList("1", "2")));
        assertTrue(a.union(b).equals(new StringMatchList("0", "1", "2", "3")));
        assertTrue(a.difference(b).equals(new StringMatchList("0")));

        assertTrue(a.equals(new StringMatchList(a)));
        StringMatchList aa = new StringMatchList(a);
        aa.add(a);
        assertTrue(a.equals(aa));
    }

    @Test
    public void tupleFreq() {
        StringMatchList a = new StringMatchList("0", "1", "1", "1", "2", "2", "2", "2");

        assertTrue(a.getMostFrequentString().equals("2"));
        assertFalse(a.getMostFrequentString().equals("1"));

        a.add("1");
        a.add("1");

        assertTrue(a.getMostFrequentString().equals("1"));
    }
}
