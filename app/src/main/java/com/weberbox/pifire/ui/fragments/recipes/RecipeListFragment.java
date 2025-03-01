package com.weberbox.pifire.ui.fragments.recipes;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuProvider;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.weberbox.pifire.R;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.databinding.FragmentRecipeListBinding;
import com.weberbox.pifire.interfaces.RecipesCallback;
import com.weberbox.pifire.interfaces.ToolbarTitleCallback;
import com.weberbox.pifire.model.remote.RecipesModel;
import com.weberbox.pifire.model.remote.RecipesModel.RecipeDetails;
import com.weberbox.pifire.model.view.RecipesViewModel;
import com.weberbox.pifire.recycler.adapter.RecipeListAdapter;
import com.weberbox.pifire.recycler.adapter.RecipeListAdapter.OnRecipeItemCallback;
import com.weberbox.pifire.ui.utils.AnimUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.chrisbanes.insetter.Insetter;
import timber.log.Timber;

public class RecipeListFragment extends Fragment implements OnRecipeItemCallback,
        SearchView.OnQueryTextListener, ActionMode.Callback {

    private FragmentRecipeListBinding binding;
    private RecipesCallback recipesCallback;
    private ToolbarTitleCallback toolbarTitleCallback;
    private ExtendedFloatingActionButton fabDeleteRecipes;
    private CircularProgressIndicator progressIndicator;
    private RecipeListAdapter recipeListAdapter;
    private SwipeRefreshLayout swipeRefresh;
    private ActionMode actionMode;
    private MenuProvider searchMenu;
    private SearchView searchView;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRecipeListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recipesRecycler = binding.recipesRecycler;
        fabDeleteRecipes = binding.fabDeleteRecipes;
        swipeRefresh = binding.recipesPullRefresh;
        progressIndicator = binding.recipeListLoading;

        Insetter.builder()
                .padding(WindowInsetsCompat.Type.navigationBars())
                .applyToView(recipesRecycler);
        Insetter.builder()
                .margin(WindowInsetsCompat.Type.systemBars())
                .applyToView(fabDeleteRecipes);

        fabDeleteRecipes.setOnClickListener(v -> {
            if (fabDeleteRecipes.isExtended() && actionMode != null) {
                deleteSelectedItems();
                actionMode.finish();
            }
        });

        searchMenu = new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menu_search, menu);

                SearchManager searchManager = (SearchManager)
                        requireActivity().getSystemService(Context.SEARCH_SERVICE);
                final MenuItem searchItem = menu.findItem(R.id.action_search);
                searchView = (SearchView) searchItem.getActionView();
                if (searchView != null) {
                    searchView.setSearchableInfo(searchManager.getSearchableInfo(
                            requireActivity().getComponentName()));
                    searchView.setMaxWidth(Integer.MAX_VALUE);
                    searchView.setOnQueryTextListener(RecipeListFragment.this);
                }
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                return false;
            }
        };

        recipeListAdapter = new RecipeListAdapter(this);

        recipesRecycler.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recipesRecycler.setAdapter(recipeListAdapter);

        if (recipesCallback != null) {
            swipeRefresh.setOnRefreshListener(() -> recipesCallback.onRetrieveRecipes());
            swipeRefresh.setEnabled(false);
        }

        if (toolbarTitleCallback != null) {
            toolbarTitleCallback.onTitleChange(getString(R.string.menu_recipes));
        }

        RecipesViewModel recipesViewModel = new ViewModelProvider(
                requireActivity()).get(RecipesViewModel.class);
        recipesViewModel.getRecipesData().observe(getViewLifecycleOwner(), recipesData -> {
            swipeRefresh.setEnabled(true);
            hideSwipeRefresh();
            if (recipesData != null) {
                updateUIWithData(recipesData);
            }
        });

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            recipesCallback = (RecipesCallback) context;
            toolbarTitleCallback = (ToolbarTitleCallback) context;
        } catch (ClassCastException e) {
            Timber.e(e, "Activity does not implement callback");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (searchMenu != null) {
            requireActivity().addMenuProvider(searchMenu);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (searchMenu != null) {
            requireActivity().removeMenuProvider(searchMenu);
        }
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
    public boolean onQueryTextChange(String query) {
        recipeListAdapter.getFilter().filter(query);
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        recipeListAdapter.getFilter().filter(query);
        recipeListAdapter.getFilter().filter(null);
        return false;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        menu.clear();
        fabDeleteRecipes.extend();
        AnimUtils.fadeInAnimation(fabDeleteRecipes, 300);
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
        recipeListAdapter.clearSelectedState();
        actionMode = null;
        if (!swipeRefresh.isEnabled()) swipeRefresh.setEnabled(true);
        AnimUtils.fadeOutAnimation(fabDeleteRecipes, 300);
        fabDeleteRecipes.shrink();
    }

    @Override
    public void onRecipeSelected(ImageView imageView, String filename) {
        if (searchView != null) {
            searchView.setIconified(true);
        }
        Fragment fragment = new RecipeViewFragment();
        if (!filename.isBlank()) {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.INTENT_RECIPE_FILENAME, filename);
            fragment.setArguments(bundle);
        }
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_left,
                        R.anim.slide_out_left,
                        R.anim.slide_in_right,
                        R.anim.slide_out_right)
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(fragment.getClass().getName())
                .commit();
    }

    @Override
    public void onRecipeMultiSelect() {
        if (actionMode == null) {
            actionMode = requireActivity().startActionMode(this);
        }
        if (swipeRefresh.isEnabled()) swipeRefresh.setEnabled(false);
        toggleSelection();
    }

    private void toggleSelection() {
        int count = recipeListAdapter.getSelectedItemCount();
        if (count == 0) {
            recipeListAdapter.setIsInChoiceMode(false);
            actionMode.finish();
        } else {
            actionMode.setTitle(getString(R.string.action_item_selected, String.valueOf(count)));
            actionMode.invalidate();
        }
    }

    private void deleteSelectedItems() {
        List<RecipeDetails> recipes = recipeListAdapter.getRecipes();
        Map<Integer, RecipeDetails> delRecipe = new HashMap<>();

        for (int i = recipes.size() - 1; i >= 0; i--) {
            if (recipes.get(i).isSelected()) {
                RecipeDetails recipe = recipes.get(i);
                recipes.remove(recipe);
                delRecipe.put(i, recipe);
            }
        }
        recipeListAdapter.setIsInChoiceMode(false);

        for (RecipeDetails recipe : delRecipe.values()) {
            if (recipesCallback != null) {
                recipesCallback.onRecipeDelete(recipe.getFilename());
            }
        }
    }

    private void updateUIWithData(RecipesModel recipesModel) {
        if (recipesModel.getRecipeDetails() != null) {
            requireActivity().runOnUiThread(() -> {
                recipeListAdapter.setRecipes(recipesModel.getRecipeDetails());
                progressIndicator.setVisibility(View.GONE);
            });
        }
    }

    private void hideSwipeRefresh() {
        if (swipeRefresh.isRefreshing()) {
            swipeRefresh.setRefreshing(false);
        }
    }

}