/*
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.systemui.monet;

import com.google.ux.material.libmonet.dynamiccolor.ColorSpec.SpecVersion;
import com.google.ux.material.libmonet.dynamiccolor.ColorSpecs;
import com.google.ux.material.libmonet.dynamiccolor.DynamicScheme;
import com.google.ux.material.libmonet.dynamiccolor.Variant;
import com.google.ux.material.libmonet.hct.Hct;
import com.google.ux.material.libmonet.palettes.TonalPalette;

import java.util.List;

/**
 * The haretic style: tonal spot, but with the seed itself on the dark theme's accents. A dark
 * theme takes its primary at tone 80, a pale tone of any saturated seed, so the R1's orange came
 * out salmon; here the primary palette keeps the seed's chroma and, dark, is shifted so its tone
 * 80 is the seed, the tones on and around it following (near black on it, a deep container).
 * The neutrals stay tonal spot's, faintly of the seed's hue.
 */
public class SchemeHaretic extends DynamicScheme {

    /** The dark theme's primary tone, which the seed takes. */
    private static final double DARK_PRIMARY_TONE = 80.0;

    public SchemeHaretic(List<Hct> sourceColorHctList, boolean isDark, double contrastLevel,
            SpecVersion specVersion, Platform platform) {
        super(
                sourceColorHctList,
                Variant.TONAL_SPOT,
                isDark,
                contrastLevel,
                platform,
                specVersion,
                primary(sourceColorHctList.get(0), isDark),
                ColorSpecs.get(specVersion).getSecondaryPalette(Variant.TONAL_SPOT,
                        sourceColorHctList.get(0), isDark, platform, contrastLevel),
                ColorSpecs.get(specVersion).getTertiaryPalette(Variant.TONAL_SPOT,
                        sourceColorHctList.get(0), isDark, platform, contrastLevel),
                ColorSpecs.get(specVersion).getNeutralPalette(Variant.TONAL_SPOT,
                        sourceColorHctList.get(0), isDark, platform, contrastLevel),
                ColorSpecs.get(specVersion).getNeutralVariantPalette(Variant.TONAL_SPOT,
                        sourceColorHctList.get(0), isDark, platform, contrastLevel),
                ColorSpecs.get(specVersion).getErrorPalette(Variant.TONAL_SPOT,
                        sourceColorHctList.get(0), isDark, platform, contrastLevel));
    }

    private static TonalPalette primary(Hct seed, boolean isDark) {
        final TonalPalette palette = TonalPalette.fromHueAndChroma(seed.getHue(), seed.getChroma());
        // A seed lighter than the dark primary keeps the plain tones: shifting up would wash out
        // the dark tones the containers take
        return isDark && seed.getTone() < DARK_PRIMARY_TONE
                ? palette.shifted(seed.getTone() - DARK_PRIMARY_TONE) : palette;
    }
}
