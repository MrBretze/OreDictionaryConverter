package exter.fodc.proxy;

import exter.fodc.container.ContainerAutomaticOreConverter;
import exter.fodc.container.ContainerOreConverter;
import exter.fodc.gui.GuiAutomaticOreConverter;
import exter.fodc.gui.GuiOreConverter;
import exter.fodc.tileentity.TileEntityAutomaticOreConverter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class CommonODCProxy implements IGuiHandler
{

    static public final int GUI_ORECONVERTER = 0;
    static public final int GUI_ORECONVERTIONTABLE = 1;
    static public final int GUI_OREAUTOCONVERTER = 2;

    public void init()
    {

    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        switch (ID)
        {
            case GUI_ORECONVERTER:
                return new ContainerOreConverter(player.inventory, world);
            case GUI_ORECONVERTIONTABLE:
                return new ContainerOreConverter(player.inventory, world, new BlockPos(x, y, z));
            case GUI_OREAUTOCONVERTER:
                return new ContainerAutomaticOreConverter((TileEntityAutomaticOreConverter) world.getTileEntity(new BlockPos(x, y, z)), player);
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        switch (ID)
        {
            case GUI_ORECONVERTER:
                return new GuiOreConverter(player.inventory, world);
            case GUI_ORECONVERTIONTABLE:
                return new GuiOreConverter(player.inventory, world, x, y, z);
            case GUI_OREAUTOCONVERTER:
            {
                TileEntityAutomaticOreConverter te = (TileEntityAutomaticOreConverter) world.getTileEntity(new BlockPos(x, y, z));
                return new GuiAutomaticOreConverter(te, player);
            }
        }
        return null;
    }
}
