/*
 * MIT License
 *
 * Copyright (c) 2021 MinecraftOnline
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
 *
 */

/*
 * MIT License
 *
 * Copyright (c) 2021 MinecraftOnline
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
 *
 */

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

import com.google.common.collect.Lists;
import com.helion3.prism.api.flags.Flag;
import com.helion3.prism.api.services.PrismService;
import com.helion3.prism.api.services.Request;
import com.helion3.prism.util.PrismEvents;
import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.enums.Permissions;
import com.minecraftonline.griefalert.util.enums.Settings;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.enchantment.Enchantment;
import org.spongepowered.api.item.enchantment.EnchantmentTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.entity.PlayerInventory;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.sql.Date;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

public class ToolHandler {

  @Listener(order = Order.FIRST)
  public void InteractBlockSecondary(InteractBlockEvent.Secondary event) {
    event.getTargetBlock().getLocation().ifPresent(location ->
        fix(event, location.getBlockRelative(event.getTargetSide())));
  }

  @Listener(order = Order.FIRST)
  public void InteractBlockPrimary(InteractBlockEvent.Primary event) {
    event.getTargetBlock().getLocation().ifPresent(location ->
        fix(event, location));
  }

  @Listener(order = Order.FIRST)
  public void Interact(InteractEntityEvent event) {
    event.getCause().first(Player.class)
        .flatMap(player ->
            player.getItemInHand(HandTypes.MAIN_HAND)
                .filter(this::isTool))
        .ifPresent(tool -> event.setCancelled(true));
  }

  @Listener(order = Order.FIRST)
  public void InventoryEvent(ClickInventoryEvent event) {
    event.getTransactions().forEach(slotTransaction -> {
      if (isTool(slotTransaction.getFinal())) {
        if (event.getTargetInventory() instanceof PlayerInventory) {
          Optional<Player> carrier = ((PlayerInventory) event.getTargetInventory()).getCarrier();
          if (carrier.isPresent()) {
            if (carrier.get().hasPermission(Permissions.GRIEFALERT_COMMAND_FIX.get())) {
              return;
            }
          }
        }
        slotTransaction.setValid(false);
        slotTransaction.setCustom(ItemStackSnapshot.NONE);
      }
    });
  }

  @Listener(order = Order.FIRST)
  public void DropToolEvent(DropItemEvent.Dispense event) {
    clearTools(event.getEntities());
  }

  @Listener(order = Order.FIRST)
  public void DropToolEvent(DropItemEvent.Destruct event) {
    clearTools(event.getEntities());
  }

  private void clearTools(List<Entity> entityList) {
    entityList.forEach(entity -> {
      if (entity instanceof Item) {
        if (isTool(((Item) entity).getItemData().item().get())) {
          entity.remove();
        }
      }
    });
  }

  private void fix(InteractBlockEvent event, Location<World> location) {
    event.getCause().first(Player.class).ifPresent(player ->
        player.getItemInHand(HandTypes.MAIN_HAND)
            .filter(this::isTool)
            .ifPresent(tool -> {
              if (!player.hasPermission(Permissions.GRIEFALERT_TOOL.get())) {
                player.setItemInHand(HandTypes.MAIN_HAND, ItemStack.empty());
                player.sendMessage(Format.error("You don't have permission to use this!"));
                return;
              }
              event.setCancelled(true);

              Task.builder().async().execute(() -> {
                Request.Builder builder = Request.builder()
                    .setLatest(Date.from(Instant.now()))
                    .addEvent(PrismEvents.BLOCK_BREAK)
                    .addEvent(PrismEvents.BLOCK_PLACE)
                    .setxRange(location.getBlockX(), location.getBlockX())
                    .setyRange(location.getBlockY(), location.getBlockY())
                    .setzRange(location.getBlockZ(), location.getBlockZ())
                    .addFlag(Flag.QUIET);
                if (Settings.TOOL_ROLLBACK_RANGE.getValue() < 0) {
                  builder.setEarliest(Date.from(Instant.EPOCH));
                } else {
                  builder.setEarliest(Date.from(Instant.now().minus(Settings.TOOL_ROLLBACK_RANGE.getValue(), ChronoUnit.HOURS)));
                }
                AtomicReference<GameProfile> profileReference = new AtomicReference<>();
                Optional<UUID> uuid = grieferUuid(tool);
                if (uuid.isPresent()) {
                  try {
                    Sponge.getServer()
                        .getGameProfileManager()
                        .get(uuid.get())
                        .whenComplete((profile, exception) -> {
                          if (profile != null) {
                            profileReference.set(profile);
                            builder.addPlayerUuid(profile.getUniqueId());
                          }
                        }).get();
                  } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                  }
                } else {
                  player.setItemInHand(HandTypes.MAIN_HAND, ItemStack.empty());
                  player.sendMessage(Format.error("This tool is expired, please make a new one"));
                  return;
                }
                Optional<PrismService> prism = Sponge.getServiceManager().provide(PrismService.class);
                try {
                  if (prism.isPresent()) {
                    if (profileReference.get() != null && !profileReference.get().getName().isPresent()) {
                      player.sendMessage(Format.success("Undoing events by ", TextColors.GOLD, profileReference.get().getName().get()));
                    }
                    prism.get().rollback(player, builder.build());
                  } else {
                    throw new RuntimeException("Could not get PrismService from Sponge Service Manager");
                  }
                } catch (Exception e) {
                  player.sendMessage(Format.error("An error occurred communicating with Prism"));
                  e.printStackTrace();
                }
              }).submit(GriefAlert.getInstance());
            }));
  }

  private boolean isTool(ItemStackSnapshot itemStackSnapshot) {
    if (!itemStackSnapshot.getType().equals(ItemTypes.GLASS)) {
      return false;
    }
    return itemStackSnapshot.get(ImmutableToolManipulator.class)
        .map(ImmutableToolManipulator::isTool)
        .map(BaseValue::get)
        .orElse(false);
  }

  private boolean isTool(ItemStack itemStack) {
    if (!itemStack.getType().equals(ItemTypes.GLASS)) {
      return false;
    }
    return itemStack.get(ToolManipulator.class)
        .map(ToolManipulator::isTool)
        .map(Value::get)
        .orElse(false);
  }

  private Optional<UUID> grieferUuid(ItemStack itemStack) {
    Optional<ToolGrieferManipulator> manipulatorOptional = itemStack.get(ToolGrieferManipulator.class);
    return manipulatorOptional
        .map(ToolGrieferManipulator::getValueGetter)
        .map(Value::get);
  }

  public ItemStack tool(String playerName, UUID playerUuid) {
    ItemStack itemStack = ItemStack.builder()
        .itemType(ItemTypes.GLASS)
        .quantity(1)
        .build();

    itemStack.offer(Keys.ITEM_ENCHANTMENTS, Lists.newArrayList(Enchantment.builder()
        .type(EnchantmentTypes.MENDING)
        .level(1).build()));

    itemStack.offer(Keys.DISPLAY_NAME,
        Text.of(TextColors.AQUA, "GriefAlert Tool - ", TextColors.GOLD, playerName));
    itemStack.offer(Keys.ITEM_LORE, Lists.newArrayList(
        Text.of(TextColors.GOLD, "Left Click ", TextColors.WHITE, "to fix addition grief"),
        Text.of(TextColors.LIGHT_PURPLE, "Right Click ", TextColors.WHITE, "to fix breakage grief")));

    itemStack.offer(new ToolManipulator(true));
    itemStack.offer(new ToolGrieferManipulator(playerUuid));
    return itemStack;
  }

}
