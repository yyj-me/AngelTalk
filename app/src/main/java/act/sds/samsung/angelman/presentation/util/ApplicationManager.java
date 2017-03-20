package act.sds.samsung.angelman.presentation.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.DrawableRes;
import android.support.annotation.VisibleForTesting;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

import act.sds.samsung.angelman.R;
import act.sds.samsung.angelman.domain.model.CategoryModel;
import act.sds.samsung.angelman.presentation.custom.AngelmanWidgetProvider;
import act.sds.samsung.angelman.presentation.custom.ChildModeManager;
import act.sds.samsung.angelman.presentation.service.ScreenService;

public class ApplicationManager {

    public static final String PRIVATE_PREFERENCE_NAME = "act.sds.samsung.angelman";
    private static final String CATEGORY_MODEL_TITLE = "categoryModelTitle";
    private static final String CATEGORY_MODEL_ICON = "categoryModelIcon";
    private static final String CATEGORY_MODEL_COLOR = "categoryModelColor";
    private static final String CATEGORY_MODEL_INDEX = "categoryModelIndex";
    private static final String CHILD_MODE = "childMode";
    private static final String FIRST_LAUNCH = "firstLaunch";

    private SharedPreferences preferences;



    private ChildModeManager childModeManager;
    private Context context;


    public ApplicationManager(Context context) {
        this.context = context;
        this.preferences = context.getSharedPreferences(PRIVATE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        this.childModeManager = new ChildModeManager(context);
    }

    public void setCategoryModel(CategoryModel categoryModel){
        SharedPreferences.Editor edit = preferences.edit();
        edit.putString(CATEGORY_MODEL_TITLE, categoryModel.title);
        edit.putInt(CATEGORY_MODEL_INDEX, categoryModel.index);
        edit.putInt(CATEGORY_MODEL_ICON, categoryModel.icon);
        edit.putInt(CATEGORY_MODEL_COLOR, categoryModel.color);
        edit.commit();
    }

    public CategoryModel getCategoryModel(){
        CategoryModel categoryModel = new CategoryModel();
        categoryModel.title = preferences.getString(CATEGORY_MODEL_TITLE, null);
        categoryModel.index = preferences.getInt(CATEGORY_MODEL_INDEX, -1);
        categoryModel.icon = preferences.getInt(CATEGORY_MODEL_ICON, -1);
        categoryModel.color = preferences.getInt(CATEGORY_MODEL_COLOR, -1);
        return categoryModel;
    }
    //@ResourcesUtil.BackgroundColors
    //int cate

    @ResourcesUtil.BackgroundColors
    public int getCategoryModelColor(){
        return this.getCategoryModel().color;
    }

    public void setCategoryBackground(View rootView,@ResourcesUtil.BackgroundColors int color){
        ResourcesUtil.setViewBackground(rootView, color, context);
    }

    public void setChildMode(){
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean(CHILD_MODE, true);
        if(!isServiceRunningCheck()) {
            Intent screenService = new Intent(context, ScreenService.class);
            updateWidgetView(R.drawable.widget_on);
            context.startService(screenService);
        }
        edit.commit();
        Toast.makeText(context, R.string.inform_show_child_mode, Toast.LENGTH_LONG).show();
    }

    public void setNotChildMode(){
        SharedPreferences.Editor edit = preferences.edit();
        edit.putBoolean(CHILD_MODE, false);
        ActivityManager manager = ((ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE));
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (service.service.getClassName().contains(ScreenService.class.getCanonicalName())) {
                Intent stop = new Intent();
                stop.setComponent(service.service);
                context.stopService(stop);
            }
        }
        updateWidgetView(R.drawable.widget_off);
        edit.commit();
        Toast.makeText(context, R.string.inform_hide_child_mode, Toast.LENGTH_LONG).show();
    }

    public boolean isChildMode(){
        return preferences.getBoolean(CHILD_MODE, true);
    }

    public boolean isServiceRunningCheck() {
        ActivityManager manager = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (service.service.getClassName().contains(ScreenService.class.getCanonicalName())) {
                return true;
            }
        }
        return false;
    }

    public void updateWidgetView(@DrawableRes int drawable) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_angelman);
        views.setImageViewResource(R.id.angelman_button, drawable);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisWidget = new ComponentName(context, AngelmanWidgetProvider.class);
        appWidgetManager.updateAppWidget(thisWidget, views);
    }

    public void changeChildMode(boolean mode) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PRIVATE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();

        if (mode) {
            edit.putBoolean(CHILD_MODE, true);
            if (!isServiceRunningCheck()) {
                Intent screenService = new Intent(context, ScreenService.class);
                context.startService(screenService);
            }
            edit.commit();
            Toast.makeText(context, R.string.inform_show_child_mode, Toast.LENGTH_LONG).show();
        } else {
            edit.putBoolean(CHILD_MODE, false);
            ActivityManager manager = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (service.service.getClassName().contains(ScreenService.class.getCanonicalName())) {
                    Intent stop = new Intent();
                    stop.setComponent(service.service);
                    context.stopService(stop);
                }
            }
            edit.commit();
            Toast.makeText(context, R.string.inform_hide_child_mode, Toast.LENGTH_LONG).show();
        }
    }

    @VisibleForTesting
    public ChildModeManager getChildModeManager() {
        return childModeManager;
    }

    public void makeChildView(){
        childModeManager.removeAllView();
        childModeManager.createAndAddCategoryMenu();
    }

    public void setNotFirstLaunched() {
        if(preferences.getBoolean(FIRST_LAUNCH, true)) {
            SharedPreferences.Editor edit = preferences.edit();
            edit.putBoolean(FIRST_LAUNCH, false);
            edit.commit();
        }
    }

    public boolean isFirstLaunched(){
        return preferences.getBoolean(FIRST_LAUNCH, true);
    }

}
