package dust.mj02.sandbox.jdbc;

import dust.mj02.dust.knowledge.DustKernelComponents;

public interface DustJdbcComponents  extends DustKernelComponents {
    
    enum DustJdbcTypes implements DustEntityKey {
        JdbcConnector
    };
    
    enum DustJdbcServices implements DustEntityKey {
        JdbcConnector
    };


}
