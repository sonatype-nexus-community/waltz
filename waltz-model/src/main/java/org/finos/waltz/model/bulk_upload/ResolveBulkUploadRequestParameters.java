package org.finos.waltz.model.bulk_upload;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.finos.waltz.model.EntityKind;
import org.finos.waltz.model.EntityReference;
import org.immutables.value.Value;

import java.util.List;
import java.util.Optional;

@Value.Immutable
@JsonSerialize(as = ImmutableResolveBulkUploadRequestParameters.class)
@JsonDeserialize(as = ImmutableResolveBulkUploadRequestParameters.class)
public abstract class ResolveBulkUploadRequestParameters {

    public abstract String inputString();

    public abstract EntityReference targetDomain();

    public abstract EntityKind rowSubjectKind();

    public abstract Optional<EntityReference> rowSubjectQualifier();
}
