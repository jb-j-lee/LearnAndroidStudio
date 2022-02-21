package com.apress.gerber.simplelayouts;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends FragmentActivity
        implements BuddyListFragment.OnListItemSelectedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(findViewById(R.id.empty_fragment_container)!=null) {
            // MainActivity 인스턴스가 다시 생성되어 복원될 때는
            // 프래그먼트를 중복 생성하지 않기 위해 return 한다.
            // to avoid overlapping fragments.
            if (savedInstanceState != null) {
                return;
            }
            BuddyListFragment buddyListFragment = new BuddyListFragment();

            // 인텐트 엑스트라 데이터를 프래그먼트 인자로 전달한다
            buddyListFragment.setArguments(getIntent().getExtras());
            FragmentTransaction transaction =
                    getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.empty_fragment_container, buddyListFragment);
            transaction.commit();
        }
    }

    @Override
    public void onListItemSelected(Person selectedPerson) {
        BuddyDetailFragment buddyDetailFragment = (BuddyDetailFragment)
                getSupportFragmentManager().findFragmentById(R.id.detail_fragment);
        if (buddyDetailFragment != null) {
            buddyDetailFragment.updateDetailView(selectedPerson);
        } else {
            buddyDetailFragment = new BuddyDetailFragment();
            Bundle args = new Bundle();
            args.putInt(BuddyDetailFragment.IMAGE, selectedPerson.image);
            args.putString(BuddyDetailFragment.NAME, selectedPerson.name);
            args.putString(BuddyDetailFragment.LOCATION, selectedPerson.location);
            args.putString(BuddyDetailFragment.WEBSITE, selectedPerson.website);
            args.putString(BuddyDetailFragment.DESCRIPTION, selectedPerson.descr);
            buddyDetailFragment.setArguments(args);

            FragmentTransaction transaction =
                    getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.empty_fragment_container, buddyDetailFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }
}