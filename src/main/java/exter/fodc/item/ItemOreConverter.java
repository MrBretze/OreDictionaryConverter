package exter.fodc.item;


import exter.fodc.ModOreDicConvert;
import exter.fodc.proxy.CommonODCProxy;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemOreConverter extends Item
{
    public ItemOreConverter()
    {
        super();
        maxStackSize = 1;
        setCreativeTab(CreativeTabs.MISC);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
    {
        player.openGui(ModOreDicConvert.instance, CommonODCProxy.GUI_ORECONVERTER, world, 0, 0, 0);
        return ActionResult.newResult(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }
}
