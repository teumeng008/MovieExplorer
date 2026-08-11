package com.rupp.movieexplorer.viewModel;

import androidx.lifecycle.ViewModel;
import com.rupp.movieexplorer.R;

/**
 * ViewModel to store the state of the Main Navigation.
 * This survives configuration changes (like screen rotation).
 */
public class NavViewModel extends ViewModel {
    // Stores the ID of the currently selected menu item
    private int selectedNavId = R.id.menu_home;

    public int getSelectedNavId() {
        return selectedNavId;
    }

    public void setSelectedNavId(int id) {
        this.selectedNavId = id;
    }
}
