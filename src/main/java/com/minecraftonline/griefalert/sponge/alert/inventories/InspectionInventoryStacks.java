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

package com.minecraftonline.griefalert.sponge.alert.inventories;

import com.google.common.collect.Lists;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

/**
 * A utility class to outline all the different items that make up the
 * {@link InspectionInventory}.
 */
public final class InspectionInventoryStacks {

  public static final ItemStack INFO = ItemStack.builder()
      .itemType(ItemTypes.BOOK)
      .add(Keys.DISPLAY_NAME, Text.of(TextColors.YELLOW, "Alert Info"))
      .add(Keys.ITEM_LORE, Lists.newArrayList(Text.of(TextColors.GRAY, "/ga info <index>")))
      .build();
  public static final ItemStack SHOW = ItemStack.builder()
      .itemType(ItemTypes.ENDER_EYE)
      .add(Keys.DISPLAY_NAME, Text.of(TextColors.BLUE, "Show Hologram"))
      .add(Keys.ITEM_LORE, Lists.newArrayList(Text.of(TextColors.GRAY, "/ga show <index>")))
      .build();
  public static final ItemStack TELEPORT = ItemStack.builder()
      .itemType(ItemTypes.ENDER_PEARL)
      .add(Keys.DISPLAY_NAME, Text.of(TextColors.RED, "Teleport"))
      .build();
  public static final ItemStack FIX = ItemStack.builder()
      .itemType(ItemTypes.COBBLESTONE_WALL)
      .add(Keys.DISPLAY_NAME, Text.of(TextColors.GREEN, "Fix Grief"))
      .add(Keys.ITEM_LORE, Lists.newArrayList(Text.of(TextColors.GRAY, "/ga fix <index>")))
      .build();
  public static final ItemStack QUERY = ItemStack.builder()
      .itemType(ItemTypes.GOLDEN_SHOVEL)
      .add(Keys.DISPLAY_NAME, Text.of(TextColors.LIGHT_PURPLE, "Query Griefer"))
      .add(Keys.ITEM_LORE, Lists.newArrayList(Text.of(TextColors.GRAY, "/ga query -p <griefer>")))
      .build();
  public static final ItemStack TOOL = ItemStack.builder()
      .itemType(ItemTypes.STICK)
      .add(Keys.DISPLAY_NAME, Text.of(TextColors.DARK_GREEN, "GriefAlert Tool"))
      .add(Keys.ITEM_LORE, Lists.newArrayList(Text.of(TextColors.GRAY, "/ga tool <griefer>")))
      .add(Keys.ITEM_ENCHANTMENTS, Lists.newArrayList(Enchantment.builder()
          .type(EnchantmentTypes.MENDING)
          .level(1).build()))
      .quantity(1)
      .build();
  public static final ItemStack NONE = ItemStack.builder()
      .itemType(ItemTypes.STAINED_GLASS_PANE)
      .add(Keys.DISPLAY_NAME, Text.of())
      .add(Keys.DYE_COLOR, DyeColors.GRAY)
      .build();

  private InspectionInventoryStacks() {
  }

}
