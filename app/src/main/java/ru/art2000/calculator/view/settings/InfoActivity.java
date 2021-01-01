package ru.art2000.calculator.view.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageButton;
import android.widget.Space;

import androidx.lifecycle.ViewModelProvider;

import java.util.Objects;

import ru.art2000.calculator.R;
import ru.art2000.calculator.databinding.ActivityAppInfoBinding;
import ru.art2000.calculator.databinding.AuthorLinkItemBinding;
import ru.art2000.calculator.model.settings.AuthorLink;
import ru.art2000.calculator.view_model.settings.InfoViewModel;
import ru.art2000.extensions.activities.AutoThemeActivity;

public class InfoActivity extends AutoThemeActivity {

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        ActivityAppInfoBinding binding = ActivityAppInfoBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        InfoViewModel model = new ViewModelProvider(this).get(InfoViewModel.class);

        String changelogText = model.getChangeLogText();
        if (changelogText == null) changelogText = getString(R.string.changelog_load_failed);

        binding.changelog.setText(changelogText);

        int width;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            width = getWindowManager().getCurrentWindowMetrics().getBounds().width();
        } else {
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            width = dm.widthPixels;
        }

        int imageSize = getResources().getDimensionPixelSize(R.dimen.author_info_link_image_size);

        int linksCount = model.getAuthorLinks().size();
        int gapSize = width / linksCount - imageSize;

        for (int i = 0; i < linksCount; ++i) {
            AuthorLink link = model.getAuthorLinks().get(i);

            ImageButton linkButton = AuthorLinkItemBinding.inflate(getLayoutInflater()).getRoot();

            linkButton.setImageResource(link.getImage());
            linkButton.setOnClickListener(v -> {
                String url = getString(link.getLink());
                Intent githubIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(githubIntent);
            });

            binding.linksBlock.addView(linkButton, imageSize, imageSize);

            if (i < linksCount - 1) {
                Space space = new Space(this);
                binding.linksBlock.addView(space, gapSize, 0);
            }

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
