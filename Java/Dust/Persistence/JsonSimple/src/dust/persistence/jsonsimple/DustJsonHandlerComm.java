package dust.persistence.jsonsimple;

import java.io.IOException;
import java.util.EnumMap;

import org.json.simple.parser.ParseException;

import dust.gen.knowledge.comm.DustKnowledgeCommComponents;
import dust.gen.knowledge.meta.DustKnowledgeMetaComponents;
import dust.gen.knowledge.proc.DustKnowledgeProcComponents;
import dust.pub.Dust;
import dust.pub.DustUtilsJava;

class DustJsonHandlerComm extends DustJsonComponents.ContentHandlerDefault implements DustJsonComponents,
		DustKnowledgeProcComponents, DustKnowledgeMetaComponents, DustKnowledgeCommComponents {

	private static final EnumMap<DustConstKnowledgeCommStatementType, DustConstKnowledgeMetaCardinality> TYPE_CARD_MAP = new EnumMap<>(
			DustConstKnowledgeCommStatementType.class);

	static {
		TYPE_CARD_MAP.put(DustConstKnowledgeCommStatementType.Discussion, DustConstKnowledgeMetaCardinality.Single);
		TYPE_CARD_MAP.put(DustConstKnowledgeCommStatementType.Entity, DustConstKnowledgeMetaCardinality.Array);
		TYPE_CARD_MAP.put(DustConstKnowledgeCommStatementType.Model, DustConstKnowledgeMetaCardinality.Map);
		TYPE_CARD_MAP.put(DustConstKnowledgeCommStatementType.Data, DustConstKnowledgeMetaCardinality.Single);
	}

	DustConstKnowledgeCommStatementType state = null;

	int idx = -1;
	String key;
	Object value;

	DustEntity msg;

	void talkInit() {
		state = DustConstKnowledgeCommStatementType.Discussion;
		msg = Dust.getRefEntity(DustConstKnowledgeInfoContext.Self, true, DustLinkToolsGenericChain.DefaultMessage,
				null);
	}

	void talkRelease() {
		state = null;
		msg = null;
	}

	void msgInit(DustEntity cmd) {
		Dust.modifyRefs(DustConstKnowledgeInfoLinkCommand.Replace, msg, DustLinkKnowledgeProcMessage.Command, cmd);

		Dust.modifyRefs(DustConstKnowledgeInfoLinkCommand.Replace, msg, DustLinkKnowledgeCommStatement.Type, state);

		Dust.setAttrValue(msg, DustAttributeKnowledgeInfoVariant.value, null);
		Dust.setAttrValue(msg, DustAttributeToolsGenericIdentified.idLocal, null);

		Dust.modifyRefs(DustConstKnowledgeInfoLinkCommand.Remove, msg, DustLinkKnowledgeInfoIterator.Cardinality, null);
		Dust.setAttrValue(msg, DustAttributeKnowledgeInfoIterator.index, null);
		Dust.setAttrValue(msg, DustAttributeKnowledgeInfoIterator.key, null);
	}

	void doSend() {
		Dust.send(msg);
	}

	void sendValue() {
		msgInit(DustCommandKnowledgeProcVisitor.Visit);

		Dust.setAttrValue(msg, DustAttributeKnowledgeInfoVariant.value, value);
		Dust.setAttrValue(msg, DustAttributeToolsGenericIdentified.idLocal, key);

		doSend();
	}

	void sendBlock(boolean begin) {
		msgInit(begin ? DustCommandKnowledgeProcProcessor.Begin : DustCommandKnowledgeProcProcessor.End);

		if (begin) {
			Dust.modifyRefs(DustConstKnowledgeInfoLinkCommand.Replace, msg, DustLinkKnowledgeInfoIterator.Cardinality, TYPE_CARD_MAP.get(state));
			Dust.setAttrValue(msg, DustAttributeKnowledgeInfoIterator.index, idx);
			Dust.setAttrValue(msg, DustAttributeKnowledgeInfoIterator.key, key);
//			DustUtilsDev.dump("Start block...", state,
//					(state == DustConstKnowledgeCommStatementType.Entity) ? idx : key);
//		} else {
//			DustUtilsDev.dump("End block...", state);
		}
		doSend();
	}

	@Override
	public boolean startArray() throws ParseException, IOException {
		talkInit();
		sendBlock(true);
		return true;
	}

	@Override
	public boolean endArray() throws ParseException, IOException {
		sendBlock(false);
		talkRelease();
		return true;
	}

	@Override
	public boolean startObject() throws ParseException, IOException {
		state = DustUtilsJava.shiftEnum(state, true, false);
		if (state == DustConstKnowledgeCommStatementType.Entity) {
			++idx;
		}
		sendBlock(true);

		return true;
	}

	@Override
	public boolean endObject() throws ParseException, IOException {
		sendBlock(false);
		state = DustUtilsJava.shiftEnum(state, false, false);
		return true;
	}

	@Override
	public boolean startObjectEntry(String key) throws ParseException, IOException {
		this.key = key;
		return true;
	}

	@Override
	public boolean endObjectEntry() throws ParseException, IOException {
		this.key = null;
		return true;
	}

	@Override
	public boolean primitive(Object value) throws ParseException, IOException {
		this.value = value;
		state = DustConstKnowledgeCommStatementType.Data;

		sendValue();
		state = DustConstKnowledgeCommStatementType.Model;

		return true;
	}
}