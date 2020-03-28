package com.shimadove.coronago;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class ProfileFragment extends Fragment {

    Toolbar toolbar;
    Spinner spinner;
    SharedPreferences preferences;
    SharedPreferences.Editor preferencesEditor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Getting the toolbar with the Language spinner setup
        toolbar = view.findViewById(R.id.profile_toolbar);
        spinner = view.findViewById(R.id.language_select_spinner);

        toolbar.setLogo(R.mipmap.logo);

        preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        preferencesEditor = preferences.edit();

        // Managing spinner, default setting to appropriate language.
        ArrayAdapter<String> languagesAdapter = new ArrayAdapter<String>(getActivity(), R.layout.profile_spinner_item, getResources().getStringArray(R.array.languages));
        languagesAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(languagesAdapter);
        String language = preferences.getString("lang", "English");
        int spinnerPosition = languagesAdapter.getPosition(language);
        spinner.setSelection(spinnerPosition);



        // Saving language info.
        spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                preferencesEditor.putString("lang", spinner.getSelectedItem().toString());
                preferencesEditor.apply();
            }
        });

        // Manage all button intents.

        return view;
    }

}
