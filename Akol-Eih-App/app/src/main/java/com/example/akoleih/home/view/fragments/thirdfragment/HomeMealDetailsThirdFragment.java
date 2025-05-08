package com.example.akoleih.home.view.fragments.thirdfragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.akoleih.R;
import com.example.akoleih.calendar.model.db.CalendarMeal;
import com.example.akoleih.calendar.model.repository.CalendarRepository;
import com.example.akoleih.calendar.model.repository.CalendarRepositoryImpl;
import com.example.akoleih.calendar.model.repository.FirebaseService;
import com.example.akoleih.calendar.model.repository.FirebaseServiceImpl;
import com.example.akoleih.calendar.presenter.CalendarPresenterImpl;
import com.example.akoleih.calendar.presenter.CalendarPresenter;
import com.example.akoleih.calendar.view.viewinterface.CalendarView;
import com.example.akoleih.favorite.model.FavoriteMeal;
import com.example.akoleih.favorite.model.repository.SyncFavoriteRepositoryImpl;
import com.example.akoleih.home.view.adapters.ingredients.IngredientAdapter;
import com.example.akoleih.home.model.Meal;
import com.example.akoleih.home.model.repository.HomeViewModel;
import com.example.akoleih.home.view.fragments.videofragment.VideoFragment;
import com.example.akoleih.home.view.fragments.datepickerfragment.DatePickerDialogFragment;
import com.example.akoleih.utils.CountryFlagUtil;
import com.example.akoleih.utils.CustomLoginDialog;
import com.example.akoleih.utils.NoInternetDialog;
import com.example.akoleih.utils.SharedPrefUtil;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.imageview.ShapeableImageView;
import com.squareup.picasso.Picasso;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomeMealDetailsThirdFragment extends Fragment implements CalendarView {
    private HomeViewModel viewModel;
    private SyncFavoriteRepositoryImpl favRepo;
    private CalendarPresenter calendarPresenter;
    private String mealId;
    private ShapeableImageView mealImage;
    private TextView mealName, mealArea, mealInstructions;
    private ImageView areaFlag, favIcon, calendarIcon;
    private RecyclerView ingredientsRecyclerView;
    private IngredientAdapter ingredientsAdapter;
    private MaterialButton watchButton;
    private ChipGroup chipGroup;
    private String youtubeUrl;
    private boolean isFavorite = false;
    private final Set<String> favoriteIds = new HashSet<>();
    private Meal currentMeal;

    public static HomeMealDetailsThirdFragment newInstance(String mealId) {
        HomeMealDetailsThirdFragment fragment = new HomeMealDetailsThirdFragment();
        Bundle args = new Bundle();
        args.putString("mealId", mealId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mealId = getArguments().getString("mealId");
        }
        viewModel = new ViewModelProvider(this, new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())).get(HomeViewModel.class);
        favRepo = new SyncFavoriteRepositoryImpl(requireContext());
        FirebaseService firebaseService = new FirebaseServiceImpl();
        CalendarRepository calendarRepo = new CalendarRepositoryImpl(requireContext(), firebaseService);
        calendarPresenter = new CalendarPresenterImpl(calendarRepo);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meal_details, container, false);
        initializeViews(view);
        return view;
    }

    private void initializeViews(View view) {
        mealImage = view.findViewById(R.id.meal_image);
        mealName = view.findViewById(R.id.meal_name);
        mealArea = view.findViewById(R.id.meal_area);
        areaFlag = view.findViewById(R.id.iv_area_flag);
        mealInstructions = view.findViewById(R.id.meal_instructions);
        watchButton = view.findViewById(R.id.watch);
        favIcon = view.findViewById(R.id.iv_favorite);
        calendarIcon = view.findViewById(R.id.iv_calendar);
        chipGroup = view.findViewById(R.id.chip_group);
        ingredientsRecyclerView = view.findViewById(R.id.ingredients_recycler_view);

        ingredientsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        ingredientsAdapter = new IngredientAdapter();
        ingredientsRecyclerView.setAdapter(ingredientsAdapter);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        calendarPresenter.attachView(this, this);
        setupClickListeners();
        setupObservers();
        viewModel.loadMealDetails(mealId);
    }

    private void setupClickListeners() {
        calendarIcon.setOnClickListener(v -> {
            if (SharedPrefUtil.isGuestMode(requireContext())) {
                CustomLoginDialog.show(requireContext());
            } else {
                showDatePickerDialog();
            }
        });
        favIcon.setOnClickListener(v -> {
            if (SharedPrefUtil.isGuestMode(requireContext())) {
                CustomLoginDialog.show(requireContext());
            } else {
                toggleFavorite();
            }
        });
        watchButton.setOnClickListener(v -> handleVideoPlayback());
    }

    private void setupObservers() {
        favRepo.getFavorites().observe(getViewLifecycleOwner(), this::updateFavoriteStatus);
        viewModel.getMealDetailsLive().observe(getViewLifecycleOwner(), this::showMealDetails);
        viewModel.getErrorLive().observe(getViewLifecycleOwner(), message -> {
            Log.d("HomeMealDetailsFragment", "Error observed: " + message);
            handleError(message);
        });
    }

    private void showDatePickerDialog() {
        DatePickerDialogFragment dialog = new DatePickerDialogFragment();
        dialog.setOnDateSelectedListener(date -> {
            if (currentMeal != null) {
                CalendarMeal calendarMeal = createCalendarMeal(date);
                calendarPresenter.addMealToCalendar(calendarMeal);
                Bundle result = new Bundle();
                result.putLong("addedDate", date);
                getParentFragmentManager().setFragmentResult("mealAdded", result);
                Toast.makeText(getContext(), "Meal added to calendar", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show(getParentFragmentManager(), "datePicker");
    }

    private CalendarMeal createCalendarMeal(long date) {
        CalendarMeal meal = new CalendarMeal();
        meal.setMealId(mealId);
        meal.setMealName(currentMeal.getName());
        meal.setMealThumb(currentMeal.getThumbnail());
        meal.setDate(date);
        return meal;
    }

    private void showMealDetails(Meal meal) {
        currentMeal = meal;
        updateUI(meal);
        updateChips(meal);
        updateFlag(meal);
        updateVideoButton(meal);
    }

    private void updateUI(Meal meal) {
        Picasso.get().load(meal.getThumbnail()).into(mealImage);
        mealName.setText(meal.getName());
        mealArea.setText(meal.getArea());
        mealInstructions.setText(meal.getInstructions());
        ingredientsAdapter.updateIngredients(meal.getIngredientList());
    }

    private void updateChips(Meal meal) {
        chipGroup.removeAllViews();
        addChip(meal.getCategory());
        addChip(meal.getCalculatedTime());
        for (String tag : meal.getTags().split(",")) {
            if (!tag.trim().isEmpty()) addChip(tag.trim());
        }
    }

    private void addChip(String text) {
        Chip chip = new Chip(requireContext());
        chip.setText(text);
        chip.setChipBackgroundColorResource(R.color.chip_background);
        ChipGroup.LayoutParams params = new ChipGroup.LayoutParams(
                ChipGroup.LayoutParams.WRAP_CONTENT,
                ChipGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 16, 0);
        chip.setLayoutParams(params);
        chipGroup.addView(chip);
    }

    private void updateFlag(Meal meal) {
        String countryCode = CountryFlagUtil.getCountryCode(meal.getArea());
        if (countryCode != null) {
            Picasso.get().load("https://flagsapi.com/" + countryCode + "/flat/64.png")
                    .placeholder(R.drawable.gradient_overlay)
                    .error(R.drawable.foodloading)
                    .into(areaFlag);
            areaFlag.setVisibility(View.VISIBLE);
        } else {
            areaFlag.setVisibility(View.GONE);
        }
    }

    private void updateVideoButton(Meal meal) {
        youtubeUrl = meal.getYoutubeUrl();
    }

    private void handleVideoPlayback() {
        if (youtubeUrl != null && !youtubeUrl.isEmpty()) {
            VideoFragment.newInstance(youtubeUrl).show(getParentFragmentManager(), "video_dialog");
        } else {
            Toast.makeText(getContext(), "No video available", Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleFavorite() {
        if (currentMeal != null) {
            FavoriteMeal fm = new FavoriteMeal(
                    currentMeal.getIdMeal(),
                    currentMeal.getName(),
                    currentMeal.getThumbnail()
            );
            if (isFavorite) {
                favRepo.removeFavorite(fm);
            } else {
                favRepo.addFavorite(fm);
            }
        }
    }

    private void updateFavoriteStatus(List<FavoriteMeal> favList) {
        favoriteIds.clear();
        for (FavoriteMeal fm : favList) favoriteIds.add(fm.getIdMeal());
        isFavorite = favoriteIds.contains(mealId);
        updateFavIcon();
    }

    private void updateFavIcon() {
        favIcon.setImageResource(isFavorite ? R.drawable.clcikedfav : R.drawable.notclickedfav);
    }

    @Override
    public void showCalendarMeals(List<CalendarMeal> meals) {
        // Not used in this fragment
    }

    @Override
    public void showPlannedDates(Set<Long> dates) {
        // Not used in this fragment
    }

    @Override
    public void showMealThumbnail(long date, String thumbnailUrl) {
        // Not used in this fragment
    }

    @Override
    public void showError(String message) {
        // Not used in this fragment
    }

    @Override
    public void refreshCalendar() {
        // Not used in this fragment
    }

    @Override
    public void showUndoSnackbar() {
        // Not used in this fragment
    }

    @Override
    public void restoreMeal(CalendarMeal meal, int position) {
        // Not used in this fragment
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        calendarPresenter.detachView();
    }

    private void handleError(String message) {
        if ("NO_INTERNET".equals(message)) {
            Log.d("HomeMealDetailsFragment", "Showing NoInternetDialog");
            NoInternetDialog.show(requireContext(), () -> viewModel.loadMealDetails(mealId));
        } else {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}