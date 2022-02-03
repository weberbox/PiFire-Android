package com.weberbox.pifire.ui.fragments;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.tapadoo.alerter.Alerter;
import com.weberbox.pifire.R;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.utils.executors.AppExecutors;
import com.weberbox.pifire.database.RecipeDatabase;
import com.weberbox.pifire.databinding.FragmentRecipesBinding;
import com.weberbox.pifire.interfaces.OnRecipeItemCallback;
import com.weberbox.pifire.model.local.RecipesModel;
import com.weberbox.pifire.recycler.adapter.RecipesAdapter;
import com.weberbox.pifire.ui.activities.RecipeActivity;
import com.weberbox.pifire.ui.utils.ViewUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipesFragment extends Fragment implements OnRecipeItemCallback,
        SearchView.OnQueryTextListener, ActionMode.Callback {

    private FragmentRecipesBinding mBinding;
    private ExtendedFloatingActionButton mFloatingActionButton;
    private RecipesAdapter mAdapter;
    private RecipeDatabase mDb;
    private SwipeRefreshLayout mSwipeRefresh;
    private ActionMode mActionMode;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentRecipesBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recycler = mBinding.recipesRecycler;
        mFloatingActionButton = mBinding.fabAddRecipes;
        mSwipeRefresh = mBinding.recipesPullRefresh;

        mFloatingActionButton.shrink();

        if (getActivity() != null) {
            recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
            mFloatingActionButton.setOnClickListener(v -> {
                if (mFloatingActionButton.isExtended() && mActionMode != null) {
                    deleteSelectedItems();
                    mActionMode.finish();
                } else {
                    startNewRecipeFragment();
                }
            });
        }

        if (getActivity() != null && getActivity().getApplicationContext() != null) {
            mDb = RecipeDatabase.getInstance(getActivity().getApplicationContext());
        }

        mAdapter = new RecipesAdapter(this);
        recycler.setNestedScrollingEnabled(false);
        recycler.setAdapter(mAdapter);

        mSwipeRefresh.setOnRefreshListener(this::retrieveRecipes);

    }

    @Override
    public void onResume() {
        super.onResume();
        retrieveRecipes();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mActionMode != null) {
            mActionMode.finish();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBinding = null;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search, menu);

        if (getActivity() != null) {
            SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
            final MenuItem searchItem = menu.findItem(R.id.action_search);
            SearchView searchView = (SearchView) searchItem.getActionView();
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
            searchView.setMaxWidth(Integer.MAX_VALUE);
            searchView.setOnQueryTextListener(this);
        }
    }

    @Override
    public boolean onQueryTextChange(String query) {
        mAdapter.getFilter().filter(query);
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mAdapter.getFilter().filter(query);
        return false;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        menu.clear();
        if (getActivity() != null) {
            ViewUtils.toggleStatusBarColor(getActivity(), true);
        }
        mFloatingActionButton.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_delete));
        mFloatingActionButton.extend();
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        mAdapter.clearSelectedState();
        mActionMode = null;
        if (!mSwipeRefresh.isEnabled()) mSwipeRefresh.setEnabled(true);
        if (getActivity() != null) {
            ViewUtils.toggleStatusBarColor(getActivity(), false);
        }
        mFloatingActionButton.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_add_white));
        mFloatingActionButton.shrink();
    }

    @Override
    public void onRecipeSelected(int recipeId) {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), RecipeActivity.class);
            intent.putExtra(Constants.INTENT_RECIPE_FRAGMENT, Constants.FRAG_VIEW_RECIPE);
            intent.putExtra(Constants.INTENT_RECIPE_ID, recipeId);
            startActivity(intent);
        }
    }

    @Override
    public void onRecipeMultiSelect() {
        if (mActionMode == null && getActivity() != null) {
            mActionMode = getActivity().startActionMode(this);
        }
        if (mSwipeRefresh.isEnabled()) mSwipeRefresh.setEnabled(false);
        toggleSelection();
    }

    private void toggleSelection() {
        int count = mAdapter.getSelectedItemCount();
        if (count == 0) {
            mAdapter.setIsInChoiceMode(false);
            mActionMode.finish();
        } else {
            mActionMode.setTitle(getString(R.string.action_item_selected, String.valueOf(count)));
            mActionMode.invalidate();
        }
    }

    private void deleteSelectedItems() {
        List<RecipesModel> recipes = mAdapter.getRecipes();
        List<RecipesModel> filtered = mAdapter.getFilteredRecipes();
        Map<Integer, RecipesModel> delRecipe = new HashMap<>();
        Map<Integer, RecipesModel> delRecipeFilter = new HashMap<>();

        for (int i = recipes.size() - 1; i >= 0; i--) {
            if (recipes.get(i).isSelected()) {
                RecipesModel recipe = recipes.get(i);
                int id = recipe.getId();
                recipes.remove(recipe);
                delRecipe.put(i, recipe);
                mAdapter.notifyItemRemoved(i);
                for (int j = filtered.size() - 1; j >= 0; j--) {
                    if (id == filtered.get(j).getId()) {
                        delRecipeFilter.put(j, filtered.get(j));
                        filtered.remove(j);
                    }
                }
            }
        }
        mAdapter.setIsInChoiceMode(false);

        if (mDb != null) {
            for (RecipesModel recipe : delRecipe.values()) {
                AppExecutors.getInstance().diskIO().execute(() -> mDb.recipeDao().delete(recipe));
            }

            if (getActivity() != null) {
                Alerter.create(getActivity())
                        .setText(R.string.recipes_removed)
                        .setIcon(R.drawable.ic_delete)
                        .setBackgroundColorRes(R.color.colorPrimaryLighter)
                        .setDuration(3000)
                        .setTextAppearance(R.style.Text14Aller)
                        .setIconSize(R.dimen.alerter_icon_size_small)
                        .addButton(getString(R.string.undo), R.style.AlerterButton, v -> {
                            for (Map.Entry<Integer, RecipesModel> pair : delRecipe.entrySet()) {
                                pair.getValue().setSelected(false);
                                AppExecutors.getInstance().diskIO().execute(() ->
                                        mDb.recipeDao().insert(pair.getValue()));

                                mAdapter.getRecipes().add(pair.getKey(), pair.getValue());
                                mAdapter.notifyItemInserted(pair.getKey());
                            }
                            for (Map.Entry<Integer, RecipesModel> filterPair :
                                    delRecipeFilter.entrySet()) {
                                filterPair.getValue().setSelected(false);
                                mAdapter.getFilteredRecipes().add(filterPair.getKey(),
                                        filterPair.getValue());
                            }
                            Alerter.hide();
                        }).show();
            }
        }
    }

    private void retrieveRecipes() {
        AppExecutors.getInstance().diskIO().execute(() -> {
            List<RecipesModel> recipeModels = mDb.recipeDao().loadAllRecipes();
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    mAdapter.setRecipes(recipeModels);
                    if (mSwipeRefresh.isRefreshing()) {
                        mSwipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
    }

    private void startNewRecipeFragment() {
        if (getActivity() != null) {
            Intent intent = new Intent(getActivity(), RecipeActivity.class);
            intent.putExtra(Constants.INTENT_RECIPE_FRAGMENT, Constants.FRAG_EDIT_RECIPE);
            intent.putExtra(Constants.INTENT_RECIPE_ID, -1);
            startActivity(intent);
        }
    }
}
