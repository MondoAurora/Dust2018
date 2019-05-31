package dust.mj02.sandbox;

import dust.mj02.dust.Dust;
import dust.mj02.dust.DustUtils;
import dust.mj02.sandbox.http.DustHttpClient;
import dust.mj02.sandbox.http.DustHttpComponents;
import dust.mj02.sandbox.http.DustHttpServerJetty;
import dust.mj02.sandbox.http.DustHttpServerCoreServices;

public class DustSandbox implements DustSandboxComponents, DustHttpComponents {

    private static boolean inited = false;

    public static void init() {
        if (!inited) {

            initHttpTest();
            initDumpListener();
            // initJsonLoader();

            initScheduler();
            initFinderTest();
            
            inited = true;
        }
    }

    public static void initFinderTest() {
        DustUtils.registerService(DustSandboxFinder.class, false, DustSandboxServices.SandboxFinder, DustProcServices.Evaluator);
        DustUtils.accessEntity(DataCommand.setRef, DustSandboxTypes.SandboxFinder, DustMetaLinks.TypeLinkedServices, DustSandboxServices.SandboxFinder);

        DustUtils.accessEntity(DataCommand.setRef, DustSandboxLinks.SandboxFinderPath, DustMetaLinks.LinkDefType, DustMetaLinkDefTypeValues.LinkDefArray);
    }

    public static void initHttpTest() {
        DustUtils.registerService(DustHttpClient.class, false, DustNetServices.NetClient, DustProcServices.Processor);
        DustUtils.accessEntity(DataCommand.setRef, DustNetTypes.NetClient, DustMetaLinks.TypeLinkedServices, DustNetServices.NetClient);

        DustUtils.registerService(DustHttpServerJetty.class, false, DustNetServices.NetServer, DustProcServices.Active);
        DustUtils.accessEntity(DataCommand.setRef, DustNetTypes.NetServer, DustMetaLinks.TypeLinkedServices, DustNetServices.NetServer);
        
        DustUtils.registerService(DustHttpServerCoreServices.HttpServletGetRef.class, false, DustNetServices.NetGetRef, DustProcServices.Processor);
        DustUtils.accessEntity(DataCommand.setRef, DustNetTypes.NetGetRef, DustMetaLinks.TypeLinkedServices, DustNetServices.NetGetRef);
        
        DustUtils.accessEntity(DataCommand.setRef, DustNetLinks.NetClientProxyEntities, DustMetaLinks.LinkDefType, DustMetaLinkDefTypeValues.LinkDefSet);
        
        DustUtils.accessEntity(DataCommand.setRef, DustNetAtts.NetProcessContextRequest, DustMetaLinks.AttDefType, DustMetaAttDefTypeValues.AttDefRaw);
        DustUtils.accessEntity(DataCommand.setRef, DustNetAtts.NetProcessContextResponse, DustMetaLinks.AttDefType, DustMetaAttDefTypeValues.AttDefRaw);
    }

    public static void initDumpListener() {
        DustUtils.registerService(DustSandboxListenerDump.class, false, DustSandboxServices.SandboxChangeDump, DustProcServices.Listener);

        DustEntity listener = Dust.getEntity("Dumper");
        DustUtils.accessEntity(DataCommand.setRef, listener, DustDataLinks.EntityServices, DustSandboxServices.SandboxChangeDump);
        DustUtils.accessEntity(DataCommand.setRef, ContextRef.session, DustProcLinks.SessionChangeListeners, listener);
    }

    public static void initScheduler() {
        DustUtils.registerService(DustSandboxScheduler.class, true, DustProcServices.Scheduler, DustProcServices.Active, DustProcServices.Listener);
        DustEntity sch = DustUtils.accessEntity(DataCommand.getEntity, DustProcTypes.Scheduler);
        DustUtils.accessEntity(DataCommand.setValue, sch, DustGenericAtts.IdentifiedIdLocal, "Default Scheduler");
   }

    // public static void initJsonLoader() {
    // DustEntity daGenId =
    // EntityResolver.getEntity(DustGenericAtts.IdentifiedIdLocal);
    //
    // DustEntity dlGenOwner =
    // EntityResolver.getEntity(DustGenericLinks.ConnectedOwner);
    // DustEntity dsCommStore = EntityResolver.getEntity(DustCommServices.Store);
    // DustEntity dcStoreLoad =
    // EntityResolver.getEntity(DustCommMessages.StoreLoad);
    // DustEntity dcStoreSave =
    // EntityResolver.getEntity(DustCommMessages.StoreSave);
    //
    // DustUtils.registerService(DustSandboxJsonLoader.class, false,
    // DustCommServices.Store);
    // DustUtils.accessEntity(DataCommand.setRef, DustCommTypes.Store,
    // DustMetaLinks.TypeLinkedServices, DustCommServices.Store);
    //
    // Dust.accessEntity(DataCommand.setValue, dsCommStore, daGenId,
    // "DustCommStore", null);
    // Dust.accessEntity(DataCommand.setValue, dcStoreLoad, daGenId, "Load", null);
    // Dust.accessEntity(DataCommand.setRef, dcStoreLoad, dlGenOwner, dsCommStore,
    // null);
    //
    // Dust.accessEntity(DataCommand.setValue, dcStoreSave, daGenId, "Save", null);
    // Dust.accessEntity(DataCommand.setRef, dcStoreSave, dlGenOwner, dsCommStore,
    // null);
    // }
}
