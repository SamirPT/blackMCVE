package tech.peller.mrblackandroidwatch.utils.imageSlider;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import roboguice.fragment.provided.RoboFragment;
import roboguice.inject.InjectView;
import tech.peller.mrblackandroidwatch.R;
import tech.peller.mrblackandroidwatch.models.reservation.SignatureTO;
import tech.peller.mrblackandroidwatch.models.venue.ImageWithTitle;

/**
 * Created by Sam (samir@peller.tech) on 21.04.2017
 */

public class SliderFragment extends RoboFragment {
    @InjectView(R.id.imageVP)
    ViewPager viewPager;
    @InjectView(R.id.elementCounter)
    private TextView elementCounter;
    @InjectView(R.id.elementTitle)
    private TextView elementTitle;

    private List<ImageWithTitle> imagesList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.image_slider, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupViewPager();
    }

    private void setupViewPager() {
        FragmentPagerAdapter pagerAdapter = new FragmentPagerAdapter(getFragmentManager()) {
            @Override
            public int getCount() {
                return imagesList.size();
            }

            @Override
            public Fragment getItem(int position) {
                SliderElementFragment elementFragment = new SliderElementFragment();
                elementFragment.setImage(imagesList.get(position));
                return elementFragment;
            }
        };

        viewPager.setAdapter(pagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(imagesList.size() > 1) {
                    String positionText = (position + 1) + " of " + imagesList.size();
                    elementCounter.setText(positionText);
                } else {
                    elementCounter.setVisibility(View.GONE);
                }
                if(imagesList.get(position).getTitle() != null) {
                    elementTitle.setText(imagesList.get(position).getTitle());
                }
            }

            @Override
            public void onPageSelected(int position) {}

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    public void setImage(ImageWithTitle image) {
        List<ImageWithTitle> list = new ArrayList<>();
        list.add(image);
        this.imagesList.addAll(list);
    }

    public void setSignatures(List<SignatureTO> signatures) {
        List<ImageWithTitle> list = new ArrayList<>();
        for(SignatureTO element : signatures) {
            ImageWithTitle imageWithTitle = new ImageWithTitle();
            imageWithTitle.setUrl(element.getUrl());
            list.add(imageWithTitle);
        }
        this.imagesList.addAll(list);
    }
}
