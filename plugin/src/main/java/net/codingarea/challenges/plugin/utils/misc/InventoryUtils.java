package net.codingarea.challenges.plugin.utils.misc;

import net.codingarea.challenges.plugin.utils.animation.AnimationFrame;
import net.codingarea.challenges.plugin.utils.item.DefaultItem;
import net.codingarea.challenges.plugin.utils.item.ItemBuilder;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author anweisen | https://github.com/anweisen
 * @author KxmischesDomi | https://github.com/kxmischesdomi
 * @since 2.0
 */
public final class InventoryUtils {

	private InventoryUtils() {}

	public static void fillInventory(@Nonnull Inventory inventory, @Nullable ItemStack item) {
		for (int i = 0; i < inventory.getSize(); i++) {
			inventory.setItem(i, item);
		}
	}

	public static void fillInventory(@Nonnull Inventory inventory, @Nullable ItemStack item, @Nonnull int... slots) {
		for (int i : slots) {
			inventory.setItem(i, item);
		}
	}

	@FunctionalInterface
	interface InventorySetter<I> {

		InventorySetter<AnimationFrame> FRAME = (frame, slot, item) -> frame.setItem(slot, item);
		InventorySetter<Inventory> INVENTORY = (inventory, slot, item) -> inventory.setItem(slot, item.build());

		void set(@Nonnull I inventory, int slot, @Nonnull ItemBuilder item);

	}

	public static void setNavigationItemsToInventory(@Nonnull List<Inventory> inventories, @Nonnull int[] navigationSlots) {
		setNavigationItemsToInventory(inventories, navigationSlots, true);
	}

	public static void setNavigationItemsToInventory(@Nonnull List<Inventory> inventories, @Nonnull int[] navigationSlots, boolean goBackExit) {
		setNavigationItems(inventories, navigationSlots, goBackExit, InventorySetter.INVENTORY);
	}

	public static void setNavigationItemsToFrame(@Nonnull List<AnimationFrame> frames, @Nonnull int[] navigationSlots) {
		setNavigationItemsToFrame(frames, navigationSlots, true);
	}

	public static void setNavigationItemsToFrame(@Nonnull List<AnimationFrame> inventories, @Nonnull int[] navigationSlots, boolean goBackExit) {
		setNavigationItems(inventories, navigationSlots, goBackExit, InventorySetter.FRAME);
	}

	public static void setNavigationItemsToFrame(@Nonnull AnimationFrame frame, @Nonnull int[] navigationSlots, boolean goBackExit, int index, int size) {
		setNavigationItems(frame, navigationSlots, goBackExit, InventorySetter.FRAME, index, size);
	}

	public static <I> void setNavigationItems(@Nonnull List<I> inventories, @Nonnull int[] navigationSlots, boolean goBackExit, @Nonnull InventorySetter<I> setter) {
		for (int i = 0; i < inventories.size(); i++) {
			setNavigationItems(inventories.get(i), navigationSlots, goBackExit, setter, i, inventories.size());
		}
	}

	public static <I> void setNavigationItems(@Nonnull I inventory, @Nonnull int[] navigationSlots, boolean goBackExit, @Nonnull InventorySetter<I> setter, int index, int size) {
		ItemBuilder left = index == 0 && goBackExit ? DefaultItem.navigateBackMainMenu() : DefaultItem.navigateBack();
		setter.set(inventory, navigationSlots[0], left);
		if (index < (size - 1))
			setter.set(inventory, navigationSlots[1], DefaultItem.navigateNext());
	}

	public static boolean isEmpty(@Nonnull Inventory inventory) {
		for (ItemStack content : inventory.getContents()) {
			if (content != null) return false;
		}
		return true;
	}

	public static boolean inventoryContainsSequence(@Nonnull Inventory inventory, @Nonnull ItemStack[] sequence) {
		for (int i = 0; i < sequence.length && i < inventory.getSize(); i++) {
			ItemStack expected = sequence[i];
			ItemStack found = inventory.getItem(i);
			if (expected == null && found == null)  continue;
			if (expected == null)                   return false;
			if (found == null)                      return false;
			if (!expected.isSimilar(found))         return false;
		}
		return true;
	}

	public static int getRandomEmptySlot(@Nonnull Inventory inventory) {
		List<Integer> emptySlots = new ArrayList<>();

		for (int slot = 0; slot < inventory.getSize(); slot++) {
			if (inventory.getItem(slot) == null) {
				emptySlots.add(slot);
			}

		}

		if (emptySlots.isEmpty()) return -1;
		return emptySlots.get(new Random().nextInt(emptySlots.size()));
	}

	public static int getRandomFullSlot(@Nonnull Inventory inventory) {
		List<Integer> fullSlots = new ArrayList<>();

		for (int slot = 0; slot < inventory.getSize(); slot++) {
			ItemStack item = inventory.getItem(slot);
			if (item != null && !item.isSimilar(ItemBuilder.BLOCKED_ITEM)) {
				fullSlots.add(slot);
			}
		}

		if (fullSlots.isEmpty()) return -1;

		return fullSlots.get(new Random().nextInt(fullSlots.size()));
	}

	public static int getRandomSlot(@Nonnull Inventory inventory) {
		List<Integer> slots = new ArrayList<>();

		for (int slot = 0; slot < inventory.getSize(); slot++) {
			ItemStack item = inventory.getItem(slot);
			if (item != null && item.isSimilar(ItemBuilder.BLOCKED_ITEM)) continue;
			slots.add(slot);

		}

		if (slots.isEmpty()) return -1;
		return slots.get(new Random().nextInt(slots.size()));
	}

}
