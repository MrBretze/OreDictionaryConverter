package exter.fodc;

import exter.fodc.network.MessageODC;
import exter.fodc.proxy.CommonODCProxy;
import exter.fodc.registry.BlockRegistry;
import exter.fodc.registry.OreNameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.Logger;

@Mod(
        modid = ModOreDicConvert.MODID,
        name = ModOreDicConvert.MODNAME,
        version = ModOreDicConvert.MODVERSION
)
public class ModOreDicConvert
{
    public static final String MODID = "fodc";
    public static final String MODNAME = "Ore Dictionary Converter";
    public static final String MODVERSION = "1.10.0";

    @Instance("fodc")
    public static ModOreDicConvert instance;

    // Says where the client and server 'proxy' code is loaded.
    @SidedProxy(clientSide = "exter.fodc.proxy.ClientODCProxy", serverSide = "exter.fodc.proxy.CommonODCProxy")
    public static CommonODCProxy proxy;

    public static Logger log;

    public static boolean log_orenames;

    public static SimpleNetworkWrapper network_channel;


    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        log = event.getModLog();
        Configuration config = new Configuration(event.getSuggestedConfigurationFile());
        config.load();
        log_orenames = config.getBoolean("log_names", Configuration.CATEGORY_GENERAL, false, "Log registered ore names.");
        OreNameRegistry.preInit(config);
        config.save();

        NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);

        network_channel = NetworkRegistry.INSTANCE.newSimpleChannel("EXTER.FODC");
        network_channel.registerMessage(MessageODC.Handler.class, MessageODC.class, 0, Side.SERVER);
        network_channel.registerMessage(MessageODC.Handler.class, MessageODC.class, 0, Side.CLIENT);
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init();
    }


    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        //log.setParent(FMLLog.getLogger());
        String[] ore_names = OreDictionary.getOreNames();
        for (String name : ore_names)
        {
            if (name == null)
            {
                log.warn("null name in Ore Dictionary.");
                continue;
            }
            
            OreNameRegistry.registerOreName(name);
        }
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void remapBlock(RegistryEvent.MissingMappings<Block> event)
    {

        for (RegistryEvent.MissingMappings.Mapping<Block> mapping : event.getMappings())
        {
            String name = mapping.key.getPath();

            if (name.equals("oreconvtable"))
            {
                mapping.remap(BlockRegistry.BLOCK_ORE_CONVERTER);
            } else if (name.equals("oreautoconverter"))
            {
                mapping.remap(BlockRegistry.BLOCK_AUTO_ORE_CONVERTER);
            }
        }
    }

    @SubscribeEvent
    public void remapItem(RegistryEvent.MissingMappings<Item> event)
    {

        for (RegistryEvent.MissingMappings.Mapping<Item> mapping : event.getMappings())
        {
            String name = mapping.key.getPath();

            if (name.equals("oreconvtable"))
            {
                mapping.remap(ItemBlock.getItemFromBlock(BlockRegistry.BLOCK_ORE_CONVERTER));
            } else if (name.equals("oreautoconverter"))
            {
                mapping.remap(ItemBlock.getItemFromBlock(BlockRegistry.BLOCK_AUTO_ORE_CONVERTER));
            }
        }
    }

    @SubscribeEvent
    public void onOreDictionaryRegister(OreDictionary.OreRegisterEvent event)
    {
        OreNameRegistry.registerOreName(event.getName());
    }
}
