package act.sds.samsung.angelman.presentation.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import javax.inject.Inject;

import act.sds.samsung.angelman.AngelmanApplication;
import act.sds.samsung.angelman.R;
import act.sds.samsung.angelman.presentation.custom.CardCategoryLayout;
import act.sds.samsung.angelman.presentation.util.ApplicationManager;
import act.sds.samsung.angelman.presentation.util.ResourcesUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CameraGallerySelectionActivity extends AbstractActivity {

    @Inject
    ApplicationManager applicationManager;

    @BindView(R.id.layout_camera)
    public RelativeLayout cameraCard;

    @BindView(R.id.layout_gallery)
    public RelativeLayout galleryCard;

    @BindView(R.id.layout_video)
    public RelativeLayout videoCard;

    private static final int SELECT_PICTURE = 1;
    static final int REQUEST_VIDEO_CAPTURE = 1;

    CardCategoryLayout titleLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((AngelmanApplication) getApplication()).getAngelmanComponent().inject(this);
        setContentView(R.layout.activity_camera_gallery_selection);
        ButterKnife.bind(this);


        applicationManager.setCategoryBackground(
                findViewById(R.id.camera_gallery_selection_container),
                applicationManager.getCategoryModelColor()
        );

        titleLayout = (CardCategoryLayout) findViewById(R.id.title_container);
        titleLayout.setCategoryModelTitle(applicationManager.getCategoryModel().title);
        titleLayout.setCardCountVisible(View.GONE);



        setCameraGalleryIconColor();

    }

    private void setCameraGalleryIconColor() {
        @ResourcesUtil.BackgroundColors
        int color = applicationManager.getCategoryModel().color;

        ImageView cameraIcon = (ImageView) findViewById(R.id.image_camera);
        cameraIcon.setImageDrawable(ContextCompat.getDrawable(this, ResourcesUtil.getCameraIconBy(color)));

        ImageView galleryIcon = (ImageView) findViewById(R.id.image_gallery);
        galleryIcon.setImageDrawable(ContextCompat.getDrawable(this, ResourcesUtil.getGalleryIconBy(color)));

        ImageView videoIcon = (ImageView) findViewById(R.id.image_video);
        videoIcon.setImageDrawable(ContextCompat.getDrawable(this, ResourcesUtil.getVideoIconBy(color)));
    }

    @OnClick({R.id.layout_camera})
    public void onClickCamera(View view){
        startNextActivity(Camera2Activity.class);
    }

    @OnClick({R.id.layout_gallery})
    public void onClickGallery(View view){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.choose_picture)), SELECT_PICTURE);
    }

    @OnClick({R.id.layout_video})
    public void onClickVideo(View view){
        startNextActivity(VideoActivity.class);
    }

    private void startNextActivity(Class nextClass) {
        Intent intent = new Intent(CameraGallerySelectionActivity.this, nextClass);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            Intent intent = new Intent(CameraGallerySelectionActivity.this, PhotoEditorActivity.class);
            intent.putExtra(PhotoEditorActivity.IMAGE_PATH_EXTRA, data.getData());
            startActivity(intent);
        }
    }
}
