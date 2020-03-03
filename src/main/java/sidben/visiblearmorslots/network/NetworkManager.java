package sidben.visiblearmorslots.network;

import net.minecraft.inventory.container.Slot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import sidben.visiblearmorslots.util.LogHelper;


public class NetworkManager
{

    private static final String         MOD_CHANNEL = "ch_sidben_vsa";
    private static int                  packetdId   = 0;
    private static SimpleChannel simpleChannel;



    public static void registerMessages()
    {
        simpleChannel = NetworkRegistry.newSimpleChannel(MOD_CHANNEL);

        simpleChannel.registerMessage(MessageSlotAction.Handler.class, MessageSlotAction.class, packetdId++, Dist.SERVER);
    }



    public static void sendSlotActionToServer(Integer resolverIndex, Slot targetSlot)
    {
        final MessageSlotAction message = new MessageSlotAction(resolverIndex, targetSlot);
        LogHelper.trace("NetworkManager.send - %s", message);

        simpleChannel.sendToServer(message);
    }


}
