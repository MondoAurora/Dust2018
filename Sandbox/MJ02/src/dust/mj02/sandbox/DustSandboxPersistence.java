package dust.mj02.sandbox;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import dust.mj02.dust.Dust;
import dust.mj02.dust.DustUtils;
import dust.mj02.dust.knowledge.DustKernelComponents;
import dust.mj02.sandbox.DustSandboxPersistence.SaveContext.SaveUnitContext;
import dust.utils.DustUtilsDev;
import dust.utils.DustUtilsFactory;
import dust.utils.DustUtilsJava;

@SuppressWarnings("rawtypes")
public class DustSandboxPersistence implements DustKernelComponents {

	public enum TempUnit {
		Meta(DustMetaTypes.class, DustMetaAttDefTypeValues.class, DustMetaLinkDefTypeValues.class), Data(
				DustDataTypes.class), Proc(DustProcTypes.class,
						DustProcServices.class), Comm(DustCommTypes.class, DustCommServices.class),

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
			return EntityResolver.getKey(DustUtils.getByPath(key, DustMetaLinks.LinkDefType));
		}
	};

	static class SaveContext {
		private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");
		static final DustEntity eEntityId = EntityResolver.getEntity(DustCommAtts.PersistentEntityId);
		static final DustEntity ePersUnit = EntityResolver.getEntity(DustCommLinks.PersistentContainingUnit);

		String commitId = SDF.format(new Date());
		String keyLinkUnit;
		String keyAttEntityId;
		String keyThisUnit;

		Set<DustEntity> refEntitiesWithNoUnit = new HashSet<>();

		class SaveUnitContext {
			DustEntity myUnit;
			Set<DustEntity> refUnits = new HashSet<>();
			long id = 0;

			LinkedList<DustEntity> toSave = new LinkedList<>();
			Set<DustEntity> saved = new HashSet<>();

			public SaveUnitContext(DustEntity myUnit) {
				this.myUnit = myUnit;

				keyLinkUnit = factEntities.get(ePersUnit);
				keyAttEntityId = factEntities.get(eEntityId);
				keyThisUnit = factEntities.get(myUnit);
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
					String persId = DustUtilsJava.toString(id++);

					if (0 < hints.length) {
						DustUtils.accessEntity(DataCommand.setValue, key, DustCommAtts.PersistentEntityId, persId);
						DustUtils.accessEntity(DataCommand.setRef, key, DustCommLinks.PersistentContainingUnit,
								hints[0]);

						// if changed...
						DustUtils.accessEntity(DataCommand.setValue, key, DustCommAtts.PersistentCommitId, commitId);
					}

					if (!saved.contains(key)) {
						toSave.add(key);
					}

					return persId;
				}
			};

			void optSaveEntity(DustEntity e) {
				DustEntity eSaveUnit = whereToSave(e);

				if (null != eSaveUnit) {
					boolean local = myUnit == eSaveUnit;
					String localId = local ? factEntities.get(e, myUnit) : factEntities.get(e);

					Map<String, Object> data = factResults.get(localId);
					data.put(keyLinkUnit, factEntities.get(eSaveUnit));

					if (local) {
						data.put(keyAttEntityId, localId);

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

									coll = null;
									switch (ldt) {
									case LinkDefArray:
										coll = new ArrayList<String>();
										break;
									case LinkDefMap:
										coll = new HashMap<String, String>();
										break;
									case LinkDefSet:
										coll = new HashSet<String>();
										break;
									case LinkDefSingle:
										break;
									}

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
									data.put(mapKey, value);
								}
							}
						}, null);
					} else {
						refUnits.add(eSaveUnit);
						SaveUnitContext suc = factUnitCtx.get(eSaveUnit);
						String entityId = suc.factEntities.get(e, eSaveUnit);

						data.put(keyAttEntityId, entityId);
					}
				}
			}
		}

		DustUtilsFactory<DustEntity, SaveUnitContext> factUnitCtx = new DustUtilsFactory<DustEntity, SaveUnitContext>(
				false) {
			@Override
			protected SaveUnitContext create(DustEntity key, Object... hints) {
				return new SaveUnitContext(key);
			}
		};

		DustEntity whereToSave(DustEntity e) {
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

			for (SaveUnitContext suc : factUnitCtx.values()) {
				String uid = DustUtils.accessEntity(DataCommand.getValue, suc.myUnit,
						DustGenericAtts.IdentifiedIdLocal);

				for (int i = 0; i < suc.toSave.size(); ++i) {
					suc.optSaveEntity(suc.toSave.get(i));
				}

				ret.put(uid, suc.factResults.copyShallow(null));
			}

			return ret;
		}
	}

	public static void loadTempUnits() {
		TempUnit.optInit();
	}

	public static void saveTempUnits(Object... unitNames) {
		TempUnit.optInit();

		SaveContext svctx = new SaveContext();

		for (Object o : unitNames) {
			DustEntity eu = getUnit(o);
			SaveUnitContext suc = svctx.factUnitCtx.get(eu);

			DustUtils.accessEntity(DataCommand.processRef, eu, DustCommLinks.UnitEntities, new RefProcessor() {
				@Override
				public void processRef(DustRef ref) {
					suc.optSaveEntity(ref.get(RefKey.target));
				}
			});
		}

		Map<String, Map> result = svctx.doSave();

		StringBuilder sb = new StringBuilder();
		for (Map.Entry<String, Map> r : result.entrySet()) {
			DustUtilsJava.toStringBuilder(sb, r.getValue().entrySet(), true, r.getKey());
		}
		
		DustUtilsDev.dump("Save result", sb);
	}

}
