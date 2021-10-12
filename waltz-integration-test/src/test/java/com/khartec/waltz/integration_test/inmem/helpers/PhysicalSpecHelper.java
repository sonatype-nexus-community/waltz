package com.khartec.waltz.integration_test.inmem.helpers;

import com.khartec.waltz.common.DateTimeUtilities;
import com.khartec.waltz.model.EntityReference;
import com.khartec.waltz.model.UserTimestamp;
import com.khartec.waltz.model.physical_specification.DataFormatKind;
import com.khartec.waltz.model.physical_specification.ImmutablePhysicalSpecification;
import com.khartec.waltz.service.physical_specification.PhysicalSpecificationService;

import static com.khartec.waltz.integration_test.inmem.helpers.NameHelper.mkName;
import static com.khartec.waltz.integration_test.inmem.helpers.NameHelper.mkUserId;

public class PhysicalSpecHelper {

    private final PhysicalSpecificationService physicalSpecificationService;

    public PhysicalSpecHelper(PhysicalSpecificationService physicalSpecificationService) {
        this.physicalSpecificationService = physicalSpecificationService;
    }

    public Long createPhysicalSpec(EntityReference owningEntity, String name) {
        String specName = mkName(name);
        String user = mkUserId(name);
        return physicalSpecificationService.create(ImmutablePhysicalSpecification.builder()
                .externalId(specName)
                .owningEntity(owningEntity)
                .name(specName)
                .description(name)
                .format(DataFormatKind.UNKNOWN)
                .lastUpdatedBy(user)
                .isRemoved(false)
                .created(UserTimestamp.mkForUser(user, DateTimeUtilities.nowUtcTimestamp()))
                .build());
    }

}
