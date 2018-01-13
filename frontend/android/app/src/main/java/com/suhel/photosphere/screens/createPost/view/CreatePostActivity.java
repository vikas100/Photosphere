package com.suhel.photosphere.screens.createPost.view;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.suhel.photosphere.R;
import com.suhel.photosphere.application.contract.AppContract;
import com.suhel.photosphere.base.view.ImageChooserActivity;
import com.suhel.photosphere.databinding.ActivityCreatePostBinding;
import com.suhel.photosphere.screens.createPost.contract.CreatePostContract;
import com.suhel.photosphere.screens.createPost.di.CreatePostComponent;
import com.suhel.photosphere.screens.createPost.di.CreatePostModule;
import com.suhel.photosphere.screens.createPost.presenter.CreatePostPresenter;

import java.io.File;
import java.util.List;

public class CreatePostActivity extends ImageChooserActivity<ActivityCreatePostBinding, CreatePostPresenter, CreatePostComponent> implements CreatePostContract.View {

    private ChoosePhotoSourceDialog dialog = new ChoosePhotoSourceDialog();
    private File chosenFile = null;
    private boolean isUploading = false;

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_create_post;
    }

    @Override
    protected CreatePostComponent createComponent(AppContract contract) {
        return contract.getCreatePostComponent().addModule(new CreatePostModule(this)).build();
    }

    @Override
    protected void inject(CreatePostComponent component) {
        component.inject(this);
    }

    @Override
    protected void onSelectPhotos(List<File> chosenOnes) {
        chosenFile = chosenOnes.get(0);
        binding.imgPhoto.setImageURI(Uri.fromFile(chosenFile));
        supportInvalidateOptionsMenu();
    }

    @Override
    protected void onActivityCreated() {
        super.onActivityCreated();
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        dialog.setListener(new ChoosePhotoSourceDialog.OnClickListener() {

            @Override
            public void onCamera() {
                chooseCamera();
            }

            @Override
            public void onGallery() {
                chooseGallery();
            }

        });
        binding.btnAddPhoto.setOnClickListener(view -> {
            if (!isUploading)
                dialog.show(getSupportFragmentManager(), "choose");
        });
        binding.txtTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                supportInvalidateOptionsMenu();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        binding.txtDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                supportInvalidateOptionsMenu();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_post, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean isUploadEnabled = (!isUploading && chosenFile != null && !getPostTitle().isEmpty() && !getPostDescription().isEmpty());
        menu.getItem(0).setEnabled(isUploadEnabled);
        Drawable drawable = menu.getItem(0).getIcon();
        if (drawable != null) {
            drawable.mutate();
            drawable.setColorFilter(ContextCompat.getColor(this, isUploadEnabled ? R.color.colorAccent : R.color.colorMedium), PorterDuff.Mode.SRC_ATOP);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.btnSave:
                if (!isUploading && chosenFile != null && validate())
                    presenter.upload(getPostTitle(), getPostDescription(), chosenFile);
                return true;

        }
        return false;
    }

    private boolean validate() {
        boolean isValid = true;

        if (getPostTitle().isEmpty()) {
            isValid = false;
            binding.tilTitle.setError("Title is mandatory");
        } else {
            binding.tilTitle.setError(null);
        }

        if (getPostDescription().isEmpty()) {
            isValid = false;
            binding.tilDescription.setError("Title is mandatory");
        } else {
            binding.tilDescription.setError(null);
        }

        return isValid;
    }

    @NonNull
    private String getPostTitle() {
        return binding.txtTitle.getText().toString().trim();
    }

    @NonNull
    private String getPostDescription() {
        return binding.txtDescription.getText().toString().trim();
    }

    @Override
    public void onUploadProgressUpdate(int percent) {
        binding.progress.setProgress(percent);
    }

    @Override
    public void onUploadSuccess() {
        isUploading = false;
        onBackPressed();
    }

    @Override
    public void onUploadFailed() {
        isUploading = false;
    }

    @Override
    public void onShowBusy() {
        isUploading = true;
        binding.progress.setVisibility(View.VISIBLE);
        binding.progress.setProgress(0);
        binding.tilTitle.setEnabled(false);
        binding.txtTitle.setEnabled(false);
        binding.tilDescription.setEnabled(false);
        binding.txtDescription.setEnabled(false);
        supportInvalidateOptionsMenu();
    }

    @Override
    public void onHideBusy() {
        binding.progress.setVisibility(View.GONE);
        binding.tilTitle.setEnabled(true);
        binding.txtTitle.setEnabled(true);
        binding.tilDescription.setEnabled(true);
        binding.txtDescription.setEnabled(true);
        supportInvalidateOptionsMenu();
    }

    @Override
    public void onBackPressed() {
        if (!isUploading)
            super.onBackPressed();
    }

}