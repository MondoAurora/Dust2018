package dust.mj02.sandbox;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import dust.mj02.dust.Dust;
import dust.mj02.dust.DustUtils;
import dust.mj02.dust.knowledge.DustCommComponents;
import dust.mj02.dust.knowledge.DustCommDiscussion;
import dust.mj02.dust.knowledge.DustCommJsonLoader;

public class DustSandboxJsonLoader implements DustSandboxComponents, DustCommComponents.DustCommStore {
	
	private static DustEntity DA_STREAM_FILENAME = EntityResolver.getEntity(DustGenericAtts.streamFileName);
	private static boolean inited = false;
	SimpleDateFormat sdf = new SimpleDateFormat("_yyyyMMdd_HHmmss");
	
	DustCommComponents.DustCommSource rdr = new DustCommJsonLoader();
	DustCommDiscussion disc = new DustCommDiscussion();

	@Override
	public void dustCommStoreLoad() throws Exception {
		String fileName = Dust.accessEntity(DataCommand.getValue, ContextRef.self, DA_STREAM_FILENAME, null, null);
		disc.load(rdr, fileName);
		
		DustEntity eAttDef = EntityResolver.getEntity(DustMetaTypes.AttDef);
		DustEntity eLinkDef = EntityResolver.getEntity(DustMetaTypes.LinkDef);
		
		Dust.processRefs(new RefProcessor() {
			@Override
			public void processRef(DustRef ref) {
				DustEntity src = ref.get(RefKey.source);
				
				DustRef r = DustUtils.accessEntity(DataCommand.getValue, src, DustDataLinks.EntityPrimaryType);
				
				if ( null != r ) {
					DustEntity t = r.get(RefKey.target);
					if ( eAttDef == t ) {
						DustUtils.accessEntity(DataCommand.setRef, src, DustMetaLinks.AttDefParent, ref.get(RefKey.target));
					} else if ( eLinkDef == t ) {
						DustUtils.accessEntity(DataCommand.setRef, src, DustMetaLinks.LinkDefParent, ref.get(RefKey.target));
					}
				}
			}
		}, null, EntityResolver.getEntity(DustGenericLinks.Owner), null);
		
	}
	
	@Override
	public void dustCommStoreSave() throws Exception {
		String fileName = Dust.accessEntity(DataCommand.getValue, ContextRef.self, DA_STREAM_FILENAME, null, null);
		
		int dot = fileName.lastIndexOf(".");
		String fn = fileName.substring(0, dot) + sdf.format(new Date()) + fileName.substring(dot);
		
		DustEntity ldStore = EntityResolver.getEntity(DustCommLinks.TermStore);
		Set<DustEntity> toSave = new HashSet<>();

		Dust.processRefs(new RefProcessor() {
			@Override
			public void processRef(DustRef ref) {
				toSave.add(ref.get(RefKey.source));
			}
		}, null, ldStore, ContextRef.self);
		
		disc.save(rdr, fn, toSave);

	}
	
	public static void init() {
		if ( !inited ) {
			DustEntity daGenId = EntityResolver.getEntity(DustGenericAtts.identifiedIdLocal);
			
			DustEntity daBinObj = EntityResolver.getEntity(DustProcAtts.BinaryObjectName);
			DustEntity dlBinImplSvc = EntityResolver.getEntity(DustProcLinks.BinaryImplementedServices);
			DustEntity dlCtxBin = EntityResolver.getEntity(DustProcLinks.ContextBinaryAssignments);

			DustEntity dlGenOwner = EntityResolver.getEntity(DustGenericLinks.Owner);
			DustEntity dsCommStore = EntityResolver.getEntity(DustCommServices.Store);
			DustEntity dcStoreLoad = EntityResolver.getEntity(DustCommMessages.StoreLoad);
			DustEntity dcStoreSave = EntityResolver.getEntity(DustCommMessages.StoreSave);

			String cName = DustSandboxJsonLoader.class.getName();
			DustEntity ba = Dust.getEntity("BinaryAssignment: " + cName);
			
			Dust.accessEntity(DataCommand.setValue, ba, daBinObj, cName, null);
			Dust.accessEntity(DataCommand.setRef, ba, dlBinImplSvc, dsCommStore, null);

			Dust.accessEntity(DataCommand.setRef, ContextRef.ctx, dlCtxBin, ba, null);

			Dust.accessEntity(DataCommand.setValue, dsCommStore, daGenId, "DustCommStore", null);
			Dust.accessEntity(DataCommand.setValue, dcStoreLoad, daGenId, "Load", null);
			Dust.accessEntity(DataCommand.setRef, dcStoreLoad, dlGenOwner, dsCommStore, null);

			Dust.accessEntity(DataCommand.setValue, dcStoreSave, daGenId, "Save", null);
			Dust.accessEntity(DataCommand.setRef, dcStoreSave, dlGenOwner, dsCommStore, null);

			inited = true;
		}
	}
}
