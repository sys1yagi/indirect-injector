package com.sys1yagi.indirectinjector.develop;

import com.sys1yagi.indirectinjector.IndirectInjector;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

public class MainActivity extends FragmentActivity {

    private final static String TAG = ItemListFragment.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate Activity:" + this);

        IndirectInjector.addDependency(this, new ItemListFragment.Callbacks() {
            @Override
            public void onItemSelected(String id) {
                FragmentManager fragmentManager = getSupportFragmentManager();

                ItemDetailFragment fragment = ItemDetailFragment.newInstance(id);
                fragmentManager.beginTransaction()
                        .addToBackStack(null)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(android.R.id.content, fragment)
                        .commit();

            }
        }, true);

        setContentView(R.layout.activity_item_list);
        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();

            ItemListFragment fragment = ItemListFragment.newInstance();
            fragmentManager.beginTransaction()
                    .add(android.R.id.content, fragment)
                    .commit();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IndirectInjector.releaseDependencies(this);
    }
}
