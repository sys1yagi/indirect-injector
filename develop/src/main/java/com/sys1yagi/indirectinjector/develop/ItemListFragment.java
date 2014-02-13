package com.sys1yagi.indirectinjector.develop;

import com.sys1yagi.indirectinjector.IndirectInjector;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import javax.inject.Inject;

public class ItemListFragment extends ListFragment {

    private final static String TAG = ItemListFragment.class.getSimpleName();

    public static ItemListFragment newInstance() {
        return new ItemListFragment();
    }

    @Inject
    private Callbacks mCallbacks;

    public interface Callbacks {

        public void onItemSelected(String id);
    }

    public ItemListFragment() {
    }

    public void setCallbacks(Callbacks callbacks) {
        mCallbacks = callbacks;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1);

        for (int i = 0; i < 20; i++) {
            adapter.add("item " + i);
        }

        setListAdapter(adapter);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated:" + getActivity());
        System.gc();
        IndirectInjector.sweep();
        IndirectInjector.inject(getActivity(), this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d(TAG, "onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        if (mCallbacks != null) {
            mCallbacks.onItemSelected((String) listView.getItemAtPosition(position));
        }
    }
}
