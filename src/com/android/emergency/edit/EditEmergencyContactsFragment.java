/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.emergency.edit;

import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import com.android.emergency.PreferenceKeys;
import com.android.emergency.R;
import com.android.emergency.preferences.EmergencyContactsPreference;

import java.util.List;

/**
 * Fragment that displays emergency contacts. These contacts can be added or removed.
 */
public class EditEmergencyContactsFragment extends PreferenceFragment {
    private static final String TAG = "EditEmergencyContactsFragment";

    /** Result code for contact picker */
    private static final int CONTACT_PICKER_RESULT = 1001;

    /** The category that holds the emergency contacts. */
    private EmergencyContactsPreference mEmergencyContactsPreferenceCategory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.edit_emergency_contacts);
        mEmergencyContactsPreferenceCategory = (EmergencyContactsPreference)
                findPreference(PreferenceKeys.KEY_EMERGENCY_CONTACTS);

        Preference addEmergencyContact = findPreference(PreferenceKeys.KEY_ADD_CONTACT);
        addEmergencyContact.setOnPreferenceClickListener(new Preference
                .OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                // By using ContactsContract.CommonDataKinds.Phone.CONTENT_URI, the user is
                // presented with a list of contacts, with one entry per phone number.
                // The selected contact is guaranteed to have a name and phone number.
                Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                try {
                    startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
                    return true;
                } catch (ActivityNotFoundException e) {
                    Log.w(TAG, "No contact app available to display the contacts", e);
                    Toast.makeText(getContext(),
                                   getContext().getString(R.string.fail_load_contact_picker),
                                   Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        reloadFromPreference();
    }

    /** Reloads the contacts by reading the value from the shared preferences. */
    public void reloadFromPreference() {
        if (mEmergencyContactsPreferenceCategory != null) {
            mEmergencyContactsPreferenceCategory.reloadFromPreference();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CONTACT_PICKER_RESULT && resultCode == Activity.RESULT_OK) {
            Uri phoneUri = data.getData();
            mEmergencyContactsPreferenceCategory.addNewEmergencyContact(phoneUri);
        }
    }

    public static Fragment newInstance() {
        return new EditEmergencyContactsFragment();
    }
}
