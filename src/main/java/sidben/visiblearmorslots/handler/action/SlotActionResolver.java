package sidben.visiblearmorslots.handler.action;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Slot;


public abstract class SlotActionResolver implements ISlotActionResolver
{


    @Override
    public void handleClientSide(Slot targetSlot, PlayerEntity player)
    {
    }


    @Override
    public void handleServerSide(Slot targetSlot, PlayerEntity player)
    {
    }


    @Override
    public boolean requiresServerSideHandling()
    {
        return false;
    }


    @Override
    public final boolean isSatisfiedBy(SlotActionType action)
    {
        final boolean result = this.isSatisfiedByInternal(action);
        return result;
    }


    @Override
    public String toString()
    {
        return this.getClass().getSimpleName();
    }


    protected abstract boolean isSatisfiedByInternal(SlotActionType action);
}
