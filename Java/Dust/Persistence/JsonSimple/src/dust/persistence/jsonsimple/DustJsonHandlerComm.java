package dust.persistence.jsonsimple;

import java.io.IOException;

import org.json.simple.parser.ParseException;

import dust.gen.knowledge.comm.DustKnowledgeCommComponents;
import dust.gen.knowledge.meta.DustKnowledgeMetaComponents;
import dust.gen.knowledge.proc.DustKnowledgeProcComponents;
import dust.pub.Dust;
import dust.utils.DustUtilsJava;

class DustJsonHandlerComm extends DustJsonComponents.ContentHandlerDefault implements DustJsonComponents,
		DustKnowledgeProcComponents, DustKnowledgeMetaComponents, DustKnowledgeCommComponents {

	DustConstKnowledgeCommStatementType state = null;

	int idx = -1;
	String key;
	Object value;

	DustEntity msg;

	void talkInit() {
		state = DustConstKnowledgeCommStatementType.Discussion;
		msg = DustLinkToolsGenericChain.DefaultMessage.get(DustConstKnowledgeInfoContext.Self.entity(), true, null);
	}

	void talkRelease() {
		state = null;
		msg = null;
	}

	void msgInit(DustEntity cmd) {
		DustLinkKnowledgeProcMessage.Command.modify(msg, DustRefCommand.Replace, cmd, null);

		DustLinkKnowledgeCommStatement.Type.modify(msg, DustRefCommand.Replace, state.entity(), null);

		DustAttributeKnowledgeInfoVariant.value.setValue(msg, null);
		DustAttributeToolsGenericIdentified.idLocal.setValue(msg, null);

		DustLinkKnowledgeInfoIterator.Cardinality.modify(msg, DustRefCommand.Remove, null, null);
		DustAttributeKnowledgeInfoIterator.index.setValue(msg, null);
		DustAttributeKnowledgeInfoIterator.key.setValue(msg, null);
	}

	void doSend() {
		Dust.send(msg);
	}

	void sendValue() {
		msgInit(DustCommandKnowledgeProcVisitor.Visit.entity());

		DustAttributeKnowledgeInfoVariant.value.setValue(msg, value);
		DustAttributeToolsGenericIdentified.idLocal.setValue(msg, key);

		doSend();
	}

	void sendBlock(boolean begin, DustConstKnowledgeMetaCardinality card) {
		msgInit(begin ? DustCommandKnowledgeProcProcessor.Begin.entity() : DustCommandKnowledgeProcProcessor.End.entity());

		if (begin) {
			DustLinkKnowledgeInfoIterator.Cardinality.modify(msg, DustRefCommand.Replace, card.entity(), null);
			DustAttributeKnowledgeInfoIterator.index.setValue(msg, idx);
			DustAttributeKnowledgeInfoIterator.key.setValue(msg, key);
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