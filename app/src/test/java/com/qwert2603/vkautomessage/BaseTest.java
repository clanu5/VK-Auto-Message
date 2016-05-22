package com.qwert2603.vkautomessage;

import com.qwert2603.vkautomessage.di.TestComponent;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

@Ignore
@RunWith(RobolectricGradleTestRunner.class)
@Config(
        constants = BuildConfig.class,
        application = TestApplication.class,
        sdk = 19,
        packageName ="com.qwert2603.vkautomessage",
        manifest = "AndroidManifest.xml"
)
public abstract class BaseTest {
    protected TestComponent getTestComponent() {
        return (TestComponent) TestApplication.getAppComponent();
    }
}
