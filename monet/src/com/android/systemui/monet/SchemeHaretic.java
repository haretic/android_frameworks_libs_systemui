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
 * colour, as the R1's orange body around its black screen. The tertiary is tonal spot's chroma on
 * the seed's own hue, a softer tone of it rather than tonal spot's rotated hue (gold beside the
 * orange, on the calculator's equals key, the timer's keys and the music app's buttons); the
 * error stays tonal spot's.
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
                tertiary(ColorSpecs.get(specVersion).getTertiaryPalette(Variant.TONAL_SPOT,
                        sourceColorHctList.get(0), isDark, platform, contrastLevel),
                        sourceColorHctList.get(0)),
                grey(sourceColorHctList.get(0)),
                grey(sourceColorHctList.get(0)),
                ColorSpecs.get(specVersion).getErrorPalette(Variant.TONAL_SPOT,
                        sourceColorHctList.get(0), isDark, platform, contrastLevel));
    }

    /**
     * The color the haretic style gives the role {@code color} is: the primary container is a
     * grey container, a tone off the highest, the color on it the primary, the seed. Others are as
     * they are.
     */
    public static DynamicColor role(DynamicColor color) {
        final MaterialDynamicColors colors = new MaterialDynamicColors();
        return switch (color.name) {
            // A step off the highest container, the same grey to the eye but not its value:
            // Compose's contentColorFor matches the primary container before the surfaces, so
            // as equal colours anything on the highest container, as Settings' and the Updater's
            // round back buttons, took the orange meant for the primary container's content
            case "primary_container" -> new DynamicColor.Builder()
                    .setName("primary_container")
                    .setPalette((s) -> s.neutralPalette)
                    .setTone((s) -> colors.surfaceContainerHighest().getTone(s)
                            + (s.isDark ? 1.0 : -1.0))
                    .setIsBackground(true)
                    .build();
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

    /** Tonal spot's tertiary, its chroma, turned back to the seed's hue. */
    private static TonalPalette tertiary(TonalPalette tonalSpot, Hct seed) {
        return TonalPalette.fromHueAndChroma(seed.getHue(), tonalSpot.getChroma());
    }

    private static TonalPalette primary(Hct seed, boolean isDark) {
        final TonalPalette palette = TonalPalette.fromHueAndChroma(seed.getHue(), seed.getChroma());
        // A seed lighter than the dark primary keeps the plain tones: shifting up would wash out
        // the dark tones the containers take
        return isDark && seed.getTone() < DARK_PRIMARY_TONE
                ? palette.shifted(seed.getTone() - DARK_PRIMARY_TONE) : palette;
    }
}
