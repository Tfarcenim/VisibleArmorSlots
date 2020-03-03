package sidben.visiblearmorslots.handler.action;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Slot;


public interface ISlotActionResolver
{
    void handleClientSide(Slot targetSlot, PlayerEntity player);

    void handleServerSide(Slot targetSlot, PlayerEntity player);

    boolean requiresServerSideHandling();

    boolean isSatisfiedBy(SlotActionType action);
}
