package ru.myocr.viewmodel;

import android.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import ru.myocr.model.City;
import rx.Scheduler;
import rx.android.plugins.RxAndroidPlugins;
import rx.android.plugins.RxAndroidSchedulersHook;
import rx.schedulers.Schedulers;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static ru.myocr.util.LiveDataTestUtil.getValue;

public class ShopAddingViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @BeforeClass
    public static void setUpRxSchedulers() {
        RxAndroidPlugins.getInstance().registerSchedulersHook(new TestRxAndroidSchedulersHook());
    }

    @Test
    public void getEmptyCityList() throws Exception {
        ShopAddingViewModel viewModel = new ShopAddingViewModel(new DataSourceImpl<>(Collections::emptyList));
        final List<City> emptyList = getValue(viewModel.getCities());
        assertTrue(emptyList.isEmpty());
    }

    @Test
    public void getSingleCityList() throws Exception {
        ShopAddingViewModel viewModel = new ShopAddingViewModel(new DataSourceImpl<>(
                () -> singletonList(new City(1, "Nsk"))));
        final List<City> oneCityList = getValue(viewModel.getCities());
        assertFalse(oneCityList.isEmpty());
        final City oneCity = oneCityList.get(0);
        assertEquals(new City(1, "Nsk"), oneCity);
    }

    static class TestRxAndroidSchedulersHook extends RxAndroidSchedulersHook {
        @Override
        public Scheduler getMainThreadScheduler() {
            return Schedulers.immediate();
        }
    }
}