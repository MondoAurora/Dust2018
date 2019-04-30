package dust.mj02.dust.knowledge;

import dust.mj02.dust.knowledge.DustDataContext.SimpleEntity;

public class DustAccessControl implements DustKernelImplComponents {
    abstract class TestBase extends LazyMsgContainer {
        boolean test(SimpleEntity root, DustEntityKey... path) {
            if ( null == root ) {
                return true;
            }
            SimpleEntity ac = root.getSingleRefByPath(path);
            if (null != ac) {
                SimpleEntity msgChk = getMsg();
                ctx.binConn.send(ac, msgChk);
                return Boolean.TRUE.equals(msgChk.get(DustDataAtts.MessageReturn));
            }
            return true;
        }
    }

    DustDataContext ctx;

    public DustAccessControl(DustDataContext ctx) {
        this.ctx = ctx;
    }

    public boolean isCommandAllowed(SimpleEntity source, SimpleEntity target, SimpleEntity message) {
        DustProcLinks acLink = DustProcLinks.AccessControlAccess;

        TestBase tb = new TestBase() {
            @Override
            protected SimpleEntity createMsg() {
                SimpleEntity msgChk = null;
                msgChk = ctx.new SimpleEntity(true);

                msgChk.putLocalRef(DustDataLinks.MessageCommand, DustProcMessages.EvaluatorEvaluate);

                msgChk.putLocalRef(DustProcLinks.TaskMessage, message);
                msgChk.putLocalRef(DustProcLinks.TaskEntity, target);
                msgChk.putLocalRef(DustProcLinks.TaskInitiator, source);

                return msgChk;
            }
        };

        if (!tb.test(target, DustDataLinks.EntityAccessControl, acLink)) {
            return false;
        }
        
        SimpleEntity cmd = message.getSingleRef(DustDataLinks.MessageCommand);

        if (!tb.test(cmd, DustMetaLinks.MetaAccessControl, acLink)) {
            return false;
        }

        if (!tb.test(cmd, DustGenericLinks.ConnectedOwner, DustMetaLinks.MetaAccessControl, acLink)) {
            return false;
        }

        return true;
    }

    public boolean isAccessAllowed(SimpleEntity source, SimpleEntity target, SimpleEntity key, DataCommand cmd) {
        DustProcLinks acLink = cmd.isChange() ? DustProcLinks.AccessControlChange : DustProcLinks.AccessControlAccess;

        TestBase tb = new TestBase() {
            @Override
            protected SimpleEntity createMsg() {
                SimpleEntity msgChk = null;
                msgChk = ctx.new SimpleEntity(true);

                msgChk.putLocalRef(DustDataLinks.MessageCommand, DustProcMessages.EvaluatorEvaluate);

                msgChk.putLocalRef(DustProcLinks.ChangeCmd, cmd);
                msgChk.putLocalRef(DustProcLinks.ChangeEntity, target);
                msgChk.putLocalRef(DustProcLinks.ChangeKey, (SimpleEntity) key);
                msgChk.putLocalRef(DustProcLinks.ChangeSource, source);

                return msgChk;
            }
        };

        if (!tb.test(target, DustDataLinks.EntityAccessControl, acLink)) {
            return false;
        }

        if (!tb.test(key, DustMetaLinks.MetaAccessControl, acLink)) {
            return false;
        }

        if (!tb.test(key, (cmd.isRef()) ? DustMetaLinks.LinkDefParent : DustMetaLinks.AttDefParent, DustMetaLinks.MetaAccessControl, acLink)) {
            return false;
        }

        return true;
    }

}
