package dust.persistence.jsonsimple;

import java.io.IOException;

import org.json.simple.parser.ParseException;

import dust.gen.knowledge.meta.DustKnowledgeMetaComponents;
import dust.gen.knowledge.proc.DustKnowledgeProcComponents;
import dust.pub.Dust;
import dust.pub.DustUtilsDev;
import dust.pub.DustUtilsJava;

class DustJsonHandlerComm extends DustJsonComponents.ContentHandlerDefault
		implements DustJsonComponents, DustKnowledgeProcComponents, DustKnowledgeMetaComponents {

	enum CommState {
		Comm(DustConstKnowledgeMetaCardinality.Single), Entity(DustConstKnowledgeMetaCardinality.Array), Model(
				DustConstKnowledgeMetaCardinality.Map), Data(DustConstKnowledgeMetaCardinality.Single);

		final DustConstKnowledgeMetaCardinality card;

		private CommState(DustConstKnowledgeMetaCardinality card) {
			this.card = card;
		}
	}

	CommState state = null;

	int idx = -1;
	String key;
	Object value;
	
	DustEntity msg;

	void talkInit() {
		state = CommState.Comm;
		msg = Dust.getRefEntity(DustConstKnowledgeInfoContext.Self, true, DustLinkToolsGenericChain.DefaultMessage, null);
	}

	void talkRelease() {
		state = null;
		msg = null;
	}
	
	void msgInit(DustEntity cmd) {
		Dust.modifyRefs(DustConstKnowledgeInfoLinkCommand.Replace, msg, cmd, DustLinkKnowledgeProcMessage.Command);

		Dust.setAttrValue(msg, DustAttributeKnowledgeInfoVariant.value, null);
		Dust.setAttrValue(msg, DustAttributeToolsGenericIdentified.idLocal, null);

		Dust.setAttrValue(msg, DustAttributeKnowledgeInfoIterator.cardinality, null);
		Dust.setAttrValue(msg, DustAttributeKnowledgeInfoIterator.index, null);
		Dust.setAttrValue(msg, DustAttributeKnowledgeInfoIterator.key, null);
	}

	void doSend() {
		DustUtilsDev.dump();
//		Dust.send(msg);
	}

	void sendValue() {
		msgInit(DustCommandKnowledgeProcVisitor.Visit);

		Dust.setAttrValue(msg, DustAttributeKnowledgeInfoVariant.value, value);
		Dust.setAttrValue(msg, DustAttributeToolsGenericIdentified.idLocal, key);

		DustUtilsDev.dump("Sending value...", key, value);
		doSend();
	}

	void sendBlock(boolean begin) {
		msgInit(begin ? DustCommandKnowledgeProcProcessor.Begin : DustCommandKnowledgeProcProcessor.End);

		if ( begin ) {
		Dust.setAttrValue(msg, DustAttributeKnowledgeInfoIterator.cardinality, state.card);
		Dust.setAttrValue(msg, DustAttributeKnowledgeInfoIterator.index, idx);
		Dust.setAttrValue(msg, DustAttributeKnowledgeInfoIterator.key, key);
		DustUtilsDev.dump("Start block...", state, (state == CommState.Entity) ? idx : key);

		} else {
			DustUtilsDev.dump("End block...", state);
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
		if ( state == CommState.Entity ) {
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
		state = CommState.Data;

		sendValue();
		state = CommState.Model;

		return true;
	}
}