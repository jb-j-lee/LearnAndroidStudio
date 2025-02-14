package com.apress.gerber.simplelayouts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

public class BuddyDetailFragment extends Fragment {
    public static final String IMAGE = "IMAGE";
    public static final String NAME = "NAME";
    public static final String LOCATION = "LOCATION";
    public static final String WEBSITE = "WEBSITE";
    public static final String DESCRIPTION = "DESCRIPTION";
    private Person person;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle bundle) {
        updatePersonDetail(bundle);
        return inflater.inflate(R.layout.relative_example, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        updatePersonDetail(getArguments());
    }

    private void updatePersonDetail(Bundle bundle) {
        if (bundle != null) {
            this.person = new Person(
                    bundle.getInt(IMAGE),
                    bundle.getString(NAME),
                    bundle.getString(LOCATION),
                    bundle.getString(WEBSITE),
                    bundle.getString(DESCRIPTION)
            );
        }
        if(this.person !=null){
            updateDetailView(this.person);
        }
    }

    public void updateDetailView(Person person) {
        FragmentActivity activity = getActivity();
        ImageView profileImage = (ImageView) activity.findViewById(R.id.profile_image);
        TextView name = (TextView) activity.findViewById(R.id.name);
        TextView location = (TextView) activity.findViewById(R.id.location);
        TextView website = (TextView) activity.findViewById(R.id.website);
        EditText description = (EditText) activity.findViewById(R.id.description);

        profileImage.setImageDrawable(getResources().getDrawable(person.image));
        name.setText(person.name);
        location.setText(person.location);
        website.setText(person.website);
        description.setText(person.descr);
    }
}
