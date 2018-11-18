package com.lzh.versionablehelper.infrastructure;

import com.lzh.versionablehelper.infrastructure.dto.ParameterForm;
import com.lzh.versionablehelper.infrastructure.po.BasePO;
import com.lzh.versionablehelper.infrastructure.po.DemoBasePO;
import com.lzh.versionablehelper.infrastructure.po.VersionablePO;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public interface VersionableHelper<M extends BasePO,H extends BasePO & VersionablePO<M>> {

    Class<M> getMasterPOClass();

    Class<H> getHistoryPOClass();

    List<H> getVersionablePOByParamtertForm(ParameterForm params);

    List<H> getHistoryPOsByMasterPO(M masterPO, ParameterForm params);

    void persist(M masterPO);

    void persistAll(Collection<M> masterPOs);

    void persist(H historyPO,boolean crossDayChange);

    void persistAll(Map<H,Boolean> historyPOs);


}
