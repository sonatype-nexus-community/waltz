package org.finos.waltz.service.aggregate_overlay_diagram;

import org.finos.waltz.data.GenericSelector;
import org.finos.waltz.data.GenericSelectorFactory;
import org.finos.waltz.data.aggregate_overlay_diagram.*;
import org.finos.waltz.model.AssessmentBasedSelectionFilter;
import org.finos.waltz.model.EntityKind;
import org.finos.waltz.model.IdSelectionOptions;
import org.finos.waltz.model.aggregate_overlay_diagram.AggregateOverlayDiagram;
import org.finos.waltz.model.aggregate_overlay_diagram.AggregateOverlayDiagramInfo;
import org.finos.waltz.model.aggregate_overlay_diagram.BackingEntity;
import org.finos.waltz.model.aggregate_overlay_diagram.ImmutableAggregateOverlayDiagramInfo;
import org.finos.waltz.model.aggregate_overlay_diagram.overlay.*;
import org.finos.waltz.model.aggregate_overlay_diagram.overlay.widget_parameters.AppCostWidgetParameters;
import org.finos.waltz.model.aggregate_overlay_diagram.overlay.widget_parameters.AppCountWidgetParameters;
import org.finos.waltz.model.aggregate_overlay_diagram.overlay.widget_parameters.AssessmentWidgetParameters;
import org.finos.waltz.model.aggregate_overlay_diagram.overlay.widget_parameters.TargetAppCostWidgetParameters;
import org.jooq.Record1;
import org.jooq.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

import static org.finos.waltz.data.assessment_rating.AssessmentRatingBasedGenericSelectorFactory.applyFilterToSelector;

@Service
public class AggregateOverlayDiagramService {


    private final AggregateOverlayDiagramDao aggregateOverlayDiagramDao;
    private final AppCountWidgetDao appCountWidgetDao;
    private final TargetAppCostWidgetDao targetAppCostWidgetDao;
    private final AppCostWidgetDao appCostWidgetDao;
    private final AppAssessmentWidgetDao appAssessmentWidgetDao;
    private final BackingEntityWidgetDao backingEntityWidgetDao;

    private final GenericSelectorFactory genericSelectorFactory = new GenericSelectorFactory();

    @Autowired
    public AggregateOverlayDiagramService(AggregateOverlayDiagramDao aggregateOverlayDiagramDao,
                                          AppCountWidgetDao appCountWidgetDao,
                                          TargetAppCostWidgetDao targetAppCostWidgetDao,
                                          AppAssessmentWidgetDao appAssessmentWidgetDao,
                                          BackingEntityWidgetDao backingEntityWidgetDao,
                                          AppCostWidgetDao appCostWidgetDao) {
        this.aggregateOverlayDiagramDao = aggregateOverlayDiagramDao;
        this.appCountWidgetDao = appCountWidgetDao;
        this.targetAppCostWidgetDao = targetAppCostWidgetDao;
        this.appCostWidgetDao = appCostWidgetDao;
        this.appAssessmentWidgetDao = appAssessmentWidgetDao;
        this.backingEntityWidgetDao = backingEntityWidgetDao;
    }


    public AggregateOverlayDiagramInfo getById(Long diagramId) {
        AggregateOverlayDiagram diagram = aggregateOverlayDiagramDao.getById(diagramId);
        Set<BackingEntity> backingEntities = aggregateOverlayDiagramDao.findBackingEntities(diagramId);

        return ImmutableAggregateOverlayDiagramInfo.builder()
                .diagram(diagram)
                .backingEntities(backingEntities)
                .build();
    }


    public Set<AggregateOverlayDiagram> findAll() {
        return aggregateOverlayDiagramDao.findAll();
    }


    public Set<CountWidgetDatum> findAppCountWidgetData(Long diagramId,
                                                        IdSelectionOptions appSelectionOptions,
                                                        Optional<AssessmentBasedSelectionFilter> filterParams,
                                                        AppCountWidgetParameters appCountWidgetParameters) {

        GenericSelector genericSelector = genericSelectorFactory.applyForKind(EntityKind.APPLICATION, appSelectionOptions);
        Select<Record1<Long>> entityIdSelector = applyFilterToSelector(genericSelector, filterParams);

        return appCountWidgetDao.findWidgetData(diagramId, entityIdSelector, appCountWidgetParameters.targetDate());
    }


    public Set<TargetCostWidgetDatum> findTargetAppCostWidgetData(Long diagramId,
                                                                  IdSelectionOptions appSelectionOptions,
                                                                  Optional<AssessmentBasedSelectionFilter> filterParams,
                                                                  TargetAppCostWidgetParameters targetAppCostWidgetParameters) {

        GenericSelector genericSelector = genericSelectorFactory.applyForKind(EntityKind.APPLICATION, appSelectionOptions);

        Select<Record1<Long>> entityIdSelector = applyFilterToSelector(genericSelector, filterParams);
        return targetAppCostWidgetDao.findWidgetData(diagramId, entityIdSelector, targetAppCostWidgetParameters.targetDate());
    }


    public Set<CostWidgetDatum> findAppCostWidgetData(Long diagramId,
                                                      Optional<AssessmentBasedSelectionFilter> filterParams,
                                                      IdSelectionOptions appSelectionOptions,
                                                      AppCostWidgetParameters appCostWidgetParameters) {

        GenericSelector genericSelector = genericSelectorFactory.applyForKind(EntityKind.APPLICATION, appSelectionOptions);
        Select<Record1<Long>> entityIdSelector = applyFilterToSelector(genericSelector, filterParams);

        return appCostWidgetDao.findWidgetData(
                diagramId,
                appCostWidgetParameters.costKindIds(),
                appCostWidgetParameters.allocationSchemeId(),
                entityIdSelector);
    }


    public Set<AssessmentRatingsWidgetDatum> findAppAssessmentWidgetData(Long diagramId,
                                                                         Optional<AssessmentBasedSelectionFilter> filterParams,
                                                                         IdSelectionOptions appSelectionOptions,
                                                                         AssessmentWidgetParameters assessmentWidgetParameters) {

        GenericSelector genericSelector = genericSelectorFactory.applyForKind(EntityKind.APPLICATION, appSelectionOptions);
        Select<Record1<Long>> entityIdSelector = applyFilterToSelector(genericSelector, filterParams);

        return appAssessmentWidgetDao.findWidgetData(
                diagramId,
                assessmentWidgetParameters.assessmentDefinitionId(),
                entityIdSelector);
    }


    public Set<BackingEntityWidgetDatum> findBackingEntityWidgetData(Long diagramId) {
        return backingEntityWidgetDao.findWidgetData(diagramId);
    }


}
