package com.minecraftonline.griefalert.api.structures;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RotatingStackTest {

  @Test
  public void pushAndGet() {
    RotatingStack<Integer> queue = new RotatingStack<Integer>(4) {};

    assertEquals((Object) 0, queue.size());

    queue.push(1);
    assertEquals((Object) 1, queue.size());

    queue.push(5);
    assertEquals((Object) 2, queue.size());

    queue.push(2);
    assertEquals((Object) 3, queue.size());

    System.out.println(queue.get(0));
    assertEquals(Integer.valueOf(1), queue.get(0));
    System.out.println(queue.get(2));
    assertEquals(Integer.valueOf(2), queue.get(2));

    queue.push(15);
    assertEquals((Object) 4, queue.size());

    queue.push(52);
    assertEquals((Object) 4, queue.size());

    assertEquals((Object) 15, queue.get(3));
    assertEquals((Object) 52, queue.get(0));
    assertEquals((Object) 5, queue.get(1));
    assertEquals((Object) 2, queue.get(2));
  }

  @Test
  public void getDataByTime() {
    RotatingStack<Integer> queue = new RotatingStack<Integer>(4) {};

    queue.push(1);
    queue.push(2);
    queue.push(3);

    Assert.assertArrayEquals(new Integer[]{1, 2, 3}, queue.getDataByTime().toArray(new Integer[3]));
    assertEquals((Object) 3, queue.getDataByTime().size());

    queue.push(4);
    queue.push(5);

    Assert.assertArrayEquals(new Integer[]{2, 3, 4, 5}, queue.getDataByTime().toArray(new Integer[4]));
    assertEquals((Object) 4, queue.getDataByTime().size());

  }

  @Test
  public void getDataByIndex() {
    RotatingStack<Integer> queue = new RotatingStack<Integer>(4) {};

    queue.push(1);
    queue.push(2);
    queue.push(3);

    Assert.assertArrayEquals(new Integer[]{1, 2, 3}, queue.getDataByIndex().toArray(new Integer[3]));
    assertEquals((Object) 3, queue.getDataByTime().size());

    queue.push(4);
    queue.push(5);

    Assert.assertArrayEquals(new Integer[]{5, 2, 3, 4}, queue.getDataByIndex().toArray(new Integer[4]));
    assertEquals((Object) 4, queue.getDataByTime().size());

  }

}