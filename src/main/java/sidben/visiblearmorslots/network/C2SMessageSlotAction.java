package sidben.visiblearmorslots.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import sidben.visiblearmorslots.handler.action.SlotActionManager;
import sidben.visiblearmorslots.inventory.SlotArmor;
import sidben.visiblearmorslots.inventory.SlotOffHand;
import sidben.visiblearmorslots.util.LogHelper;

import java.util.function.Supplier;


public class C2SMessageSlotAction {

	// ---------------------------------------------
	// Fields
	// ---------------------------------------------

	private int actionResolverIndex = 0;
	private int playerContainerSlotIndex = -1;
	private boolean usePlayerInventory = true;


	// ---------------------------------------------
	// Constructors
	// ---------------------------------------------

	public C2SMessageSlotAction() {
	}

	public C2SMessageSlotAction(int resolverIndex, Slot targetSlot) {
		this.playerContainerSlotIndex = targetSlot.slotNumber;
		this.actionResolverIndex = resolverIndex;
		this.usePlayerInventory = targetSlot instanceof SlotArmor || targetSlot instanceof SlotOffHand;
	}

	public C2SMessageSlotAction(PacketBuffer buffer) {
		playerContainerSlotIndex = buffer.readInt();
		actionResolverIndex = buffer.readInt();
		usePlayerInventory = buffer.readBoolean();
	}


	// ---------------------------------------------
	// Properties
	// ---------------------------------------------

	public int getContainerSlotIndex() {
		return this.playerContainerSlotIndex;
	}

	public int getActionResolverIndex() {
		return this.actionResolverIndex;
	}

	/**
	 * If TRUE, the container slot index must be used with player.inventoryContainer, because
	 * the slot is not part of the open container.<br/>
	 * <br/>
	 * <p>
	 * If FALSE, the container slot index must be used with player.openContainer.
	 */
	public boolean usePlayerInventory() {
		return this.usePlayerInventory;
	}


	// ---------------------------------------------
	// Methods
	// ---------------------------------------------


	public void encode(ByteBuf buf) {
		buf.writeInt(this.playerContainerSlotIndex);
		buf.writeInt(this.actionResolverIndex);
		buf.writeBoolean(this.usePlayerInventory);
	}

	@Override
	public String toString() {
		return String.format("MessageSlotAction [slot index=%d, resolver index=%d, use player inventory=%s]", this.getContainerSlotIndex(), this.getActionResolverIndex(),
						this.usePlayerInventory());
	}



		public void handle(Supplier<NetworkEvent.Context> ctx) {
			LogHelper.trace("IMessage.handle");

			final PlayerEntity player = ctx.get().getSender();
			final int actionIndex = getActionResolverIndex();
			Slot targetSlot = null;

			// NOTE: The extra slots are not part of the openContainer, so I need to
			// find them directly on the player inventory.
			try {
				if (usePlayerInventory()) {
					targetSlot = player.container.getSlot(getContainerSlotIndex());
				} else {
					if (player.openContainer != null) {
						targetSlot = player.openContainer.getSlot(getContainerSlotIndex());
					}
				}
			} catch (final Exception e1) {
				LogHelper.error("ERROR - could not find target slot: %s", e1);
				targetSlot = null;
			}

			if (player == null || targetSlot == null) {
				return;
			}
			SlotActionManager.instance.processActionOnServer(actionIndex, targetSlot, player);
		}
	}
