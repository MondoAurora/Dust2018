package dust.mj02.montru.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import dust.mj02.dust.Dust;
import dust.mj02.dust.knowledge.DustProcComponents;
import dust.utils.DustUtilsFactory;


public class MontruGuiEditorModel implements MontruGuiComponents, DustProcComponents.DustProcChangeListener, MontruGuiComponents.GuiEditorModel {
	
	DustUtilsFactory<DustEntity, GuiEntityInfo> factEntityInfo = new DustUtilsFactory<DustEntity, GuiEntityInfo>(false) {
		@Override
		protected GuiEntityInfo create(DustEntity key, Object... hints) {
			GuiEntityInfo ret = new GuiEntityInfo();
			ret.put(GuiEntityKey.entity, key);

			String id = Dust.accessEntity(DataCommand.getValue, key,
					EntityResolver.getEntity(DustGenericAtts.identifiedIdLocal), null, null);

			ret.put(GuiEntityKey.id, id);

			return ret;
		}
	};
	
	DustUtilsFactory<DustRef, GuiRefInfo> factRefs = new DustUtilsFactory<DustRef, GuiRefInfo>(false) {
		@Override
		protected GuiRefInfo create(DustRef ref, Object... hints) {
			return new GuiRefInfo(MontruGuiEditorModel.this, ref);
		}
	};
	
	ArrayList<GuiEntityInfo> arrTypes = new ArrayList<>();

	
	public GuiEntityInfo getEntityInfo(DustEntity entity) {
		return factEntityInfo.get(entity);
	}
	
	@Override
	public GuiRefInfo getRefInfo(DustRef ref) {
		return factRefs.get(ref);
	}
	
	@Override
	public Iterable<GuiEntityInfo> getAllEntities() {
		return factEntityInfo.values();
	}

	@Override
	public Iterable<GuiRefInfo> getAllRefs() {
		return factRefs.values();
	}
	
	@Override
	public ArrayList<GuiEntityInfo> getAllTypes() {
		return arrTypes;
	}

	public void refreshData() {
		arrTypes.clear();

		DustEntity eldPrimaryType = EntityResolver.getEntity(DustDataLinks.EntityPrimaryType);
		DustEntity eldEntityModels = EntityResolver.getEntity(DustDataLinks.EntityModels);

		Dust.processRefs(new RefProcessor() {
			@Override
			public void processRef(DustRef ref) {
				GuiRefInfo ri = factRefs.get(ref);

				GuiEntityInfo eiSource = ri.get(GuiRefKey.source);
				GuiEntityInfo eiTarget = ri.get(GuiRefKey.target);

				Object ld = ri.getEntity(GuiRefKey.linkDef);				
				if (ld == eldPrimaryType) {
					eiSource.put(GuiEntityKey.type, eiTarget);
				} else if (ld == eldEntityModels) {
					eiSource.add(GuiEntityKey.models, eiTarget);
				}
			}
		}, null, null, null);

		Map<GuiEntityInfo, DustMetaTypes> mapMeta = new HashMap<>();
		for (DustMetaTypes dmt : DustMetaTypes.values()) {
			mapMeta.put(factEntityInfo.get(EntityResolver.getEntity(dmt)), dmt);
		}
		GuiEntityInfo eiGenericOwner = factEntityInfo.get(EntityResolver.getEntity(DustGenericLinks.Owner));

		Dust.processEntities(new EntityProcessor() {
			@Override
			public void processEntity(Object key, DustEntity entity) {
				GuiEntityInfo ei = factEntityInfo.get(entity);
				GuiEntityInfo eiType = ei.get(GuiEntityKey.type);
				DustMetaTypes dmt = mapMeta.get(eiType);

				GuiEntityKey ekInfo = null;

				if (null != dmt) {
					switch (dmt) {
					case Type:
						arrTypes.add(ei);
						break;
					case AttDef:
						ekInfo = GuiEntityKey.attDefs;
						break;
					case LinkDef:
						ekInfo = GuiEntityKey.linkDefs;
						break;
					default:
						break;
					}
				}

				if (null != ekInfo) {
					for (GuiRefInfo ri : getAllRefs()) {
						if ((eiGenericOwner == ri.get(GuiRefKey.linkDef)) && (ei == ri.get(GuiRefKey.source))) {
							GuiEntityInfo eiOwnerType = ri.get(GuiRefKey.target);
							eiOwnerType.add(ekInfo, ei);
						}
					}
				}
			}
		});
	}
	
	@Override
	public Iterable<GuiEntityInfo> dropRefs(Iterable<GuiRefInfo> refs) {
		Set<GuiEntityInfo> changedEntities = new HashSet<>();
		
		for (GuiRefInfo ri : refs) {
			ri.remove();
			factRefs.drop(ri);
			changedEntities.add(ri.get(GuiRefKey.source));
			changedEntities.add(ri.get(GuiRefKey.target));
		}
	
		return changedEntities;
	}
	
	public void updateTypeStructure() {
		GuiEntityKey[] refkeys = {GuiEntityKey.attDefs, GuiEntityKey.linkDefs};
		
		for (GuiEntityInfo ti : arrTypes) {
			for (GuiEntityKey k : refkeys) {
				Object r = ti.get(k);
				if (r instanceof Collection<?>) {
					((Collection<?>) r).clear();
				}
			}
		}

		for (GuiRefInfo ri : getAllRefs()) {
			GuiEntityInfo is = ri.get(GuiRefKey.source);
			GuiEntityInfo it = ri.get(GuiRefKey.target);
			GuiEntityInfo sType = is.get(GuiEntityKey.type);
			
			if ((null != sType) && arrTypes.contains(it)) {
				Object tk = EntityResolver.getKey(sType.get(GuiEntityKey.entity));
				
				if ( DustMetaTypes.AttDef == tk ) {
					it.add(GuiEntityKey.attDefs, is);
				} else if ( DustMetaTypes.LinkDef == tk ) {
					it.add(GuiEntityKey.linkDefs, is);
				} 
			}
		}
	}
	
	@Override
	public void dustProcChangedAttribute(DustEntity entity, DustEntity att, Object value) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dustProcChangedRef(DustEntity entity, DustRef ref, DataCommand cmd) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
}
