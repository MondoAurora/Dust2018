package dust.persistence.jsonsimple;

import java.io.IOException;

import org.json.simple.parser.ParseException;

import dust.gen.knowledge.comm.DustKnowledgeCommComponents;
import dust.gen.knowledge.meta.DustKnowledgeMetaComponents;
import dust.gen.knowledge.proc.DustKnowledgeProcComponents;
import dust.pub.Dust;
import dust.pub.DustUtilsJava;

class DustJsonHandlerComm extends DustJsonComponents.ContentHandlerDefault implements DustJsonComponents,
		DustKnowledgeProcComponents, DustKnowledgeMetaComponents, DustKnowledgeCommComponents {

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

	void sendBlock(boolean begin, DustConstKnowledgeMetaCardinality card) {
		msgInit(begin ? DustCommandKnowledgeProcProcessor.Begin : DustCommandKnowledgeProcProcessor.End);

		if (begin) {
			Dust.modifyRefs(DustConstKnowledgeInfoLinkCommand.Replace, msg, DustLinkKnowledgeInfoIterator.Cardinality, card);
			Dust.setAttrValue(msg, DustAttributeKnowledgeInfoIterator.index, idx);
			Dust.setAttrValue(msg, DustAttributeKnowledgeInfoIterator.key, key);
		}
		doSend();
	}

	@Override
	public boolean startArray() throws ParseException, IOException {
		if (null == state) {
			talkInit();
		} else {
			state = DustUtilsJava.shiftEnum(state, true, false);
		}
		sendBlock(true, DustConstKnowledgeMetaCardinality.Array);
		return true;
	}

	@Override
	public boolean endArray() throws ParseException, IOException {
		sendBlock(false, DustConstKnowledgeMetaCardinality.Array);
		if (state == DustConstKnowledgeCommStatementType.Discussion) {
			talkRelease();
		} else {
			state = DustUtilsJava.shiftEnum(state, false, false);
		}
		return true;
	}

	@Override
	public boolean startObject() throws ParseException, IOException {
		state = DustUtilsJava.shiftEnum(state, true, false);
		if (state == DustConstKnowledgeCommStatementType.Entity) {
			++idx;
		}
		sendBlock(true, DustConstKnowledgeMetaCardinality.Map);

		return true;
	}

	@Override
	public boolean endObject() throws ParseException, IOException {
		sendBlock(false, DustConstKnowledgeMetaCardinality.Map);
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
		sendValue();
		return true;
	}
}