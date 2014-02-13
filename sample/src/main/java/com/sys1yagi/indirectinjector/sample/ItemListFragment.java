package com.sys1yagi.indirectinjector.sample;

import com.sys1yagi.indirectinjector.IndirectInjector;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import javax.inject.Inject;

public class ItemListFragment extends ListFragment {
    
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        IndirectInjector.inject(getActivity(), this);
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        if (mCallbacks != null) {
            mCallbacks.onItemSelected((String) listView.getItemAtPosition(position));
        }
    }
}
