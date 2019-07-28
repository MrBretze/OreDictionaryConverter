package exter.fodc.proxy;

import exter.fodc.ModOreDicConvert;
import exter.fodc.registry.BlockRegistry;
import exter.fodc.registry.ItemRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;

public class ClientODCProxy extends CommonODCProxy
{

    @Override
    public void init()
    {
        RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
        renderItem.getItemModelMesher().register(ItemRegistry.ITEM_ORE_CONVERTER, 0, new ModelResourceLocation(ModOreDicConvert.MODID + ":" + "ore_converter", "inventory"));
        renderItem.getItemModelMesher().register(Item.getItemFromBlock(BlockRegistry.BLOCK_ORE_CONVERTER), 0, new ModelResourceLocation(ModOreDicConvert.MODID + ":" + "ore_conv_table", "inventory"));
        renderItem.getItemModelMesher().register(Item.getItemFromBlock(BlockRegistry.BLOCK_AUTO_ORE_CONVERTER), 0, new ModelResourceLocation(ModOreDicConvert.MODID + ":" + "ore_autoconverter", "inventory"));
    }

}
