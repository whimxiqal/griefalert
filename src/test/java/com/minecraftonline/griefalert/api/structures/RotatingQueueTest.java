package com.minecraftonline.griefalert.api.structures;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class RotatingQueueTest {

  @Test
  public void pushAndGet() {
    RotatingQueue<Integer> queue = new RotatingQueue<Integer>(4) {};

    queue.push(1);
    queue.push(5);
    queue.push(2);

    assertEquals(Integer.valueOf(1), queue.get(0));
    assertEquals(Integer.valueOf(2), queue.get(2));

    queue.push(15);
    queue.push(52);

    assertEquals(Integer.valueOf(15), queue.get(3));
    assertEquals(Integer.valueOf(52), queue.get(0));
    assertEquals(Integer.valueOf(5), queue.get(1));
    assertEquals(Integer.valueOf(2), queue.get(2));
  }

  @Test
  public void getChronologicalData() {
    RotatingQueue<Integer> queue = new RotatingQueue<Integer>(4) {};

    queue.push(1);
    queue.push(2);
    queue.push(3);
    queue.push(4);
    queue.push(5);

    Assert.assertArrayEquals(new Integer[]{2, 3, 4, 5}, queue.getChronologicalData().toArray(new Integer[5]));
  }

  @Test
  public void getAntiChronologicalData() {
    RotatingQueue<Integer> queue = new RotatingQueue<Integer>(4) {};

    queue.push(1);
    queue.push(2);
    queue.push(3);
    queue.push(4);
    queue.push(5);

    Assert.assertArrayEquals(new Integer[]{5, 4, 3, 2}, queue.getChronologicalData().toArray(new Integer[5]));
  }
}