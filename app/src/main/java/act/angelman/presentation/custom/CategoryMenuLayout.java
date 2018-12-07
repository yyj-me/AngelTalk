package act.angelman.presentation.custom;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.VisibleForTesting;
import android.support.percent.PercentRelativeLayout;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.DragEvent;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import act.angelman.AngelmanApplication;
import act.angelman.R;
import act.angelman.domain.model.CategoryModel;
import act.angelman.domain.repository.CategoryRepository;
import act.angelman.presentation.adapter.CategoryAdapter;
import jp.wasabeef.blurry.Blurry;

import static act.angelman.R.id.category_list;


public class CategoryMenuLayout extends LinearLayout {

    @Inject
    CategoryRepository categoryRepository;

    private OnCategoryViewChangeListener onCategoryViewChangeListener;
    private View subject;
    private TextView lockLongPressGuide;
    private ImageView lockButton;
    private static final int CLICKED = 0;
    private static final int NON_CLICKED = 1;

    private int clickState = NON_CLICKED;

    public CategoryMenuLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(final Context context) {
        ((AngelmanApplication) context.getApplicationContext()).getAngelmanComponent().inject(this);

        subject = inflate(context, R.layout.category_menu_layout, this);
        lockLongPressGuide = (TextView) subject.findViewById(R.id.lock_long_press_guide);
        lockButton = (ImageView) subject.findViewById(R.id.lock_image);

        getAllCategoryList(context);
        setLockView();

        if(hasNavigationBar(context)) {
            setSmallerMarginLayout();
        }
        setClockTypeface(context);
    }

    public void setLockAreaVisibleWithGone() {
        lockLongPressGuide.setVisibility(GONE);
        lockButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_lock_disabled));
        clickState = NON_CLICKED;
    }

    private void getAllCategoryList(final Context context) {
        final GridView categoryList = (GridView) findViewById(category_list);

        List<CategoryModel> categoryAllList = categoryRepository.getCategoryAllList();
        final CategoryAdapter categoryAdapter = new CategoryAdapter(context, categoryAllList);
        categoryList.setAdapter(categoryAdapter);

        categoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(onCategoryViewChangeListener != null && clickState == NON_CLICKED) {
                    changeClickedState();
                    onCategoryViewChangeListener.categoryClick(((CategoryModel) categoryList.getItemAtPosition(position)));
                }
            }
        });
    }

    public void setOnCategoryViewChangeListener(OnCategoryViewChangeListener onCategoryViewChangeListener){
        this.onCategoryViewChangeListener = onCategoryViewChangeListener;
    }

    public interface OnCategoryViewChangeListener {
        void onUnLock();
        void categoryClick(CategoryModel categoryModel);
    }

    public void changeClickedState(){
        if(clickState == NON_CLICKED){
            clickState = CLICKED;
        } else {
            clickState = NON_CLICKED;
        }
    }

    @VisibleForTesting void setLockView() {
        lockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lockLongPressGuide.setVisibility(VISIBLE);
                try {
                   Thread.sleep(3000);
                   lockLongPressGuide.setVisibility(GONE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        lockButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                lockLongPressGuide.setVisibility(GONE);
                onCategoryViewChangeListener.onUnLock();
                return true;
            }
        });
    }

    @VisibleForTesting void setSmallerMarginLayout() {
        DisplayMetrics dm = getResources().getDisplayMetrics();

        PercentRelativeLayout.LayoutParams lp = ((PercentRelativeLayout.LayoutParams) subject.findViewById(R.id.clock_layout).getLayoutParams());
        lp.topMargin = Math.round(10 * dm.density);
        lp.bottomMargin = Math.round(2 * dm.density);
        subject.findViewById(R.id.clock_layout).setLayoutParams(lp);

        int p = Math.round(4*dm.density);
        subject.findViewById(R.id.lock_image).setPadding(p,p,p,p);
    }

    private boolean hasNavigationBar(Context context) {
        boolean hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        double dmRatio = (double)dm.heightPixels/dm.widthPixels;

        return !hasMenuKey && !hasBackKey && (dmRatio < 1.72);
    }

    private void setClockTypeface(Context context) {
        ((TextClock) subject.findViewById(R.id.clock_ampm)).setTypeface(Typeface.createFromAsset(context.getAssets(), context.getString(R.string.font_regular)));
        ((TextClock) subject.findViewById(R.id.clock_date)).setTypeface(Typeface.createFromAsset(context.getAssets(), context.getString(R.string.font_regular)));
        ((TextClock) subject.findViewById(R.id.clock_time)).setTypeface(Typeface.createFromAsset(context.getAssets(), context.getString(R.string.font_demilight)));
    }
}
