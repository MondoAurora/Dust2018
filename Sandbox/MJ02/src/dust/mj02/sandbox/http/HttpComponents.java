package dust.mj02.sandbox.http;

import dust.mj02.dust.knowledge.DustKernelComponents;

public interface HttpComponents extends DustKernelComponents {
    
    enum DustNetTypes implements DustEntityKey {
        NetAddress, NetPinger
    };
    
    enum DustNetAtts implements DustEntityKey {
        NetAddressUrl
    }
    
    enum DustNetServices implements DustEntityKey {
        NetPinger
    };


}
