package dust.mj02.sandbox;

import dust.mj02.dust.Dust;
import dust.mj02.dust.DustComponents;
import dust.mj02.dust.knowledge.DustCommComponents;
import dust.mj02.dust.knowledge.DustCommDiscussion;
import dust.mj02.dust.knowledge.DustCommJsonLoader;
import dust.mj02.dust.knowledge.DustMetaComponents;
import dust.mj02.dust.knowledge.DustProcComponents;
import dust.mj02.dust.tools.DustGenericComponents;
import dust.utils.DustUtilsDev;

public class DustSandboxJsonLoader implements DustComponents, DustGenericComponents, DustCommComponents.DustCommStore {
	
	private static DustEntity DA_STREAM_FILENAME = EntityResolver.getEntity(DustGenericAtts.streamFileName);
	private static boolean inited = false;
	
	DustCommComponents.DustCommSource rdr = new DustCommJsonLoader();
	DustCommDiscussion disc = new DustCommDiscussion();

	@Override
	public void dustCommStoreLoad() throws Exception {
		String fileName = Dust.accessEntity(DataCommand.getValue, ContextRef.self, DA_STREAM_FILENAME, null, null);
		disc.load(rdr, fileName);
	}
	
	@Override
	public void dustCommStoreSave() throws Exception {
		String fileName = Dust.accessEntity(DataCommand.getValue, ContextRef.self, DA_STREAM_FILENAME, null, null);
		DustUtilsDev.dump("Would save into", fileName);
	}
	
	public static void init() {
		if ( !inited ) {
			DustEntity daGenId = EntityResolver.getEntity(DustGenericComponents.DustGenericAtts.identifiedIdLocal);
			
			DustEntity daBinObj = EntityResolver.getEntity(DustProcComponents.DustProcAtts.BinaryObjectName);
			DustEntity dlBinImplSvc = EntityResolver.getEntity(DustProcComponents.DustProcLinks.BinaryImplementedServices);
			DustEntity dsCommStore = EntityResolver.getEntity(DustCommComponents.DustCommServices.Store);
			DustEntity dlCtxBin = EntityResolver.getEntity(DustProcComponents.DustProcLinks.ContextBinaryAssignments);
			DustEntity dlLinkDefType = EntityResolver.getEntity(DustMetaComponents.DustMetaLinks.LinkDefType);
			DustEntity dlLinkDefTypeSet = EntityResolver.getEntity(DustMetaComponents.DustMetaValueLinkDefType.LinkDefSet);

			DustEntity dlGenOwner = EntityResolver.getEntity(DustGenericComponents.DustGenericLinks.Owner);
			DustEntity dcStoreLoad = EntityResolver.getEntity(DustCommComponents.DustCommMessages.StoreLoad);

			String cName = DustSandboxJsonLoader.class.getName();
			DustEntity ba = Dust.getEntity("BinaryAssignment: " + cName);
			
			Dust.accessEntity(DataCommand.setRef, dlCtxBin, dlLinkDefType, dlLinkDefTypeSet, null);
			Dust.accessEntity(DataCommand.setRef, dlBinImplSvc, dlLinkDefType, dlLinkDefTypeSet, null);
			
			Dust.accessEntity(DataCommand.setValue, ba, daBinObj, cName, null);
			Dust.accessEntity(DataCommand.setRef, ba, dlBinImplSvc, dsCommStore, null);

			Dust.accessEntity(DataCommand.setRef, ContextRef.ctx, dlCtxBin, ba, null);

			Dust.accessEntity(DataCommand.setValue, dsCommStore, daGenId, "DustCommStore", null);
			Dust.accessEntity(DataCommand.setValue, dcStoreLoad, daGenId, "Load", null);
			Dust.accessEntity(DataCommand.setRef, dcStoreLoad, dlGenOwner, dsCommStore, null);

			inited = true;
		}
	}
}
