package dust.mj02.sandbox;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONObject;

import dust.mj02.dust.Dust;
import dust.mj02.dust.DustUtils;
import dust.mj02.dust.knowledge.DustKernelComponents;
import dust.utils.DustUtilsFactory;
import dust.utils.DustUtilsJava;

@SuppressWarnings("rawtypes")
public class DustSandboxPersistence implements DustKernelComponents {

	public enum TempUnit {
		Meta(DustMetaTypes.class, DustMetaAttDefTypeValues.class, DustMetaLinkDefTypeValues.class), Data(
				DustDataTypes.class), Proc(DustProcTypes.class,
						DustProcServices.class), Comm(DustCommTypes.class, DustCommServices.class),
		Generic(DustGenericTypes.class),

		;

		private static boolean inited = false;
		private final Class<?>[] keys;

		private TempUnit(Class<?>... keys) {
			this.keys = keys;
		}

		public static void optInit() {
			if (!inited) {
				for (TempUnit tu : values()) {

					DustEntity eu = getUnit(tu.name());

					for (Class<?> c : tu.keys) {
						for (Object e : c.getEnumConstants()) {
							DustEntity ee = EntityResolver.getEntity(e);
							DustUtils.accessEntity(DataCommand.setRef, ee, DustCommLinks.PersistentContainingUnit, eu);
						}
					}

					inited = true;
				}
			}
		}
	}

	public static DustEntity getUnit(Object key) {
		String name = (key instanceof Enum) ? ((Enum<?>) key).name() : (String) key;

		DustEntity eu = DustUtils.accessEntity(DataCommand.getEntity, DustCommTypes.Unit, null, name,
				new EntityProcessor() {
					@Override
					public void processEntity(DustEntity entity) {
						DustUtils.accessEntity(DataCommand.setValue, entity, DustGenericAtts.IdentifiedIdLocal, name);
					}
				});

		return eu;
	}

	static DustUtilsFactory<DustEntity, DustMetaLinkDefTypeValues> factLinkTypes = new DustUtilsFactory<DustEntity, DustMetaLinkDefTypeValues>(
			false) {
		@Override
		protected DustMetaLinkDefTypeValues create(DustEntity key, Object... hints) {
			DustMetaLinkDefTypeValues ldt = EntityResolver.getKey(DustUtils.getByPath(key, DustMetaLinks.LinkDefType));
			return (null == ldt) ? DustMetaLinkDefTypeValues.LinkDefSingle : ldt;
		}
	};

	enum ContextKeys {
		ThisUnit, CommitId, EntityUnit, EntityId
	}

	static class SaveContext {
		private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");
		static final DustEntity eEntityId = EntityResolver.getEntity(DustCommAtts.PersistentEntityId);
		static final DustEntity ePersUnit = EntityResolver.getEntity(DustCommLinks.PersistentContainingUnit);
		static final DustEntity eCommitId = EntityResolver.getEntity(DustCommAtts.PersistentCommitId);

		String commitId = SDF.format(new Date());

		Set<DustEntity> refEntitiesWithNoUnit = new HashSet<>();

		class SaveUnitContext {
			EnumMap<ContextKeys, String> ctxKeys = new EnumMap<>(ContextKeys.class);

			DustEntity myUnit;
			Set<DustEntity> refUnits = new HashSet<>();

			ArrayList<DustEntity> toSave = new ArrayList<>();

			public SaveUnitContext(DustEntity myUnit) {
				this.myUnit = myUnit;

				ctxKeys.put(ContextKeys.ThisUnit, factEntities.get(myUnit));
				ctxKeys.put(ContextKeys.CommitId, factEntities.get(eCommitId));
				ctxKeys.put(ContextKeys.EntityUnit, factEntities.get(ePersUnit));
				ctxKeys.put(ContextKeys.EntityId, factEntities.get(eEntityId));
			}

			DustUtilsFactory<String, Map<String, Object>> factResults = new DustUtilsFactory<String, Map<String, Object>>(
					false) {
				@Override
				protected Map<String, Object> create(String key, Object... hints) {
					return new HashMap<>();
				}
			};

			DustUtilsFactory<DustEntity, String> factEntities = new DustUtilsFactory<DustEntity, String>(false) {
				@Override
				protected String create(DustEntity key, Object... hints) {
					String persId = DustUtilsJava.toString(toSave.size());

					if (0 < hints.length) {
						DustUtils.accessEntity(DataCommand.setValue, key, DustCommAtts.PersistentEntityId, persId);
						DustUtils.accessEntity(DataCommand.setRef, key, DustCommLinks.PersistentContainingUnit,
								hints[0]);

						// if changed...
						DustUtils.accessEntity(DataCommand.setValue, key, DustCommAtts.PersistentCommitId, commitId);
					}

					toSave.add(key);

					return persId;
				}
			};

			void optSaveEntity(DustEntity e) {
				DustEntity eSaveUnit = whereToSave(e);

				if (null != eSaveUnit) {
					boolean local = myUnit == eSaveUnit;
					String localId = local ? factEntities.get(e, myUnit) : factEntities.get(e);

					Map<String, Object> data = factResults.get(localId);
					data.put(ctxKeys.get(ContextKeys.EntityUnit), factEntities.get(eSaveUnit));

					if (local) {
						data.put(ctxKeys.get(ContextKeys.EntityId), localId);

						Dust.accessEntity(DataCommand.processContent, e, null, new ContentProcessor() {
							Object coll = null;

							@Override
							public void processContent(DustEntity eOwner, DustEntity eKey, Object value) {
								DustEntity uKey = whereToSave(eKey);

								if (uKey != myUnit) {
									refUnits.add(uKey);
								}
								String mapKey = factEntities.get(eKey);

								if (value instanceof DustRef) {
									DustMetaLinkDefTypeValues ldt = factLinkTypes
											.get(((DustRef) value).get(RefKey.linkDef));

									coll = DustUtils.getColl(ldt);

									if (null != coll) {
										data.put(mapKey, coll);
									}

									((DustRef) value).processAll(new RefProcessor() {
										@SuppressWarnings("unchecked")
										@Override
										public void processRef(DustRef ref) {
											String refId = factEntities.get(ref.get(RefKey.target));

											switch (ldt) {
											case LinkDefArray:
											case LinkDefSet:
												((Collection<String>) coll).add(refId);
												break;
											case LinkDefMap:
												String keyId = factEntities.get(ref.get(RefKey.key));
												((Map<String, String>) coll).put(keyId, refId);
												break;
											case LinkDefSingle:
												data.put(mapKey, refId);
												break;
											default:
												break;

											}
										}
									});
								} else {
									data.put(mapKey, (null == value) ? "" : value);
								}
							}
						}, null);
					} else {
						refUnits.add(eSaveUnit);

						if (refUnits.contains(e)) {
							data.put(ctxKeys.get(ContextKeys.EntityId),
									DustUtils.accessEntity(DataCommand.getValue, e, DustGenericAtts.IdentifiedIdLocal));
							data.put(ctxKeys.get(ContextKeys.CommitId),  commitId);
//									DustUtils.accessEntity(DataCommand.getValue, e, DustCommAtts.PersistentCommitId));
						} else {
							SaveUnitContext suc = factUnitCtx.get(eSaveUnit);
							String entityId = suc.factEntities.get(e, eSaveUnit);

							data.put(ctxKeys.get(ContextKeys.EntityId), entityId);
						}
					}
				}
			}
		}

		ArrayList<SaveUnitContext> arrUctx = new ArrayList<>();

		DustUtilsFactory<DustEntity, SaveUnitContext> factUnitCtx = new DustUtilsFactory<DustEntity, SaveUnitContext>(
				false) {
			@Override
			protected SaveUnitContext create(DustEntity key, Object... hints) {
				SaveUnitContext suc = new SaveUnitContext(key);
				arrUctx.add(suc);
				return suc;
			}
		};

		DustEntity whereToSave(DustEntity e) {
			DustEntity tu = EntityResolver.getEntity(DustCommTypes.Unit);

			if (tu == DustUtils.getByPath(e, DustDataLinks.EntityPrimaryType)) {
				return e;
			}

			DustEntity eUnit = DustUtils
					.toEntity(DustUtils.accessEntity(DataCommand.getValue, e, DustCommLinks.PersistentContainingUnit));

			if (null == eUnit) {
				DustEntity ee = DustUtils.getByPath(e, DustDataLinks.EntityPrimaryType,
						DustCommLinks.PersistentStoreWith);

				if (null != ee) {
					eUnit = whereToSave(DustUtils.getByPath(e, ee));
				}
			}

			if (null == eUnit) {
				refEntitiesWithNoUnit.add(e);
			}

			return eUnit;
		};

		public Map<String, Map> doSave() {
			Map<String, Map> ret = new HashMap<>();

			for (int u = 0; u < arrUctx.size(); ++u) {
				SaveUnitContext suc = arrUctx.get(u);
				String uid = DustUtils.accessEntity(DataCommand.getValue, suc.myUnit,
						DustGenericAtts.IdentifiedIdLocal);

				for (int i = 0; i < suc.toSave.size(); ++i) {
					suc.optSaveEntity(suc.toSave.get(i));
				}

				Map<String, Object> sm = new HashMap<>();
				sm.put("header", suc.ctxKeys);
				sm.put("data", suc.factResults.copyShallow(null));

				ret.put(uid, sm);
			}

			return ret;
		}
	}

	public static void update() {
		TempUnit.optInit();
	}

	public static void commit() {
		TempUnit.optInit();

		SaveContext svctx = new SaveContext();

		DustEntity tu = EntityResolver.getEntity(DustCommTypes.Unit);

		Dust.processEntities(new EntityProcessor() {

			@Override
			public void processEntity(DustEntity entity) {
				if (tu == DustUtils.getByPath(entity, DustDataLinks.EntityPrimaryType)) {
					svctx.factUnitCtx.get(entity).toSave.add(entity);
				}
			}
		});

		Map<String, Map> result = svctx.doSave();

		File dirHistory = new File("output/persistence/history");
		dirHistory.mkdirs();

		File dirPers = dirHistory.getParentFile();

		for (Map.Entry<String, Map> r : result.entrySet()) {
			FileWriter fw;
			String key = r.getKey();
			try {
				File file = new File(dirPers, key + ".json");
				fw = new FileWriter(file);
				JSONObject.writeJSONString(r.getValue(), fw);
				fw.flush();
				fw.close();
				
				Files.copy(file.toPath(), new File(dirHistory, key + "_" + svctx.commitId + ".json").toPath());
			} catch (IOException e) {
				Dust.wrapAndRethrowException("Saving " + key, e);
			}
		}

	}

}
