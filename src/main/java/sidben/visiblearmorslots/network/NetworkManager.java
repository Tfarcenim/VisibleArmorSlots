package sidben.visiblearmorslots.network;

import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import sidben.visiblearmorslots.VisibleArmorSlots;
import sidben.visiblearmorslots.util.LogHelper;


public class NetworkManager
{

    private static final String         MOD_CHANNEL = "ch_sidben_vsa";
    private static int id = 0;
    private static SimpleChannel simpleChannel;



    public static void registerMessages()
    {
        simpleChannel = NetworkRegistry.newSimpleChannel(new ResourceLocation(VisibleArmorSlots.MOD_ID, VisibleArmorSlots.MOD_ID),
                () -> "1.0", s -> true, s -> true);

        //simpleChannel.registerMessage(MessageSlotAction.Handler.class, MessageSlotAction.class, id++, Dist.SERVER);


        simpleChannel.registerMessage(id++, C2SMessageSlotAction.class,
                C2SMessageSlotAction::encode,
                C2SMessageSlotAction::new,
                C2SMessageSlotAction::handle);
    }



    public static void sendSlotActionToServer(Integer resolverIndex, Slot targetSlot)
    {
        final C2SMessageSlotAction message = new C2SMessageSlotAction(resolverIndex, targetSlot);
        LogHelper.trace("NetworkManager.send - %s", message);

        simpleChannel.sendToServer(message);
    }


}
