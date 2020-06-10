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

package com.minecraftonline.griefalert.inventories;

import com.google.common.collect.Lists;
import com.minecraftonline.griefalert.GriefAlert;
import com.minecraftonline.griefalert.api.alerts.Alert;
import com.minecraftonline.griefalert.api.data.Permission;
import com.minecraftonline.griefalert.util.Alerts;
import com.minecraftonline.griefalert.util.Format;
import com.minecraftonline.griefalert.util.SpongeUtil;
import com.minecraftonline.griefalert.util.enums.Permissions;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.Transform;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.permission.Subject;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class InspectionInventory {

  private static Map<Integer, InventoryItem> inventoryItems = Lists.newArrayList(
      new InventoryItem(
          InspectionInventoryStacks.INFO,
          11,
          Permissions.GRIEFALERT_COMMAND_INFO,
          (officer, alertIndex) -> Sponge.getCommandManager().process(
              officer,
              "griefalert info " + alertIndex)),
      new InventoryItem(
          InspectionInventoryStacks.QUERY,
          12,
          Permissions.GRIEFALERT_COMMAND_QUERY,
          (officer, alertIndex) -> Sponge.getCommandManager().process(
              officer,
              "griefalert query -p " + SpongeUtil.getUser(GriefAlert.getInstance()
                  .getAlertService()
                  .getAlert(alertIndex)
                  .getGrieferUuid())
                  .orElseThrow(() -> new RuntimeException(String.format("Query command failed while {} using panel", officer.getName())))
                  .getName())),
      new InventoryItem(
          InspectionInventoryStacks.SHOW,
          13,
          Permissions.GRIEFALERT_COMMAND_SHOW,
          (officer, alertIndex) -> Sponge.getCommandManager().process(
              officer,
              "griefalert show " + alertIndex)),
      new InventoryItem(
          InspectionInventoryStacks.FIX,
          14,
          Permissions.GRIEFALERT_COMMAND_FIX,
          (officer, alertIndex) -> Sponge.getCommandManager().process(
              officer,
              "griefalert fix " + alertIndex)),
      new InventoryItem(
          InspectionInventoryStacks.TELEPORT,
          15,
          Permissions.GRIEFALERT_COMMAND_CHECK,
          (officer, alertIndex) -> {
            Alert alert = GriefAlert.getInstance().getAlertService().getAlert(alertIndex);
            officer.setTransform(new Transform<>(Alerts.getWorld(alert), alert.getGrieferPosition(), alert.getGrieferRotation()));
          }))
      .stream()
      .collect(Collectors.toMap(InventoryItem::getSlotIndex, item -> item));


  public static void openInspectionPanel(Player officer, int alertIndex) {

    Inventory inventory = Inventory.builder()
        .of(InventoryArchetypes.CHEST)
        .property(
            InventoryTitle.PROPERTY_NAME,
            new InventoryTitle(Text.of(
                Format.GRIEF_ALERT_THEME,
                "Inspection Panel: ",
                TextColors.BLACK,
                alertIndex)))
        .listener(
            ClickInventoryEvent.class,
            clickInventoryEvent -> {
              Optional<SlotIndex> slotIndexOptional = clickInventoryEvent.getSlot().flatMap(slot -> slot.getInventoryProperty(SlotIndex.class));
              if (slotIndexOptional.isPresent()) {
                if (inventoryItems.containsKey(slotIndexOptional.get().getValue())) {
                  clickInventoryEvent.setCancelled(true);
                  Task.builder().execute(() -> {
                    officer.closeInventory();
                    inventoryItems.get(slotIndexOptional.get().getValue()).accept(officer, alertIndex);
                  }).submit(GriefAlert.getInstance());
                } else {
                  clickInventoryEvent.setCancelled(true);
                }
              }
            })
        .build(GriefAlert.getInstance());

    for (Slot slot : inventory.<Slot>slots()) {
      Optional<SlotIndex> slotIndexOptional = slot.getInventoryProperty(SlotIndex.class);
      if (slotIndexOptional.isPresent()) {
        Integer index = slotIndexOptional.get().getValue();
        if (inventoryItems.containsKey(index) && inventoryItems.get(index).hasPermission(officer)) {
          slot.set(inventoryItems.get(slotIndexOptional.get().getValue()).getItemStack());
        } else {
          slot.set(InspectionInventoryStacks.NONE);
        }
      }
    }

    officer.openInventory(inventory);
  }

  private static class InventoryItem implements BiConsumer<Player, Integer> {

    private ItemStack itemStack;
    private int slotIndex;
    private BiConsumer<Player, Integer> onClick;
    private Permission permission;

    public InventoryItem(ItemStack itemStack, int slotIndex, Permission permission, BiConsumer<Player, Integer> onClick) {
      this.itemStack = itemStack;
      this.slotIndex = slotIndex;
      this.permission = permission;
      this.onClick = onClick;
    }

    @Override
    public void accept(Player officer, Integer alertIndex) {
      onClick.accept(officer, alertIndex);
    }

    public boolean hasPermission(Subject subject) {
      return Permissions.has(subject, permission);
    }

    public ItemStack getItemStack() {
      return itemStack;
    }

    public int getSlotIndex() {
      return slotIndex;
    }
  }

}
