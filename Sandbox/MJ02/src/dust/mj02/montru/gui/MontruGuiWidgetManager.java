package dust.mj02.montru.gui;

import java.util.HashSet;
import java.util.Set;

import dust.mj02.dust.knowledge.DustProcComponents;
import dust.utils.DustUtilsFactory;

public abstract class MontruGuiWidgetManager<BaseType> implements MontruGuiComponents,
		DustProcComponents.DustProcChangeListener, MontruGuiComponents.GuiChangeListener {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	class WidgetUpdater implements GuiChangeListener {
		GuiEntityInfo myEI;

		Set<GuiEntityElement> entityPanels = new HashSet<>();
		DustUtilsFactory<GuiEntityInfo, Set<GuiEntityDataElement>> factDataWidgets = new DustUtilsFactory<GuiEntityInfo, Set<GuiEntityDataElement>>(
				false) {
			@Override
			protected Set<GuiEntityDataElement> create(GuiEntityInfo key, Object... hints) {
				return new HashSet<>();
			}
		};

		public WidgetUpdater(GuiEntityInfo myEI) {
			this.myEI = myEI;
		}

		<WT> WT createWidget(WidgetType wt, GuiEntityInfo eEntity, GuiEntityInfo eData) {
			if (null == eData) {
				DustEntity dea = EntityResolver.getEntity(DustGenericAtts.identifiedIdLocal);
				eData = editorModel.getEntityInfo(dea);
			}
			WT ret = createWidgetInt(wt, eEntity, eData);

			Set set = (wt == WidgetType.entityPanel) ? entityPanels : factDataWidgets.get(eData);
			set.add(ret);

			return ret;
		}

		@Override
		public void guiChangedAttribute(GuiEntityInfo entity, GuiEntityInfo att, Object value) {
			Set<GuiEntityDataElement> ds = factDataWidgets.peek(att);

			if (null != ds) {
				for (GuiEntityDataElement de : ds) {
					guiChangedAttributeInt(false, de, entity, att, value);
				}
			}

			for (GuiEntityElement de : entityPanels) {
				guiChangedAttributeInt(false, de, entity, att, value);
			}
		}

		@Override
		public void guiChangedRef(GuiEntityInfo entity, GuiRefInfo ref, DataCommand cmd) {
			Set<GuiEntityDataElement> ds = factDataWidgets.peek(ref.get(GuiRefKey.linkDef));

			if (null != ds) {
				for (GuiEntityDataElement de : ds) {
					guiChangedRefInt(false, de, entity, ref, cmd);
				}
			}

			for (GuiEntityElement de : entityPanels) {
				guiChangedRefInt(false, de, entity, ref, cmd);
			}
		}

	}

	private final DustUtilsFactory<GuiEntityInfo, WidgetUpdater> factWidgetUpdaters = new DustUtilsFactory<GuiEntityInfo, WidgetUpdater>(
			false) {
		@Override
		protected WidgetUpdater create(GuiEntityInfo key, Object... hints) {
			return new WidgetUpdater(key);
		}
	};

	protected final GuiEditorModel editorModel;
	
	protected Object updateSource;

	
	protected GuiEntityInfo dragEntityInfo;
	protected boolean dragIsType;
	protected GuiEntityElement dragTarget;


	public MontruGuiWidgetManager(GuiEditorModel editorModel) {
		super();
		this.editorModel = editorModel;
	}

	protected abstract <WT> WT createWidgetInt(WidgetType wt, GuiEntityInfo eEntity, GuiEntityInfo eData);

	protected void guiChangedAttributeInt(boolean panel, GuiEntityElement e, GuiEntityInfo entity, GuiEntityInfo att,
			Object value) {
		e.guiChangedAttribute(entity, att, value);
	}

	protected void guiChangedRefInt(boolean panel, GuiEntityElement e, GuiEntityInfo entity, GuiRefInfo ref,
			DataCommand cmd) {
		e.guiChangedRef(entity, ref, cmd);
	}

	public <WT extends BaseType> WT createWidget(WidgetType wt, GuiEntityInfo eEntity, GuiEntityInfo eData) {
		return factWidgetUpdaters.get(eEntity).createWidget(wt, eEntity, eData);
	}

	@Override
	public void guiChangedAttribute(GuiEntityInfo entity, GuiEntityInfo att, Object value) {
		WidgetUpdater wu = factWidgetUpdaters.peek(entity);

		if (null != wu) {
			wu.guiChangedAttribute(entity, att, value);
		}
	}

	@Override
	public void guiChangedRef(GuiEntityInfo entity, GuiRefInfo ref, DataCommand cmd) {
		WidgetUpdater wu = factWidgetUpdaters.peek(entity);

		if (null != wu) {
			wu.guiChangedRef(entity, ref, cmd);
		}
	}

	@Override
	public void dustProcChangedAttribute(DustEntity entity, DustEntity att, Object value) throws Exception {
		// TODO later translate to Gui calls
	}

	@Override
	public void dustProcChangedRef(DustEntity entity, DustRef ref, DataCommand cmd) throws Exception {
		// TODO later translate to Gui calls
	}
}
