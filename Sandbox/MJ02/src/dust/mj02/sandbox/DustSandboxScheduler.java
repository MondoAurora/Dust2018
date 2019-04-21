package dust.mj02.sandbox;

import javax.swing.SwingUtilities;

import dust.mj02.dust.DustUtils;
import dust.mj02.dust.knowledge.DustProcComponents;

public class DustSandboxScheduler implements DustSandboxComponents, DustProcComponents.DustProcActive, DustProcComponents.DustProcListener {

    DustEntity eSelf;
    DustEntity eNextTask;
    long nextTs = Long.MAX_VALUE;

    Runnable runSched = new Runnable() {
        @Override
        public void run() {
            synchronized (this) {
                while (true) {
                    setNext();
                    try {
                        wait(nextTs);
                    } catch (InterruptedException e) {
                        return;
                    }

                    if (null != eNextTask) {
                        launch(eNextTask);
                    }
                }
            }
        }
    };

    Thread thSched = new Thread(runSched);

    @Override
    public void activeInit() throws Exception {
//        eSelf = DustUtils.getByPath(ContextRef.self);
        eSelf = DustUtils.getCtxVal(ContextRef.self, null, false);
        DustUtils.accessEntity(DataCommand.setRef, ContextRef.self, DustProcLinks.ChangeEntity, ContextRef.self);
        DustUtils.accessEntity(DataCommand.setRef, ContextRef.ctx, DustProcLinks.ContextChangeListeners, ContextRef.self);

        thSched.start();
    }

    protected void launch(DustEntity eTask) {
        DustUtils.accessEntity(DataCommand.setValue, eTask, DustProcAtts.TaskNextRun, null);

        new Thread(new Runnable() {
            @Override
            public void run() {
                DustEntity eTarget = DustUtils.getByPath(eTask, DustProcLinks.TaskEntity);
                DustEntity eMsg = DustUtils.getByPath(eTask, DustProcLinks.TaskMessage);

                DustUtils.accessEntity(DataCommand.tempSend, eTarget, eMsg);
            }
        }).start();
    }

    @Override
    public void activeRelease() throws Exception {
        thSched.interrupt();
        thSched.join(5000);
    }

    @Override
    public void dustProcListenerProcessChange() throws Exception {
        DustEntity eKey = DustUtils.getMsgVal(DustProcLinks.ChangeKey, true);

        if (EntityResolver.getEntity(DustProcLinks.SchedulerTasks) == eKey) {
            eNextTask = null;
            synchronized (runSched) {
                runSched.notify();
            }
        }
    }

    synchronized void setNext() {
//        long now = System.currentTimeMillis();
        nextTs = Long.MAX_VALUE;
        eNextTask = null;

        DustUtils.accessEntity(DataCommand.processRef, eSelf, DustProcLinks.SchedulerTasks, new RefProcessor() {
            @Override
            public void processRef(DustRef ref) {
                DustEntity eTask = ref.get(RefKey.target);

                Long nextRun = DustUtils.accessEntity(DataCommand.getValue, eTask, DustProcAtts.TaskNextRun);

                if (null == nextRun) {
                    String s = DustUtils.accessEntity(DataCommand.getValue, eTask, DustProcAtts.TaskRepeatSec);
                    long delay = Long.parseLong(s);
//                    Long nr = nextRun = now + 1000 * delay;
                    Long nr = nextRun = 1000 * delay;
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            DustUtils.accessEntity(DataCommand.setValue, eTask, DustProcAtts.TaskNextRun, nr);
                        }
                    });
                }

                if (nextTs > nextRun) {
                    nextTs = nextRun;
                    eNextTask = eTask;
                }
            }
        });
    }

}
