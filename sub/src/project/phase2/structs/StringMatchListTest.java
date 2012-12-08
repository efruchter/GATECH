package project.phase2.structs;

import org.junit.Test;

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
        aa.addIfNotContains(a);
        assertTrue(a.equals(aa));
    }
}
