package exter.fodc.jei;

import exter.fodc.container.ContainerOreConverter;
import exter.fodc.registry.BlockRegistry;
import exter.fodc.registry.ItemRegistry;
import mezz.jei.api.*;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import net.minecraft.item.ItemStack;

@JEIPlugin
public class ODCJEIPlugin implements IModPlugin
{
    @Override
    public void register(IModRegistry registry)
    {
        IJeiHelpers helpers = registry.getJeiHelpers();
        registry.addRecipeCategories(new OreConverterJEI.Category(helpers));
        registry.addRecipeHandlers(new OreConverterJEI.Handler());
        registry.addRecipes(OreConverterJEI.getRecipes());
        registry.getRecipeTransferRegistry().addRecipeTransferHandler(
                ContainerOreConverter.class, "fodc.oreconverter", 0, 25, 25, 36);
        registry.addRecipeCategoryCraftingItem(new ItemStack(ItemRegistry.ITEM_ORE_CONVERTER), "fodc.oreconverter");
        registry.addRecipeCategoryCraftingItem(new ItemStack(BlockRegistry.BLOCK_ORE_CONVERTER), "fodc.oreconverter");
        registry.addRecipeCategoryCraftingItem(new ItemStack(BlockRegistry.BLOCK_AUTO_ORE_CONVERTER), "fodc.oreconverter");
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime runtime)
    {

    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry)
    {

    }

    @Override
    public void registerIngredients(IModIngredientRegistration registry)
    {

    }
}
