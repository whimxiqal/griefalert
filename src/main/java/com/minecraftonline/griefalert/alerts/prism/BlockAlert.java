/*
 * MIT License
 *
 * Copyright (c) 2020 Pieter Svenson
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

package com.minecraftonline.griefalert.alerts.prism;

import com.helion3.prism.api.records.PrismRecord;
import com.minecraftonline.griefalert.api.alerts.Detail;
import com.minecraftonline.griefalert.api.records.GriefProfile;
import com.minecraftonline.griefalert.util.General;
import com.minecraftonline.griefalert.util.PrismUtil;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.data.persistence.DataFormats;
import org.spongepowered.api.text.Text;

/**
 * An Alert relating specifically to events pertaining to blocks.
 */
public abstract class BlockAlert extends PrismAlert {

  private final String originalBlockState;
  private final String replacementBlockState;

  BlockAlert(GriefProfile griefProfile, PrismRecord prismRecord) {
    super(griefProfile, prismRecord);
    try {
      this.originalBlockState = DataFormats.JSON
          .write(PrismUtil.getOriginalBlockState(prismRecord.getDataContainer())
              .get()
              .toContainer());
      this.replacementBlockState = DataFormats.JSON
          .write(PrismUtil.getReplacementBlock(prismRecord.getDataContainer())
              .get()
              .toContainer());
    } catch (NoSuchElementException e) {
      throw new RuntimeException(
          "Prism did not supply necessary information about an event");
    } catch (IOException e) {
      e.printStackTrace();
      throw new RuntimeException(
          "Prism information could not be handled for an event");
    }

    // Add block details
    getOriginalBlockState().map(BlockState::getTraitMap).ifPresent(map ->
        map.forEach((key, value) -> addDetail(Detail.of(
            "(Original) " + General.capitalize(key.getName()),
            "The " + key.getName() + " trait of the original block of this transaction.",
            Text.of(value.toString())))));
    getReplacementBlockState().map(BlockState::getTraitMap).ifPresent(map ->
        map.forEach((key, value) -> addDetail(Detail.of(
            "(New) " + General.capitalize(key.getName()),
            "The " + key.getName() + " trait of the newly created block of this transaction.",
            Text.of(value.toString())))));
  }

  private Optional<BlockState> getOriginalBlockState() {
    try {
      return Sponge.getDataManager().deserialize(
          BlockState.class,
          DataFormats.JSON.read(originalBlockState));
    } catch (IOException e) {
      e.printStackTrace();
      return Optional.empty();
    }
  }

  private Optional<BlockState> getReplacementBlockState() {
    try {
      return Sponge.getDataManager().deserialize(
          BlockState.class,
          DataFormats.JSON.read(replacementBlockState));
    } catch (IOException e) {
      e.printStackTrace();
      return Optional.empty();
    }
  }
}
