package dust.mj02.sandbox;

import dust.mj02.dust.DustUtils;
import dust.mj02.dust.knowledge.DustProcComponents;

public class DustSandboxScheduler implements DustSandboxComponents, DustProcComponents.DustProcActive, DustProcComponents.DustProcListener {

    // class TaskInfo {
    // DustEntity eTask;
    //
    // long nextRun;
    // boolean removed;
    //
    // public TaskInfo(DustEntity eTask) {
    // super();
    // this.eTask = eTask;
    //
    // setTime(System.currentTimeMillis());
    // }
    //
    // void setTime(long now) {
    // nextRun = now + 2000;
    // }
    // }

    // DustUtilsFactory<DustEntity, TaskInfo> factTasks = new
    // DustUtilsFactory<DustEntity, TaskInfo>(false) {
    // @Override
    // protected TaskInfo create(DustEntity key, Object... hints) {
    // return new TaskInfo(key);
    // }
    // };

    // TaskInfo next;

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
        long now = System.currentTimeMillis();
        nextTs = Long.MAX_VALUE;
        eNextTask = null;

        DustUtils.accessEntity(DataCommand.processRef, ContextRef.self, DustProcLinks.SchedulerTasks, new RefProcessor() {
            @Override
            public void processRef(DustRef ref) {
                DustEntity eTask = ref.get(RefKey.target);

                Long nextRun = DustUtils.accessEntity(DataCommand.getValue, eTask, DustProcAtts.TaskNextRun);

                if (null == nextRun) {
                    String s = DustUtils.accessEntity(DataCommand.getValue, eTask, DustProcAtts.TaskRepeatSec);
                    long delay = Long.parseLong(s);
                    nextRun = now + 1000 * delay;
                    DustUtils.accessEntity(DataCommand.setValue, eTask, DustProcAtts.TaskNextRun, nextRun);
                }

                if (nextTs > nextRun) {
                    nextTs = nextRun;
                    eNextTask = eTask;
                }
            }
        });
    }

}
