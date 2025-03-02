package com.weberbox.pifire.model.view;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.JsonSyntaxException;
import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.R;
import com.weberbox.pifire.constants.ServerConstants;
import com.weberbox.pifire.model.SingleLiveEvent;
import com.weberbox.pifire.model.remote.RecipesModel;
import com.weberbox.pifire.utils.HTTPUtils;
import com.weberbox.pifire.utils.NetworkUtils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import timber.log.Timber;

public class RecipesViewModel extends AndroidViewModel {

    private final Context context;
    private final MutableLiveData<RecipesModel> recipesData;
    private final MutableLiveData<String> toolbarTitle;
    private final SingleLiveEvent<Boolean> onError;
    private final SingleLiveEvent<String> onRecipeDelete;
    private final SingleLiveEvent<String> onRunRecipe;
    private final SingleLiveEvent<String> onApiCallFailed;
    private final SingleLiveEvent<Void> onNoNetwork;

    public RecipesViewModel(@NonNull Application application) {
        super(application);
        context = getApplication().getApplicationContext();
        recipesData = new MutableLiveData<>();
        toolbarTitle = new MutableLiveData<>();
        onError = new SingleLiveEvent<>();
        onRecipeDelete = new SingleLiveEvent<>();
        onRunRecipe = new SingleLiveEvent<>();
        onApiCallFailed = new SingleLiveEvent<>();
        onNoNetwork = new SingleLiveEvent<>();
        retrieveRecipes();
    }

    public LiveData<RecipesModel> getRecipesData() {
        return recipesData;
    }

    public void setRecipesData(@NotNull RecipesModel recipesData) {
        this.recipesData.postValue(recipesData);
    }

    public LiveData<String> getToolbarTitle() {
        return toolbarTitle;
    }

    public void setToolbarTitle(@NotNull String title) {
        this.toolbarTitle.postValue(title);
    }

    public SingleLiveEvent<Boolean> getOnError() {
        return onError;
    }

    public void setOnError(@NotNull Boolean error) {
        this.onError.postValue(error);
    }

    public SingleLiveEvent<String> getOnRecipeDelete() {
        return onRecipeDelete;
    }

    public void setOnRecipeDelete(@NotNull String filename) {
        this.onRecipeDelete.postValue(filename);
    }

    public SingleLiveEvent<String> getOnRunRecipe() {
        return onRunRecipe;
    }

    public void setOnRunRecipe(@NotNull String filename) {
        this.onRunRecipe.postValue(filename);
    }

    public SingleLiveEvent<String> getOnApiCallFailed() {
        return onApiCallFailed;
    }

    public void setOnApiCallFailed(@NotNull String error) {
        this.onApiCallFailed.postValue(error);
    }

    public SingleLiveEvent<Void> getOnNoNetwork() {
        return onNoNetwork;
    }

    public void setOnNoNetwork() {
        this.onNoNetwork.call();
    }

    public void retrieveRecipes() {
        if (NetworkUtils.isNetworkAvailable(getApplication())) {
            String url = Prefs.getString(context.getString(R.string.prefs_server_address)) +
                    ServerConstants.URL_RECIPE_DETAILS;
            HTTPUtils.createHttpGet(context, url, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    Timber.d(e, "onFailure");
                    setOnApiCallFailed(context.getString(R.string.http_on_failure));
                    setOnError(true);
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response)
                        throws IOException {
                    if (!response.isSuccessful()) {
                        setOnError(true);
                        setOnApiCallFailed(context.getString(R.string.http_unsuccessful,
                                String.valueOf(response.code()),
                                HTTPUtils.getReasonPhrase(response.code())));
                    } else {
                        if (response.body() != null) {
                            try {
                                RecipesModel recipesModel = RecipesModel.parseJSON(
                                        response.body().string());
                                setRecipesData(recipesModel);
                            } catch (JsonSyntaxException e) {
                                setOnError(true);
                                setOnApiCallFailed(context.getString(
                                        R.string.recipes_retrieve_error));
                            }
                        } else {
                            setOnError(true);
                            setOnApiCallFailed(context.getString(R.string.http_response_null));
                        }
                    }
                    response.close();
                }
            });
        } else {
            setOnNoNetwork();
        }
    }

}
