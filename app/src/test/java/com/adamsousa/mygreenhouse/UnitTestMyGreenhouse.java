package com.adamsousa.mygreenhouse;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;

import com.adamsousa.mygreenhouse.model.LocationModel;
import com.adamsousa.mygreenhouse.ui.BaseNavigationActivity;
import com.adamsousa.mygreenhouse.ui.fragments.locations.LocationsFragment;
import com.adamsousa.mygreenhouse.ui.fragments.plants.PlantsFragment;

import org.junit.Test;

public class UnitTestMyGreenhouse {
    private Context context = ApplicationProvider.getApplicationContext();

    @Test
    public void validPlantFragment() {
        // Given a Context object retrieved from Robolectric...
        BaseNavigationActivity mainActivity = new BaseNavigationActivity();

        // ...when the string is returned from the object under test...
        mainActivity.loadFragment(new PlantsFragment());

        PlantsFragment plantsFragment = (PlantsFragment) mainActivity.getSupportFragmentManager().getFragments().get(0);

        // ...then the result should be the expected one.
        if (plantsFragment.equals(new PlantsFragment())) {
            assert true;
        } else {
            assert false;
        }
    }

    public void validPlants() {
        // Given a Context object retrieved from Robolectric...
        BaseNavigationActivity mainActivity = new BaseNavigationActivity();

        // ...when the string is returned from the object under test...
        mainActivity.loadFragment(new PlantsFragment());

        PlantsFragment plantsFragment = (PlantsFragment) mainActivity.getSupportFragmentManager().getFragments().get(0);

        plantsFragment.getFireBaseLocationPlants(new LocationModel("#%345", "Kitchen", 1, "https:"));

        assert true;
    }

    public void validLocations() {
        // Given a Context object retrieved from Robolectric...
        BaseNavigationActivity mainActivity = new BaseNavigationActivity();

        // ...when the string is returned from the object under test...
        mainActivity.loadFragment(new LocationsFragment());

        LocationsFragment locationFragment = (LocationsFragment) mainActivity.getSupportFragmentManager().getFragments().get(0);

        locationFragment.getFireBaseLocations();

        assert true;
    }
}