/*
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.systemui.monet;

import com.google.ux.material.libmonet.dynamiccolor.ColorSpec.SpecVersion;
import com.google.ux.material.libmonet.dynamiccolor.ColorSpecs;
import com.google.ux.material.libmonet.dynamiccolor.DynamicColor;
import com.google.ux.material.libmonet.dynamiccolor.DynamicScheme;
import com.google.ux.material.libmonet.dynamiccolor.MaterialDynamicColors;
import com.google.ux.material.libmonet.dynamiccolor.Variant;
import com.google.ux.material.libmonet.hct.Hct;
import com.google.ux.material.libmonet.palettes.TonalPalette;

import java.util.List;

/**
 * The haretic style: tonal spot, but with the seed itself on the dark theme's accents. A dark
 * theme takes its primary at tone 80, a pale tone of any saturated seed, so the R1's orange came
 * out salmon; here the primary palette keeps the seed's chroma and, dark, is shifted so its tone
 * 80 is the seed, the tones on and around it following (near black on it, a deep container).
 * Everything else is neutral grey, the surfaces, the secondary roles and, by {@link #role}, the
 * primary container, which takes the seed's colour for what is on it: the seed is the only
 * colour, as the R1's orange body around its black screen. The tertiary and error stay tonal
 * spot's.
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
                grey(sourceColorHctList.get(0)),
                ColorSpecs.get(specVersion).getTertiaryPalette(Variant.TONAL_SPOT,
                        sourceColorHctList.get(0), isDark, platform, contrastLevel),
                grey(sourceColorHctList.get(0)),
                grey(sourceColorHctList.get(0)),
                ColorSpecs.get(specVersion).getErrorPalette(Variant.TONAL_SPOT,
                        sourceColorHctList.get(0), isDark, platform, contrastLevel));
    }

    /**
     * The color the haretic style gives the role {@code color} is: the primary container is a
     * grey container, the color on it the primary, the seed. Others are as they are.
     */
    public static DynamicColor role(DynamicColor color) {
        final MaterialDynamicColors colors = new MaterialDynamicColors();
        return switch (color.name) {
            case "primary_container" -> colors.surfaceContainerHighest();
            case "on_primary_container" -> colors.primary();
            // The shade's tiles where it can't blur: near black, solid, not a translucent
            // grey over its background
            case "surface_effect_1" -> new DynamicColor.Builder()
                    .setName("surface_effect_1")
                    .setPalette((s) -> s.neutralPalette)
                    .setTone((s) -> s.isDark ? 6.0 : 98.0)
                    .setIsBackground(true)
                    .setOpacity((s) -> s.isDark ? 1.0 : .54)
                    .build();
            default -> color;
        };
    }

    private static TonalPalette grey(Hct seed) {
        return TonalPalette.fromHueAndChroma(seed.getHue(), 0.0);
    }

    private static TonalPalette primary(Hct seed, boolean isDark) {
        final TonalPalette palette = TonalPalette.fromHueAndChroma(seed.getHue(), seed.getChroma());
        // A seed lighter than the dark primary keeps the plain tones: shifting up would wash out
        // the dark tones the containers take
        return isDark && seed.getTone() < DARK_PRIMARY_TONE
                ? palette.shifted(seed.getTone() - DARK_PRIMARY_TONE) : palette;
    }
}
