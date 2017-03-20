package act.sds.samsung.angelman;


import android.app.Application;
import android.content.Context;
import android.support.annotation.VisibleForTesting;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import act.sds.samsung.angelman.dagger.components.AngelmanComponent;
import act.sds.samsung.angelman.dagger.components.DaggerAngelmanComponent;
import act.sds.samsung.angelman.dagger.modules.AngelmanModule;
import act.sds.samsung.angelman.presentation.util.FileUtil;


@ReportsCrashes(
        mailTo = "act.angeltalk@gmail.com",
        mode = ReportingInteractionMode.DIALOG,
        resDialogTitle= R.string.bug_report_title,
        resDialogText= R.string.bug_report
)
public class AngelmanApplication extends Application {

    private AngelmanComponent angelmanComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        angelmanComponent = DaggerAngelmanComponent.builder()
                .angelmanModule(new AngelmanModule(this))
                .build();

        FileUtil.initExternalStorageFolder();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        ACRA.init(this);
    }

    public AngelmanComponent getAngelmanComponent() {
        return this.angelmanComponent;
    }

    @VisibleForTesting
    public void setComponent(AngelmanComponent angelmanComponent) {
        this.angelmanComponent = angelmanComponent;
    }
}
