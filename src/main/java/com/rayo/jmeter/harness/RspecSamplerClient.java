package com.rayo.jmeter.harness;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

public class RspecSamplerClient extends AbstractJavaSamplerClient {

	public static final String FUNCTIONAL_TESTS_HOME = "functional.tests.home";
	public static final String FUNCTIONAL_TESTS_LAUNCHER = "functional.tests.launcher";
	
	public static final List<Integer> ports = new ArrayList<Integer>();
	public static final Lock lock = new ReentrantLock();
	
	@Override
	public Arguments getDefaultParameters() {
		
		Arguments arguments = new Arguments();
		arguments.addArgument(FUNCTIONAL_TESTS_LAUNCHER,"");
		arguments.addArgument(FUNCTIONAL_TESTS_HOME,"");
		return arguments;
	}
	
	@Override
	public SampleResult runTest(JavaSamplerContext context)  {
		
		String home = context.getParameter(FUNCTIONAL_TESTS_HOME);
		String launcher = context.getParameter(FUNCTIONAL_TESTS_LAUNCHER);
		int drbPort = getAvailablePort();
		String threadName = Thread.currentThread().getName();
		int threadNumber = getThreadNumber(threadName);
		String username = buildUsername(threadNumber);
		String jid = username + "@127.0.0.1";
		String tropoSipUri= jid+":5060";
		
		SampleResult result = new SampleResult();
		result.setSampleLabel("Running RSpec test");
		int exitValue = -2;
		
	    List<String> commands = new ArrayList<String>();
	    commands.add("/bin/bash");
	    commands.add("-c");
	    commands.add(launcher + " " + home + " " + drbPort + " " + jid + " " + tropoSipUri);
	    SystemCommandExecutor commandExecutor = new SystemCommandExecutor(commands, threadName);
	    
	    // execute the command
	    try {
	    	result.sampleStart();
	    	exitValue = commandExecutor.executeCommand();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			result.sampleEnd();
			if (exitValue == 0) {
				result.setSuccessful(true);
			} else {
				result.setSuccessful(false);
			}
			releasePort(drbPort);
		}
		System.out.println(Formatter.format("The numeric result of the command was: " + exitValue));
		
		return result;
	}

	
	private String buildUsername(int threadNumber) {

		return "user" + threadNumber;
	}

	private int getThreadNumber(String threadName) {

		return Integer.parseInt(threadName.substring(threadName.lastIndexOf('-') + 1, threadName.length()));
	}

	public static void main(String[] args) throws Exception {
		
	    // build the system command we want to run
	    List<String> commands = new ArrayList<String>();
	    int port = 2666;
	    commands.add("/bin/bash");
	    commands.add("-c");
	    commands.add("/home/martin/Workspace-2.6.0/jmeter-test/scripts/script.sh /home/martin/git/tropo/tropo2_functional_tester " + port);

	    // execute the command
	    SystemCommandExecutor commandExecutor = new SystemCommandExecutor(commands,Thread.currentThread().getName());
	    int result = commandExecutor.executeCommand();
	    
	    // print the stdout and stderr
	    System.out.println(Formatter.format("The numeric result of the command was: " + result));

	}
	
	public static int getAvailablePort() {
		
		try {
			lock.lock();
			int port = 0;
			boolean valid = true; // can be used later to check used ports.
			do {
				port = RandomUtils.nextInt(10000) + 20000;
			} while (ports.contains(port) || !valid);
			
			ports.add(port);
			
			return port;
		} finally {
			lock.unlock();
		}
	}
	
	public static void releasePort(int port) {
		
		if (port != 0) {
			try {
				lock.lock();
				ports.remove(ports.indexOf(port));
			} finally {
				lock.unlock();
			}
		}
	}
}
