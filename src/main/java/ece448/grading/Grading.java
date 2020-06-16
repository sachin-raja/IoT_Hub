package ece448.grading;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class Grading {
	static void run(Object obj, int n) {
		ExecutorService exe = Executors.newSingleThreadExecutor();
		String className = obj.getClass().getSimpleName();		
		int grade = 0;
		try
		{
			for (int i = 0; i < n; i++)
			{
				String testCaseName = String.format("testCase%02d", i);
				try
				{
					Method testCase = obj.getClass().getDeclaredMethod(testCaseName);

					Future<Boolean> f = exe.submit(() -> {
						return (Boolean)testCase.invoke(obj);
					});

					if (f.get(60, TimeUnit.SECONDS))
					{
						logger.info("{}-{}: success", className, testCaseName);
						++grade;
					}
					else
					{
						logger.info("{}-{}: failed", className, testCaseName);
					}
				}
				catch (ExecutionException e)
				{
					logger.info("{}-{}: exception", className, testCaseName, e.getCause());
				}
				catch (TimeoutException e)
				{
					logger.info("{}-{}: timeout, abort", className, testCaseName);
					throw new RuntimeException(e);
				}
				catch (Throwable t)
				{
					logger.info("{}-{}: unknown error, abort", className, testCaseName, t);
					throw new RuntimeException(t);
				}
			}
		}
		finally
		{
			logger.info("{}: grade {}", className, grade);
			System.out.printf("@name %s, grade %d%n", className, grade);
			exe.shutdownNow();
		}
	}

	private static final Logger logger = LoggerFactory.getLogger(Grading.class);
}
