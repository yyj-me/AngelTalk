package act.angelman.presentation.custom;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import act.angelman.BuildConfig;
import act.angelman.presentation.activity.VideoActivity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class AutoFitTextureViewTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private VideoActivity activity;
    private AutoFitTextureView subject;

    @Before
    public void setUp() throws Exception {
        activity = Robolectric.setupActivity(VideoActivity.class);
        subject = new AutoFitTextureView(activity);
    }

    @Test
    public void setAspectRatioTest() throws Exception {
        assertThat(shadowOf(subject).didRequestLayout()).isFalse();
        subject.setAspectRatio(1, 1);
        assertThat(shadowOf(subject).didRequestLayout()).isTrue();
    }

    @Test
    public void setAspectRatioExceptionTest() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Size cannot be negative.");
        subject.setAspectRatio(-1, 1);
    }
}