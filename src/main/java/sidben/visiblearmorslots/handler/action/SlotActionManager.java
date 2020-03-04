package sidben.visiblearmorslots.handler.action;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Slot;
import sidben.visiblearmorslots.network.NetworkManager;
import sidben.visiblearmorslots.util.LogHelper;


/**
 * Works like an event handler for slot actions.
 */
public class SlotActionManager {

	private final String CREATIVE_CONTAINER_NAME = "ContainerCreative";
	private final Map<Integer, ISlotActionResolver> slotActionResolver = new HashMap<Integer, ISlotActionResolver>();

	public static SlotActionManager instance = new SlotActionManager();

	private SlotActionManager() {
		int index = 0;

		// NOTE: The order is essential
		slotActionResolver.put(index++, new SlotActionResolver_Debug());
		slotActionResolver.put(index++, new SlotActionResolver_Clone());
		slotActionResolver.put(index++, new SlotActionResolver_TryPlacingOneItemOnSlot());
		slotActionResolver.put(index++, new SlotActionResolver_QuickTakeFromSlot());
		slotActionResolver.put(index++, new SlotActionResolver_TakeHalfStack());
		slotActionResolver.put(index++, new SlotActionResolver_TrySwapMouseWithSlot());
		for (int j = 0; j < 9; j++) {
			slotActionResolver.put(index++, new SlotActionResolver_TrySwapSlotWithHotbar(j));
		}
		slotActionResolver.put(index++, new SlotActionResolver_TrySwapWithOffHandSlot());
		slotActionResolver.put(index++, new SlotActionResolver_DoesNothing());
	}


	private Map.Entry<Integer, ISlotActionResolver> getResolverForAction(SlotActionType actionType) {
		// for (ISlotActionResolver actionResolver : this.slotActionResolver.)
		for (final Map.Entry<Integer, ISlotActionResolver> entry : this.slotActionResolver.entrySet()) {
			if (entry.getValue().isSatisfiedBy(actionType)) {
				LogHelper.trace("SlotActionManager: Using [%s], index %d, to resolve %s", entry.getValue(), entry.getKey(), actionType);
				return entry;
			}
		}

		return null;
	}

	private ISlotActionResolver getResolverByIndex(int actionResolverIndex) {
		final ISlotActionResolver actionResolver = this.slotActionResolver.getOrDefault(actionResolverIndex, new SlotActionResolver_DoesNothing());
		LogHelper.trace("SlotActionManager: Using [%s] to resolve index %d", actionResolver, actionResolverIndex);
		return actionResolver;
	}


	/**
	 * Process what should happen on the server.
	 */
	public void processActionOnServer(int actionResolverIndex, Slot targetSlot, PlayerEntity player) {
		if (targetSlot == null || player == null || player.isSpectator()) {
			return;
		}
		final ISlotActionResolver actionResolver = this.getResolverByIndex(actionResolverIndex);
		if (actionResolver != null) {
			actionResolver.handleServerSide(targetSlot, player);
		}
	}


	/**
	 * Process what should happen on the client.
	 */
	public void processActionOnClient(SlotActionType actionType, Slot targetSlot, PlayerEntity player) {
		if (actionType == null || targetSlot == null || player == null || actionType == SlotActionType.EMPTY || player.isSpectator()) {
			return;
		}

		final Map.Entry<Integer, ISlotActionResolver> resolverEntry = this.getResolverForAction(actionType);
		if (resolverEntry != null) {
			final ISlotActionResolver actionResolver = resolverEntry.getValue();
			final boolean isPlayerOnCreativeInventory = player.openContainer.getClass().getName().contains(CREATIVE_CONTAINER_NAME);

			actionResolver.handleClientSide(targetSlot, player);

			// NOTE: the creative mode player inventory must be client-side only
			if (isPlayerOnCreativeInventory) {
				player.container.detectAndSendChanges();
			} else if (actionResolver.requiresServerSideHandling()) {
				NetworkManager.sendSlotActionToServer(resolverEntry.getKey(), targetSlot);
			}
		}
	}
}
