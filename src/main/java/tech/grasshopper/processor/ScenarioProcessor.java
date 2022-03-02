package tech.grasshopper.processor;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Builder;
import lombok.Setter;
import tech.grasshopper.pojo.Feature;
import tech.grasshopper.pojo.Hook;
import tech.grasshopper.pojo.Hook.HookType;
import tech.grasshopper.pojo.Scenario;
import tech.grasshopper.pojo.Step;
import tech.grasshopper.util.DateConverter;

@Builder
public class ScenarioProcessor {

	@Setter
	private Feature feature;

	@Setter
	private Scenario scenario;

	public void process() {
		updateUri();
		collectStepLineNumbers();
		updateScenarioStepHookType();
		updateScenarioStepHookStartAndEndTimes();
	}

	private void updateUri() {
		scenario.setUri(feature.getUri());
	}

	private void collectStepLineNumbers() {
		scenario.setStepLines(scenario.getSteps().stream().map(s -> s.getLine()).collect(Collectors.toList()));
	}

	private void updateScenarioStepHookType() {
		scenario.getBefore().forEach(h -> h.setHookType(HookType.BEFORE));

		scenario.getSteps().stream().flatMap(s -> s.getBefore().stream()).collect(Collectors.toList())
				.forEach(h -> h.setHookType(HookType.BEFORE_STEP));

		scenario.getSteps().stream().flatMap(s -> s.getAfter().stream()).collect(Collectors.toList())
				.forEach(h -> h.setHookType(HookType.AFTER_STEP));

		scenario.getAfter().forEach(h -> h.setHookType(HookType.AFTER));
	}

	private void updateScenarioStepHookStartAndEndTimes() {
		ZonedDateTime zoned = DateConverter.parseToZonedDateTime(scenario.getStartTimestamp());
		scenario.setStartTime(DateConverter.parseToDate(scenario.getStartTimestamp()));

		zoned = updateHookStartEndTimes(zoned, scenario.getBefore());
		for (Step step : scenario.getSteps()) {
			zoned = updateHookStartEndTimes(zoned, step.getBefore());

			step.setStartTime(DateConverter.parseToDate(zoned));
			zoned = zoned.plusNanos(step.getResult().getDuration());
			step.setEndTime(DateConverter.parseToDate(zoned));

			zoned = updateHookStartEndTimes(zoned, step.getAfter());
		}
		zoned = updateHookStartEndTimes(zoned, scenario.getAfter());
		scenario.setEndTime(DateConverter.parseToDate(zoned));
	}

	private ZonedDateTime updateHookStartEndTimes(ZonedDateTime zoned, List<Hook> hooks) {
		for (Hook hook : hooks) {
			hook.setStartTime(DateConverter.parseToDate(zoned));
			zoned = zoned.plusNanos(hook.getResult().getDuration());
			hook.setEndTime(DateConverter.parseToDate(zoned));
		}
		return zoned;
	}
}
