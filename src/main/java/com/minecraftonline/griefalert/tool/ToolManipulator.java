/*
 * MIT License
 *
 * Copyright (c) 2020 MinecraftOnline
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.minecraftonline.griefalert.tool;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractBooleanData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.value.mutable.Value;

import java.util.Optional;

public class ToolManipulator extends AbstractBooleanData<ToolManipulator, ImmutableToolManipulator> {
  public static final DataQuery QUERY = DataQuery.of("griefalerttool");

  public ToolManipulator(boolean value) {
    super(Keys.GA_TOOL, value, false);
  }

  public Value<Boolean> isTool() {
    return this.getValueGetter();
  }

  @Override
  public Optional<ToolManipulator> fill(DataHolder dataHolder, MergeFunction overlap) {
    return from(dataHolder.toContainer());
  }

  @Override
  public Optional<ToolManipulator> from(DataContainer container) {
    return container.getBoolean(Keys.GA_TOOL.getQuery()).map(ToolManipulator::new);
  }

  @Override
  public ToolManipulator copy() {
    return new ToolManipulator(this.getValue());
  }

  @Override
  public ImmutableToolManipulator asImmutable() {
    return new ImmutableToolManipulator(this.getValue());
  }

  @Override
  public int getContentVersion() {
    return 0;
  }

  public static class Builder implements DataManipulatorBuilder<ToolManipulator, ImmutableToolManipulator> {

    @Override
    public ToolManipulator create() {
      return new ToolManipulator(false);
    }

    @Override
    public Optional<ToolManipulator> createFrom(DataHolder dataHolder) {
      return build(dataHolder.toContainer());
    }

    @Override
    public Optional<ToolManipulator> build(DataView container) throws InvalidDataException {
      return container.getBoolean(Keys.GA_TOOL.getQuery()).map(ToolManipulator::new);
    }
  }
}
