package tech.peller.mrblackandroidwatch.utils;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.annotation.IdRes;

import com.google.inject.Singleton;


@Singleton
public class FragmentUtils {

    public void changeFragmentContent(android.app.FragmentManager fragmentManager, android.app.Fragment fragment, @IdRes int containerViewId) {
        changeFragmentContent(fragmentManager, fragment, containerViewId, true);
    }

    public void changeFragmentContent(android.app.FragmentManager fragmentManager, android.app.Fragment fragment, @IdRes int containerViewId, Boolean addToBackStack) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (addToBackStack) fragmentTransaction.addToBackStack(fragment.getClass().getSimpleName());
        fragmentTransaction.replace(containerViewId, fragment, fragment.getClass().getSimpleName()).attach(fragment).commitAllowingStateLoss();
    }

    /**
     * Получить предыдущий фрагмент из backStack
     *
     * @param fragmentManager - используем для работой со стэком фрагментов
     * @return Fragment - предыдущий фрагмент
     */
    public Fragment getPreviousBackStackFragment(FragmentManager fragmentManager) {
        int indexOfPreviousFragment = fragmentManager.getBackStackEntryCount() - 2;
        String tag = fragmentManager.getBackStackEntryAt(indexOfPreviousFragment).getName();
        return fragmentManager.findFragmentByTag(tag);
    }
}
