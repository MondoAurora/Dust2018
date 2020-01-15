package dust.mj02.sandbox;

import dust.mj02.dust.Dust;
import dust.mj02.dust.DustUtils;
import dust.mj02.dust.geometry.DustGeometryComponents;
import dust.mj02.dust.geometry.DustGeometryCoreServices;
import dust.mj02.sandbox.development.DustDevelopmentUtils;
import dust.mj02.sandbox.http.DustHttpClient;
import dust.mj02.sandbox.http.DustHttpComponents;
import dust.mj02.sandbox.http.DustHttpServerCoreServices;
import dust.mj02.sandbox.http.DustHttpServerJetty;
import dust.mj02.sandbox.jdbc.DustJdbcComponents;
import dust.mj02.sandbox.jdbc.DustJdbcConnector;

public class DustSandbox implements DustSandboxComponents, DustHttpComponents, DustGeometryComponents, DustJdbcComponents {

    private static boolean inited = false;

    public static void init() {
        if (!inited) {

            initHttpTest();
//            initDumpListener();
            // initJsonLoader();
            
            initJdbc();
            
            initScheduler();
            initFinderTest();
            
            initSrcGen();
            
            initDraw();
            
            DustDevelopmentUtils.init();
            
            inited = true;
        }
    }

    public static void initDraw() {
//        DustUtils.registerService(DustGeometryCoreServices.DustRenderSourceSimple.class, false, DustGeometryServices.RenderSource, DustProcServices.Processor);
//        DustUtils.accessEntity(DataCommand.setRef, DustGeometryTypes.RenderSource, DustMetaLinks.TypeLinkedServices, DustGeometryServices.RenderSource);

        DustUtils.registerService(DustGeometryCoreServices.DustRenderSourceSimple.class, false, DustGeometryServices.RenderSourceSimple, DustProcServices.Processor);
        DustUtils.accessEntity(DataCommand.setRef, DustGeometryTypes.RenderSource, DustMetaLinks.TypeLinkedServices, DustGeometryServices.RenderSourceSimple);
        DustUtils.accessEntity(DataCommand.setRef, DustGeometryTypes.ShapePath, DustMetaLinks.TypeLinkedServices, DustGeometryServices.RenderSourceSimple);
        DustUtils.accessEntity(DataCommand.setRef, DustGeometryTypes.ShapeArc, DustMetaLinks.TypeLinkedServices, DustGeometryServices.RenderSourceSimple);
        DustUtils.accessEntity(DataCommand.setRef, DustGeometryTypes.ShapeBox, DustMetaLinks.TypeLinkedServices, DustGeometryServices.RenderSourceSimple);

        DustUtils.registerService(DustGeometryCoreServices.DustRenderSourceComposite.class, false, DustGeometryServices.RenderSourceComposite, DustProcServices.Processor);
        DustUtils.accessEntity(DataCommand.setRef, DustGeometryTypes.ShapeComposite, DustMetaLinks.TypeLinkedServices, DustGeometryServices.RenderSourceComposite);

        
        DustUtils.registerService(DustGeometryCoreServices.DustRenderTargetAwtGeom.class, false, DustGeometryServices.RenderTarget, DustProcServices.Processor, DustProcServices.Evaluator, DustProcServices.Active);
        DustUtils.accessEntity(DataCommand.setRef, DustGeometryTypes.RenderTarget, DustMetaLinks.TypeLinkedServices, DustGeometryServices.RenderTarget);
    }

    public static void initFinderTest() {
        DustUtils.registerService(DustSandboxFinder.class, false, DustSandboxServices.SandboxFinder, DustProcServices.Evaluator);
        DustUtils.accessEntity(DataCommand.setRef, DustSandboxTypes.SandboxFinder, DustMetaLinks.TypeLinkedServices, DustSandboxServices.SandboxFinder);

        DustUtils.accessEntity(DataCommand.setRef, DustSandboxLinks.SandboxFinderPath, DustMetaLinks.LinkDefType, DustMetaLinkDefTypeValues.LinkDefArray);
    }

    public static void initJdbc() {
        DustUtils.registerService(DustJdbcConnector.class, false, DustJdbcServices.JdbcConnector, DustProcServices.Processor, DustProcServices.Evaluator, DustProcServices.Active, DustProcServices.Listener);
        DustUtils.accessEntity(DataCommand.setRef, DustJdbcTypes.JdbcConnector, DustMetaLinks.TypeLinkedServices, DustJdbcServices.JdbcConnector);
    }

    public static void initSrcGen() {
        DustUtils.registerService(DustSandboxSrcGen.class, false, DustSandboxServices.SandboxSrcGen, DustProcServices.Processor);
        DustUtils.accessEntity(DataCommand.setRef, DustSandboxTypes.SandboxSrcGen, DustMetaLinks.TypeLinkedServices, DustSandboxServices.SandboxSrcGen);

        DustUtils.accessEntity(DataCommand.setRef, DustSandboxLinks.SandboxSrcGenUnits, DustMetaLinks.LinkDefType, DustMetaLinkDefTypeValues.LinkDefSet);
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

}
