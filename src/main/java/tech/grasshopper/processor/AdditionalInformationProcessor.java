package tech.grasshopper.processor;

import com.aventstack.extentreports.ExtentTest;

import lombok.Builder;
import tech.grasshopper.pojo.AdditionalData;

public interface AdditionalInformationProcessor {

	static AdditionalInformationProcessor noAddInfoProcessor() {
		return NoAddInfoProcessor.builder().build();
	}

	default void process(ExtentTest extentTest, AdditionalData additionalData) {
	}

	@Builder
	static class DefaultAddInfoProcessor implements AdditionalInformationProcessor {

		@Override
		public void process(ExtentTest extentTest, AdditionalData additionalData) {
			extentTest.getModel().getInfoMap().putAll(additionalData.getInfo());
		}
	}

	@Builder
	static class NoAddInfoProcessor implements AdditionalInformationProcessor {

	}
}
