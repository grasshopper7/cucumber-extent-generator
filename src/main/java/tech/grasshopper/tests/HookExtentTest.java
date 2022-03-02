package tech.grasshopper.tests;

import java.util.ArrayList;
import java.util.List;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.model.Test;

import lombok.Builder;
import lombok.Builder.Default;
import tech.grasshopper.pojo.Hook;
import tech.grasshopper.processor.EmbeddedProcessor;

@Builder
public class HookExtentTest {

	private List<Hook> hooks;

	private ExtentTest parentTest;

	@Default
	private boolean displayAllHooks = true;

	@Default
	private boolean strictCucumber6Behavior = true;

	public List<ExtentTest> createTests() {
		List<ExtentTest> hookTests = new ArrayList<>();

		if (!displayAllHooks)
			hooks.removeIf(h -> h.getEmbeddings().isEmpty() && h.getOutput().isEmpty());

		hooks.forEach(h -> hookTests.add(createTest(h)));
		return hookTests;
	}

	private ExtentTest createTest(Hook hook) {
		ExtentTest hookExtentTest = parentTest.createNode(com.aventstack.extentreports.gherkin.model.Asterisk.class,
				hook.getMatch().getLocation(), hook.getHookType().toString().toUpperCase());

		hook.setTestId(hookExtentTest.getModel().getId());

		hook.getOutput().forEach(o -> hookExtentTest.info(o));

		EmbeddedProcessor.builder().test(hookExtentTest).embeddings(hook.getEmbeddings()).build().process();

		ExtentTestResult.builder().extentTest(hookExtentTest).result(hook.getResult())
				.strictCucumber6Behavior(strictCucumber6Behavior).build().updateTestLogStatus();

		Test test = hookExtentTest.getModel();
		test.setStartTime(hook.getStartTime());
		test.setEndTime(hook.getEndTime());

		return hookExtentTest;
	}
}
