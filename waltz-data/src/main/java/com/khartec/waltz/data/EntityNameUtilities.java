package com.khartec.waltz.data;

import com.khartec.waltz.model.EntityKind;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.khartec.waltz.common.Checks.checkNotNull;
import static com.khartec.waltz.schema.tables.Actor.ACTOR;
import static com.khartec.waltz.schema.tables.Application.APPLICATION;
import static com.khartec.waltz.schema.tables.ApplicationGroup.APPLICATION_GROUP;
import static com.khartec.waltz.schema.tables.Capability.CAPABILITY;
import static com.khartec.waltz.schema.tables.ChangeInitiative.CHANGE_INITIATIVE;
import static com.khartec.waltz.schema.tables.DataType.DATA_TYPE;
import static com.khartec.waltz.schema.tables.EndUserApplication.END_USER_APPLICATION;
import static com.khartec.waltz.schema.tables.EntityStatisticDefinition.ENTITY_STATISTIC_DEFINITION;
import static com.khartec.waltz.schema.tables.OrganisationalUnit.ORGANISATIONAL_UNIT;
import static com.khartec.waltz.schema.tables.PerfMetricPack.PERF_METRIC_PACK;
import static com.khartec.waltz.schema.tables.Person.PERSON;
import static com.khartec.waltz.schema.tables.PhysicalSpecification.PHYSICAL_SPECIFICATION;
import static com.khartec.waltz.schema.tables.Process.PROCESS;
import static java.util.stream.Collectors.toList;
import static org.jooq.impl.DSL.val;
import static org.jooq.lambda.tuple.Tuple.tuple;

public class EntityNameUtilities {

    private static final Map<EntityKind, Tuple3<Table, Field<Long>, Field<String>>> MAPPINGS;


    static {
        MAPPINGS = new HashMap<>(EntityKind.values().length);
        MAPPINGS.put(EntityKind.ACTOR, tuple(ACTOR, ACTOR.ID, ACTOR.NAME));
        MAPPINGS.put(EntityKind.APPLICATION, tuple(APPLICATION, APPLICATION.ID, APPLICATION.NAME));
        MAPPINGS.put(EntityKind.APP_GROUP, tuple(APPLICATION_GROUP, APPLICATION_GROUP.ID, APPLICATION_GROUP.NAME));
        MAPPINGS.put(EntityKind.CAPABILITY, tuple(CAPABILITY, CAPABILITY.ID, CAPABILITY.NAME));
        MAPPINGS.put(EntityKind.CHANGE_INITIATIVE, tuple(CHANGE_INITIATIVE, CHANGE_INITIATIVE.ID, CHANGE_INITIATIVE.NAME));
        MAPPINGS.put(EntityKind.DATA_TYPE, tuple(DATA_TYPE, DATA_TYPE.ID, DATA_TYPE.NAME));
        MAPPINGS.put(EntityKind.END_USER_APPLICATION, tuple(END_USER_APPLICATION, END_USER_APPLICATION.ID, END_USER_APPLICATION.NAME));
        MAPPINGS.put(EntityKind.ENTITY_STATISTIC, tuple(ENTITY_STATISTIC_DEFINITION, ENTITY_STATISTIC_DEFINITION.ID, ENTITY_STATISTIC_DEFINITION.NAME));
        MAPPINGS.put(EntityKind.ORG_UNIT, tuple(ORGANISATIONAL_UNIT, ORGANISATIONAL_UNIT.ID, ORGANISATIONAL_UNIT.NAME));
        MAPPINGS.put(EntityKind.PERFORMANCE_METRIC_PACK, tuple(PERF_METRIC_PACK, PERF_METRIC_PACK.ID, PERF_METRIC_PACK.NAME));
        MAPPINGS.put(EntityKind.PERSON, tuple(PERSON, PERSON.ID, PERSON.DISPLAY_NAME));
        MAPPINGS.put(EntityKind.PHYSICAL_SPECIFICATION, tuple(PHYSICAL_SPECIFICATION, PHYSICAL_SPECIFICATION.ID, PHYSICAL_SPECIFICATION.NAME));
        MAPPINGS.put(EntityKind.PROCESS, tuple(PROCESS, PROCESS.ID, PROCESS.NAME));
    }


    public static Field<String> mkEntityNameField(Field<Long> idCompareField,
                                                  Field<String> kindCompareField,
                                                  List<EntityKind> searchEntityKinds) {
        checkNotNull(idCompareField, "idCompareField cannot be null");
        checkNotNull(kindCompareField, "kindCompareField cannot be null");
        checkNotNull(searchEntityKinds, "searchEntityKinds cannot be null");

        // create case condition and select statement pairs
        List<Tuple2<Condition, Select<Record1<String>>>> caseSteps = MAPPINGS.entrySet().stream()
                .filter(e -> searchEntityKinds.contains(e.getKey()))
                .map(e -> tuple(kindCompareField.eq(val(e.getKey().name())),
                                mkNameSelect(e.getValue(), idCompareField)))
                .collect(toList());

        // form the where condition field
        // jOOQ doesn't seem to allow creation of case statements
        // through a clean factory method, hence this logic
        CaseConditionStep<String> caseField = null;
        for (Tuple2<Condition, Select<Record1<String>>> caseStep : caseSteps) {
            if (caseField == null) {
                caseField = DSL.when(caseStep.v1(), caseStep.v2());
            } else {
                caseField = caseField.when(caseStep.v1(), caseStep.v2());
            }
        }

        return caseField;
    }


    private static Select<Record1<String>> mkNameSelect(Tuple3<Table, Field<Long>, Field<String>> mapping,
                                                        Field<Long> idCompareField) {
        return DSL.select(mapping.v3())
                .from(mapping.v1())
                .where(mapping.v2().eq(idCompareField));
    }
}
