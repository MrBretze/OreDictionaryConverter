package exter.fodc.gui;

import exter.fodc.ModOreDicConvert;
import exter.fodc.container.ContainerAutomaticOreConverter;
import exter.fodc.registry.OreNameRegistry;
import exter.fodc.tileentity.TileEntityAutomaticOreConverter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.io.IOException;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiAutomaticOreConverter extends GuiContainer
{
    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(ModOreDicConvert.MODID, "textures/gui/aoc_gui.png");

    private TileEntityAutomaticOreConverter te_autoconverter;
    private NonNullList<TargetSlot> target_slots = NonNullList.withSize(18, new TargetSlot(-1, -1, 0));

    public GuiAutomaticOreConverter(TileEntityAutomaticOreConverter aoc, EntityPlayer player)
    {
        super(new ContainerAutomaticOreConverter(aoc, player));
        allowUserInput = false;
        ySize = 210;
        te_autoconverter = aoc;

        int i, j;

        for (i = 0; i < 2; i++)
        {
            for (j = 0; j < 9; j++)
            {
                int s = i * 9 + j;
                target_slots.set(s, new TargetSlot(j * 18 + 8, i * 18 + 76, s));
            }
        }
    }

    public TargetSlot getTargetSlotAt(int x, int y)
    {
        for (TargetSlot s : target_slots)
        {
            if (x >= s.x + 1 && x <= s.x + 17 && y >= s.y + 1 && y <= s.y + 17)
            {
                return s;
            }
        }
        return null;
    }

    @Override
    public void initGui()
    {
        super.initGui();
    }

    @Override
    protected void mouseClicked(int x, int y, int par3) throws IOException
    {
        super.mouseClicked(x, y, par3);
        int window_x = (width - xSize) / 2;
        int window_y = (height - ySize) / 2;

        TargetSlot slot = getTargetSlotAt(x - window_x, y - window_y);

        if (slot != null)
        {
            slot.onClick();
        }
    }

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the
     * items)
     */
    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
        fontRenderer.drawString(I18n.format("gui.automatic_ore_converter.name"), 8, 6, 4210752);
        fontRenderer.drawString(I18n.format("gui.automatic_ore_converter.targets"), 8, 65, 4210752);
        fontRenderer.drawString(I18n.format("gui.automatic_ore_converter.inventory"), 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);

        Slot slot = this.getSlotUnderMouse();

        if (this.mc.player.inventory.getItemStack().isEmpty() && slot != null && !slot.getStack().isEmpty())
        {
            showToolTip(mouseX, mouseY, slot.getStack());
        }

        int window_x = (width - xSize) / 2;
        int window_y = (height - ySize) / 2;

        TargetSlot targetSlot = getTargetSlotAt(mouseX - window_x, mouseY - window_y);

        if (targetSlot != null && isPointInRegion(targetSlot.x, targetSlot.y, 16, 16, mouseX, mouseY))
        {
            RenderHelper.enableGUIStandardItemLighting();
            targetSlot.drawSlot();
            RenderHelper.enableStandardItemLighting();

            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            GlStateManager.colorMask(true, true, true, false);
            this.drawGradientRect(targetSlot.x + window_x, targetSlot.y + window_y, (targetSlot.x + window_x) + 16, (targetSlot.y + window_y) + 16, -2130706433, -2130706433);
            GlStateManager.colorMask(true, true, true, true);
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();

            ItemStack targetStack = te_autoconverter.getTarget(targetSlot.position);

            if (this.mc.player.inventory.getItemStack().isEmpty() && !targetStack.isEmpty())
            {
                showToolTip(mouseX, mouseY, targetStack);
            }
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
     * Draw the background layer for the GuiContainer (everything behind the
     * items)
     */
    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        Minecraft.getMinecraft().getTextureManager().bindTexture(GUI_TEXTURE);
        int window_x = (this.width - this.xSize) / 2;
        int window_y = (this.height - this.ySize) / 2;

        drawTexturedModalRect(window_x, window_y, 0, 0, xSize, ySize);

        GL11.glPushMatrix();
        RenderHelper.enableGUIStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glEnable(GL11.GL_LIGHTING);
        int i;

        for (i = 0; i < 18; i++)
        {
            target_slots.get(i).drawSlot();
        }

        GL11.glPopMatrix();
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        RenderHelper.enableStandardItemLighting();
    }

    public class TargetSlot
    {

        final public int x, y;
        final public int position;

        public TargetSlot(int xx, int yy, int pos)
        {
            x = xx;
            y = yy;
            position = pos;
        }

        public void onClick()
        {
            ItemStack player_stack = Minecraft.getMinecraft().player.inventory.getItemStack();

            ItemStack target_stack;

            if (!player_stack.isEmpty() && !OreNameRegistry.findAllOreNames(player_stack).isEmpty())
            {
                target_stack = player_stack.copy();
                target_stack.setCount(1);
            } else
            {
                target_stack = ItemStack.EMPTY;
            }

            te_autoconverter.setTarget(position, target_stack);
        }

        public void drawSlot()
        {
            ItemStack item = te_autoconverter.getTarget(position); //te_autoconverter.getTargets().get(position); //te_autoconverter.getTarget(position);

            if (!item.isEmpty())
            {
                int window_x = (width - xSize) / 2;
                int window_y = (height - ySize) / 2;

                GlStateManager.translate(0.0F, 0.0F, 32.0F);
                zLevel = 200.0F;
                itemRender.zLevel = 200.0F;
                FontRenderer font = null;
                if (item != null) font = item.getItem().getFontRenderer(item);
                if (font == null) font = fontRenderer;
                itemRender.renderItemAndEffectIntoGUI(item, window_x + x, window_y + y);
                itemRender.renderItemOverlayIntoGUI(font, item, window_x + x, window_y + y, null);
                zLevel = 0.0F;
                itemRender.zLevel = 0.0F;
            }
        }
    }
}
