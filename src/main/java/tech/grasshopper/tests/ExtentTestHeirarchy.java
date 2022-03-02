package tech.grasshopper.tests;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;

import lombok.Builder;
import lombok.Builder.Default;
import tech.grasshopper.pojo.Feature;
import tech.grasshopper.pojo.Scenario;
import tech.grasshopper.pojo.Step;
import tech.grasshopper.processor.AdditionalInformationProcessor;
import tech.grasshopper.processor.FeatureProcessor;
import tech.grasshopper.processor.ScenarioProcessor;
import tech.grasshopper.processor.StepProcessor;

@Builder
public class ExtentTestHeirarchy {

	private List<Feature> features;
	private ExtentReports extent;
	@Default
	private boolean displayAllHooks = true;
	@Default
	private boolean strictCucumber6Behavior = true;

	private final Map<String, ExtentTest> uriFeatureTestMap = new HashMap<>();
	private final Map<String, ExtentTest> uriLinesScenarioOutlineTestMap = new HashMap<>();

	private final FeatureProcessor featureProcessor = FeatureProcessor.builder().build();
	private final ScenarioProcessor scenarioProcessor = ScenarioProcessor.builder().build();
	private final StepProcessor stepProcessor = StepProcessor.builder().build();

	@Default
	private AdditionalInformationProcessor featureAddInfoProcessor = AdditionalInformationProcessor
			.noAddInfoProcessor();
	@Default
	private AdditionalInformationProcessor scenarioAddInfoProcessor = AdditionalInformationProcessor
			.noAddInfoProcessor();

	public void createTestHeirarchy() {
		features.forEach(feature -> {

			ExtentTest featureTest = createFeatureExtentTest(feature);
			featureAddInfoProcessor.process(featureTest, feature);

			feature.getElements().forEach(scenario -> {

				ExtentTest scenarioTest = createScenarioExtentTest(featureTest, feature, scenario);
				scenarioAddInfoProcessor.process(scenarioTest, scenario);

				createStepHookExtentTests(scenarioTest, scenario);
			});
		});
	}

	private ExtentTest createFeatureExtentTest(Feature feature) {
		featureProcessor.setFeature(feature);
		featureProcessor.process();

		String uri = feature.getUri();
		if (uriFeatureTestMap.containsKey(uri))
			return uriFeatureTestMap.get(uri);

		ExtentTest featureExtentTest = FeatureExtentTest.builder().feature(feature).extent(extent).build().createTest();
		uriFeatureTestMap.put(uri, featureExtentTest);

		return featureExtentTest;
	}

	private ExtentTest createScenarioExtentTest(ExtentTest parentTest, Feature feature, Scenario scenario) {
		scenarioProcessor.setFeature(feature);
		scenarioProcessor.setScenario(scenario);
		scenarioProcessor.process();

		if (scenario.getKeyword().equalsIgnoreCase("Scenario Outline"))
			parentTest = createScenarioOutlineExtentTest(parentTest, scenario);

		ExtentTest scenarioExtentTest = ScenarioExtentTest.builder().scenario(scenario).parentTest(parentTest).build()
				.createTest();

		return scenarioExtentTest;
	}

	private ExtentTest createScenarioOutlineExtentTest(ExtentTest parentTest, Scenario scenario) {
		String uriStepLines = scenario.getUriStepLines();
		if (uriLinesScenarioOutlineTestMap.containsKey(uriStepLines))
			return uriLinesScenarioOutlineTestMap.get(uriStepLines);

		ExtentTest scenarioOutlineExtentTest = ScenarioOutlineExtentTest.builder().scenarioOutline(scenario)
				.parentTest(parentTest).build().createTest();
		uriLinesScenarioOutlineTestMap.put(uriStepLines, scenarioOutlineExtentTest);

		return scenarioOutlineExtentTest;
	}

	private void createStepHookExtentTests(ExtentTest parentTest, Scenario scenario) {
		createBeforeHookExtentTests(parentTest, scenario);

		scenario.getSteps().forEach(step -> {
			stepProcessor.setStep(step);
			stepProcessor.process();

			createBeforeStepHookExtentTests(parentTest, step);

			createStepExtentNode(parentTest, step);

			createAfterStepHookExtentTests(parentTest, step);
		});

		createAfterHookExtentTests(parentTest, scenario);
	}

	private void createBeforeHookExtentTests(ExtentTest parentTest, Scenario scenario) {
		HookExtentTest.builder().parentTest(parentTest).hooks(scenario.getBefore()).displayAllHooks(displayAllHooks)
				.build().createTests();
	}

	private void createAfterHookExtentTests(ExtentTest parentTest, Scenario scenario) {
		HookExtentTest.builder().parentTest(parentTest).hooks(scenario.getAfter()).displayAllHooks(displayAllHooks)
				.build().createTests();
	}

	private void createBeforeStepHookExtentTests(ExtentTest parentTest, Step step) {
		HookExtentTest.builder().parentTest(parentTest).hooks(step.getBefore()).displayAllHooks(displayAllHooks).build()
				.createTests();
	}

	private void createAfterStepHookExtentTests(ExtentTest parentTest, Step step) {
		HookExtentTest.builder().parentTest(parentTest).hooks(step.getAfter()).displayAllHooks(displayAllHooks).build()
				.createTests();
	}

	private void createStepExtentNode(ExtentTest parentTest, Step step) {
		StepExtentTest.builder().parentTest(parentTest).step(step).strictCucumber6Behavior(strictCucumber6Behavior)
				.build().createTest();
	}
}
