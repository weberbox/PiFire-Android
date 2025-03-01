package com.weberbox.pifire.model.view;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.weberbox.pifire.model.remote.RecipesModel;

public class RecipesViewModel extends ViewModel {

    private final MutableLiveData<RecipesModel> recipesData;

    public RecipesViewModel() {
        recipesData = new MutableLiveData<>();
    }

    public LiveData<RecipesModel> getRecipesData() {
        return recipesData;
    }

    public void setRecipesData(RecipesModel recipesData) {
        this.recipesData.postValue(recipesData);
    }
}
