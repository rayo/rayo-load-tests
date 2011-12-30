from net.grinder.script.Grinder import grinder
from net.grinder.script import Test
from com.rayo.functional.load import LoadTest
 
# An instance of this class is created for every thread.
class TestRunner:
    # This method is called for every run.
    def __call__(self):

		loadTest = LoadTest()
		test = Test(1, "Log method")
		wrapper = test.wrap(loadTest)

        # Per thread scripting goes here.
		testResult = wrapper.loadTest()
		if testResult > 0:
			grinder.statistics.success = 0




