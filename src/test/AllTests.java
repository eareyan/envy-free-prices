package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import test.algorithms.SearchMetaHeuristicTest;
import test.factory.RandomMarketFactoryTest;
import test.factory.SingletonMarketFactoryTest;
import test.factory.UnitMarketFactoryTest;

@RunWith(Suite.class)
@SuiteClasses({ RandomMarketFactoryTest.class, SingletonMarketFactoryTest.class, UnitMarketFactoryTest.class, SearchMetaHeuristicTest.class })
public class AllTests {

}
