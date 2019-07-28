package exter.fodc.registry;

import com.google.common.base.Preconditions;
import exter.fodc.block.BlockAutomaticOreConverter;
import exter.fodc.block.BlockOreConversionTable;
import exter.fodc.tileentity.TileEntityAutomaticOreConverter;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.HashSet;
import java.util.Set;

import static exter.fodc.ModOreDicConvert.MODID;

@GameRegistry.ObjectHolder(MODID)
public class BlockRegistry
{
    public static BlockAutomaticOreConverter BLOCK_AUTO_ORE_CONVERTER = new BlockAutomaticOreConverter();
    public static BlockOreConversionTable BLOCK_ORE_CONVERTER = new BlockOreConversionTable();

    public static <BLOCK extends Block> BLOCK setBlockName(final BLOCK block, final String blockName)
    {
        block.setRegistryName(MODID, blockName);
        final ResourceLocation registryName = Preconditions.checkNotNull(block.getRegistryName());
        block.setTranslationKey(registryName.toString());
        return block;
    }

    @Mod.EventBusSubscriber(modid = MODID)
    public static class RegistrationHandler
    {
        public static final Set<ItemBlock> ITEM_BLOCKS = new HashSet<>();

        @SubscribeEvent
        public static void registerBlocks(final RegistryEvent.Register<Block> event)
        {
            final IForgeRegistry<Block> registry = event.getRegistry();

            final Block[] blocks = {
                    setBlockName(BLOCK_AUTO_ORE_CONVERTER, "ore_autoconverter"),
                    setBlockName(BLOCK_ORE_CONVERTER, "ore_conv_table")
            };

            registry.registerAll(blocks);


            GameRegistry.registerTileEntity(TileEntityAutomaticOreConverter.class, new ResourceLocation(MODID, "AutoOreConverter"));
        }

        @SubscribeEvent
        public static void registerItemBlocks(final RegistryEvent.Register<Item> event)
        {
            final ItemBlock[] items = {
                    new ItemBlock(BLOCK_AUTO_ORE_CONVERTER),
                    new ItemBlock(BLOCK_ORE_CONVERTER)
            };

            final IForgeRegistry<Item> registry = event.getRegistry();

            for (final ItemBlock item : items)
            {
                final Block block = item.getBlock();
                final ResourceLocation registryName = Preconditions.checkNotNull(block.getRegistryName(), "Block %s has null registry name", block);
                registry.register(item.setRegistryName(registryName));
                ITEM_BLOCKS.add(item);
            }
        }
    }
}
