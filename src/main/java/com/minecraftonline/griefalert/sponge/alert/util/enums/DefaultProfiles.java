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

package com.minecraftonline.griefalert.sponge.alert.util.enums;

import com.minecraftonline.griefalert.common.alert.records.GriefProfile;
import com.minecraftonline.griefalert.common.alert.struct.GriefEvent;
import com.minecraftonline.griefalert.sponge.alert.util.SpongeUtil;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.DimensionTypes;
import org.spongepowered.api.world.World;

/**
 * A utility class to manage default {@link GriefProfile}s when first starting a server
 * with GriefAlert for the first time.
 *
 * @author PietElite
 */
public final class DefaultProfiles {

  private DefaultProfiles() {
  }

  /**
   * Give a list of all default profiles to use for the first use of GriefAlert.
   *
   * @return all default profiles
   */
  public static List<GriefProfile> getAll() {
    return Arrays.asList(
        // ====================================================================================
        GriefProfile.builder(GriefEvent.ATTACK, EntityTypes.ARMOR_STAND.getId()).build(),
        GriefProfile.builder(GriefEvent.ATTACK, EntityTypes.CHESTED_MINECART.getId()).build(),
        GriefProfile.builder(GriefEvent.ATTACK, EntityTypes.COMMANDBLOCK_MINECART.getId()).build(),
        GriefProfile.builder(GriefEvent.ATTACK, EntityTypes.FURNACE_MINECART.getId()).build(),
        GriefProfile.builder(GriefEvent.ATTACK, EntityTypes.HOPPER_MINECART.getId()).build(),
        GriefProfile.builder(GriefEvent.ATTACK, EntityTypes.ITEM_FRAME.getId()).build(),
        GriefProfile.builder(GriefEvent.ATTACK, EntityTypes.PAINTING.getId()).build(),
        GriefProfile.builder(GriefEvent.ATTACK, EntityTypes.TNT_MINECART.getId()).build(),
        GriefProfile.builder(GriefEvent.ATTACK, EntityTypes.WITHER.getId()).build(),

        // ====================================================================================
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.ACACIA_DOOR.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.ACACIA_FENCE.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.ACACIA_FENCE_GATE.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.ACTIVATOR_RAIL.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.ANVIL.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.BEACON.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.BED.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.BEDROCK.getId())
            .putColored(GriefProfile.Colorable.TARGET, TextColors.GRAY).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.BEETROOTS.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.BIRCH_DOOR.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.BIRCH_FENCE.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.BIRCH_FENCE_GATE.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.BLACK_GLAZED_TERRACOTTA.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.BLACK_SHULKER_BOX.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.BONE_BLOCK.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.BREWING_STAND.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.BRICK_BLOCK.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.BRICK_STAIRS.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.BROWN_GLAZED_TERRACOTTA.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.BROWN_MUSHROOM.getId())
            .addAllIgnored(SpongeUtil.worldsWithDimension(DimensionTypes.OVERWORLD)
                .stream()
                .map(World::getProperties)
                .collect(Collectors.toList()))
            .build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.BROWN_MUSHROOM_BLOCK.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.BROWN_SHULKER_BOX.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.CACTUS.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.CAKE.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.CARPET.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.CAULDRON.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.CHAIN_COMMAND_BLOCK.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.CHEST.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.CHORUS_FLOWER.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.CHORUS_PLANT.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.COAL_BLOCK.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.COAL_ORE.getId())
            .addAllIgnored(SpongeUtil.worldsWithDimension(DimensionTypes.OVERWORLD)
                .stream()
                .map(World::getProperties)
                .collect(Collectors.toList()))
            .build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.COBBLESTONE.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.COBBLESTONE_WALL.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.COMMAND_BLOCK.getId())
            .putColored(GriefProfile.Colorable.TARGET, TextColors.GRAY).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.CONCRETE.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.CONCRETE_POWDER.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.CRAFTING_TABLE.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.CYAN_GLAZED_TERRACOTTA.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.DARK_OAK_DOOR.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.DARK_OAK_FENCE.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.DARK_OAK_FENCE_GATE.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.DARK_OAK_STAIRS.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.DAYLIGHT_DETECTOR.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.DAYLIGHT_DETECTOR_INVERTED.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.DETECTOR_RAIL.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.DIAMOND_BLOCK.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.DIAMOND_ORE.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.DISPENSER.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.DROPPER.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.EMERALD_BLOCK.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.EMERALD_ORE.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.END_BRICKS.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.END_GATEWAY.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.END_PORTAL.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.END_PORTAL_FRAME.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.END_ROD.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.END_STONE.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.ENDER_CHEST.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.FARMLAND.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.FENCE.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.FENCE_GATE.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.FLOWER_POT.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.FURNACE.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.GLASS.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.GLASS_PANE.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.GLOWSTONE.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.GOLD_BLOCK.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.GOLD_ORE.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.GOLDEN_RAIL.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.GRASS_PATH.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.GRAY_GLAZED_TERRACOTTA.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.GRAY_SHULKER_BOX.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.GREEN_GLAZED_TERRACOTTA.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.GREEN_SHULKER_BOX.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.HARDENED_CLAY.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.HAY_BLOCK.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.HEAVY_WEIGHTED_PRESSURE_PLATE.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.HOPPER.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.ICE.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.IRON_BARS.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.IRON_BLOCK.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.IRON_ORE.getId())
            .addAllIgnored(SpongeUtil.worldsWithDimension(DimensionTypes.OVERWORLD)
                .stream()
                .map(World::getProperties)
                .collect(Collectors.toList()))
            .build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.IRON_TRAPDOOR.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.JUKEBOX.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.JUNGLE_DOOR.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.JUNGLE_FENCE.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.JUNGLE_FENCE_GATE.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.JUNGLE_STAIRS.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.LADDER.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.LAPIS_BLOCK.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.LAPIS_ORE.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.LEAVES.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.LEAVES2.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.LEVER.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.LIGHT_BLUE_GLAZED_TERRACOTTA.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.LIGHT_BLUE_SHULKER_BOX.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.LIGHT_WEIGHTED_PRESSURE_PLATE.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.LIME_GLAZED_TERRACOTTA.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.LIT_FURNACE.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.LIT_PUMPKIN.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.LIT_REDSTONE_LAMP.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.LOG.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.LOG2.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.MAGENTA_GLAZED_TERRACOTTA.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.MAGENTA_SHULKER_BOX.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.MAGMA.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.MELON_BLOCK.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.MELON_BLOCK.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.MELON_STEM.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.MOB_SPAWNER.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.MONSTER_EGG.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.MOSSY_COBBLESTONE.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.MYCELIUM.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.NETHER_BRICK.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.NETHER_BRICK_FENCE.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.NETHER_BRICK_STAIRS.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.NETHER_WART.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.NETHER_WART_BLOCK.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.NETHERRACK.getId())
            .addAllIgnored(SpongeUtil.worldsWithDimension(DimensionTypes.NETHER)
                .stream()
                .map(World::getProperties)
                .collect(Collectors.toList()))
            .build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.NOTEBLOCK.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.OAK_STAIRS.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.OBSERVER.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.OBSIDIAN.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.ORANGE_GLAZED_TERRACOTTA.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.ORANGE_SHULKER_BOX.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.PACKED_ICE.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.PINK_GLAZED_TERRACOTTA.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.PINK_SHULKER_BOX.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.PISTON.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.PISTON_EXTENSION.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.PISTON_HEAD.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.PLANKS.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.PORTAL.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.POWERED_COMPARATOR.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.POWERED_REPEATER.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.PRISMARINE.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.PUMPKIN.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.PUMPKIN_STEM.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.PURPLE_GLAZED_TERRACOTTA.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.PURPLE_SHULKER_BOX.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.PURPUR_BLOCK.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.PURPUR_DOUBLE_SLAB.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.PURPUR_PILLAR.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.PURPUR_SLAB.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.PURPUR_STAIRS.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.QUARTZ_BLOCK.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.QUARTZ_ORE.getId())
            .addAllIgnored(SpongeUtil.worldsWithDimension(DimensionTypes.NETHER)
                .stream()
                .map(World::getProperties)
                .collect(Collectors.toList()))
            .build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.QUARTZ_STAIRS.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.RAIL.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.RED_FLOWER.getId())
            .addAllIgnored(SpongeUtil.worldsWithDimension(DimensionTypes.OVERWORLD)
                .stream()
                .map(World::getProperties)
                .collect(Collectors.toList()))
            .build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.RED_GLAZED_TERRACOTTA.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.RED_MUSHROOM.getId())
            .addAllIgnored(SpongeUtil.worldsWithDimension(DimensionTypes.OVERWORLD)
                .stream()
                .map(World::getProperties)
                .collect(Collectors.toList()))
            .build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.RED_MUSHROOM_BLOCK.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.RED_NETHER_BRICK.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.RED_SANDSTONE.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.RED_SANDSTONE_STAIRS.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.RED_SHULKER_BOX.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.REDSTONE_BLOCK.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.REDSTONE_LAMP.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.REDSTONE_ORE.getId())
            .addAllIgnored(SpongeUtil.worldsWithDimension(DimensionTypes.OVERWORLD)
                .stream()
                .map(World::getProperties)
                .collect(Collectors.toList()))
            .build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.REDSTONE_TORCH.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.REDSTONE_WIRE.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.REPEATING_COMMAND_BLOCK.getId())
            .putColored(GriefProfile.Colorable.TARGET, TextColors.GRAY).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.SANDSTONE.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.SANDSTONE_STAIRS.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.SAPLING.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.SEA_LANTERN.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.SILVER_GLAZED_TERRACOTTA.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.SILVER_SHULKER_BOX.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.SKULL.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.SNOW.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.SNOW_LAYER.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.SOUL_SAND.getId())
            .addAllIgnored(SpongeUtil.worldsWithDimension(DimensionTypes.NETHER)
                .stream()
                .map(World::getProperties)
                .collect(Collectors.toList()))
            .build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.SPONGE.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.SPRUCE_DOOR.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.SPRUCE_FENCE.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.SPRUCE_FENCE_GATE.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.SPRUCE_STAIRS.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.STAINED_GLASS.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.STAINED_GLASS_PANE.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.STAINED_HARDENED_CLAY.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.STANDING_BANNER.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.STANDING_SIGN.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.STICKY_PISTON.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.STONE_BRICK_STAIRS.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.STONE_BUTTON.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.STONE_PRESSURE_PLATE.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.STONE_SLAB.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.STONE_SLAB2.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.STONEBRICK.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.STRUCTURE_BLOCK.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.STRUCTURE_VOID.getId())
            .putColored(GriefProfile.Colorable.TARGET, TextColors.GRAY).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.TORCH.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.TRAPDOOR.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.TRAPPED_CHEST.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.TRIPWIRE.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.TRIPWIRE_HOOK.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.UNLIT_REDSTONE_TORCH.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.UNPOWERED_COMPARATOR.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.UNPOWERED_REPEATER.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.WALL_BANNER.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.WALL_SIGN.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.WATERLILY.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.WEB.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.WHEAT.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.WHITE_GLAZED_TERRACOTTA.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.WHITE_SHULKER_BOX.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.WOODEN_BUTTON.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.WOODEN_DOOR.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.WOODEN_PRESSURE_PLATE.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.WOODEN_SLAB.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.WOOL.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.YELLOW_FLOWER.getId())
            .addAllIgnored(SpongeUtil.worldsWithDimension(DimensionTypes.OVERWORLD)
                .stream()
                .map(World::getProperties)
                .collect(Collectors.toList()))
            .build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.YELLOW_GLAZED_TERRACOTTA.getId()).build(),
        GriefProfile.builder(GriefEvent.BREAK, BlockTypes.YELLOW_SHULKER_BOX.getId()).build(),

        // ====================================================================================
        GriefProfile.builder(GriefEvent.DEATH, EntityTypes.HORSE.getId()).build(),
        GriefProfile.builder(GriefEvent.DEATH, EntityTypes.MUSHROOM_COW.getId()).build(),

        // ====================================================================================
        GriefProfile.builder(GriefEvent.EDIT, BlockTypes.STANDING_SIGN.getId()).build(),
        GriefProfile.builder(GriefEvent.EDIT, BlockTypes.WALL_SIGN.getId()).build(),

        // ====================================================================================
        GriefProfile.builder(GriefEvent.INTERACT, BlockTypes.TNT.getId()).build(),

        // ====================================================================================
        GriefProfile.builder(GriefEvent.ITEM_APPLY, ItemTypes.FLINT_AND_STEEL.getId()).build(),
        GriefProfile.builder(GriefEvent.ITEM_APPLY, ItemTypes.LAVA_BUCKET.getId()).build(),
        GriefProfile.builder(GriefEvent.ITEM_APPLY, ItemTypes.SPAWN_EGG.getId()).build(),
        GriefProfile.builder(GriefEvent.ITEM_APPLY, ItemTypes.WATER_BUCKET.getId()).build(),

        // ====================================================================================
        GriefProfile.builder(GriefEvent.ITEM_USE, ItemTypes.DRAGON_BREATH.getId()).build(),
        GriefProfile.builder(GriefEvent.ITEM_USE, ItemTypes.ENDER_EYE.getId()).build(),
        GriefProfile.builder(GriefEvent.ITEM_USE, ItemTypes.ENDER_PEARL.getId()).build(),
        GriefProfile.builder(GriefEvent.ITEM_USE, ItemTypes.EXPERIENCE_BOTTLE.getId()).build(),
        GriefProfile.builder(GriefEvent.ITEM_USE, ItemTypes.FIRE_CHARGE.getId()).build(),
        GriefProfile.builder(GriefEvent.ITEM_USE, ItemTypes.SPLASH_POTION.getId()).build(),

        // ====================================================================================
        GriefProfile.builder(GriefEvent.PLACE, BlockTypes.MOB_SPAWNER.getId()).build(),
        GriefProfile.builder(GriefEvent.PLACE, BlockTypes.PISTON.getId()).build(),
        GriefProfile.builder(GriefEvent.PLACE, BlockTypes.SPONGE.getId()).build(),
        GriefProfile.builder(GriefEvent.PLACE, BlockTypes.STANDING_BANNER.getId()).build(),
        GriefProfile.builder(GriefEvent.PLACE, BlockTypes.STANDING_SIGN.getId()).build(),
        GriefProfile.builder(GriefEvent.PLACE, BlockTypes.STICKY_PISTON.getId()).build(),
        GriefProfile.builder(GriefEvent.PLACE, BlockTypes.TNT.getId()).build(),
        GriefProfile.builder(GriefEvent.PLACE, BlockTypes.WALL_BANNER.getId()).build(),
        GriefProfile.builder(GriefEvent.PLACE, BlockTypes.WALL_SIGN.getId()).build(),

        // ====================================================================================
        GriefProfile.builder(GriefEvent.TRANSFORM, BlockTypes.DIRT.getId()).build(),
        GriefProfile.builder(GriefEvent.TRANSFORM, BlockTypes.FARMLAND.getId()).build(),
        GriefProfile.builder(GriefEvent.TRANSFORM, BlockTypes.GRASS_PATH.getId()).build()
    );
  }

}
