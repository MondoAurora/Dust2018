package dust.mj02.sandbox.http;

import dust.mj02.dust.knowledge.DustKernelComponents;

public interface DustHttpComponents extends DustKernelComponents {
    String CONTENT_JSON = "application/json";
    
    enum DustNetTypes implements DustEntityKey {
        NetAddress, NetClient, NetServer, NetSSL
    };
    
    enum DustNetAtts implements DustEntityKey {
        NetAddressUrl, 
        NetServerPublicPort, NetServerSslPort, 
        NetSSLStorePath, NetSSLStorePass, NetSSLManagerPass
    }
    
    enum DustNetServices implements DustEntityKey {
        NetClient, NetServer
    };


}
