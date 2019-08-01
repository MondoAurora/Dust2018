package dust.mj02.sandbox.media;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequencer;

import dust.mj02.dust.DustUtils;
import dust.mj02.dust.knowledge.DustKernelComponents;
import dust.mj02.dust.knowledge.DustProcComponents;

public class DustMidiPlayer01 implements DustKernelComponents, DustProcComponents.DustProcPocessor, DustProcComponents.DustProcActive {

    // dust.mj02.sandbox.media.DustMidiPlayer01
    
    Sequencer sequencer;

    public void optInit() throws Exception {
        if ( null == sequencer ) {
            sequencer = MidiSystem.getSequencer();
        }
        
        if ( ! sequencer.isOpen() ) {
            sequencer.open();            
        }
    }

    public void optRelease() throws Exception {
        if ( null != sequencer ) {
            sequencer.stop();
            
//            sequencer.close();            
//            sequencer = null;
        }
    }

    @Override
    public void activeInit() throws Exception {
        optInit();
    }

    @Override
    public void activeRelease() throws Exception {
        optRelease();
    }

    @Override
    public void processorProcess() throws Exception {
        String fName = DustUtils.getByPath(ContextRef.msg, DustGenericLinks.ContextAwareEntity, DustGenericAtts.StreamFileName);
        // String fName = DustUtils.getMsgVal(DustGenericAtts.StreamFileName, false);

        // create a stream from a file
        InputStream is = new BufferedInputStream(new FileInputStream(new File(fName)));
        
        optInit();

        // Sets the current sequence on which the sequencer operates.
        // The stream must point to MIDI file data.
        sequencer.setSequence(is);

        sequencer.addMetaEventListener(new MetaEventListener() {
            public void meta(MetaMessage event) {
                if (event.getType() == 88) {
                    System.out.println("Sequencer started playing");
                } else if (event.getType() == 47) {
                    System.out.println("Sequencer finished playing");
                    sequencer.close();
                }
            }
        });

        // Starts playback of the MIDI data in the currently loaded sequence.
        sequencer.start();
    }

}
