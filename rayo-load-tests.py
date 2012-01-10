# A script that demonstrates how the various parts of a script and
# their effects on worker threads.
 
# The "top level" of the script is called once for each worker
# process. Perform any one-off initialisation here. For example,
# import all the modules, set up shared data structures, and declare
# all the Test objects you will use.
 
from net.grinder.script.Grinder import grinder
from net.grinder.script import Test
from com.rayo.functional.load import LoadTest
from java.lang import System
from java.util.concurrent.locks import ReentrantLock

# The totalNumberOfRuns variable is shared by all worker threads.
totalNumberOfRuns = 0
testNumber = 0
rayolock = ReentrantLock()
 
# An instance of the TestRunner class is created for each worker thread.
class TestRunner:
	 
    # There's a runsForThread variable for each worker thread. This
    # statement specifies a class-wide initial value.
    runsForThread = 0
 
    # The __init__ method is called once for each thread.
    def __init__(self):
        self.barrier = grinder.barrier("initialization")
    
        global rayolock
        rayolock.lock()
        try:
            global testNumber        
            testNumber += 1
    
            # There's an initialisationTime variable for each worker thread.
            self.initialisationTime = System.currentTimeMillis()
            self.loadTest = LoadTest()
            self.loadTest.loadTest(testNumber)
        
            self.test = Test(testNumber, "Load Test")
            self.wrapper = self.test.wrap(self.loadTest) 
        
            grinder.logger.output("New thread started at time %s" %
                              self.initialisationTime)
        finally:
            rayolock.unlock()
        grinder.logger.output("Waiting for other threads to initialize")                  
        self.barrier.await()
 
    # The __call__ method is called once for each test run performed by
    # a worker thread.
    def __call__(self):
 
        # Turn off automatic reporting for the current worker thread.
        # Having done this, the script can modify or set the statistics
        # before they are sent to the log and the console.
        grinder.statistics.delayReports = 1    
        
        # We really should synchronise this access to the shared
        # totalNumberOfRuns variable. See JMS receiver example for how
        # to use the Python Condition class.
        global totalNumberOfRuns
        totalNumberOfRuns += 1
 
        self.runsForThread += 1
 
        grinder.logger.output(
            "runsForThread=%d, totalNumberOfRuns=%d, initialisationTime=%d" %
            (self.runsForThread, totalNumberOfRuns, self.initialisationTime))
 		
        try:
            self.wrapper.testLoadScenario2()
        except:
            grinder.statistics.forLastTest.success = 0
 		
        # You can also vary behaviour based on thread ID.
        if grinder.threadNumber % 2 == 0:
            grinder.logger.output("I have an even thread ID.")
 
    # Scripts can optionally define a __del__ method. The Grinder
    # guarantees this will be called at shutdown once for each thread
    # It is useful for closing resources (e.g. database connections)
    # that were created in __init__.
    def __del__(self):
        grinder.logger.output("Thread shutting down") 