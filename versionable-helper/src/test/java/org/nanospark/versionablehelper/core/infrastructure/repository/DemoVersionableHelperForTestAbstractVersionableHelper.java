package org.nanospark.versionablehelper.core.infrastructure.repository;

import org.nanospark.versionablehelper.core.infrastructure.AbstractVersionableHelper;
import org.nanospark.versionablehelper.core.infrastructure.po.DemoHistoryPO;
import org.nanospark.versionablehelper.core.infrastructure.po.DemoMasterPO;
import org.springframework.stereotype.Repository;

@Repository("demoVersionableHelperForTestAbstractVersionableHelper")
public class DemoVersionableHelperForTestAbstractVersionableHelper extends AbstractVersionableHelper<DemoMasterPO, Long, DemoHistoryPO, Long> {


    public DemoVersionableHelperForTestAbstractVersionableHelper() {
    }

    @Override
    protected ConstructorParameter<DemoMasterPO, Long, DemoHistoryPO, Long> getConstructorParameter() {
        ConstructorParameter<DemoMasterPO, Long, DemoHistoryPO, Long> constructorParameter = new ConstructorParameter<>();
        constructorParameter.masterPOClass = DemoMasterPO.class;
        constructorParameter.historyPOClass = DemoHistoryPO.class;
        constructorParameter.masterPOConstructor = DemoMasterPO::new;
        constructorParameter.historyPOConstructor = DemoHistoryPO::new;
        constructorParameter.masterPOKeyExtractor = DemoMasterPO::getId;
        constructorParameter.historyPOKeyExtractor = DemoHistoryPO::getHistId;
        return constructorParameter;
    }
}
