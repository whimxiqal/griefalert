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

import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractSingleData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.value.mutable.Value;

/**
 * Magic method to help define a GriefAlert tool with a specific griefer.
 */
public class ToolGrieferManipulator
    extends AbstractSingleData<UUID, ToolGrieferManipulator, ImmutableToolGrieferManipulator> {
  public static final DataQuery QUERY = DataQuery.of("griefalerttoolgriefer");

  public ToolGrieferManipulator(UUID value) {
    super(Keys.TOOL_GRIEFER_UUID, value);
  }

  @Nonnull
  @Override
  public Optional<ToolGrieferManipulator> fill(DataHolder dataHolder, @Nonnull MergeFunction overlap) {
    return from(dataHolder.toContainer());
  }

  @Nonnull
  @Override
  public Optional<ToolGrieferManipulator> from(DataContainer container) {
    return container.getString(Keys.TOOL_GRIEFER_UUID.getQuery())
        .map(UUID::fromString).map(ToolGrieferManipulator::new);
  }

  @Nonnull
  @Override
  protected DataContainer fillContainer(@Nonnull DataContainer dataContainer) {
    return super.fillContainer(dataContainer.set(Keys.TOOL_GRIEFER_UUID.getQuery(),
        this.getValue().toString()));
  }

  @Nonnull
  @Override
  public ToolGrieferManipulator copy() {
    return new ToolGrieferManipulator(this.getValue());
  }

  @Nonnull
  @Override
  protected Value<UUID> getValueGetter() {
    return Sponge.getRegistry().getValueFactory().createValue(Keys.TOOL_GRIEFER_UUID, this.getValue());
  }

  @Nonnull
  @Override
  public ImmutableToolGrieferManipulator asImmutable() {
    return new ImmutableToolGrieferManipulator(this.getValue());
  }

  @Override
  public int getContentVersion() {
    return 1;
  }

  /**
   * A builder for the manipulator.
   */
  public static class Builder
      implements DataManipulatorBuilder<ToolGrieferManipulator, ImmutableToolGrieferManipulator> {

    @Nonnull
    @Override
    public ToolGrieferManipulator create() {
      return new ToolGrieferManipulator(new UUID(0, 0));
    }

    @Nonnull
    @Override
    public Optional<ToolGrieferManipulator> createFrom(DataHolder dataHolder) {
      return build(dataHolder.toContainer());
    }

    @Nonnull
    @Override
    public Optional<ToolGrieferManipulator> build(DataView container) throws InvalidDataException {
      return container.getString(Keys.GA_TOOL.getQuery()).map(UUID::fromString)
          .map(ToolGrieferManipulator::new);
    }
  }
}
