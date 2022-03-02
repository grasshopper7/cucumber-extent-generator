package tech.grasshopper.processor;

import java.lang.reflect.Constructor;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Builder;
import tech.grasshopper.pojo.Result;

@Builder
public class ExceptionProcessor {

	private Result result;

	private final static Logger logger = Logger.getLogger(ExceptionProcessor.class.getName());

	public Throwable process() {
		String[] details = retrieveExceptionNameAndStack(result.getErrorMessage());

		String exceptionClzName = details[0];
		String exceptionMessage = details[1];

		return createThrowableInstance(exceptionClzName, exceptionMessage);
	}

	private String[] retrieveExceptionNameAndStack(String stackTrace) {
		String[] details = { "", "" };

		Matcher m = Pattern.compile("\\R").matcher(stackTrace);
		// Exception stacktrace will always contain and end with newline character.
		if (m.find()) {
			String excepNameMsg = stackTrace.substring(0, m.start());

			int colonIndex = excepNameMsg.indexOf(":");
			if (colonIndex > -1) {
				// Name: Msg\Rat stacktrace\R
				details[0] = excepNameMsg.substring(0, colonIndex);
				details[1] = stackTrace.substring(colonIndex + 2);
			} else {
				// Name\Rat stacktrace\R
				details[0] = excepNameMsg;
				details[1] = stackTrace.substring(m.start());
			}
		}
		return details;
	}

	private Throwable createThrowableInstance(String className, String message) {
		Class<?> throwableClass = null;

		try {
			throwableClass = Class.forName(className);
			if (!Throwable.class.isAssignableFrom(throwableClass))
				throw new ClassNotFoundException();
		} catch (ClassNotFoundException e) {
			logger.warning(className + " class cannot be found or not an instance of Throwable.");
			return new Exception("Generic Exception for " + className + " : " + message);
		}
		return createThrowableInstance(className, message, throwableClass);
	}

	private Throwable createThrowableInstance(String className, String message, Class<?> throwableClass) {
		Constructor<?> throwableConstructor = null;
		Throwable throwableInstance = null;

		try {
			if (message.isEmpty()) {
				throwableConstructor = throwableClass.getConstructor();
				throwableInstance = (Throwable) throwableConstructor.newInstance();
			} else {
				try {
					throwableConstructor = throwableClass.getConstructor(String.class);
				} catch (NoSuchMethodException e) {
					throwableConstructor = throwableClass.getConstructor(Object.class);
				}
				throwableInstance = (Throwable) throwableConstructor.newInstance(message);
			}
		} catch (ReflectiveOperationException | SecurityException e) {
			logger.warning(className + " constructor cannot be found or cannot be instanciated.");
			throwableInstance = new Exception("Generic Exception for " + className + " : " + message);
		}
		return throwableInstance;
	}
}
