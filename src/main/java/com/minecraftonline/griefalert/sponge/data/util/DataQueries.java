/*
 * This file is part of Prism, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2015 Helion3 http://helion3.com/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.minecraftonline.griefalert.sponge.data.util;

import org.spongepowered.api.data.DataQuery;

/**
 * @author viveleroi
 */
public final class DataQueries {

  public static final DataQuery BlockType = DataQuery.of("BlockType");
  public static final DataQuery BlockState = DataQuery.of("BlockState");
  public static final DataQuery Cause = DataQuery.of("Cause");
  public static final DataQuery Container = DataQuery.of("Container");
  public static final DataQuery ContentVersion = DataQuery.of("ContentVersion");
  public static final DataQuery Count = DataQuery.of("Count");
  public static final DataQuery Created = DataQuery.of("Created");
  public static final DataQuery Entity = DataQuery.of("Entity");
  public static final DataQuery EntityType = DataQuery.of("EntityType");
  public static final DataQuery EventName = DataQuery.of("EventName");
  public static final DataQuery Id = DataQuery.of("Id");
  public static final DataQuery Location = DataQuery.of("Location");
  public static final DataQuery OriginalBlock = DataQuery.of("Original");
  public static final DataQuery Player = DataQuery.of("Player");
  public static final DataQuery Position = DataQuery.of("Position");
  public static final DataQuery Quantity = DataQuery.of("Quantity");
  public static final DataQuery ReplacementBlock = DataQuery.of("Replacement");
  public static final DataQuery Rotation = DataQuery.of("Rotation");
  public static final DataQuery Target = DataQuery.of("Target");
  public static final DataQuery UnsafeData = DataQuery.of("UnsafeData");
  public static final DataQuery WorldName = DataQuery.of("WorldName");
  public static final DataQuery WorldUuid = DataQuery.of("WorldUuid");
  public static final DataQuery X = DataQuery.of("X");
  public static final DataQuery Y = DataQuery.of("Y");
  public static final DataQuery Z = DataQuery.of("Z");
  // Pending cleanup from Sponge?
  public static final DataQuery Pos = DataQuery.of("Pos");

  private DataQueries() {
  }
}