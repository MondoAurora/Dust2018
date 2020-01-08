package dust.mj02.dust.knowledge;

public class DustProcAccessControl implements DustKernelImplComponents {
    abstract class TestBase extends LazyMsgContainer {
        boolean test(DustDataEntity root, DustEntityKey... path) {
            if ( null == root ) {
                return true;
            }
            DustDataEntity ac = root.getSingleRefByPath(path);
            if (null != ac) {
                DustDataEntity msgChk = getMsg();
                ctx.binConn.send(ac, msgChk);
                return Boolean.TRUE.equals(msgChk.get(DustDataAtts.MessageReturn));
            }
            return true;
        }
    }

    DustProcSession ctx;

    public DustProcAccessControl(DustProcSession ctx) {
        this.ctx = ctx;
    }

    public boolean isCommandAllowed(DustDataEntity source, DustDataEntity target, DustDataEntity message) {
        DustProcLinks acLink = DustProcLinks.AccessControlAccess;

        TestBase tb = new TestBase() {
            @Override
            protected DustDataEntity createMsg() {
                DustDataEntity msgChk = null;
                msgChk = new DustDataEntity(ctx, true);

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
        
        DustDataEntity cmd = message.getSingleRef(DustDataLinks.MessageCommand);

        if (!tb.test(cmd, DustMetaLinks.MetaAccessControl, acLink)) {
            return false;
        }

        if (!tb.test(cmd, DustGenericLinks.ConnectedOwner, DustMetaLinks.MetaAccessControl, acLink)) {
            return false;
        }

        return true;
    }

    public boolean isAccessAllowed(DustDataEntity source, DustDataEntity target, DustDataEntity key, DataCommand cmd) {
        DustProcLinks acLink = cmd.isChange() ? DustProcLinks.AccessControlChange : DustProcLinks.AccessControlAccess;

        TestBase tb = new TestBase() {
            @Override
            protected DustDataEntity createMsg() {
                DustDataEntity msgChk = null;
                msgChk = new DustDataEntity(ctx, true);

                msgChk.putLocalRef(DustDataLinks.MessageCommand, DustProcMessages.EvaluatorEvaluate);

                msgChk.putLocalRef(DustCommLinks.ChangeItemCmd, cmd);
                msgChk.putLocalRef(DustCommLinks.ChangeItemEntity, target);
                msgChk.putLocalRef(DustCommLinks.ChangeItemKey, (DustDataEntity) key);
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
