package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import test.algorithms.EVPApproximationTest;
import test.algorithms.MaxWEQReservePricesTest;
import test.algorithms.MaxWEQTest;
import test.algorithms.SearchMetaHeuristicTest;
import test.algorithms.SimplePricingTest;
import test.algorithms.UnlimitedSupplyApproximationTest;
import test.factory.RandomMarketFactoryTest;
import test.factory.SingletonMarketFactoryTest;
import test.factory.UnitMarketFactoryTest;

@RunWith(Suite.class)
@SuiteClasses({ RandomMarketFactoryTest.class, SingletonMarketFactoryTest.class, UnitMarketFactoryTest.class, SearchMetaHeuristicTest.class,
    MaxWEQReservePricesTest.class, UnitDemandMarkets.class, MaxWEQTest.class, EVPApproximationTest.class, UnlimitedSupplyApproximationTest.class, SimplePricingTest.class })
public class AllTests {

}
