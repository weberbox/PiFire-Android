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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.skydoves.androidveil.VeilRecyclerFrameView;
import com.tapadoo.alerter.Alerter;
import com.weberbox.pifire.R;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.database.RecipeDatabase;
import com.weberbox.pifire.databinding.FragmentRecipesBinding;
import com.weberbox.pifire.interfaces.OnRecipeItemCallback;
import com.weberbox.pifire.model.local.RecipesModel;
import com.weberbox.pifire.recycler.adapter.RecipesAdapter;
import com.weberbox.pifire.ui.activities.RecipeActivity;
import com.weberbox.pifire.ui.utils.ViewUtils;
import com.weberbox.pifire.utils.executors.AppExecutors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipesFragment extends Fragment implements OnRecipeItemCallback,
        SearchView.OnQueryTextListener, ActionMode.Callback {

    private FragmentRecipesBinding binding;
    private ExtendedFloatingActionButton floatingActionButton;
    private VeilRecyclerFrameView recipesRecycler;
    private RecipesAdapter adapter;
    private RecipeDatabase database;
    private SwipeRefreshLayout swipeRefresh;
    private ActionMode actionMode;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRecipesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recipesRecycler = binding.recipesRecycler;
        floatingActionButton = binding.fabAddRecipes;
        swipeRefresh = binding.recipesPullRefresh;

        floatingActionButton.shrink();
        floatingActionButton.setOnClickListener(v -> {
            if (floatingActionButton.isExtended() && actionMode != null) {
                deleteSelectedItems();
                actionMode.finish();
            } else {
                startNewRecipeFragment();
            }
        });

        if (getActivity() != null && getActivity().getApplicationContext() != null) {
            database = RecipeDatabase.getInstance(getActivity().getApplicationContext());
        }

        adapter = new RecipesAdapter(this);

        int padding = getResources().getDimensionPixelOffset(R.dimen.recycler_padding);

        recipesRecycler.getRecyclerView().setClipToPadding(false);
        recipesRecycler.getRecyclerView().setPadding(0, padding,0, padding);
        recipesRecycler.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recipesRecycler.setAdapter(adapter);
        recipesRecycler.addVeiledItems(10);

        swipeRefresh.setOnRefreshListener(this::retrieveRecipes);

        retrieveRecipes();

    }

    @Override
    public void onResume() {
        super.onResume();
        retrieveRecipes();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (actionMode != null) {
            actionMode.finish();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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
        adapter.getFilter().filter(query);
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        adapter.getFilter().filter(query);
        return false;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        menu.clear();
        if (getActivity() != null) {
            ViewUtils.toggleStatusBarColor(getActivity(), true);
        }
        floatingActionButton.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_delete));
        floatingActionButton.extend();
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
        adapter.clearSelectedState();
        actionMode = null;
        if (!swipeRefresh.isEnabled()) swipeRefresh.setEnabled(true);
        if (getActivity() != null) {
            ViewUtils.toggleStatusBarColor(getActivity(), false);
        }
        floatingActionButton.setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_add_white));
        floatingActionButton.shrink();
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
        if (actionMode == null && getActivity() != null) {
            actionMode = getActivity().startActionMode(this);
        }
        if (swipeRefresh.isEnabled()) swipeRefresh.setEnabled(false);
        toggleSelection();
    }

    private void toggleSelection() {
        int count = adapter.getSelectedItemCount();
        if (count == 0) {
            adapter.setIsInChoiceMode(false);
            actionMode.finish();
        } else {
            actionMode.setTitle(getString(R.string.action_item_selected, String.valueOf(count)));
            actionMode.invalidate();
        }
    }

    private void deleteSelectedItems() {
        List<RecipesModel> recipes = adapter.getRecipes();
        List<RecipesModel> filtered = adapter.getFilteredRecipes();
        Map<Integer, RecipesModel> delRecipe = new HashMap<>();
        Map<Integer, RecipesModel> delRecipeFilter = new HashMap<>();

        for (int i = recipes.size() - 1; i >= 0; i--) {
            if (recipes.get(i).isSelected()) {
                RecipesModel recipe = recipes.get(i);
                int id = recipe.getId();
                recipes.remove(recipe);
                delRecipe.put(i, recipe);
                adapter.notifyItemRemoved(i);
                for (int j = filtered.size() - 1; j >= 0; j--) {
                    if (id == filtered.get(j).getId()) {
                        delRecipeFilter.put(j, filtered.get(j));
                        filtered.remove(j);
                    }
                }
            }
        }
        adapter.setIsInChoiceMode(false);

        if (database != null) {
            for (RecipesModel recipe : delRecipe.values()) {
                AppExecutors.getInstance().diskIO().execute(() -> database.recipeDao().delete(recipe));
            }

            if (getActivity() != null) {
                Alerter.create(getActivity())
                        .setText(R.string.recipes_removed)
                        .setIcon(R.drawable.ic_delete)
                        .setBackgroundColorRes(R.color.colorPrimaryLighter)
                        .setDuration(3000)
                        .setTextAppearance(R.style.Text14AllerBold)
                        .setIconSize(R.dimen.alerter_icon_size_small)
                        .addButton(getString(R.string.undo), R.style.AlerterButton, v -> {
                            for (Map.Entry<Integer, RecipesModel> pair : delRecipe.entrySet()) {
                                pair.getValue().setSelected(false);
                                AppExecutors.getInstance().diskIO().execute(() ->
                                        database.recipeDao().insert(pair.getValue()));

                                adapter.getRecipes().add(pair.getKey(), pair.getValue());
                                adapter.notifyItemInserted(pair.getKey());
                            }
                            for (Map.Entry<Integer, RecipesModel> filterPair :
                                    delRecipeFilter.entrySet()) {
                                filterPair.getValue().setSelected(false);
                                adapter.getFilteredRecipes().add(filterPair.getKey(),
                                        filterPair.getValue());
                            }
                            Alerter.hide();
                        }).show();
            }
        }
    }

    private void retrieveRecipes() {
        AppExecutors.getInstance().diskIO().execute(() -> {
            List<RecipesModel> recipeModels = database.recipeDao().loadAllRecipes();
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    adapter.setRecipes(recipeModels);
                    recipesRecycler.unVeil();
                    recipesRecycler.getVeiledRecyclerView().setVisibility(View.GONE);
                    if (swipeRefresh.isRefreshing()) {
                        swipeRefresh.setRefreshing(false);
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
