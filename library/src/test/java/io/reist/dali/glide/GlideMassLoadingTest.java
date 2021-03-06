/*
 * Copyright (C) 2017 Renat Sarymsakov.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.reist.dali.glide;

import android.os.Build;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import io.reist.dali.BuildConfig;
import io.reist.dali.Dali;
import io.reist.dali.DeferredImageLoader;
import io.reist.dali.MassLoadingTest;
import io.reist.dali.ShadowFadingDaliDrawable;
import io.reist.dali.TestShadowBitmap;

/**
 * Created by Reist on 14.06.16.
 */
@RunWith(RobolectricTestRunner.class)
@Config(
        constants = BuildConfig.class,
        sdk = {Build.VERSION_CODES.JELLY_BEAN},
        shadows = {
                GlideSingleLoadingTest.ShadowNetwork.class,
                GlideShadowStreamBitmapDecoder.class,
                TestShadowBitmap.class,
                ShadowFadingDaliDrawable.class
        },
        application = GlideTestApp.class
)
public class GlideMassLoadingTest extends MassLoadingTest {

    @BeforeClass
    public static void init() {
        Dali.setMainImageLoaderClass(GlideImageLoader.class);
        Dali.setDeferredImageLoaderClass(DeferredImageLoader.class);
    }

}
