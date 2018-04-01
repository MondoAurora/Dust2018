package dust.persistence.jsonsimple;

import java.io.IOException;

import org.json.simple.parser.ParseException;

import dust.gen.dust.core.exec.DustCoreExecComponents;
import dust.gen.dust.core.meta.DustCoreMetaComponents;
import dust.pub.Dust;
import dust.pub.DustUtilsDev;
import dust.pub.DustUtilsJava;

class DustJsonHandlerComm extends DustJsonComponents.ContentHandlerDefault
		implements DustJsonComponents, DustCoreExecComponents, DustCoreMetaComponents {

	enum CommState {
		Comm(DustConstCoreMetaCardinality.Single), Entity(DustConstCoreMetaCardinality.Array), Model(
				DustConstCoreMetaCardinality.Map), Data(DustConstCoreMetaCardinality.Single);

		final DustConstCoreMetaCardinality card;

		private CommState(DustConstCoreMetaCardinality card) {
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
		msg = Dust.getRefEntity(DustConstCoreDataContext.Self, true, DustLinkToolsGenericChain.DefaultMessage, null);
	}

	void talkRelease() {
		state = null;
		msg = null;
	}
	
	void msgInit(DustEntity cmd) {
		Dust.modifyRefs(DustConstCoreDataLinkCommand.Replace, msg, cmd, DustLinkCoreExecMessage.Command);

		Dust.setAttrValue(msg, DustAttributeCoreDataVariant.value, null);
		Dust.setAttrValue(msg, DustAttributeToolsGenericIdentified.idLocal, null);

		Dust.setAttrValue(msg, DustAttributeCoreDataIterator.cardinality, null);
		Dust.setAttrValue(msg, DustAttributeCoreDataIterator.index, null);
		Dust.setAttrValue(msg, DustAttributeCoreDataIterator.key, null);
	}

	void doSend() {
//		Dust.send(msg);
	}

	void sendValue() {
		msgInit(DustCommandCoreExecVisitor.Visit);

		Dust.setAttrValue(msg, DustAttributeCoreDataVariant.value, value);
		Dust.setAttrValue(msg, DustAttributeToolsGenericIdentified.idLocal, key);

		DustUtilsDev.dump("Sending value...", key, value);
		doSend();
	}

	void sendBlock(boolean begin) {
		msgInit(begin ? DustCommandCoreExecProcessor.Begin : DustCommandCoreExecProcessor.End);

		if ( begin ) {
		Dust.setAttrValue(msg, DustAttributeCoreDataIterator.cardinality, state.card);
		Dust.setAttrValue(msg, DustAttributeCoreDataIterator.index, idx);
		Dust.setAttrValue(msg, DustAttributeCoreDataIterator.key, key);
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