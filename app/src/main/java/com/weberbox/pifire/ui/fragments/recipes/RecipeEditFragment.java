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
import com.weberbox.pifire.interfaces.PickerOptionCallback;
import com.weberbox.pifire.interfaces.RecipeEditCallback;
import com.weberbox.pifire.interfaces.StartDragListener;
import com.weberbox.pifire.model.local.RecipesModel;
import com.weberbox.pifire.model.local.RecipesModel.RecipeItems;
import com.weberbox.pifire.recycler.adapter.RecipeEditAdapter;
import com.weberbox.pifire.recycler.callback.ItemMoveCallback;
import com.weberbox.pifire.recycler.callback.SwipeToDeleteCallback;
import com.weberbox.pifire.ui.activities.ImagePickerActivity;
import com.weberbox.pifire.ui.activities.RecipeActivity;
import com.weberbox.pifire.ui.dialogs.ImagePickerDialog;
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

    private FragmentRecipeEditBinding mBinding;
    private RecipeEditAdapter mInstructionsAdapter;
    private RecipeEditAdapter mIngredientsAdapter;
    private FloatingActionButton mFloatingActionButton;
    private RecipeDatabase mDb;
    private RecipesModel mRecipe;
    private ItemTouchHelper mIngredientsTouchHelper;
    private ItemTouchHelper mInstructionsTouchHelper;
    private TextInputEditText mRecipeName;
    private TextInputEditText mRecipeTime;
    private TextInputEditText mRecipeDifficulty;
    private EditText mRecipeNotes;
    private ImageView mRecipeImage;
    private int mRecipeId;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mRecipeId = bundle.getInt(Constants.INTENT_RECIPE_ID, -1);
        } else {
            mRecipeId = -1;
        }

        // TODO check for unsaved changes
        //requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackCallback);

        if (getActivity() != null && getActivity().getApplicationContext() != null) {
            mDb = RecipeDatabase.getInstance(getActivity().getApplicationContext());
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mBinding = FragmentRecipeEditBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecipeImage = mBinding.reImage;
        mRecipeName = mBinding.reNameText;
        mRecipeTime = mBinding.reTimeText;
        mRecipeDifficulty = mBinding.reDifficultyText;
        mRecipeNotes = mBinding.reNotesEditText;

        TextView addIngredient = mBinding.reIngredientsAddItem;
        TextView addIngredientSection = mBinding.reIngredientsAddSection;
        TextView addInstruction = mBinding.reInstructionsAddStep;
        TextView addInstructionsSection = mBinding.reInstructionsAddSection;

        mFloatingActionButton = mBinding.fabSaveRecipe;

        // TODO check for unsaved changes
        //mRecipeName.addTextChangedListener(textWatcher);
        //mRecipeTime.addTextChangedListener(textWatcher);
        //mRecipeDifficulty.addTextChangedListener(textWatcher);
        //mRecipeNotes.addTextChangedListener(textWatcher);

        mBinding.reScrollView.setOnScrollChangeListener(scrollListener);

        RecyclerView ingredientsRecycler = mBinding.reIngredientsRecycler;
        RecyclerView instructionsRecycler = mBinding.reInstructionsRecycler;

        mIngredientsAdapter = new RecipeEditAdapter(new ArrayList<>(), ingredientsDragListener);
        ingredientsRecycler.setAdapter(mIngredientsAdapter);
        ingredientsRecycler.setLayoutManager(new LinearLayoutManager(requireActivity()));

        mInstructionsAdapter = new RecipeEditAdapter(new ArrayList<>(), instructionsDragListener);
        instructionsRecycler.setAdapter(mInstructionsAdapter);
        instructionsRecycler.setLayoutManager(new LinearLayoutManager(requireActivity()));

        ItemTouchHelper.Callback ingredientsCallback = new ItemMoveCallback(mIngredientsAdapter);
        ItemTouchHelper.Callback instructionsCallback = new ItemMoveCallback(mInstructionsAdapter);

        mIngredientsTouchHelper  = new ItemTouchHelper(ingredientsCallback);
        mIngredientsTouchHelper.attachToRecyclerView(ingredientsRecycler);

        mInstructionsTouchHelper  = new ItemTouchHelper(instructionsCallback);
        mInstructionsTouchHelper.attachToRecyclerView(instructionsRecycler);

        ItemTouchHelper ingredientsTouchHelper = new ItemTouchHelper(ingredientsDeleteCallback);
        ingredientsTouchHelper.attachToRecyclerView(ingredientsRecycler);

        ItemTouchHelper instructionsTouchHelper = new ItemTouchHelper(instructionsDeleteCallback);
        instructionsTouchHelper.attachToRecyclerView(instructionsRecycler);

        mRecipeImage.setOnClickListener(v -> showImagePickerDialog());

        mFloatingActionButton.setOnClickListener(v -> updateRecipe());

        addIngredient.setOnClickListener(v ->
                mIngredientsAdapter.addNewRecipeItem(RecipeEditAdapter.RECIPE_TYPE_ITEM));

        addIngredientSection.setOnClickListener(v ->
                mIngredientsAdapter.addNewRecipeItem(RecipeEditAdapter.RECIPE_ITEM_SECTION));

        addInstruction.setOnClickListener(v ->
                mInstructionsAdapter.addNewRecipeItem(RecipeEditAdapter.RECIPE_TYPE_STEP));

        addInstructionsSection.setOnClickListener(v ->
                mInstructionsAdapter.addNewRecipeItem(RecipeEditAdapter.RECIPE_STEP_SECTION));

        mRecipeTime.setOnClickListener(v -> {
            TimerPickerDialog dialog = new TimerPickerDialog(requireActivity(), this);
            dialog.showDialog();
        });

        mRecipeDifficulty.setOnClickListener(v -> {
            RecipeDiffDialog dialog = new RecipeDiffDialog(requireActivity(), this);
            dialog.showDialog();
        });

        if (mRecipeId != -1) {
            if (mDb != null) {
                AppExecutors.getInstance().diskIO().execute(() -> {
                    RecipesModel recipe = mDb.recipeDao().loadRecipeById(mRecipeId);
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
        mBinding = null;
    }

    private void updateUIWithData(RecipesModel recipe) {
        String name = recipe.getName();
        String time = recipe.getTime();
        String difficulty = recipe.getDifficulty();
        String ingredients = recipe.getIngredients();
        String instructions = recipe.getInstructions();
        String notes = recipe.getNotes();
        String image = recipe.getImage();

        mRecipe = recipe;

        if (mRecipeId == -1) {
            updateActionBarTitle(getString(R.string.recipes_add));
        } else {
            updateActionBarTitle(name);
        }

        mRecipeName.setText(name);

        if (image != null) loadRecipeImage(Uri.parse(image));
        if (time != null) mRecipeTime.setText(time);
        if (difficulty != null) mRecipeDifficulty.setText(difficulty);

        if (ingredients != null) {
            Type collectionType = new TypeToken<List<RecipesModel.RecipeItems>>(){}.getType();
            List<RecipesModel.RecipeItems> list  = new Gson().fromJson(ingredients, collectionType);
            mIngredientsAdapter.setRecipeItems(list);
        }

        if (instructions != null) {
            Type collectionType = new TypeToken<List<RecipesModel.RecipeItems>>(){}.getType();
            List<RecipesModel.RecipeItems> list  = new Gson().fromJson(instructions, collectionType);
            mInstructionsAdapter.setRecipeItems(list);
        }

        if (notes != null) {
            mRecipeNotes.setText(notes);
        }
    }

    private void loadRecipeImage(Uri uri) {
        Glide.with(this)
                .load(uri)
                .placeholder(R.drawable.ic_recipe_placeholder)
                .error(R.drawable.ic_recipe_placeholder_error)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(mRecipeImage);
    }

    @Override
    public void onRecipeUpdated() {

    }

    @Override
    public void onRecipeDifficulty(String difficulty) {
        mRecipeDifficulty.setText(difficulty);
    }

    @Override
    public void onRecipeTime(String hours, String minutes) {
        mRecipeTime.setText(formatRecipeTime(hours, minutes));
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
        new ImagePickerDialog(getActivity(), new PickerOptionCallback() {
            @Override
            public void onTakeCameraSelected() {
                Intent intent = new Intent(getActivity(), ImagePickerActivity.class);
                intent.putExtra(Constants.INTENT_IMAGE_PICKER_OPTION,
                        Constants.INTENT_REQUEST_IMAGE_CAPTURE);
                requestNewImageUri.launch(intent);
            }

            @Override
            public void onChooseGallerySelected() {
                Intent intent = new Intent(getActivity(), ImagePickerActivity.class);
                intent.putExtra(Constants.INTENT_IMAGE_PICKER_OPTION,
                        Constants.INTENT_REQUEST_GALLERY_IMAGE);
                requestNewImageUri.launch(intent);
            }
        }).showDialog();
    }

    private final StartDragListener ingredientsDragListener = new StartDragListener() {
        @Override
        public void requestDrag(RecyclerView.ViewHolder holder) {
            mIngredientsTouchHelper.startDrag(holder);
        }
    };

    private final StartDragListener instructionsDragListener = new StartDragListener() {
        @Override
        public void requestDrag(RecyclerView.ViewHolder holder) {
            mInstructionsTouchHelper.startDrag(holder);
        }
    };

    private final SwipeToDeleteCallback ingredientsDeleteCallback = new SwipeToDeleteCallback() {
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder holder, int i) {
            mIngredientsAdapter.removeRecipeItem(holder.getAbsoluteAdapterPosition());
        }
    };

    private final SwipeToDeleteCallback instructionsDeleteCallback = new SwipeToDeleteCallback() {
        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder holder, int i) {
            mInstructionsAdapter.removeRecipeItem(holder.getAbsoluteAdapterPosition());
        }
    };

    private final OnScrollChangeListener scrollListener = (v, scrollX, scrollY,
                                                           oldScrollX, oldScrollY) -> {
        if (scrollY < oldScrollY) {
            if (!mFloatingActionButton.isShown()) {
                mFloatingActionButton.show();
            }
        }

        if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
            if (mFloatingActionButton.isShown()) {
                mFloatingActionButton.hide();
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
                            mRecipe.setImage(uri.toString());
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
        List<RecipeItems> ingredients = mIngredientsAdapter.getRecipeItems();
        List<RecipeItems> instructions = mInstructionsAdapter.getRecipeItems();

        if (mRecipeName.getText() != null && !mRecipeName.getText().toString().isEmpty()) {
            mRecipe.setName(mRecipeName.getText().toString());
        } else {
            mRecipe.setName(getString(R.string.recipes_untitled));
        }
        if (mRecipeTime.getText() != null) {
            mRecipe.setTime(mRecipeTime.getText().toString());
        }
        if (mRecipeDifficulty.getText() != null) {
            mRecipe.setDifficulty(mRecipeDifficulty.getText().toString());
        }
        if (mRecipeNotes.getText() != null) {
            mRecipe.setNotes(mRecipeNotes.getText().toString());
        }
        if (mRecipe.getCreated() != null) {
            mRecipe.setModified(TimeUtils.getFormattedDate(System.currentTimeMillis(),
                    "MM-dd-yyyy"));
        } else {
            mRecipe.setCreated(TimeUtils.getFormattedDate(System.currentTimeMillis(),
                    "MM-dd-yyyy"));
        }

        if (ingredients != null) {
            String stdJson = new Gson().toJson(ingredients);
            mRecipe.setIngredients(stdJson);
        }

        if (instructions != null) {
            int count = 1;
            for (int i = 0 ; i < instructions.size() ; i++) {
                if (instructions.get(i).getType() == RecipeEditAdapter.RECIPE_TYPE_STEP) {
                    instructions.get(i).setKey(count);
                    count++;
                }
            }
            String stdJson = new Gson().toJson(instructions);
            mRecipe.setInstructions(stdJson);
        }

        if (mRecipeId == -1) {
            AppExecutors.getInstance().diskIO().execute(() ->
                    mDb.recipeDao().insert(mRecipe));
        } else {
            AppExecutors.getInstance().diskIO().execute(() ->
                    mDb.recipeDao().update(mRecipe));
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
                List<RecipesModel> recipes = mDb.recipeDao().loadAllRecipes();
                for (int i = 0 ; i < recipes.size() ; i++) {
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
