package com.weberbox.pifire.ui.fragments.recipes;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.NestedScrollView.OnScrollChangeListener;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.weberbox.pifire.R;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.database.AppExecutors;
import com.weberbox.pifire.database.RecipeDatabase;
import com.weberbox.pifire.databinding.FragmentRecipeEditBinding;
import com.weberbox.pifire.interfaces.RecipeEditCallback;
import com.weberbox.pifire.interfaces.StartDragListener;
import com.weberbox.pifire.model.local.RecipesModel;
import com.weberbox.pifire.model.local.RecipesModel.RecipeItems;
import com.weberbox.pifire.recycler.adapter.RecipeEditAdapter;
import com.weberbox.pifire.recycler.callback.ItemMoveCallback;
import com.weberbox.pifire.recycler.callback.SwipeToDeleteCallback;
import com.weberbox.pifire.ui.activities.ImagePickerActivity;
import com.weberbox.pifire.ui.activities.RecipeActivity;
import com.weberbox.pifire.ui.dialogs.BottomIconDialog;
import com.weberbox.pifire.ui.dialogs.RecipeDiffDialog;
import com.weberbox.pifire.ui.dialogs.TimerPickerDialog;
import com.weberbox.pifire.utils.AlertUtils;
import com.weberbox.pifire.utils.FileUtils;
import com.weberbox.pifire.utils.TimeUtils;
import com.yalantis.ucrop.UCrop;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class RecipeEditFragment extends Fragment implements RecipeEditCallback {

    private FragmentRecipeEditBinding binding;
    private RecipeEditAdapter instructionsAdapter;
    private RecipeEditAdapter ingredientsAdapter;
    private FloatingActionButton floatingActionButton;
    private RecipeDatabase recipeDB;
    private RecipesModel recipe;
    private ItemTouchHelper ingredientsTouchHelper;
    private ItemTouchHelper instructionsTouchHelper;
    private TextInputEditText recipeName;
    private TextInputEditText recipeTime;
    private TextInputEditText recipeDifficulty;
    private EditText recipeNotes;
    private ImageView recipeImage;
    private int recipeId;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            recipeId = bundle.getInt(Constants.INTENT_RECIPE_ID, -1);
        } else {
            recipeId = -1;
        }

        // TODO check for unsaved changes
        //requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackCallback);

        if (getActivity() != null && getActivity().getApplicationContext() != null) {
            recipeDB = RecipeDatabase.getInstance(getActivity().getApplicationContext());
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRecipeEditBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recipeImage = binding.reImage;
        recipeName = binding.reNameText;
        recipeTime = binding.reTimeText;
        recipeDifficulty = binding.reDifficultyText;
        recipeNotes = binding.reNotesEditText;

        TextView addIngredient = binding.reIngredientsAddItem;
        TextView addIngredientSection = binding.reIngredientsAddSection;
        TextView addInstruction = binding.reInstructionsAddStep;
        TextView addInstructionsSection = binding.reInstructionsAddSection;

        floatingActionButton = binding.fabSaveRecipe;

        // TODO check for unsaved changes
        //recipeName.addTextChangedListener(textWatcher);
        //recipeTime.addTextChangedListener(textWatcher);
        //recipeDifficulty.addTextChangedListener(textWatcher);
        //recipeNotes.addTextChangedListener(textWatcher);

        binding.reScrollView.setOnScrollChangeListener(scrollListener);

        RecyclerView ingredientsRecycler = binding.reIngredientsRecycler;
        RecyclerView instructionsRecycler = binding.reInstructionsRecycler;

        ingredientsAdapter = new RecipeEditAdapter(new ArrayList<>(), ingredientsDragListener);
        ingredientsRecycler.setAdapter(ingredientsAdapter);
        ingredientsRecycler.setLayoutManager(new LinearLayoutManager(requireActivity()));

        instructionsAdapter = new RecipeEditAdapter(new ArrayList<>(), instructionsDragListener);
        instructionsRecycler.setAdapter(instructionsAdapter);
        instructionsRecycler.setLayoutManager(new LinearLayoutManager(requireActivity()));

        ItemTouchHelper.Callback ingredientsCallback = new ItemMoveCallback(ingredientsAdapter);
        ItemTouchHelper.Callback instructionsCallback = new ItemMoveCallback(instructionsAdapter);

        ingredientsTouchHelper = new ItemTouchHelper(ingredientsCallback);
        ingredientsTouchHelper.attachToRecyclerView(ingredientsRecycler);

        instructionsTouchHelper = new ItemTouchHelper(instructionsCallback);
        instructionsTouchHelper.attachToRecyclerView(instructionsRecycler);

        ItemTouchHelper ingredientsTouchHelper = new ItemTouchHelper(ingredientsDeleteCallback);
        ingredientsTouchHelper.attachToRecyclerView(ingredientsRecycler);

        ItemTouchHelper instructionsTouchHelper = new ItemTouchHelper(instructionsDeleteCallback);
        instructionsTouchHelper.attachToRecyclerView(instructionsRecycler);

        recipeImage.setOnClickListener(v -> showImagePickerDialog());

        floatingActionButton.setOnClickListener(v -> updateRecipe());

        addIngredient.setOnClickListener(v ->
                ingredientsAdapter.addNewRecipeItem(RecipeEditAdapter.RECIPE_TYPE_ITEM));

        addIngredientSection.setOnClickListener(v ->
                ingredientsAdapter.addNewRecipeItem(RecipeEditAdapter.RECIPE_ITEM_SECTION));

        addInstruction.setOnClickListener(v ->
                instructionsAdapter.addNewRecipeItem(RecipeEditAdapter.RECIPE_TYPE_STEP));

        addInstructionsSection.setOnClickListener(v ->
                instructionsAdapter.addNewRecipeItem(RecipeEditAdapter.RECIPE_STEP_SECTION));

        recipeTime.setOnClickListener(v -> {
            TimerPickerDialog dialog = new TimerPickerDialog(requireActivity(), this);
            dialog.showDialog();
        });

        recipeDifficulty.setOnClickListener(v -> {
            RecipeDiffDialog dialog = new RecipeDiffDialog(requireActivity(), this);
            dialog.showDialog();
        });

        if (recipeId != -1) {
            if (recipeDB != null) {
                AppExecutors.getInstance().diskIO().execute(() -> {
                    RecipesModel recipe = recipeDB.recipeDao().loadRecipeById(recipeId);
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> updateUIWithData(recipe));
                    }
                });
            }
        } else {
            updateUIWithData(new RecipesModel("", null, null, null,
                    null, null, null, null, null,
                    null, null));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void updateUIWithData(RecipesModel recipe) {
        String name = recipe.getName();
        String time = recipe.getTime();
        String difficulty = recipe.getDifficulty();
        String ingredients = recipe.getIngredients();
        String instructions = recipe.getInstructions();
        String notes = recipe.getNotes();
        String image = recipe.getImage();

        this.recipe = recipe;

        if (recipeId == -1) {
            updateActionBarTitle(getString(R.string.recipes_add));
        } else {
            updateActionBarTitle(name);
        }

        recipeName.setText(name);

        if (image != null) loadRecipeImage(Uri.parse(image));
        if (time != null) recipeTime.setText(time);
        if (difficulty != null) recipeDifficulty.setText(difficulty);

        if (ingredients != null) {
            Type collectionType = new TypeToken<List<RecipesModel.RecipeItems>>() {
            }.getType();
            List<RecipesModel.RecipeItems> list = new Gson().fromJson(ingredients, collectionType);
            ingredientsAdapter.setRecipeItems(list);
        }

        if (instructions != null) {
            Type collectionType = new TypeToken<List<RecipesModel.RecipeItems>>() {
            }.getType();
            List<RecipesModel.RecipeItems> list = new Gson().fromJson(instructions, collectionType);
            instructionsAdapter.setRecipeItems(list);
        }

        if (notes != null) {
            recipeNotes.setText(notes);
        }
    }

    private void loadRecipeImage(Uri uri) {
        Glide.with(this)
                .load(uri)
                .placeholder(R.drawable.ic_recipe_placeholder)
                .error(R.drawable.ic_recipe_placeholder_error)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(recipeImage);
    }

    @Override
    public void onRecipeUpdated() {

    }

    @Override
    public void onRecipeDifficulty(String difficulty) {
        recipeDifficulty.setText(difficulty);
    }

    @Override
    public void onRecipeTime(String hours, String minutes) {
        recipeTime.setText(formatRecipeTime(hours, minutes));
    }

    // TODO Check for unsaved changes
    @SuppressWarnings("unused")
    private void showUnsavedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity(),
                R.style.AlertDialogThemeMaterial);
        builder.setTitle(R.string.recipes_unsaved_title);
        builder.setMessage(R.string.recipes_unsaved_message);
        builder.setPositiveButton(R.string.recipes_unsaved_cancel, (dialog, which) -> dialog.dismiss());
        builder.setNegativeButton(R.string.recipes_unsaved_discard, (dialog, which) -> requireActivity().onBackPressed());
        builder.create().show();
    }

    private void updateActionBarTitle(String name) {
        if (getActivity() != null) {
            ((RecipeActivity) getActivity()).setActionBarTitle(name);
        }
    }

    private void showImagePickerDialog() {
        BottomIconDialog dialog = new BottomIconDialog.Builder(requireActivity())
                .setAutoDismiss(true)
                .setNegativeButton(getString(R.string.dialog_camera),
                        R.drawable.ic_camera, (dialogInterface, which) -> {
                            Intent intent = ImagePickerActivity.ImageOptionBuilder.getBuilder()
                                    .setImageCapture()
                                    .build(requireActivity());
                            requestNewImageUri.launch(intent);
                        })
                .setPositiveButton(getString(R.string.dialog_gallery),
                        R.drawable.ic_gallery, (dialogInterface, which) -> {
                            Intent intent = ImagePickerActivity.ImageOptionBuilder.getBuilder()
                                    .setImageGallery()
                                    .build(requireActivity());
                            requestNewImageUri.launch(intent);
                        })
                .build();
        dialog.show();
    }

    private final StartDragListener ingredientsDragListener = new StartDragListener() {
        @Override
        public void requestDrag(RecyclerView.ViewHolder holder) {
            ingredientsTouchHelper.startDrag(holder);
        }
    };

    private final StartDragListener instructionsDragListener = new StartDragListener() {
        @Override
        public void requestDrag(RecyclerView.ViewHolder holder) {
            instructionsTouchHelper.startDrag(holder);
        }
    };

    private final SwipeToDeleteCallback ingredientsDeleteCallback = new SwipeToDeleteCallback() {
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder holder, int i) {
            ingredientsAdapter.removeRecipeItem(holder.getAbsoluteAdapterPosition());
        }
    };

    private final SwipeToDeleteCallback instructionsDeleteCallback = new SwipeToDeleteCallback() {
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder holder, int i) {
            instructionsAdapter.removeRecipeItem(holder.getAbsoluteAdapterPosition());
        }
    };

    private final OnScrollChangeListener scrollListener = (v, scrollX, scrollY,
                                                           oldScrollX, oldScrollY) -> {
        if (scrollY < oldScrollY) {
            if (!floatingActionButton.isShown()) {
                floatingActionButton.show();
            }
        }

        if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
            if (floatingActionButton.isShown()) {
                floatingActionButton.hide();
            }
        }
    };

    // TODO check for unsaved changes
    @SuppressWarnings("unused")
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            onRecipeUpdated();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    // TODO check for unsaved changes
    @SuppressWarnings("unused")
    private final OnBackPressedCallback onBackCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            this.setEnabled(false);
            requireActivity().onBackPressed();
        }
    };

    private final ActivityResultLauncher<Intent> requestNewImageUri = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result != null) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            // TODO Handle picture changes
                            Uri uri = data.getParcelableExtra("path");
                            recipe.setImage(uri.toString());
                            loadRecipeImage(uri);
                            cleanImageDir(uri);
                        }
                    } else if (result.getResultCode() == UCrop.RESULT_ERROR) {
                        AlertUtils.createErrorAlert(getActivity(), R.string.image_picker_result_error,
                                false);
                    } else if (result.getResultCode() == Constants.RESULT_PERMISSIONS) {
                        AlertUtils.createErrorAlert(getActivity(), R.string.app_permissions_denied,
                                false);
                    } else {
                        AlertUtils.createAlert(getActivity(), R.string.image_picker_cancelled,
                                1000);
                    }
                }
            });

    private void updateRecipe() {
        List<RecipeItems> ingredients = ingredientsAdapter.getRecipeItems();
        List<RecipeItems> instructions = instructionsAdapter.getRecipeItems();

        if (recipeName.getText() != null && !recipeName.getText().toString().isEmpty()) {
            recipe.setName(recipeName.getText().toString());
        } else {
            recipe.setName(getString(R.string.recipes_untitled));
        }
        if (recipeTime.getText() != null) {
            recipe.setTime(recipeTime.getText().toString());
        }
        if (recipeDifficulty.getText() != null) {
            recipe.setDifficulty(recipeDifficulty.getText().toString());
        }
        if (recipeNotes.getText() != null) {
            recipe.setNotes(recipeNotes.getText().toString());
        }
        if (recipe.getCreated() != null) {
            recipe.setModified(TimeUtils.getFormattedDate(System.currentTimeMillis(),
                    "MM-dd-yyyy"));
        } else {
            recipe.setCreated(TimeUtils.getFormattedDate(System.currentTimeMillis(),
                    "MM-dd-yyyy"));
        }

        if (ingredients != null) {
            String stdJson = new Gson().toJson(ingredients);
            recipe.setIngredients(stdJson);
        }

        if (instructions != null) {
            int count = 1;
            for (int i = 0; i < instructions.size(); i++) {
                if (instructions.get(i).getType() == RecipeEditAdapter.RECIPE_TYPE_STEP) {
                    instructions.get(i).setKey(count);
                    count++;
                }
            }
            String stdJson = new Gson().toJson(instructions);
            recipe.setInstructions(stdJson);
        }

        if (recipeId == -1) {
            AppExecutors.getInstance().diskIO().execute(() ->
                    recipeDB.recipeDao().insert(recipe));
        } else {
            AppExecutors.getInstance().diskIO().execute(() ->
                    recipeDB.recipeDao().update(recipe));
        }

        requireActivity().onBackPressed();
    }

    private String formatRecipeTime(String hours, String minutes) {
        if (hours.equals("00")) {
            return Integer.valueOf(minutes) + " min";
        } else {
            return Integer.valueOf(hours) + " h " + Integer.valueOf(minutes) + " min";
        }
    }

    private void cleanImageDir(Uri uri) {
        if (getActivity() != null) {
            ArrayList<Uri> imgUris = new ArrayList<>();
            imgUris.add(uri);
            AppExecutors.getInstance().diskIO().execute(() -> {
                List<RecipesModel> recipes = recipeDB.recipeDao().loadAllRecipes();
                for (int i = 0; i < recipes.size(); i++) {
                    if (recipes.get(i).getImage() != null) {
                        imgUris.add(Uri.parse(recipes.get(i).getImage()));
                    }
                }
                if (imgUris.size() > 0) {
                    FileUtils.cleanImgDir(getActivity(), imgUris);
                }
            });
        }
    }
}
