package exter.fodc.container;

import exter.fodc.ModOreDicConvert;
import exter.fodc.registry.BlockRegistry;
import exter.fodc.registry.ItemRegistry;
import exter.fodc.registry.OreNameRegistry;
import exter.fodc.slot.SlotOreConverter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.Set;

public class ContainerOreConverter extends Container
{
    // Slot numbers
    //private static final int SLOTS_RESULT = 0;
    private static final int SLOTS_MATERIALS = 16;
    private static final int SLOTS_INVENTORY = SLOTS_MATERIALS + 9;
    private static final int SLOTS_HOTBAR = SLOTS_INVENTORY + 3 * 9;
    protected World world_obj;
    private InventoryCrafting inv_inputs;
    private IInventory inv_results;
    private SlotOreConverter[] slots_results;
    private BlockPos pos;

    public ContainerOreConverter(InventoryPlayer inventory_player, World world)
    {
        this(inventory_player, world, new BlockPos(0, 9001, 0));
    }

    public ContainerOreConverter(InventoryPlayer inventory_player, World world, BlockPos bp)
    {
        inv_inputs = new InventoryCrafting(this, 3, 3);
        inv_results = new ResultInventory();
        slots_results = new SlotOreConverter[16];

        world_obj = world;
        pos = bp;

        // Result slots
        int i;
        for (i = 0; i < 16; i++)
        {
            slots_results[i] = new SlotOreConverter(inventory_player.player, inv_inputs, inv_results, i, 94 + (i % 4) * 18, 16 + (i / 4) * 18);
            addSlotToContainer(slots_results[i]);
        }

        // Ore matrix slots
        int j;
        for (i = 0; i < 3; ++i)
        {
            for (j = 0; j < 3; ++j)
            {
                addSlotToContainer(new Slot(inv_inputs, j + i * 3, 12 + j * 18, 25 + i * 18));
            }
        }

        // Player inventory
        for (i = 0; i < 3; ++i)
        {
            for (j = 0; j < 9; ++j)
            {
                addSlotToContainer(new Slot(inventory_player, j + i * 9 + 9, 8 + j * 18, 98 + i * 18));
            }
        }

        // Player hotbar
        for (i = 0; i < 9; ++i)
        {
            addSlotToContainer(new Slot(inventory_player, i, 8 + i * 18, 156));
        }

        onCraftMatrixChanged(inv_inputs);
    }

    // Workaround for shift clicking converting more than one type of ore
    @Override
    public ItemStack slotClick(int slot_index, int drag, ClickType click, EntityPlayer player)
    {
        ItemStack res_stack = ItemStack.EMPTY;
        if (click == ClickType.QUICK_MOVE && (drag == 0 || drag == 1) && slot_index != -999)
        {
            if (slot_index < 0)
            {
                return ItemStack.EMPTY;
            }

            Slot slot = this.inventorySlots.get(slot_index);

            if (slot != null && slot.canTakeStack(player))
            {
                ItemStack transfer_itemstack = this.transferStackInSlot(player, slot_index);

                if (!transfer_itemstack.isEmpty())
                {
                    res_stack = transfer_itemstack.copy();

                    if (ItemStack.areItemStacksEqual(slot.getStack(), transfer_itemstack)
                            && ItemStack.areItemsEqualIgnoreDurability(slot.getStack(), transfer_itemstack))
                    {
                        slotClick(slot_index, drag, click, player);
                        onCraftMatrixChanged(inv_inputs);
                    }
                }
            }
            onCraftMatrixChanged(inv_inputs);
            return res_stack;
        } else
        {
            res_stack = super.slotClick(slot_index, drag, click, player);
        }
        return res_stack;
    }

    @Override
    public void onCraftMatrixChanged(IInventory par1IInventory)
    {
        int i;

        ArrayList<ItemStack> results = new ArrayList<ItemStack>();
        for (i = 0; i < inv_inputs.getSizeInventory(); i++)
        {
            ItemStack in = inv_inputs.getStackInSlot(i);
            if (!in.isEmpty())
            {
                Set<String> names = OreNameRegistry.findAllOreNames(in);

                for (String n : names)
                {
                    for (ItemStack stack : OreDictionary.getOres(n))
                    {
                        if (names.containsAll(OreNameRegistry.findAllOreNames(stack)))
                        {
                            boolean found = false;
                            for (ItemStack r : results)
                            {
                                if (r.isItemEqual(stack))
                                {
                                    found = true;
                                    break;
                                }
                            }
                            if (!found)
                            {
                                int j = results.size();
                                ItemStack res = stack.copy();
                                res.setCount(1);
                                slots_results[j].setInputSlot(i);
                                inv_results.setInventorySlotContents(j, res);
                                results.add(res);
                                if (j == 15)
                                {
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        }
        for (i = results.size(); i < 16; i++)
        {
            slots_results[i].setInputSlot(-1);
            inv_results.setInventorySlotContents(i, ItemStack.EMPTY);
        }
    }

    /**
     * Callback for when the crafting gui is closed.
     */
    @Override
    public void onContainerClosed(EntityPlayer player)
    {
        super.onContainerClosed(player);

        if (!world_obj.isRemote)
        {
            for (int i = 0; i < 9; ++i)
            {
                ItemStack stack = inv_inputs.removeStackFromSlot(i);

                if (!stack.isEmpty())
                {
                    player.dropItem(stack, false);
                }
            }
        }
    }

    /**
     * Called when a player shift-clicks on a slot. You must override this or you
     * will crash when someone does that.
     */
    public ItemStack transferStackInSlot(EntityPlayer player, int slot_index)
    {
        ItemStack slot_stack = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(slot_index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack stack = slot.getStack();
            slot_stack = stack.copy();

            if (slot_index < SLOTS_MATERIALS)
            {
                if (!mergeItemStack(stack, SLOTS_INVENTORY, SLOTS_HOTBAR + 9, true))
                {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(stack, slot_stack);
            } else if (slot_index >= SLOTS_INVENTORY && slot_index < SLOTS_HOTBAR)
            {
                if (!mergeItemStack(stack, SLOTS_MATERIALS, SLOTS_MATERIALS + 9, false))
                {
                    return ItemStack.EMPTY;
                }
            } else if (slot_index >= SLOTS_HOTBAR && slot_index < SLOTS_HOTBAR + 9)
            {
                if (!mergeItemStack(stack, SLOTS_INVENTORY, SLOTS_INVENTORY + 3 * 9, false))
                {
                    return ItemStack.EMPTY;
                }
            } else if (!mergeItemStack(stack, SLOTS_INVENTORY, SLOTS_HOTBAR + 9, false))
            {
                return ItemStack.EMPTY;
            }

            if (!stack.isEmpty())
            {
                slot.onSlotChanged();
            }

            if (stack.getCount() == slot_stack.getCount())
            {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stack);
        }

        return slot_stack;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        if (pos.getY() <= 9000)
        {
            return this.world_obj.getBlockState(pos).getBlock() == BlockRegistry.BLOCK_ORE_CONVERTER && pos.distanceSq(player.posX, player.posY, player.posZ) <= 64.0D;
        }
        return player.inventory.hasItemStack(new ItemStack(ItemRegistry.ITEM_ORE_CONVERTER, 1));
    }

    private class ResultInventory implements IInventory
    {
        private ItemStack[] items;

        public ResultInventory()
        {
            items = new ItemStack[16];
            for (int i = 0; i < items.length; i++)
            {
                items[i] = ItemStack.EMPTY;
            }
        }

        @Override
        public int getSizeInventory()
        {
            return 16;
        }

        @Override
        public ItemStack getStackInSlot(int slot)
        {
            return items[slot];
        }

        @Override
        public ItemStack decrStackSize(int slot, int amount)
        {
            if (!items[slot].isEmpty())
            {
                ItemStack itemstack = items[slot];
                items[slot] = ItemStack.EMPTY;
                return itemstack;
            } else
            {
                return ItemStack.EMPTY;
            }
        }

        @Override
        public ItemStack removeStackFromSlot(int index)
        {
            if (!items[index].isEmpty())
            {
                ItemStack itemstack = items[index];
                items[index] = ItemStack.EMPTY;
                return itemstack;
            } else
            {
                return ItemStack.EMPTY;
            }
        }

        public void setInventorySlotContents(int slot, ItemStack stack)
        {
            items[slot] = stack;
        }

        @Override
        public int getInventoryStackLimit()
        {
            return 64;
        }

        @Override
        public void markDirty()
        {

        }

        @Override
        public boolean isUsableByPlayer(EntityPlayer par1EntityPlayer)
        {
            return true;
        }

        @Override
        public void openInventory(EntityPlayer playerIn)
        {

        }

        @Override
        public void closeInventory(EntityPlayer playerIn)
        {

        }

        @Override
        public boolean isItemValidForSlot(int par1, ItemStack par2ItemStack)
        {
            return true;
        }

        @Override
        public boolean hasCustomName()
        {
            return false;
        }

        @Override
        public ITextComponent getDisplayName()
        {
            return null;
        }

        @Override
        public int getField(int id)
        {
            return 0;
        }

        @Override
        public void setField(int id, int value)
        {

        }

        @Override
        public int getFieldCount()
        {
            return 0;
        }

        @Override
        public void clear()
        {

        }

        @Override
        public String getName()
        {
            return null;
        }

        @Override
        public boolean isEmpty()
        {
            return false;
        }

    }

}
