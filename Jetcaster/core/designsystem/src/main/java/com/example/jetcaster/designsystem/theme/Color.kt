/*
 * Copyright 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jetcaster.designsystem.theme
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.ColorScheme

/**
 * The primary light color used in the application's theme.
 * This color represents a lighter shade of the primary color and is often used
 * for backgrounds, lighter accents, and elements that need to stand out but not
 * as prominently as the main `colorPrimary`.
 */
val primaryLight: Color = Color(0xFF885200)
/**
 * The color to use for text and icons displayed on top of a primary color in light theme.
 * Typically, this is white to ensure contrast against a darker primary color.
 */
val onPrimaryLight: Color = Color(0xFFFFFFFF)
/**
 * The light color used for the primary container in the UI.
 * This color is a bright orange-yellow, specifically hex code #FFAC46.
 * It's typically used as a background color for components that are
 * related to the primary actions or branding of the application.
 */
val primaryContainerLight: Color = Color(0xFFFFAC46)
/**
 * The color to use for text and icons displayed on top of a primary container in a light theme.
 * This color should provide sufficient contrast against [primaryContainerLight].
 */
val onPrimaryContainerLight: Color = Color(0xFF482900)
/**
 * A secondary light color used in the application's theme.
 * This color represents a lighter shade of the secondary color palette.
 * It's typically used for elements that require a subtle visual accent,
 * such as hover states, dividers, or secondary backgrounds.
 * The hex code for this color is #7A5817.
 */
val secondaryLight: Color = Color(0xFF7A5817)
/**
 * The color to use for text and icons displayed on top of [secondaryLight].
 * This color should have a high contrast against [secondaryLight] to ensure readability.
 * Typically, this is a dark color like black or a dark gray, especially when [secondaryLight]
 * is a light color.
 */
val onSecondaryLight: Color = Color(0xFFFFFFFF)
/**
 * The light color for a secondary container.
 * This color is typically used as a background color for components that are
 * considered "secondary" in the UI hierarchy, such as cards, chips, or surfaces
 * that emphasize a secondary action or content area. It's a lighter shade that
 * contrasts well with the primary background and text colors, helping to
 * distinguish the container from the surrounding elements.
 */
val secondaryContainerLight: Color = Color(0xFFFFD798)
/**
 * The color used for text and icons that are placed on top of a secondary container
 * in a light theme. This color ensures sufficient contrast against the background
 * for readability and accessibility.
 *
 * This color should be used for elements like:
 *  - Text labels on secondary container surfaces
 *  - Icons displayed on secondary container surfaces
 *  - Dividers that are visually placed over secondary container surfaces
 *
 * @see androidx.compose.material3.ColorScheme for the Material 3 color system.
 */
val onSecondaryContainerLight: Color = Color(0xFF5C3F00)
/**
 * The tertiary color for light mode.
 *
 * This color is used for elements that need to stand out less than the primary or secondary
 * colors, but still require visual emphasis. Examples include:
 *  - Floating action buttons (FABs) when a secondary action is more prominent.
 *  - Chips and other interactive elements that are less important than primary actions.
 *  - Background accents or dividers to add visual interest without dominating the layout.
 *
 * This specific shade of orange-brown (0xFF994700) is designed to complement the primary and
 * secondary colors in a light theme.
 */
val tertiaryLight: Color = Color(0xFF994700)
/**
 * The color to use for text and icons displayed on top of [tertiaryLight].
 * Typically this is white ([Color.White]).
 */
val onTertiaryLight: Color = Color(0xFFFFFFFF)
/**
 * The light mode color for tertiary container backgrounds.
 * This color is typically used for UI elements that are less prominent than
 * the primary or secondary containers, such as chips, buttons, or dialog
 * backgrounds. It provides a visual distinction and helps organize the UI.
 * This specific color is an orange hue: #FF801F
 */
val tertiaryContainerLight: Color = Color(0xFFFF801F)
/**
 * The preferred color for text and icons that appear on [tertiaryContainerLight].
 * This color should provide sufficient contrast against the [tertiaryContainerLight] background.
 */
val onTertiaryContainerLight: Color = Color(0xFF2D1000)
/**
 * Represents a light red color typically used to indicate an error state.
 * This color is specifically defined as #A4384A in hexadecimal notation.
 * It can be used for backgrounds, text, icons, or other UI elements to visually highlight
 * errors or issues to the user.
 */
val errorLight: Color = Color(0xFFA4384A)
/**
 * The color to use for text and icons displayed on top of a light error color.
 * This color is typically white ([Color.White]). It's used to ensure sufficient contrast when
 * content is overlaid on a background with the [errorLight] color.
 */
val onErrorLight: Color = Color(0xFFFFFFFF)
/**
 * The light theme color used for error containers.
 * Error containers are surfaces that display error messages or other critical information.
 * This color provides a visually distinct background for such containers in light themes.
 */
val errorContainerLight: Color = Color(0xFFF87889)

/**
 * The color to use for text and icons displayed on top of a light error container.
 */
val onErrorContainerLight: Color = Color(0xFF32000A)
/**
 * The background color used in light theme.
 * This is a very light orange/peach color with a hex code of #FFF8F4.
 */
val backgroundLight: Color = Color(0xFFFFF8F4)
/**
 * The color of text and icons displayed on a light background.
 * This color is designed to provide good contrast and readability when placed on surfaces
 * with light color values. It's a dark, slightly desaturated brown.
 * Use this color for:
 *  - Text that sits on a white or very light grey background.
 *  - Icons that need to be visible on light surfaces.
 *  - Elements that require a high degree of contrast against light backgrounds.
 */
val onBackgroundLight: Color = Color(0xFF221A11)
/**
 * The default background color for elevated surfaces in the application.
 * This color is a light, off-white shade, typically used as the background for
 * components like cards, dialogs, and sheets that are visually elevated above
 * the primary surface. It provides a subtle contrast and helps distinguish
 * different UI layers.
 * The color is represented as a hexadecimal value `0xFFFFF8F4`, which corresponds
 * to a light cream or eggshell color.
 */
val surfaceLight: Color = Color(0xFFFFF8F4)
/**
 * The color used for text and icons that are displayed on top of a light surface color.
 * This color should have sufficient contrast against [surfaceLight] to ensure readability.
 *
 * Example uses include:
 *  - Text labels on light backgrounds.
 *  - Icons on light backgrounds.
 *  - Outlines or borders on light backgrounds.
 */
val onSurfaceLight: Color = Color(0xFF221A11)
/**
 * The light mode color used for surface variants.
 * This color is a light beige-orange, typically used for elements that are
 * visually distinct from the main surface but still part of the overall surface
 * hierarchy. Examples include elevated cards, secondary backgrounds, or containers
 * that need to stand out slightly.
 */
val surfaceVariantLight: Color = Color(0xFFF7DEC8)
/**
 * The color of content (text, icons, etc.) that is displayed on top of a [surfaceVariantLight]
 * surface in light theme. It provides a medium contrast against the surface variant.
 */
val onSurfaceVariantLight: Color = Color(0xFF544434)
/**
 * The color used for the outline when the component is in a light theme or state.
 * This color provides a subtle visual boundary or border for the component.
 * It is a light brown color with a hex value of #877461.
 */
val outlineLight: Color = Color(0xFF877461)
/**
 * A light variant of the outline color, often used for subtle visual separators or borders
 * in light theme designs. This color provides a less intense outline than the primary outline
 * color, helping to create a more layered and refined UI.
 */
val outlineVariantLight: Color = Color(0xFFDAC3AD)
/**
 * The light mode scrim color used for overlaying content.
 * This is a black color (alpha: 100%, RGB: 0, 0, 0).
 * It is typically used to darken the background behind a dialog or a bottom sheet,
 * creating a visual separation and emphasizing the overlaid content.
 */
val scrimLight: Color = Color(0xFF000000)
/**
 * The inverse surface light color. This color is used for elements that should appear
 * to be on a surface, but in an inverted or "light" context. This is often used for
 * elevated or highlighted elements in a dark theme, or less emphasized elements in a light theme.
 * The specific hex value `0xFF382F25` is a dark brownish color.
 */
val inverseSurfaceLight: Color = Color(0xFF382F25)
/**
 * A color that contrasts well with [inverseSurfaceLight]. Useful for content that sits on top of
 * containers that are [inverseSurfaceLight]. Used for text and icons on [inverseSurfaceLight]
 * backgrounds.
 */
val inverseOnSurfaceLight: Color = Color(0xFFFFEEDF)
/**
 * An inverse light color used for the primary color in a light color scheme.
 * This color is typically used to provide a contrasting visual element against
 * the main primary color, especially in situations where an inverted emphasis is needed
 * such as the button on a SnackBar.
 * This particular color, `0xFFFFB868`, is a light orange-yellow hue.
 */
val inversePrimaryLight: Color = Color(0xFFFFB868)
/**
 * The surface color used for backgrounds and containers in light mode, when they are slightly
 * dimmed. This color is a light beige, designed to provide a subtle contrast to other elements
 * while maintaining a bright and airy feel. It's often used to visually differentiate elements
 * within a layout or to indicate a state change (like a dimmed or inactive state).
 */
val surfaceDimLight: Color = Color(0xFFE8D7C9)
/**
 * Defines the surface color for components when using a bright light theme.
 * This color is a light, warm tone, often used for backgrounds and surfaces
 * that should appear elevated or distinct in a light color scheme.
 * Value: `#FFF8F4` (Light Peach)
 */
val surfaceBrightLight: Color = Color(0xFFFFF8F4)
/**
 * The color for the lowest container surface in a light theme. This is typically used
 * for elements that are visually the furthest back or at the base of a hierarchy of surfaces.
 * It's the lightest container surface color. It is [Color.White].
 *
 * Example Use Cases:
 *  - Background of the app.
 *  - Behind cards or other elements with higher elevation.
 *  - As the color for the lowest layer of a modal sheet.
 */
val surfaceContainerLowestLight: Color = Color(0xFFFFFFFF)
/**
 * The color used for low-emphasis surface containers in light mode.
 *
 * This color is a light shade, typically used for UI elements that require a subtle visual separation
 * from the background or other higher-emphasis containers. Examples include secondary panels, cards,
 * or other areas that need to be distinct but not overly prominent.
 *
 * The color value is #FFF1E6 in hexadecimal notation.
 */
val surfaceContainerLowLight: Color = Color(0xFFFFF1E6)
/**
 * The light mode color for surface containers, such as cards, sheets, and menus.
 */
val surfaceContainerLight: Color = Color(0xFFFCEBDC)

/**
 * The light mode color for surface containers with higher emphasis than [surfaceContainerLight].
 */
val surfaceContainerHighLight: Color = Color(0xFFF6E5D7)
/**
 * The highest-emphasis surface container color in light mode.
 * This color is used for the most prominent surface containers, such as
 * modal dialogs and bottom sheets, in a light theme. It provides the
 * strongest visual distinction from other surfaces and the background.
 */
val surfaceContainerHighestLight: Color = Color(0xFFF1E0D1)

/**
 * The primary color variant for light themes with medium contrast.
 * This color is typically used for elements that need to stand out against a lighter background,
 * but with a slightly lower contrast than [primaryLightHighContrast].
 *
 * Example uses include:
 *  - Buttons on light backgrounds.
 *  - Text that needs more emphasis.
 *  - Accents within UI components.
 */
val primaryLightMediumContrast: Color = Color(0xFF623A00)
/**
 * The color to use for text and icons displayed on top of a primary color surface in light mode
 * with medium contrast. This color is white, designed to provide sufficient contrast against
 * typical primary light color shades.
 */
val onPrimaryLightMediumContrast: Color = Color(0xFFFFFFFF)
/**
 * The primary container color in light mode with medium contrast.
 * This color is used for elements that sit on top of the primary color
 * and require a medium level of contrast for accessibility.  For example,
 * text or icons on a primary container background.
 */
val primaryContainerLightMediumContrast: Color = Color(0xFFA76600)
/**
 * The color to use for text and icons displayed on top of a primary container in light mode with
 * medium contrast. This color should provide sufficient contrast against
 * [primaryContainerLightMediumContrast] in light mode to ensure readability and accessibility.
 * It's typically a light color (e.g., white) that is used to display information on a darker
 * primary container surface.
 */
val onPrimaryContainerLightMediumContrast: Color = Color(0xFFFFFFFF)
/**
 * Secondary color for light themes with medium contrast.
 *
 * This color is a dark yellowish-brown, suitable for elements that require
 * medium visual emphasis in a light theme. It provides a balance between
 * standing out and blending in with the overall light theme aesthetic.
 *
 * Example Use Cases:
 *  - Accent color for buttons or interactive elements.
 *  - Highlights for text or icons.
 *  - Background for certain UI components.
 */
val secondaryLightMediumContrast: Color = Color(0xFF5A3D00)
/**
 * The color to use for content (text, icons) that is displayed on top of a secondary
 * color in a light theme, with a medium contrast level.
 *
 * This color is typically white ([Color.White]) to ensure sufficient contrast and readability
 * against lighter secondary backgrounds. It should be used when a slightly lower contrast
 * is desired compared to [onSecondaryLightHighContrast].
 *
 * Example Use Cases:
 *  - Text on a secondary colored button background in a light theme.
 *  - Icons overlaid on a secondary color surface.
 *  - Dividers or borders on secondary color surfaces where a subtle contrast is desired.
 */
val onSecondaryLightMediumContrast: Color = Color(0xFFFFFFFF)
/**
 * Represents a medium-contrast color for the secondary container in a light theme.
 *
 * This color is intended for use in UI elements that require a noticeable contrast against
 * the default secondary container background but are not intended to be as prominent as
 * the highest-contrast options. It provides a balance between visual hierarchy and
 * aesthetic appeal.
 */
val secondaryContainerLightMediumContrast: Color = Color(0xFF936E2B)
/**
 * The color to use for text and icons displayed on top of a secondary container in light mode with
 * medium contrast.
 *
 * This color should ensure sufficient contrast against the background color of the secondary
 * container in light mode. It is typically used for content elements such as text labels, icons,
 * and interactive elements.
 *
 * This particular color is white (0xFFFFFFFF).
 */
val onSecondaryContainerLightMediumContrast: Color = Color(0xFFFFFFFF)
/**
 * Tertiary color with medium contrast on light backgrounds.
 *
 * This color is designed to be used as a tertiary color in light themes,
 * providing a medium level of contrast against light background colors.
 * It's a dark orange hue. It is recommended to use for elements that need to be noticeable,
 * but not as prominent as primary or secondary elements.
 */
val tertiaryLightMediumContrast: Color = Color(0xFF6F3100)
/**
 * The color to use for content (text, icons) on [tertiaryLightMediumContrast] backgrounds.
 * This color provides a medium level of contrast against the tertiary light background.
 *
 * This is typically a very light color (e.g. white) designed to be legible against the specified
 * background.
 *
 * This color is generally used for text or icons.
 * It is designed to provide sufficient contrast against the [tertiaryLightMediumContrast]
 * background for readability.
 *
 * @see tertiaryLightMediumContrast
 */
val onTertiaryLightMediumContrast: Color = Color(0xFFFFFFFF)
/**
 * Tertiary container color in light theme with medium contrast.
 * This color is used for surfaces that contain tertiary content
 * and need a medium contrast against the background.
 *
 * Example use cases:
 *  - Elevated buttons with tertiary actions.
 *  - Chips or tags highlighting tertiary information.
 *  - Background of tertiary cards or dialogs.
 */
val tertiaryContainerLightMediumContrast: Color = Color(0xFFBC5800)
/**
 * The color to use for text and icons displayed on top of a tertiary container in light theme with
 * medium contrast.
 *
 * This color should ensure sufficient contrast against the [tertiaryContainerLightMediumContrast]
 * color for optimal readability and accessibility, while also being visually harmonious with the
 * overall light theme.  The "medium" contrast refers to a contrast level that is above the minimum
 * but may not be the absolute highest possible.
 *
 * This is a white color (`Color(0xFFFFFFFF)`) to provide maximum contrast against a colored
 * tertiary container.
 *
 * @see tertiaryContainerLight
 */
val onTertiaryContainerLightMediumContrast: Color = Color(0xFFFFFFFF)
/**
 * Error color with medium contrast suitable for light themes.
 * This color is intended for use in scenarios where an error state needs to be indicated with a
 * moderate level of emphasis in a light theme. It provides sufficient contrast against light
 * backgrounds while still maintaining visual harmony within the overall theme.
 *
 * This color should typically be used for:
 *  - Error messages and labels.
 *  - Error state icons.
 *  - Outlined text fields in an error state.
 *  - Other UI elements indicating an error condition where a medium level of contrast is desired.
 */
val errorLightMediumContrast: Color = Color(0xFF7F1B30)
/**
 * The color to use for text and icons displayed on top of a light-themed
 * surface with medium contrast error color.
 *
 * This color is typically used for elements like text labels, icons, and
 * dividers that are placed on top of an error-colored background in a light theme.
 *
 * It is designed to provide sufficient contrast against a light error
 * background to ensure readability and accessibility.
 * Value: `Color(0xFFFFFFFF)` (White)
 */
val onErrorLightMediumContrast: Color = Color(0xFFFFFFFF)
/**
 * The error container color for light mode with medium contrast.
 *
 * This color is used for the background of UI elements that indicate an error state
 * in a light theme with a medium contrast level.  It provides a balance between
 * visibility and adherence to the light theme's overall aesthetic.
 *
 * This color is specifically designed to be used with the default error color to ensure sufficient
 * contrast between the container and the error message/icon.
 *
 * The hexadecimal color code `0xFFC14E5F` represents a desaturated reddish hue.
 */
val errorContainerLightMediumContrast: Color = Color(0xFFC14E5F)
/**
 * The color to use for text and icons displayed on top of an error container in light theme with
 * medium contrast.
 *
 * This color should provide sufficient contrast against [errorContainerLightMediumContrast].
 * It is white to contrast against a darker error container background.
 */
val onErrorContainerLightMediumContrast: Color = Color(0xFFFFFFFF)
/**
 * A light background color with medium contrast.
 *
 * This color is a light shade, suitable for use as a background in light theme
 * UIs where a slightly higher contrast than the base light background is desired.
 * It provides a subtle differentiation without being overly dark, enhancing
 * readability and visual hierarchy.  It is represented by the hex code #FFF8F4.
 *
 * Use this color for surfaces where you want a gentle contrast, such as:
 *  - Secondary backgrounds in layouts.
 *  - Card backgrounds in light themes.
 *  - Areas needing a slight visual separation from the primary background.
 */
val backgroundLightMediumContrast: Color = Color(0xFFFFF8F4)
/**
 * Color used for text and icons on top of [backgroundLightMediumContrast].
 * This color is a dark brown, providing sufficient contrast for readability on lighter surfaces.
 */
val onBackgroundLightMediumContrast: Color = Color(0xFF221A11)
/**
 * A light mode surface color with medium contrast.
 *
 * This color is intended to be used as a background surface color in light themes.
 * It provides a moderate level of contrast against text and other elements placed on top of it.
 * It's suitable for UI elements where a subtle contrast is desired, like secondary backgrounds
 * or card surfaces.
 */
val surfaceLightMediumContrast: Color = Color(0xFFFFF8F4)
/**
 * The color used for text and icons that are placed on top of [surfaceLightMediumContrast].
 */
val onSurfaceLightMediumContrast: Color = Color(0xFF221A11)
/**
 * A light mode color used for surface variants with medium contrast.
 *
 * This color is used to represent elements that are slightly elevated or have a different
 * level of emphasis compared to the base surface color. It provides a subtle visual
 * distinction in the UI while maintaining a light color scheme.  Specifically designed
 * for a medium contrast look in light mode.
 */
val surfaceVariantLightMediumContrast: Color = Color(0xFFF7DEC8)
/**
 * The color to use for text and icons displayed on [surfaceVariantLightMediumContrast].
 * This color is designed for use in light color schemes where a medium level of contrast is
 * desired. It should be used for elements that need to stand out against the surface variant
 * background, but without the high contrast of [onSurfaceVariantLightHighContrast].
 *
 * This is typically used for:
 *  - Secondary text labels.
 *  - Helper text.
 *  - De-emphasized icons.
 *
 * @see surfaceVariantLight
 * @see onSurfaceVariantLightHighContrast
 */
val onSurfaceVariantLightMediumContrast: Color = Color(0xFF504030)
/**
 * A color representing a medium-contrast outline in a light theme.
 *
 * This color is intended for use in outlining elements where a subtle yet noticeable
 * boundary is desired against a light background. It offers a moderate level of
 * contrast, enhancing visual hierarchy without being overly dominant.
 *
 * Example Use Cases:
 *  - Outlining input fields or text boxes.
 *  - Defining the borders of cards or containers.
 *  - Separating elements within a list or grid.
 *  - Highlighting interactive elements on hover or focus.
 *
 * Color Value: #6E5C4A a brown-ish tone.
 */
val outlineLightMediumContrast: Color = Color(0xFF6E5C4A)
/**
 * A color representing a medium-contrast outline variant in a light color scheme.
 *
 * This color is typically used for outlines and borders that need to be visible but
 * not overly prominent against a light background.  It provides a balance between
 * visual distinction and subtlety.
 *
 * In Material Design, this would be suitable for secondary outlines, dividers,
 * or borders in components like cards, input fields, or list items when a light
 * theme is applied. It is designed to be accessible and meet contrast requirements
 * for most use cases, while still feeling lighter than a full-contrast outline.
 *
 * This specific color is a brownish tone with a hex value of `#8B7765`.
 */
val outlineVariantLightMediumContrast: Color = Color(0xFF8B7765)
/**
 * A light mode scrim color with medium contrast.
 *
 * This color is a dark shade of black, intended to be used as a scrim
 * over light backgrounds to provide a medium level of visual separation
 * or overlay effect. It's suitable for situations where a subtle but
 * noticeable darkening effect is needed without being overly strong.
 *
 * Example Use Cases:
 *  - Modal bottom sheets over a light background.
 *  - Dialog overlays on a light UI.
 *  - Dimming a portion of the screen to focus attention elsewhere.
 *  - Applying a subtle overlay to indicate an inactive or disabled state.
 */
val scrimLightMediumContrast: Color = Color(0xFF000000)
/**
 * An light mode inverse surface color with medium contrast.
 *
 * This color provides a medium level of contrast when overlaid on a light surface color.
 * It is suitable for elements that need to stand out slightly from the background without
 * being overly prominent.
 * **Example Use Cases:**
 *  - Subtle dividers or separators on a light background.
 *  - Disabled or inactive UI elements on a light background.
 *  - Text or icons that are secondary in importance.
 *
 * **Contrast:**
 *  - Medium contrast against light surface colors.
 *  - Lower contrast than [inverseSurfaceLightHighContrast] but higher contrast than
 *  [inverseSurfaceLight].
 */
val inverseSurfaceLightMediumContrast: Color = Color(0xFF382F25)
/**
 *  A color that is a medium-contrast inverse of the on-surface color in light mode.
 *  This color is typically used for text or icons that need to stand out against a
 *  surface that has the on-surface color applied. It offers a noticeable contrast
 *  but is slightly softer than the high-contrast inverse.
 *  Specifically designed for use in light themes.
 */
val inverseOnSurfaceLightMediumContrast: Color = Color(0xFFFFEEDF)
/**
 * The inverse primary color with medium contrast in light mode.
 *
 * This color is intended to be used for elements that need to stand out against a light background
 * while still maintaining a visually harmonious relationship with the primary color. It offers
 * medium contrast to ensure readability and accessibility.
 *
 * This is often used for:
 * - Secondary buttons on light backgrounds.
 * - Interactive elements that need emphasis.
 * - Text labels that require more prominence.
 */
val inversePrimaryLightMediumContrast: Color = Color(0xFFFFB868)
/**
 * A light mode surface color with medium contrast that is dimmer than [surfaceLight].
 *
 * This color is intended for use as a background or surface color where a slightly
 * muted, warm tone with moderate contrast is desired. It offers a balance between
 * visibility and a subtle, less stark appearance.
 */
val surfaceDimLightMediumContrast: Color = Color(0xFFE8D7C9)
val surfaceBrightLightMediumContrast: Color = Color(0xFFFFF8F4)
val surfaceContainerLowestLightMediumContrast: Color = Color(0xFFFFFFFF)
val surfaceContainerLowLightMediumContrast: Color = Color(0xFFFFF1E6)
val surfaceContainerLightMediumContrast: Color = Color(0xFFFCEBDC)
val surfaceContainerHighLightMediumContrast: Color = Color(0xFFF6E5D7)
val surfaceContainerHighestLightMediumContrast: Color = Color(0xFFF1E0D1)

val primaryLightHighContrast: Color = Color(0xFF351D00)
val onPrimaryLightHighContrast: Color = Color(0xFFFFFFFF)
val primaryContainerLightHighContrast: Color = Color(0xFF623A00)
val onPrimaryContainerLightHighContrast: Color = Color(0xFFFFFFFF)
val secondaryLightHighContrast: Color = Color(0xFF301F00)
val onSecondaryLightHighContrast: Color = Color(0xFFFFFFFF)
val secondaryContainerLightHighContrast: Color = Color(0xFF5A3D00)
val onSecondaryContainerLightHighContrast: Color = Color(0xFFFFFFFF)
val tertiaryLightHighContrast: Color = Color(0xFF3C1800)
val onTertiaryLightHighContrast: Color = Color(0xFFFFFFFF)
val tertiaryContainerLightHighContrast: Color = Color(0xFF6F3100)
val onTertiaryContainerLightHighContrast: Color = Color(0xFFFFFFFF)
val errorLightHighContrast: Color = Color(0xFF4C0014)
val onErrorLightHighContrast: Color = Color(0xFFFFFFFF)
val errorContainerLightHighContrast: Color = Color(0xFF7F1B30)
val onErrorContainerLightHighContrast: Color = Color(0xFFFFFFFF)
val backgroundLightHighContrast: Color = Color(0xFFFFF8F4)
val onBackgroundLightHighContrast: Color = Color(0xFF221A11)
val surfaceLightHighContrast: Color = Color(0xFFFFF8F4)
val onSurfaceLightHighContrast: Color = Color(0xFF000000)
val surfaceVariantLightHighContrast: Color = Color(0xFFF7DEC8)
val onSurfaceVariantLightHighContrast: Color = Color(0xFF2E2113)
val outlineLightHighContrast: Color = Color(0xFF504030)
val outlineVariantLightHighContrast: Color = Color(0xFF504030)
val scrimLightHighContrast: Color = Color(0xFF000000)
val inverseSurfaceLightHighContrast: Color = Color(0xFF382F25)
val inverseOnSurfaceLightHighContrast: Color = Color(0xFFFFFFFF)
val inversePrimaryLightHighContrast: Color = Color(0xFFFFE8D4)
val surfaceDimLightHighContrast: Color = Color(0xFFE8D7C9)
val surfaceBrightLightHighContrast: Color = Color(0xFFFFF8F4)
val surfaceContainerLowestLightHighContrast: Color = Color(0xFFFFFFFF)
val surfaceContainerLowLightHighContrast: Color = Color(0xFFFFF1E6)
val surfaceContainerLightHighContrast: Color = Color(0xFFFCEBDC)
val surfaceContainerHighLightHighContrast: Color = Color(0xFFF6E5D7)
val surfaceContainerHighestLightHighContrast: Color = Color(0xFFF1E0D1)

val primaryDark: Color = Color(0xFFFFCF9E)
val onPrimaryDark: Color = Color(0xFF482900)
val primaryContainerDark: Color = Color(0xFFF79900)
val onPrimaryContainerDark: Color = Color(0xFF371E00)
val secondaryDark: Color = Color(0xFFFFFEFF)
val onSecondaryDark: Color = Color(0xFF422C00)
val secondaryContainerDark: Color = Color(0xFFFBCC80)
val onSecondaryContainerDark: Color = Color(0xFF553A00)
val tertiaryDark: Color = Color(0xFFFFB68B)
val onTertiaryDark: Color = Color(0xFF522300)
val tertiaryContainerDark: Color = Color(0xFFE76E00)
val onTertiaryContainerDark: Color = Color(0xFF000000)
val errorDark: Color = Color(0xFFFFB2B9)
val onErrorDark: Color = Color(0xFF65041F)
val errorContainerDark: Color = Color(0xFFC14E5F)
val onErrorContainerDark: Color = Color(0xFFFFFFFF)
val backgroundDark: Color = Color(0xFF1A120A)
val onBackgroundDark: Color = Color(0xFFF1E0D1)
val surfaceDark: Color = Color(0xFF1A120A)
val onSurfaceDark: Color = Color(0xFFF1E0D1)
val surfaceVariantDark: Color = Color(0xFF544434)
val onSurfaceVariantDark: Color = Color(0xFFDAC3AD)
val outlineDark: Color = Color(0xFFA28D7A)
val outlineVariantDark: Color = Color(0xFF544434)
val scrimDark: Color = Color(0xFF000000)
val inverseSurfaceDark: Color = Color(0xFFF1E0D1)
val inverseOnSurfaceDark: Color = Color(0xFF382F25)
val inversePrimaryDark: Color = Color(0xFF885200)
val surfaceDimDark: Color = Color(0xFF1A120A)
val surfaceBrightDark: Color = Color(0xFF42372D)
val surfaceContainerLowestDark: Color = Color(0xFF140D06)
val surfaceContainerLowDark: Color = Color(0xFF221A11)
val surfaceContainerDark: Color = Color(0xFF271E15)
val surfaceContainerHighDark: Color = Color(0xFF32281F)
val surfaceContainerHighestDark: Color = Color(0xFF3D3329)

val primaryDarkMediumContrast: Color = Color(0xFFFFCF9E)
val onPrimaryDarkMediumContrast: Color = Color(0xFF351D00)
val primaryContainerDarkMediumContrast: Color = Color(0xFFF79900)
val onPrimaryContainerDarkMediumContrast: Color = Color(0xFF000000)
val secondaryDarkMediumContrast: Color = Color(0xFFFFFEFF)
val onSecondaryDarkMediumContrast: Color = Color(0xFF422C00)
val secondaryContainerDarkMediumContrast: Color = Color(0xFFFBCC80)
val onSecondaryContainerDarkMediumContrast: Color = Color(0xFF2C1C00)
val tertiaryDarkMediumContrast: Color = Color(0xFFFFBC95)
val onTertiaryDarkMediumContrast: Color = Color(0xFF2A0E00)
val tertiaryContainerDarkMediumContrast: Color = Color(0xFFE76E00)
val onTertiaryContainerDarkMediumContrast: Color = Color(0xFF000000)
val errorDarkMediumContrast: Color = Color(0xFFFFB8BE)
val onErrorDarkMediumContrast: Color = Color(0xFF36000C)
val errorContainerDarkMediumContrast: Color = Color(0xFFE5697A)
val onErrorContainerDarkMediumContrast: Color = Color(0xFF000000)
val backgroundDarkMediumContrast: Color = Color(0xFF1A120A)
val onBackgroundDarkMediumContrast: Color = Color(0xFFF1E0D1)
val surfaceDarkMediumContrast: Color = Color(0xFF1A120A)
val onSurfaceDarkMediumContrast: Color = Color(0xFFFFFAF8)
val surfaceVariantDarkMediumContrast: Color = Color(0xFF544434)
val onSurfaceVariantDarkMediumContrast: Color = Color(0xFFDEC7B1)
val outlineDarkMediumContrast: Color = Color(0xFFB59F8B)
val outlineVariantDarkMediumContrast: Color = Color(0xFF93806D)
val scrimDarkMediumContrast: Color = Color(0xFF000000)
val inverseSurfaceDarkMediumContrast: Color = Color(0xFFF1E0D1)
val inverseOnSurfaceDarkMediumContrast: Color = Color(0xFF32281F)
val inversePrimaryDarkMediumContrast: Color = Color(0xFF693E00)
val surfaceDimDarkMediumContrast: Color = Color(0xFF1A120A)
val surfaceBrightDarkMediumContrast: Color = Color(0xFF42372D)
val surfaceContainerLowestDarkMediumContrast: Color = Color(0xFF140D06)
val surfaceContainerLowDarkMediumContrast: Color = Color(0xFF221A11)
val surfaceContainerDarkMediumContrast: Color = Color(0xFF271E15)
val surfaceContainerHighDarkMediumContrast: Color = Color(0xFF32281F)
val surfaceContainerHighestDarkMediumContrast: Color = Color(0xFF3D3329)

val primaryDarkHighContrast: Color = Color(0xFFFFFAF8)
val onPrimaryDarkHighContrast: Color = Color(0xFF000000)
val primaryContainerDarkHighContrast: Color = Color(0xFFFFBE76)
val onPrimaryContainerDarkHighContrast: Color = Color(0xFF000000)
val secondaryDarkHighContrast: Color = Color(0xFFFFFEFF)
val onSecondaryDarkHighContrast: Color = Color(0xFF000000)
val secondaryContainerDarkHighContrast: Color = Color(0xFFFBCC80)
val onSecondaryContainerDarkHighContrast: Color = Color(0xFF000000)
val tertiaryDarkHighContrast: Color = Color(0xFFFFFAF8)
val onTertiaryDarkHighContrast: Color = Color(0xFF000000)
val tertiaryContainerDarkHighContrast: Color = Color(0xFFFFBC95)
val onTertiaryContainerDarkHighContrast: Color = Color(0xFF000000)
val errorDarkHighContrast: Color = Color(0xFFFFF9F9)
val onErrorDarkHighContrast: Color = Color(0xFF000000)
val errorContainerDarkHighContrast: Color = Color(0xFFFFB8BE)
val onErrorContainerDarkHighContrast: Color = Color(0xFF000000)
val backgroundDarkHighContrast: Color = Color(0xFF1A120A)
val onBackgroundDarkHighContrast: Color = Color(0xFFF1E0D1)
val surfaceDarkHighContrast: Color = Color(0xFF1A120A)
val onSurfaceDarkHighContrast: Color = Color(0xFFFFFFFF)
val surfaceVariantDarkHighContrast: Color = Color(0xFF544434)
val onSurfaceVariantDarkHighContrast: Color = Color(0xFFFFFAF8)
val outlineDarkHighContrast: Color = Color(0xFFDEC7B1)
val outlineVariantDarkHighContrast: Color = Color(0xFFDEC7B1)
val scrimDarkHighContrast: Color = Color(0xFF000000)
val inverseSurfaceDarkHighContrast: Color = Color(0xFFF1E0D1)
val inverseOnSurfaceDarkHighContrast: Color = Color(0xFF000000)
val inversePrimaryDarkHighContrast: Color = Color(0xFF3F2400)
val surfaceDimDarkHighContrast: Color = Color(0xFF1A120A)
val surfaceBrightDarkHighContrast: Color = Color(0xFF42372D)
val surfaceContainerLowestDarkHighContrast: Color = Color(0xFF140D06)
val surfaceContainerLowDarkHighContrast: Color = Color(0xFF221A11)
val surfaceContainerDarkHighContrast: Color = Color(0xFF271E15)
val surfaceContainerHighDarkHighContrast: Color = Color(0xFF32281F)
val surfaceContainerHighestDarkHighContrast: Color = Color(0xFF3D3329)
