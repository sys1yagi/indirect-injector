package com.sys1yagi.indirectinjector.sample;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ItemDetailFragment extends Fragment {

    public static final String ARG_VALUE = "value";

    public static ItemDetailFragment newInstance(String itemId) {
        ItemDetailFragment fragment = new ItemDetailFragment();

        Bundle args = new Bundle();
        args.putString(ARG_VALUE, itemId);
        fragment.setArguments(args);

        return fragment;
    }

    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_item_detail, container, false);

        if (getArguments() != null) {
            ((TextView) rootView.findViewById(R.id.item_detail))
                    .setText(getArguments().getString(ARG_VALUE));
        }

        return rootView;
    }
}
