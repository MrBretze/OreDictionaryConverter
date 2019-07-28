package exter.fodc.registry;

import com.google.common.base.Preconditions;
import exter.fodc.item.ItemOreConverter;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import static exter.fodc.ModOreDicConvert.MODID;

@GameRegistry.ObjectHolder(MODID)
public class ItemRegistry
{
    public static ItemOreConverter ITEM_ORE_CONVERTER;

    public static <ITEM extends Item> ITEM setItemName(final ITEM item, final String blockName)
    {
        item.setRegistryName(MODID, blockName);
        final ResourceLocation registryName = Preconditions.checkNotNull(item.getRegistryName());
        item.setTranslationKey(registryName.toString());
        return item;
    }

    @Mod.EventBusSubscriber(modid = MODID)
    public static class RegistrationHandler
    {
        @SubscribeEvent
        public static void registerItems(final RegistryEvent.Register<Item> event)
        {
            final Item[] items = {
                    ITEM_ORE_CONVERTER = setItemName(new ItemOreConverter(), "ore_converter")
            };

            final IForgeRegistry<Item> registry = event.getRegistry();

            for (final Item item : items)
            {
                registry.register(item);
            }
        }
    }
}
