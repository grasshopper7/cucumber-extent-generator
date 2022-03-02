package tech.grasshopper.processor;

import java.util.List;
import java.util.logging.Logger;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;

import lombok.Builder;
import tech.grasshopper.pojo.Embedded;

@Builder
public class EmbeddedProcessor {

	// Move this to json deserializer
	// private static final AtomicInteger EMBEDDED_INT = new AtomicInteger(0);

	private ExtentTest test;
	private List<Embedded> embeddings;

	private final static Logger logger = Logger.getLogger(EmbeddedProcessor.class.getName());

	public void process() {
		for (Embedded embed : embeddings) {

			String name = embed.getName() == null ? "" : embed.getName();
			String filePath = embed.getFilePath();

			if (filePath == null || filePath.isEmpty()) {
				logger.warning(String.format("Skipping adding embedded file as filepath is empty for step - '%s'.",
						test.getModel().getName()));
				return;
			}
			try {
				test.info(name, MediaEntityBuilder.createScreenCaptureFromPath(filePath).build());
			} catch (Exception e) {
				logger.warning(String.format("Skipping adding embedded file for step - '%s' as error in processing.",
						test.getModel().getName()));
				return;
			}
		}
	}
}
