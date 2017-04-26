/*
 * Copyright (c) 2017 大前良介 (OHMAE Ryosuke)
 *
 * This software is released under the MIT License.
 * http://opensource.org/licenses/MIT
 */

package net.mm2d.android.upnp.avt;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:ryo@mm2d.net">大前良介 (OHMAE Ryosuke)</a>
 */
public enum TransportState {
    STOPPED,
    PLAYING,
    TRANSITIONING,
    PAUSED_PLAYBACK,
    PAUSED_RECORDING,
    RECORDING,
    NO_MEDIA_PRESENT,
    OTHER;
    private static final Map<String, TransportState> sMap;

    static {
        sMap = new HashMap<>();
        for (final TransportState state : TransportState.values()) {
            sMap.put(state.name(), state);
        }
    }

    @NonNull
    public static TransportState of(final String value) {
        final TransportState state = sMap.get(value);
        return state == null ? OTHER : state;
    }
}