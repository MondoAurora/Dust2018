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
	
	private static DustEntity DA_STREAM_FILENAME = EntityResolver.getEntity(DustGenericAtts.StreamFileName);
	SimpleDateFormat sdf = new SimpleDateFormat("_yyyyMMdd_HHmmss");
	
	DustCommComponents.DustCommSource rdr = new DustCommJsonLoader();
	DustCommDiscussion disc = new DustCommDiscussion();

	@Override
	public void dustCommStoreLoad() throws Exception {
		String fileName = Dust.accessEntity(DataCommand.getValue, ContextRef.self, DA_STREAM_FILENAME, null, null);
		disc.load(rdr, fileName);
		
		DustUtils.accessEntity(DataCommand.setValue, ContextRef.self, DustGenericAtts.StreamFileAccess, "Loaded at " + sdf.format(new Date()));
	}
	
	@Override
	public void dustCommStoreSave() throws Exception {
		String fileName = Dust.accessEntity(DataCommand.getValue, ContextRef.self, DA_STREAM_FILENAME, null, null);
		
		int dot = fileName.lastIndexOf(".");
		String fn = fileName.substring(0, dot) + sdf.format(new Date()) + fileName.substring(dot);
		
		DustEntity ldStore = EntityResolver.getEntity(DustCommLinks.PersistentContainingUnit);
		Set<DustEntity> toSave = new HashSet<>();

		Dust.processRefs(new RefProcessor() {
			@Override
			public void processRef(DustRef ref) {
				toSave.add(ref.get(RefKey.source));
			}
		}, null, ldStore, ContextRef.self);
		
		disc.save(rdr, fn, toSave);
	}
}
