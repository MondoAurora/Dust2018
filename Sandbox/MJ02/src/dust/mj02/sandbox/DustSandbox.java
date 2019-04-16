package dust.mj02.sandbox;

import dust.mj02.dust.Dust;
import dust.mj02.dust.DustUtils;
import dust.mj02.sandbox.http.HttpComponents;
import dust.mj02.sandbox.http.HttpPinger;

public class DustSandbox implements DustSandboxComponents, HttpComponents {

    private static boolean inited = false;

    public static void init() {
        if (!inited) {
            
            initHttpTest();
            initDumpListener();
//            initJsonLoader();
            
            inited = true;
        }
    }

    public static void initHttpTest() {
        DustUtils.registerService(HttpPinger.class, false, DustNetServices.NetPinger, DustProcServices.Processor);
        DustUtils.accessEntity(DataCommand.setRef, DustNetTypes.NetPinger, DustMetaLinks.TypeLinkedServices, DustNetServices.NetPinger);
    }

    public static void initDumpListener() {
        DustUtils.registerService(DustSandboxListenerDump.class, false, DustSandboxServices.SandboxChangeDump, DustProcServices.Listener);

        DustEntity listener = Dust.getEntity("Dumper");
        DustUtils.accessEntity(DataCommand.setRef, listener, DustDataLinks.EntityServices, DustSandboxServices.SandboxChangeDump);
        DustUtils.accessEntity(DataCommand.setRef, ContextRef.ctx, DustProcLinks.ContextChangeListeners, listener);
    }

//    public static void initJsonLoader() {
//        DustEntity daGenId = EntityResolver.getEntity(DustGenericAtts.IdentifiedIdLocal);
//
//        DustEntity dlGenOwner = EntityResolver.getEntity(DustGenericLinks.ConnectedOwner);
//        DustEntity dsCommStore = EntityResolver.getEntity(DustCommServices.Store);
//        DustEntity dcStoreLoad = EntityResolver.getEntity(DustCommMessages.StoreLoad);
//        DustEntity dcStoreSave = EntityResolver.getEntity(DustCommMessages.StoreSave);
//
//        DustUtils.registerService(DustSandboxJsonLoader.class, false, DustCommServices.Store);
//        DustUtils.accessEntity(DataCommand.setRef, DustCommTypes.Store, DustMetaLinks.TypeLinkedServices, DustCommServices.Store);
//
//        Dust.accessEntity(DataCommand.setValue, dsCommStore, daGenId, "DustCommStore", null);
//        Dust.accessEntity(DataCommand.setValue, dcStoreLoad, daGenId, "Load", null);
//        Dust.accessEntity(DataCommand.setRef, dcStoreLoad, dlGenOwner, dsCommStore, null);
//
//        Dust.accessEntity(DataCommand.setValue, dcStoreSave, daGenId, "Save", null);
//        Dust.accessEntity(DataCommand.setRef, dcStoreSave, dlGenOwner, dsCommStore, null);
//    }
}
