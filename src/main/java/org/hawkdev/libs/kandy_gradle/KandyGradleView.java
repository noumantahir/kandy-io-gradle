package org.hawkdev.libs.kandy_gradle;

import android.content.Context;
import android.util.AttributeSet;

import com.genband.kandy.api.services.calls.KandyView;

/**
 * Created by nomo on 24/03/2017.
 *
 * The only reason for extending this view is to avoid confusion between Kandy Gradle supported classes
 * and clases originally came with Kandy SDK, other than that this class doesnt do anything
 */

public class KandyGradleView extends KandyView {

    public KandyGradleView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }
}
