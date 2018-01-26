package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import test.algorithms.EVPApproximationTest;
import test.algorithms.EgalitarianMaxAllocationTest;
import test.algorithms.GreedyAllocationTest;
import test.algorithms.MaxWEQReservePricesTest;
import test.algorithms.MaxWEQTest;
import test.algorithms.RestrictedEnvyFreePricesLPTest;
import test.algorithms.RevMaxHeuristicTest;
import test.algorithms.SimplePricingTest;
import test.algorithms.SingleMindedPricingLPTest;
import test.algorithms.SingletonEVPTest;
import test.algorithms.SingletonPricingLPTest;
import test.algorithms.UnlimitedSupplyApproximationTest;
import test.algorithms.WelfareMaxAllocationILPTest;
import test.factory.RandomMarketFactoryTest;
import test.factory.SingletonMarketFactoryTest;
import test.factory.TACMarketFactoryTest;
import test.factory.UnitDemandMarketAllocationFactoryTest;
import test.factory.UnitMarketFactoryTest;
import test.structures.BiddersComparatorByRToSqrtIRatioTest;
import test.structures.GoodsComparatorByRemainingSupplyTest;
import test.structures.MarketAllocationTest;
import test.structures.MatchingTest;
import test.structures.PricesStatisticsTest;

@RunWith(Suite.class)
@SuiteClasses({ RandomMarketFactoryTest.class, SingletonMarketFactoryTest.class, UnitMarketFactoryTest.class, RevMaxHeuristicTest.class,
    MaxWEQReservePricesTest.class, UnitDemandMarkets.class, MaxWEQTest.class, EVPApproximationTest.class, UnlimitedSupplyApproximationTest.class,
    SimplePricingTest.class, SingletonPricingLPTest.class, SingleMindedPricingLPTest.class, SingletonMarkets.class, RestrictedEnvyFreePricesLPTest.class,
    UnitDemandMarketAllocationFactoryTest.class, MatchingTest.class, SingletonEVPTest.class, MarketAllocationTest.class, PricesStatisticsTest.class,
    EgalitarianMaxAllocationTest.class, TACMarketFactoryTest.class, WelfareMaxAllocationILPTest.class, BiddersComparatorByRToSqrtIRatioTest.class,
    GoodsComparatorByRemainingSupplyTest.class, GreedyAllocationTest.class })
public class AllTests {

}
