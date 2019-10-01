package com.vladsch.flexmark.util.collection;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ConcurrentModificationException;
import java.util.Iterator;

public class OrderedSetTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testAddRemove() throws Exception {
        OrderedSet<String> orderedSet = new OrderedSet<>();

        for (int i = 0; i < 10; i++) {
            Assert.assertEquals(true, orderedSet.add(String.valueOf(i)));
            Assert.assertEquals(false, orderedSet.add(String.valueOf(i)));
        }

        Assert.assertEquals(false, orderedSet.addAll(orderedSet));

        int i = 0;
        for (String it : orderedSet) {
            Assert.assertEquals(String.valueOf(i), it);
            i++;
        }

        for (int j = 0; j < 10; j++) {
            Assert.assertEquals(true, orderedSet.remove(String.valueOf(j)));

            Assert.assertEquals(j == 9 ? 0 : 10, orderedSet.getValueList().size());

            int lastJ = j + 1;
            for (String it : orderedSet) {
                Assert.assertEquals(String.valueOf(lastJ), it);
                lastJ++;
            }
        }
    }

    @Test
    public void testAddRemoveReversed() throws Exception {
        OrderedSet<String> orderedSet = new OrderedSet<>();

        for (int i = 0; i < 10; i++) {
            Assert.assertEquals(true, orderedSet.add(String.valueOf(i)));
            Assert.assertEquals(false, orderedSet.add(String.valueOf(i)));
        }

        Assert.assertEquals(false, orderedSet.addAll(orderedSet));

        int i = 0;
        for (String it : orderedSet) {
            Assert.assertEquals(String.valueOf(i), it);
            i++;
        }

        for (int j = 10; j-- > 0; ) {
            Assert.assertEquals(true, orderedSet.remove(String.valueOf(j)));

            Assert.assertEquals(j, orderedSet.getValueList().size());

            int lastJ = 0;
            for (String it : orderedSet) {
                Assert.assertEquals(String.valueOf(lastJ++), it);
            }

            Assert.assertEquals(lastJ, j);
        }
    }

    @Test
    public void testRetainAll() throws Exception {
        OrderedSet<String> orderedSet = new OrderedSet<>();
        OrderedSet<String> retainSet = new OrderedSet<>();

        for (int i = 0; i < 10; i++) {
            Assert.assertEquals(true, orderedSet.add(String.valueOf(i)));
            Assert.assertEquals(false, orderedSet.add(String.valueOf(i)));
        }

        for (int i = 0; i < 10; i += 2) {
            Assert.assertEquals(true, retainSet.add(String.valueOf(i)));
            Assert.assertEquals(false, retainSet.add(String.valueOf(i)));
        }

        Assert.assertEquals(false, orderedSet.addAll(orderedSet));
        Assert.assertEquals(false, retainSet.addAll(retainSet));

        Assert.assertEquals(false, orderedSet.retainAll(orderedSet));
        Assert.assertEquals(false, retainSet.retainAll(retainSet));

        Assert.assertEquals(true, orderedSet.retainAll(retainSet));
        Assert.assertEquals(true, orderedSet.equals(retainSet));

        int i = 0;
        for (String it : orderedSet) {
            Assert.assertEquals(String.valueOf(i), it);
            i += 2;
        }

        for (int j = 10; j-- > 0; ) {
            Assert.assertEquals((j & 1) == 0, orderedSet.remove(String.valueOf(j)));
        }
    }

    @Test
    public void testRemoveIteration() throws Exception {
        OrderedSet<String> orderedSet = new OrderedSet<>();

        for (int i = 0; i < 10; i++) {
            Assert.assertEquals(true, orderedSet.add(String.valueOf(i)));
            Assert.assertEquals(false, orderedSet.add(String.valueOf(i)));
        }

        Assert.assertEquals(false, orderedSet.addAll(orderedSet));

        int i = 0;
        Iterator<String> iterator = orderedSet.iterator();
        while (iterator.hasNext()) {
            String it = iterator.next();
            Assert.assertEquals(String.valueOf(i), it);
            i++;
        }

        iterator = orderedSet.iterator();
        int j = 0;
        while (iterator.hasNext()) {
            String item = iterator.next();
            iterator.remove();

            Assert.assertEquals(j == 9 ? 0 : 10, orderedSet.getValueList().size());

            int lastJ = j + 1;
            for (String it : orderedSet) {
                Assert.assertEquals(String.valueOf(lastJ), it);
                lastJ++;
            }

            j++;
        }
    }

    @Test
    public void testRemoveReversedIteration() throws Exception {
        OrderedSet<String> orderedSet = new OrderedSet<>();

        for (int i = 0; i < 10; i++) {
            Assert.assertEquals(true, orderedSet.add(String.valueOf(i)));
            Assert.assertEquals(false, orderedSet.add(String.valueOf(i)));
        }

        Assert.assertEquals(false, orderedSet.addAll(orderedSet));

        int i = 9;
        Iterator<String> iterator = orderedSet.reversedIterator();
        while (iterator.hasNext()) {
            String it = iterator.next();
            Assert.assertEquals(String.valueOf(i), it);
            i--;
        }

        iterator = orderedSet.reversedIterator();
        int j = 9;
        while (iterator.hasNext()) {
            String item = iterator.next();
            iterator.remove();

            Assert.assertEquals(j, orderedSet.getValueList().size());

            int lastJ = 0;
            for (String it : orderedSet) {
                Assert.assertEquals(String.valueOf(lastJ++), it);
            }

            Assert.assertEquals(lastJ, j);
            j--;
        }
    }

    @Test
    public void testConcurrentMod() throws Exception {
        OrderedSet<String> orderedSet = new OrderedSet<>();

        for (int i = 0; i < 10; i++) {
            Assert.assertEquals(true, orderedSet.add(String.valueOf(i)));
            Assert.assertEquals(false, orderedSet.add(String.valueOf(i)));
        }

        Assert.assertEquals(false, orderedSet.addAll(orderedSet));

        int i = 0;
        Iterator<String> iterator = orderedSet.iterator();
        while (iterator.hasNext()) {
            orderedSet.removeIndex(0);
            thrown.expect(ConcurrentModificationException.class);
            String it = iterator.next();
            Assert.assertEquals("ConcurrentModificationException was not thrown on modification", false, true);
        }
    }

    @Test
    public void testSetConflict() throws Exception {
        OrderedSet<String> orderedSet = new OrderedSet<>();

        for (int i = 0; i < 10; i++) {
            Assert.assertEquals(true, orderedSet.add(String.valueOf(i)));
            Assert.assertEquals(false, orderedSet.add(String.valueOf(i)));
        }

        thrown.expect(IllegalStateException.class);
        Assert.assertEquals(false, orderedSet.setValueAt(0, String.valueOf(1), "1"));
    }
}
