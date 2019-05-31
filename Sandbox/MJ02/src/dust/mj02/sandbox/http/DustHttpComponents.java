package dust.mj02.sandbox.http;

import dust.mj02.dust.knowledge.DustKernelComponents;

public interface DustHttpComponents extends DustKernelComponents {
    
    enum DustNetTypes implements DustEntityKey {
        NetAddress, NetClient, NetServer, NetSslInfo, NetProcessor, NetGetRef, 
        NetProcessContext
    };
    
    enum DustNetAtts implements DustEntityKey {
        NetAddressUrl, 
        NetServerPublicPort, NetServerSslPort, 
        NetSslInfoStorePath, NetSslInfoStorePass, NetSslInfoManagerPass,
        NetClientModuleToUpdate,
        NetProcessorRespCharset, NetProcessorRespContentType, 
        NetProcessContextRequest, NetProcessContextResponse, NetProcessMethod
    }
    
    enum DustNetLinks implements DustEntityKey {
        NetClientProxyEntities, NetProcessorParamTypes, 
    };

    
    enum DustNetServices implements DustEntityKey {
        NetClient, NetServer, NetGetRef
    };


}
