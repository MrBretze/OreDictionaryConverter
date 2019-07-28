package exter.fodc.gui;

import exter.fodc.container.ContainerOreConverter;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiOreConverter extends GuiContainer
{
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation("fodc:textures/gui/oc_gui.png");

    public GuiOreConverter(Container cont)
    {
        super(cont);
    }

    public GuiOreConverter(InventoryPlayer player, World world)
    {
        super(new ContainerOreConverter(player, world));
        ySize = 180;
    }

    public GuiOreConverter(InventoryPlayer player, World world, int x, int y, int z)
    {
        super(new ContainerOreConverter(player, world, new BlockPos(x, y, z)));
        ySize = 180;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);

        Slot slot = getSlotUnderMouse();

        if (this.mc.player.inventory.getItemStack().isEmpty() && slot != null && !slot.getStack().isEmpty())
        {
            showToolTip(mouseX, mouseY, slot.getStack());
        }
    }

    private void showToolTip(int mouseX, int mouseY, ItemStack targetStack)
    {
        FontRenderer font = targetStack.getItem().getFontRenderer(targetStack);
        GuiUtils.preItemToolTip(targetStack);

        List<String> toolTip = this.getItemToolTip(targetStack);

        this.drawHoveringText(toolTip, mouseX, mouseY, font == null ? this.fontRenderer : font);
        GuiUtils.postItemToolTip();
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the
     * items)
     */
    protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
        fontRenderer.drawString("Ore Converter", 23, 6, 4210752);
        fontRenderer.drawString("Inventory", 8, ySize - 96 + 2, 4210752);
    }

    /**
     * Draw the background layer for the GuiContainer (everything behind the
     * items)
     */
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        mc.getTextureManager().bindTexture(GUI_TEXTURE);
        int center_x = (width - xSize) / 2;
        int center_y = (height - ySize) / 2;
        drawTexturedModalRect(center_x, center_y, 0, 0, xSize, ySize);
    }

}
