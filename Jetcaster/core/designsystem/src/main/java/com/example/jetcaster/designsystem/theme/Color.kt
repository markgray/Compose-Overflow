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
 * The color displayed most frequently across your app’s screens and components in light theme.
 *
 * @see ColorScheme.primary
 */
val primaryLight: Color = Color(0xFF885200)

/**
 * The color to use for text and icons displayed on top of the primary color in light theme.
 * Typically, this is white to ensure contrast against a darker primary color.
 *
 * @see ColorScheme.onPrimary
 */
val onPrimaryLight: Color = Color(0xFFFFFFFF)

/**
 * The primary color used for containers in light theme.
 * This color is a bright orange-yellow, specifically hex code #FFAC46.
 * It's typically used as a background color for components that are
 * related to the primary actions or branding of the application.
 *
 * @see ColorScheme.primaryContainer
 */
val primaryContainerLight: Color = Color(0xFFFFAC46)

/**
 * The color to use for text and icons displayed on top of a primary container in light theme.
 * This color should provide sufficient contrast against [primaryContainerLight].
 *
 * @see ColorScheme.onPrimaryContainer
 */
val onPrimaryContainerLight: Color = Color(0xFF482900)

/**
 * The secondary color used by the application in light theme. The secondary color provides more
 * ways to accent and distinguish your product. Secondary colors are best for:
 *  - Floating action buttons
 *  - Selection controls, like checkboxes and radio buttons
 *  - Highlighting selected text
 *  - Links and headlines
 *
 * @see ColorScheme.secondary
 */
val secondaryLight: Color = Color(0xFF7A5817)

/**
 * The color to use for text and icons displayed on top of [secondaryLight] in light theme.
 * This color should have a high contrast against [secondaryLight] to ensure readability.
 * Typically, this is a dark color like black or a dark gray, especially when [secondaryLight]
 * is a light color.
 *
 * @see ColorScheme.onSecondary
 */
val onSecondaryLight: Color = Color(0xFFFFFFFF)

/**
 * The color for a secondary container in light theme.
 * This color is typically used as a background color for components that are
 * considered "secondary" in the UI hierarchy, such as cards, chips, or surfaces
 * that emphasize a secondary action or content area. It's a lighter shade that
 * contrasts well with the primary background and text colors, helping to
 * distinguish the container from the surrounding elements.
 *
 * @see ColorScheme.secondaryContainer
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
 * @see ColorScheme.onSecondaryContainer
 */
val onSecondaryContainerLight: Color = Color(0xFF5C3F00)

/**
 * The tertiary color for light theme.
 *
 * This color is used for elements that need to stand out less than the primary or secondary
 * colors, but still require visual emphasis. Examples include:
 *  - Floating action buttons (FABs) when a secondary action is more prominent.
 *  - Chips and other interactive elements that are less important than primary actions.
 *  - Background accents or dividers to add visual interest without dominating the layout.
 *
 * This specific shade of orange-brown (0xFF994700) is designed to complement the primary and
 * secondary colors in a light theme.
 *
 * @see ColorScheme.tertiary
 */
val tertiaryLight: Color = Color(0xFF994700)

/**
 * The color to use for text and icons displayed on top of [tertiaryLight] in light theme.
 * Typically this is white ([Color.White]).
 *
 * @see ColorScheme.onTertiary
 */
val onTertiaryLight: Color = Color(0xFFFFFFFF)

/**
 * The color for tertiary container backgrounds in light theme.
 * This color is typically used for UI elements that are less prominent than
 * the primary or secondary containers, such as chips, buttons, or dialog
 * backgrounds. It provides a visual distinction and helps organize the UI.
 * This specific color is an orange hue: #FF801F
 *
 * @see ColorScheme.tertiaryContainer
 */
val tertiaryContainerLight: Color = Color(0xFFFF801F)

/**
 * The preferred color for text and icons that appear on [tertiaryContainerLight] in light theme.
 * This color should provide sufficient contrast against the [tertiaryContainerLight] background.
 *
 * @see ColorScheme.onTertiaryContainer
 */
val onTertiaryContainerLight: Color = Color(0xFF2D1000)

/**
 * Represents a red color typically used to indicate an error state in light theme.
 * This color is specifically defined as #A4384A in hexadecimal notation.
 * It can be used for backgrounds, text, icons, or other UI elements to visually highlight
 * errors or issues to the user.
 *
 * @see ColorScheme.error
 */
val errorLight: Color = Color(0xFFA4384A)

/**
 * The color to use for text and icons displayed on top the error color in light theme.
 * This color is typically white ([Color.White]). It's used to ensure sufficient contrast when
 * content is overlaid on a background with the [errorLight] color.
 *
 * @see ColorScheme.onError
 */
val onErrorLight: Color = Color(0xFFFFFFFF)

/**
 * The light theme color used for error containers.
 * Error containers are surfaces that display error messages or other critical information.
 * This color provides a visually distinct background for such containers in light themes.
 *
 * @see ColorScheme.errorContainer
 */
val errorContainerLight: Color = Color(0xFFF87889)

/**
 * The color to use for text and icons displayed on top of a error container in light theme.
 *
 * @see ColorScheme.onErrorContainer
 */
val onErrorContainerLight: Color = Color(0xFF32000A)

/**
 * The background color used in light theme.
 * This is a very light orange/peach color with a hex code of #FFF8F4.
 *
 * @see ColorScheme.background
 */
val backgroundLight: Color = Color(0xFFFFF8F4)

/**
 * The color of text and icons displayed on the background color in light theme.
 * This color is designed to provide good contrast and readability when placed on surfaces
 * with light color values. It's a dark, slightly desaturated brown.
 * Use this color for:
 *  - Text that sits on a white or very light grey background.
 *  - Icons that need to be visible on light surfaces.
 *  - Elements that require a high degree of contrast against light backgrounds.
 *
 * @see ColorScheme.onBackground
 */
val onBackgroundLight: Color = Color(0xFF221A11)

/**
 * The default background color for elevated surfaces in the application in light theme.
 * This color is a light, off-white shade, typically used as the background for
 * components like cards, dialogs, and sheets that are visually elevated above
 * the primary surface. It provides a subtle contrast and helps distinguish
 * different UI layers.
 * The color is represented as a hexadecimal value `0xFFFFF8F4`, which corresponds
 * to a light cream or eggshell color.
 *
 * @see ColorScheme.surface
 */
val surfaceLight: Color = Color(0xFFFFF8F4)

/**
 * The color used for text and icons that are displayed on top of the surface color in light theme.
 * This color should have sufficient contrast against [surfaceLight] to ensure readability.
 *
 * Example uses include:
 *  - Text labels on light backgrounds.
 *  - Icons on light backgrounds.
 *  - Outlines or borders on light backgrounds.
 *
 * @see ColorScheme.onSurface
 */
val onSurfaceLight: Color = Color(0xFF221A11)

/**
 * The color used for surface variants in light theme.
 * This color is a light beige-orange, typically used for elements that are
 * visually distinct from the main surface but still part of the overall surface
 * hierarchy. Examples include elevated cards, secondary backgrounds, or containers
 * that need to stand out slightly.
 *
 * @see ColorScheme.surfaceVariant
 */
val surfaceVariantLight: Color = Color(0xFFF7DEC8)

/**
 * The color of content (text, icons, etc.) that is displayed on top of a [surfaceVariantLight]
 * surface in light theme. It provides a medium contrast against the surface variant.
 *
 * @see ColorScheme.onSurfaceVariant
 */
val onSurfaceVariantLight: Color = Color(0xFF544434)

/**
 * The color used for the outline in light theme.
 * This color provides a subtle visual boundary or border for the component.
 * It is a light brown color with a hex value of #877461.
 *
 * @see ColorScheme.outline
 */
val outlineLight: Color = Color(0xFF877461)

/**
 * A variant of the outline color in light theme. Often used for subtle visual separators or borders
 * in light theme designs. This color provides a less intense outline than the primary outline
 * color, helping to create a more layered and refined UI.
 *
 * @see ColorScheme.outlineVariant
 */
val outlineVariantLight: Color = Color(0xFFDAC3AD)

/**
 * The light theme scrim color used for overlaying content.
 * This is a black color (alpha: 100%, RGB: 0, 0, 0).
 * It is typically used to darken the background behind a dialog or a bottom sheet,
 * creating a visual separation and emphasizing the overlaid content.
 *
 * @see ColorScheme.scrim
 */
val scrimLight: Color = Color(0xFF000000)

/**
 * The inverse surface color in light theme. This color is used for elements that should appear
 * to be on a surface, but in an inverted or "light" context. This is often used for
 * elevated or highlighted elements in a dark theme, or less emphasized elements in a light theme.
 * The specific hex value `0xFF382F25` is a dark brownish color.
 *
 * @see ColorScheme.inverseSurface
 */
val inverseSurfaceLight: Color = Color(0xFF382F25)

/**
 * A color that contrasts well with [inverseSurfaceLight] in light theme. Useful for content that
 * sits on top of containers that are [inverseSurfaceLight]. Used for text and icons on
 * [inverseSurfaceLight] backgrounds.
 *
 * @see ColorScheme.inverseOnSurface
 */
val inverseOnSurfaceLight: Color = Color(0xFFFFEEDF)

/**
 * An inverse color of the primary color in a light color scheme.
 * This color is typically used to provide a contrasting visual element against
 * the main primary color, especially in situations where an inverted emphasis is needed
 * such as the button on a SnackBar.
 * This particular color, `0xFFFFB868`, is a light orange-yellow hue.
 *
 * @see ColorScheme.inversePrimary
 */
val inversePrimaryLight: Color = Color(0xFFFFB868)

/**
 * The surface color used for backgrounds and containers in light theme, when they are slightly
 * dimmed. This color is a light beige, designed to provide a subtle contrast to other elements
 * while maintaining a bright and airy feel. It's often used to visually differentiate elements
 * within a layout or to indicate a state change (like a dimmed or inactive state).
 *
 * @see ColorScheme.surfaceDim
 */
val surfaceDimLight: Color = Color(0xFFE8D7C9)

/**
 * Defines a surface color that is brighter than surface in light theme.
 * This color is a light, warm tone, often used for backgrounds and surfaces
 * that should appear elevated or distinct in a light color scheme.
 * Value: `#FFF8F4` (Light Peach)
 *
 * @see ColorScheme.surfaceBright
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
 *
 * @see ColorScheme.surfaceContainerLowest
 */
val surfaceContainerLowestLight: Color = Color(0xFFFFFFFF)

/**
 * The color used for low-emphasis surface containers in light theme.
 *
 * This color is a light shade, typically used for UI elements that require a subtle visual separation
 * from the background or other higher-emphasis containers. Examples include secondary panels, cards,
 * or other areas that need to be distinct but not overly prominent.
 *
 * @see ColorScheme.surfaceContainerLow
 */
val surfaceContainerLowLight: Color = Color(0xFFFFF1E6)

/**
 * The light theme color for surface containers, such as cards, sheets, and menus.
 *
 * @see ColorScheme.surfaceContainer
 */
val surfaceContainerLight: Color = Color(0xFFFCEBDC)

/**
 * The light theme color for surface containers with higher emphasis than [surfaceContainerLight].
 *
 * @see ColorScheme.surfaceContainerHigh
 */
val surfaceContainerHighLight: Color = Color(0xFFF6E5D7)

/**
 * The highest-emphasis surface container color in light theme.
 * This color is used for the most prominent surface containers, such as
 * modal dialogs and bottom sheets, in a light theme. It provides the
 * strongest visual distinction from other surfaces and the background.
 *
 * @see ColorScheme.surfaceContainerHighest
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
 *
 * @see ColorScheme.primary
 */
val primaryLightMediumContrast: Color = Color(0xFF623A00)

/**
 * The color to use for text and icons displayed on top of a primary color surface in light theme
 * with medium contrast. This color is white, designed to provide sufficient contrast against
 * typical primary light color shades.
 *
 * @see ColorScheme.onPrimary
 */
val onPrimaryLightMediumContrast: Color = Color(0xFFFFFFFF)

/**
 * The primary container color in light theme with medium contrast.
 * This color is used for elements that sit on top of the primary color
 * and require a medium level of contrast for accessibility.  For example,
 * text or icons on a primary container background.
 *
 * @see ColorScheme.primaryContainer
 */
val primaryContainerLightMediumContrast: Color = Color(0xFFA76600)

/**
 * The color to use for text and icons displayed on top of a primary container in light theme with
 * medium contrast. This color should provide sufficient contrast against
 * [primaryContainerLightMediumContrast] in light theme to ensure readability and accessibility.
 * It's typically a light color (e.g., white) that is used to display information on a darker
 * primary container surface.
 *
 * @see ColorScheme.onPrimaryContainer
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
 *
 * @see ColorScheme.secondary
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
 *
 * @see ColorScheme.onSecondary
 */
val onSecondaryLightMediumContrast: Color = Color(0xFFFFFFFF)

/**
 * Represents a medium-contrast color for the secondary container in a light theme.
 *
 * This color is intended for use in UI elements that require a noticeable contrast against
 * the default secondary container background but are not intended to be as prominent as
 * the highest-contrast options. It provides a balance between visual hierarchy and
 * aesthetic appeal.
 *
 * @see ColorScheme.secondaryContainer
 */
val secondaryContainerLightMediumContrast: Color = Color(0xFF936E2B)

/**
 * The color to use for text and icons displayed on top of a secondary container in light theme with
 * medium contrast.
 *
 * This color should ensure sufficient contrast against the background color of the secondary
 * container in light theme. It is typically used for content elements such as text labels, icons,
 * and interactive elements.
 *
 * This particular color is white (0xFFFFFFFF).
 *
 * @see ColorScheme.onSecondaryContainer
 */
val onSecondaryContainerLightMediumContrast: Color = Color(0xFFFFFFFF)

/**
 * Tertiary color with medium contrast in light theme. The tertiary color can be used to balance
 * primary and secondary colors, or bring heightened attention to an element such as an input field.
 * It's a dark orange hue.
 *
 * @see ColorScheme.tertiary
 */
val tertiaryLightMediumContrast: Color = Color(0xFF6F3100)

/**
 * The color to use for content (text, icons) on [tertiaryLightMediumContrast] backgrounds in light
 * theme. This color provides a medium level of contrast against the tertiary light background.
 *
 * This is typically a very light color (e.g. white) designed to be legible against the specified
 * background.
 *
 * This color is generally used for text or icons.
 * It is designed to provide sufficient contrast against the [tertiaryLightMediumContrast]
 * background for readability.
 *
 * @see ColorScheme.onTertiary
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
 *
 * @see ColorScheme.tertiaryContainer
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
 * @see ColorScheme.onTertiaryContainer
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
 *
 * @see ColorScheme.error
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
 *
 * @see ColorScheme.onError
 */
val onErrorLightMediumContrast: Color = Color(0xFFFFFFFF)

/**
 * The error container color for light theme with medium contrast.
 *
 * This color is used for the background of UI elements that indicate an error state
 * in a light theme with a medium contrast level.  It provides a balance between
 * visibility and adherence to the light theme's overall aesthetic.
 *
 * This color is specifically designed to be used with the default error color to ensure sufficient
 * contrast between the container and the error message/icon.
 *
 * The hexadecimal color code `0xFFC14E5F` represents a desaturated reddish hue.
 *
 * @see ColorScheme.errorContainer
 */
val errorContainerLightMediumContrast: Color = Color(0xFFC14E5F)

/**
 * The color to use for text and icons displayed on top of an error container in light theme with
 * medium contrast.
 *
 * This color should provide sufficient contrast against [errorContainerLightMediumContrast].
 * It is white to contrast against a darker error container background.
 *
 * @see ColorScheme.onErrorContainer
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
 *
 * @see ColorScheme.background
 */
val backgroundLightMediumContrast: Color = Color(0xFFFFF8F4)

/**
 * Color used for text and icons on top of [backgroundLightMediumContrast] in light theme.
 * This color is a dark brown, providing sufficient contrast for readability on lighter surfaces.
 *
 * @see ColorScheme.onBackground
 */
val onBackgroundLightMediumContrast: Color = Color(0xFF221A11)

/**
 * A light theme surface color with medium contrast.
 *
 * This color is intended to be used as a background surface color in light themes.
 * It provides a moderate level of contrast against text and other elements placed on top of it.
 * It's suitable for UI elements where a subtle contrast is desired, like secondary backgrounds
 * or card surfaces.
 *
 * @see ColorScheme.surface
 */
val surfaceLightMediumContrast: Color = Color(0xFFFFF8F4)

/**
 * The color used for text and icons that are placed on top of [surfaceLightMediumContrast] in light
 * theme.
 *
 * @see ColorScheme.onSurface
 */
val onSurfaceLightMediumContrast: Color = Color(0xFF221A11)

/**
 * A light theme color used for surface variants with medium contrast.
 *
 * This color is used to represent elements that are slightly elevated or have a different
 * level of emphasis compared to the base surface color. It provides a subtle visual
 * distinction in the UI while maintaining a light color scheme.  Specifically designed
 * for a medium contrast look in light mode.
 *
 * @see ColorScheme.surfaceVariant
 */
val surfaceVariantLightMediumContrast: Color = Color(0xFFF7DEC8)

/**
 * The color to use for text and icons displayed on [surfaceVariantLightMediumContrast] in light
 * theme. This color is designed for use in light color schemes where a medium level of contrast is
 * desired. It should be used for elements that need to stand out against the surface variant
 * background, but without the high contrast of [onSurfaceVariantLightHighContrast].
 *
 * This is typically used for:
 *  - Secondary text labels.
 *  - Helper text.
 *  - De-emphasized icons.
 *
 * @see ColorScheme.onSurfaceVariant
 */
val onSurfaceVariantLightMediumContrast: Color = Color(0xFF504030)

/**
 * A color representing a medium-contrast outline in light theme.
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
 *
 * @see ColorScheme.outline
 */
val outlineLightMediumContrast: Color = Color(0xFF6E5C4A)

/**
 * A color representing a medium-contrast outline variant in light color theme.
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
 *
 * @see ColorScheme.outlineVariant
 */
val outlineVariantLightMediumContrast: Color = Color(0xFF8B7765)

/**
 * A light theme scrim color with medium contrast.
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
 *
 * @see ColorScheme.scrim
 */
val scrimLightMediumContrast: Color = Color(0xFF000000)

/**
 * An light theme inverse surface color with medium contrast.
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
 *
 * @see ColorScheme.inverseSurface
 */
val inverseSurfaceLightMediumContrast: Color = Color(0xFF382F25)

/**
 * A color that is a medium-contrast inverse of the on-surface color in light theme.
 * This color is typically used for text or icons that need to stand out against a
 * surface that has the on-surface color applied. It offers a noticeable contrast
 * but is slightly softer than the high-contrast inverse.
 *
 * @see ColorScheme.inverseOnSurface
 */
val inverseOnSurfaceLightMediumContrast: Color = Color(0xFFFFEEDF)

/**
 * The inverse primary color with medium contrast in light theme.
 *
 * This color is intended to be used for elements that need to stand out against a light background
 * while still maintaining a visually harmonious relationship with the primary color. It offers
 * medium contrast to ensure readability and accessibility.
 *
 * This is often used for:
 *  - Secondary buttons on light backgrounds.
 *  - Interactive elements that need emphasis.
 *  - Text labels that require more prominence.
 *
 * @see ColorScheme.inversePrimary
 */
val inversePrimaryLightMediumContrast: Color = Color(0xFFFFB868)

/**
 * A light theme surface color with medium contrast that is dimmer than [surfaceLight].
 *
 * This color is intended for use as a background or surface color where a slightly
 * muted, warm tone with moderate contrast is desired. It offers a balance between
 * visibility and a subtle, less stark appearance.
 *
 * @see ColorScheme.surfaceDim
 */
val surfaceDimLightMediumContrast: Color = Color(0xFFE8D7C9)

/**
 * A bright-toned surface color with medium contrast in light theme. Suitable for use as a background
 * or fill color in areas where a subtle difference from pure white is desired.
 * This color provides a visually gentle contrast and a warm, bright feel.
 * It's specifically designed to be used in light color schemes where a slight off-white
 * variation is needed for UI elements, helping to define visual hierarchy without being too stark.
 *
 * @see ColorScheme.surfaceBright
 */
val surfaceBrightLightMediumContrast: Color = Color(0xFFFFF8F4)

/**
 * The color for the lowest emphasis surface container in a light theme with medium contrast.
 *
 * This color is used for the background of the least prominent UI elements in a light theme,
 * where a medium contrast ratio is desired. It is typically a very light shade, close to white.
 * This provides a subtle separation from the underlying background while maintaining a bright
 * overall look. This color is a pure white.
 *
 * @see ColorScheme.surfaceContainerLowest
 */
val surfaceContainerLowestLightMediumContrast: Color = Color(0xFFFFFFFF)

/**
 * Surface variant for containers with lower emphasis than [surfaceContainerLightMediumContrast] in
 * light theme with medium contrast.
 *
 * This color is intended for use in surface containers where a low-light appearance
 * with medium contrast is desired.  It is a light shade of orange/beige.
 * This color is often used for backgrounds, panels or cards in situations where
 * a subtle separation from the surrounding UI is needed, while still maintaining
 * a bright and airy feel.
 *
 * @see ColorScheme.surfaceContainerLow
 */
val surfaceContainerLowLightMediumContrast: Color = Color(0xFFFFF1E6)

/**
 * Represents a light theme surface container color with medium contrast.
 * This color is used for UI elements that need a background color with
 * a medium level of contrast against the surrounding content in light mode.
 *
 * @see ColorScheme.surfaceContainer
 */
val surfaceContainerLightMediumContrast: Color = Color(0xFFFCEBDC)

/**
 * Represents a light theme medium-contrast color variant for surface containers with higher emphasis
 * than [surfaceContainerLightMediumContrast].
 *
 * This color is designed to provide a subtle highlight on surfaces, offering a visual lift
 * without being overly prominent. Its medium contrast level makes it suitable for a wide range
 * of surface colors, ensuring readability and visual interest. It is particularly well-suited
 * for interactive elements, subtle separators, or other UI components where a gentle
 * highlighting effect is desired.
 *
 *  @see ColorScheme.surfaceContainerHigh
 */
val surfaceContainerHighLightMediumContrast: Color = Color(0xFFF6E5D7)

/**
 * The highest-emphasis surface container color in light mode with medium contrast.
 * This color is typically used for UI elements that require the most emphasis or distinction
 * within the surface container hierarchy in a light theme. It maintains a medium level of
 * contrast to ensure accessibility while standing out from other surface containers.
 *
 * @see ColorScheme.surfaceContainerHighest
 */
val surfaceContainerHighestLightMediumContrast: Color = Color(0xFFF1E0D1)

/**
 * The primary color for light theme, with high contrast.
 * This color is used for prominent UI elements that need to stand out against
 * a light background, offering strong visual distinction.
 *
 * @see ColorScheme.primary
 */
val primaryLightHighContrast: Color = Color(0xFF351D00)

/**
 * Color used for text and icons displayed on top of the [primaryLightHighContrast] color in light
 * theme with high contrast.
 *
 * This color is intended for scenarios where maximum contrast is needed to ensure readability and
 * accessibility against a light primary background.  It is typically white (#FFFFFF) to provide the
 * strongest contrast possible. Use this color for elements like text labels, icons, and other
 * interactive elements that need to be clearly visible on a light primary surface.
 *
 * @see ColorScheme.onPrimary
 */
val onPrimaryLightHighContrast: Color = Color(0xFFFFFFFF)

/**
 * The primary container color in light theme with high contrast.
 *
 * This color is used for elements that contain or group primary content
 * in a light theme with high contrast requirements. It is intended to
 * provide sufficient contrast against the background and other elements.
 *
 * @see ColorScheme.primaryContainer
 */
val primaryContainerLightHighContrast: Color = Color(0xFF623A00)

/**
 * The high-contrast color to use for content on top of [primaryContainerLightHighContrast] in
 * light theme with high contrast.
 *
 * This color is typically white (#FFFFFF) and is designed to ensure sufficient contrast
 * against the light primary container color, improving accessibility and readability.
 * Use this color for text, icons, and other content that needs to be clearly visible
 * when placed on a light primary container.
 *
 * @see ColorScheme.onPrimaryContainer
 */
val onPrimaryContainerLightHighContrast: Color = Color(0xFFFFFFFF)

/**
 * A secondary color intended for use in light theme with high contrast.
 *
 * This color is a dark brown (#301F00) and provides a strong contrast against lighter
 * backgrounds, ensuring accessibility for users with visual impairments. It's suitable for
 * elements like text, icons, or borders where clear visibility is crucial. Secondary colors are
 * best for:
 *  - Floating action buttons
 *  - Selection controls, like checkboxes and radio buttons
 *  - Highlighting selected text
 *  - Links and headlines
 *
 * @see ColorScheme.secondary
 */
val secondaryLightHighContrast: Color = Color(0xFF301F00)

/**
 * The color to use for text and icons displayed on top [secondaryLightHighContrast] in a light theme
 * with high contrast. This color is specifically designed to ensure sufficient contrast
 * against the secondary light color, enhancing readability and accessibility.
 * In high-contrast mode, this is typically a dark color like pure white (0xFFFFFFFF) to maximize
 * visibility against a lighter secondary color background.
 *
 * @see ColorScheme.onSecondary
 */
val onSecondaryLightHighContrast: Color = Color(0xFFFFFFFF)

/**
 * The high-contrast color for the secondary container in a light theme.
 * This color is intended for use in scenarios where a higher contrast is needed to meet
 * accessibility standards. It represents a darker shade of the secondary container color,
 * ensuring sufficient contrast against lighter backgrounds.
 *
 * Use cases include:
 *  - Text on a secondary container background when a high contrast is needed.
 *  - Icons or other graphical elements within a secondary container.
 *  - State overlays on top of a secondary container.
 *
 * @see ColorScheme.secondaryContainer
 */
val secondaryContainerLightHighContrast: Color = Color(0xFF5A3D00)

/**
 * The color to use for content (text, icons) that is displayed on top of
 * [secondaryContainerLightHighContrast] in light theme with high contrast.
 * This is typically white to ensure sufficient contrast against the secondary
 * container's background color.
 *
 * @see ColorScheme.onSecondaryContainer
 */
val onSecondaryContainerLightHighContrast: Color = Color(0xFFFFFFFF)

/**
 * Tertiary color, light theme, high contrast.
 *
 * This color is used as a tertiary color in light theme, with high contrast.
 * It represents a strong, easily distinguishable tertiary color, suitable for
 * elements that need to stand out against a light background.
 *
 * Example Use Cases:
 *  - Accent buttons or calls to action in light themes.
 *  - Visual indicators or warnings.
 *  - Elements requiring strong visual distinction.
 *
 * @see ColorScheme.tertiary
 */
val tertiaryLightHighContrast: Color = Color(0xFF3C1800)

/**
 * The color used for content on top of [tertiaryLightHighContrast] in light theme with high contrast.
 *
 * This color should be used when a high degree of contrast is needed against
 * a light tertiary background. For example, text or icons placed directly
 * on top of a surface with a light tertiary color. Using this color ensures
 * maximum readability and accessibility. It is typically white (or very close to it).
 *
 * @see ColorScheme.onTertiary
 */
val onTertiaryLightHighContrast: Color = Color(0xFFFFFFFF)

/**
 * Tertiary container color in light theme with high contrast.
 *
 * This color is intended to be used as the background color for UI elements
 * that require a higher level of contrast in light theme to improve
 * accessibility. It is a dark orange-brown.
 *
 *  @see ColorScheme.tertiaryContainer
 */
val tertiaryContainerLightHighContrast: Color = Color(0xFF6F3100)

/**
 * The color that should be used for content on top of [tertiaryContainerLightHighContrast] in light
 * theme with high contrast.
 *
 * This color is typically used for text and icons displayed on a tertiary container
 * when the light theme is active and a high contrast ratio is desired. It is set to white (#FFFFFF)
 * to ensure sufficient contrast against typical tertiary container backgrounds in light mode.
 *
 * @see ColorScheme.onTertiaryContainer
 */
val onTertiaryContainerLightHighContrast: Color = Color(0xFFFFFFFF)

/**
 * Error color for light theme with high contrast.
 *
 * This color is designed to provide maximum visibility and contrast when used as an error indicator
 * on light-themed surfaces. It is a dark, saturated red, ensuring it stands out prominently
 * against lighter backgrounds.
 *
 * Example Use Cases:
 *  - Indicating invalid input fields in a form.
 *  - Highlighting error messages or warnings.
 *  - Drawing attention to critical issues within the UI.
 *
 *  Color Value: #4C0014 (Dark Red)
 *
 * @see ColorScheme.error
 */
val errorLightHighContrast: Color = Color(0xFF4C0014)

/**
 * Color used for text and icons displayed on top of the [errorLightHighContrast] color in light
 * theme with high contrast.
 *
 * This color is typically used for text or icons that represent errors or critical
 * states when the application is in light mode and high contrast accessibility
 * settings are enabled. It ensures sufficient contrast against the background
 * for users with low vision.
 *
 * The value is set to `0xFFFFFFFF`, which represents pure white.
 *
 * @see ColorScheme.onError
 */
val onErrorLightHighContrast: Color = Color(0xFFFFFFFF)

/**
 * The light theme color to use for error container backgrounds in high-contrast scenarios.
 *
 * This color is a dark red, specifically `#7F1B30` in hexadecimal, designed to provide
 * sufficient contrast against elements within the error container, ensuring readability
 * and accessibility for users with visual impairments.  It's intended for use in
 * UI components that need to highlight an error state when the device is in light mode
 * and high-contrast mode is enabled.
 *
 * @see ColorScheme.errorContainer
 */
val errorContainerLightHighContrast: Color = Color(0xFF7F1B30)

/**
 * Color used for text and icons displayed on top of the [errorContainerLightHighContrast] color in
 * light theme with high contrast.
 *
 * **Use Cases:**
 *  * Background of error messages in light themes.
 *  * Containers holding elements associated with errors.
 *
 * **Accessibility:**
 * This color is designed to provide high contrast with the `onErrorLight` color, ensuring
 * sufficient visibility for users with visual impairments.
 *
 * @see ColorScheme.onErrorContainer
 */
val onErrorContainerLightHighContrast: Color = Color(0xFFFFFFFF)

/**
 * High-contrast background color for light themes.
 *
 * This color is intended to be used as a background in light theme scenarios where
 * high contrast is needed to ensure readability and accessibility. It provides
 * a strong visual distinction against text and other UI elements.
 *
 * The color is a light off-white with a slight peach hue.
 *
 * **Usage examples:**
 *  - Background for cards or panels containing important information.
 *  - Background for views where text needs to be highly legible.
 *  - Background in areas with low ambient light.
 *
 * **Accessibility considerations:**
 * This color has been chosen to provide sufficient contrast against dark text
 * according to WCAG (Web Content Accessibility Guidelines) standards.
 *
 * @see ColorScheme.background
 */
val backgroundLightHighContrast: Color = Color(0xFFFFF8F4)

/**
 * Color intended to be used for text and icons that are displayed on [backgroundLightHighContrast]
 * in light theme with high contrast.
 * This color provides strong visibility and readability against bright surfaces.
 *
 * @see ColorScheme.onBackground
 */
val onBackgroundLightHighContrast: Color = Color(0xFF221A11)

/**
 * The high-contrast surface color used in light themes.
 * This color is intended to be used for surfaces that need to stand out
 * more prominently against the default surface color.
 *
 * This color is a light beige, with a hex code of #FFF8F4.
 * It's suitable for elements requiring higher visual emphasis, like:
 *  - Floating action buttons (FABs)
 *  - Elevated cards or panels
 *  - Dialog backgrounds
 *  - Selected items in a list
 *
 * Using this color helps to ensure sufficient contrast between elements
 * in the light theme, improving accessibility and readability.
 *
 * @see ColorScheme.surface
 */
val surfaceLightHighContrast: Color = Color(0xFFFFF8F4)

/**
 * High-contrast color variant intended for text and icons that appear on [surfaceLightHighContrast]
 * in light theme with high contrast.
 * This color ensures sufficient contrast for accessibility and readability.
 * This is typically used when the surface color is light and needs a dark color for text or icons
 * to stand out.
 *
 * Example Use Cases:
 *  - Text displayed on a white or very light-colored card.
 *  - Icons on a light-colored background.
 *  - Dividers separating light-colored UI sections.
 *
 * @see ColorScheme.onSurface
 */
val onSurfaceLightHighContrast: Color = Color(0xFF000000)

/**
 * Light theme color for surface variant, high contrast.
 * This color is a light beige, intended for use in UI elements where a higher contrast
 * is needed within the surface variant context.  It's designed to stand out more
 * than the standard [surfaceVariantLight] color while still maintaining visual
 * harmony within the light theme.
 *
 * @see ColorScheme.surfaceVariant
 */
val surfaceVariantLightHighContrast: Color = Color(0xFFF7DEC8)

/**
 * The color used for text and icons on top of [surfaceVariantLightHighContrast] in light theme with
 * high contrast.
 *
 * This color is intended to be used in scenarios where a higher degree of contrast is needed
 * against a light surface variant background, such as when accessibility or visibility
 * is a primary concern. It is a very dark color to ensure strong contrast.
 *
 * @see ColorScheme.onSurfaceVariant
 */
val onSurfaceVariantLightHighContrast: Color = Color(0xFF2E2113)

/**
 * A high-contrast light theme outline color.
 *
 * This color is intended for use in light themes where a stronger outline is needed for
 * improved visibility and contrast. It is typically used to highlight UI elements or
 * boundaries against a light background.
 *
 * The color is a dark shade with a hexadecimal value of `0xFF504030`, representing a
 * dark brownish hue.
 *
 * @see ColorScheme.outline
 */
val outlineLightHighContrast: Color = Color(0xFF504030)

/**
 * High contrast variant of the outline color for light themes.
 *
 * This color is intended for use in light themes where a strong visual
 * distinction is needed for outlines, such as around focused or highlighted
 * components. It provides a higher contrast against light backgrounds compared
 * to the standard [outlineVariantLight] color.
 *
 * This specific color is a dark brown.
 *
 * @see ColorScheme.outlineVariant
 */
val outlineVariantLightHighContrast: Color = Color(0xFF504030)

/**
 * A high-contrast scrim color for light theme.
 *
 * This color is a fully opaque black color ([Color.Black]) and is designed to be used as a
 * scrim (a semi-transparent overlay) in light themes where a strong visual distinction is needed.
 * It can be used, for instance, to dim the background when a modal or dialog is presented,
 * creating a high-contrast separation between the content behind and the overlaying element.
 * The strong contrast ensures the overlaying content is clearly emphasized.
 *
 * @see ColorScheme.scrim
 */
val scrimLightHighContrast: Color = Color(0xFF000000)

/**
 * @see ColorScheme.inverseSurface
 */
val inverseSurfaceLightHighContrast: Color = Color(0xFF382F25)

/**
 * @see ColorScheme.inverseOnSurface
 */
val inverseOnSurfaceLightHighContrast: Color = Color(0xFFFFFFFF)

/**
 * @see ColorScheme.inversePrimary
 */
val inversePrimaryLightHighContrast: Color = Color(0xFFFFE8D4)

/**
 * @see ColorScheme.surfaceDim
 */
val surfaceDimLightHighContrast: Color = Color(0xFFE8D7C9)

/**
 *@see ColorScheme.surfaceBright
 */
val surfaceBrightLightHighContrast: Color = Color(0xFFFFF8F4)

/**
 * @see ColorScheme.surfaceContainerLowest
 */
val surfaceContainerLowestLightHighContrast: Color = Color(0xFFFFFFFF)

/**
 * @see ColorScheme.surfaceContainerLow
 */
val surfaceContainerLowLightHighContrast: Color = Color(0xFFFFF1E6)

/**
 * @see ColorScheme.surfaceContainer
 */
val surfaceContainerLightHighContrast: Color = Color(0xFFFCEBDC)

/**
 * @see ColorScheme.surfaceContainerHigh
 */
val surfaceContainerHighLightHighContrast: Color = Color(0xFFF6E5D7)

/**
 * @see ColorScheme.surfaceContainerHighest
 */
val surfaceContainerHighestLightHighContrast: Color = Color(0xFFF1E0D1)

/**
 * @see ColorScheme.primary
 */
val primaryDark: Color = Color(0xFFFFCF9E)

/**
 * @see ColorScheme.onPrimary
 */
val onPrimaryDark: Color = Color(0xFF482900)

/**
 * @see ColorScheme.primaryContainer
 */
val primaryContainerDark: Color = Color(0xFFF79900)

/**
 * @see ColorScheme.onPrimaryContainer
 */
val onPrimaryContainerDark: Color = Color(0xFF371E00)

/**
 * @see ColorScheme.secondary
 */
val secondaryDark: Color = Color(0xFFFFFEFF)

/**
 * @see ColorScheme.onSecondary
 */
val onSecondaryDark: Color = Color(0xFF422C00)

/**
 * @see ColorScheme.secondaryContainer
 */
val secondaryContainerDark: Color = Color(0xFFFBCC80)

/**
 * @see ColorScheme.onPrimaryContainer
 */
val onSecondaryContainerDark: Color = Color(0xFF553A00)

/**
 * @see ColorScheme.tertiary
 */
val tertiaryDark: Color = Color(0xFFFFB68B)

/**
 * @see ColorScheme.onTertiary
 */
val onTertiaryDark: Color = Color(0xFF522300)

/**
 * @see ColorScheme.tertiaryContainer
 */
val tertiaryContainerDark: Color = Color(0xFFE76E00)

/**
 * @see ColorScheme.onTertiaryContainer
 */
val onTertiaryContainerDark: Color = Color(0xFF000000)

/**
 * @see ColorScheme.error
 */
val errorDark: Color = Color(0xFFFFB2B9)

/**
 * @see ColorScheme.onError
 */
val onErrorDark: Color = Color(0xFF65041F)

/**
 * @see ColorScheme.errorContainer
 */
val errorContainerDark: Color = Color(0xFFC14E5F)

/**
 * @see ColorScheme.onErrorContainer
 */
val onErrorContainerDark: Color = Color(0xFFFFFFFF)

/**
 * @see ColorScheme.background
 */
val backgroundDark: Color = Color(0xFF1A120A)

/**
 * @see ColorScheme.onBackground
 */
val onBackgroundDark: Color = Color(0xFFF1E0D1)

/**
 * @see ColorScheme.surface
 */
val surfaceDark: Color = Color(0xFF1A120A)

/**
 * @see ColorScheme.onSurface
 */
val onSurfaceDark: Color = Color(0xFFF1E0D1)

/**
 * @see ColorScheme.surfaceVariant
 */
val surfaceVariantDark: Color = Color(0xFF544434)

/**
 * @see ColorScheme.onSurfaceVariant
 */
val onSurfaceVariantDark: Color = Color(0xFFDAC3AD)

/**
 * @see ColorScheme.outline
 */
val outlineDark: Color = Color(0xFFA28D7A)

/**
 * @see ColorScheme.outlineVariant
 */
val outlineVariantDark: Color = Color(0xFF544434)

/**
 * @see ColorScheme.scrim
 */
val scrimDark: Color = Color(0xFF000000)

/**
 * @see ColorScheme.inverseSurface
 */
val inverseSurfaceDark: Color = Color(0xFFF1E0D1)

/**
 * @see ColorScheme.inverseOnSurface
 */
val inverseOnSurfaceDark: Color = Color(0xFF382F25)

/**
 * @see ColorScheme.inversePrimary
 */
val inversePrimaryDark: Color = Color(0xFF885200)

/**
 * @see ColorScheme.surfaceDim
 */
val surfaceDimDark: Color = Color(0xFF1A120A)

/**
 * @see ColorScheme.surfaceBright
 */
val surfaceBrightDark: Color = Color(0xFF42372D)

/**
 * @see ColorScheme.surfaceContainerLowest
 */
val surfaceContainerLowestDark: Color = Color(0xFF140D06)

/**
 * @see ColorScheme.surfaceContainerLow
 */
val surfaceContainerLowDark: Color = Color(0xFF221A11)

/**
 * @see ColorScheme.surfaceContainer
 */
val surfaceContainerDark: Color = Color(0xFF271E15)

/**
 * @see ColorScheme.surfaceContainerHigh
 */
val surfaceContainerHighDark: Color = Color(0xFF32281F)

/**
 * @see ColorScheme.surfaceContainerHighest
 */
val surfaceContainerHighestDark: Color = Color(0xFF3D3329)

/**
 * @see ColorScheme.primary
 */
val primaryDarkMediumContrast: Color = Color(0xFFFFCF9E)

/**
 * @see ColorScheme.onPrimary
 */
val onPrimaryDarkMediumContrast: Color = Color(0xFF351D00)

/**
 * @see ColorScheme.primaryContainer
 */
val primaryContainerDarkMediumContrast: Color = Color(0xFFF79900)

/**
 * @see ColorScheme.onPrimaryContainer
 */
val onPrimaryContainerDarkMediumContrast: Color = Color(0xFF000000)

/**
 * @see ColorScheme.secondary
 */
val secondaryDarkMediumContrast: Color = Color(0xFFFFFEFF)

/**
 * @see ColorScheme.onSecondary
 */
val onSecondaryDarkMediumContrast: Color = Color(0xFF422C00)

/**
 * @see ColorScheme.secondaryContainer
 */
val secondaryContainerDarkMediumContrast: Color = Color(0xFFFBCC80)

/**
 * @see ColorScheme.onSecondaryContainer
 */
val onSecondaryContainerDarkMediumContrast: Color = Color(0xFF2C1C00)

/**
 * @see ColorScheme.tertiary
 */
val tertiaryDarkMediumContrast: Color = Color(0xFFFFBC95)

/**
 * @see ColorScheme.onTertiary
 */
val onTertiaryDarkMediumContrast: Color = Color(0xFF2A0E00)

/**
 * @see ColorScheme.tertiaryContainer
 */
val tertiaryContainerDarkMediumContrast: Color = Color(0xFFE76E00)

/**
 * @see ColorScheme.onTertiaryContainer
 */
val onTertiaryContainerDarkMediumContrast: Color = Color(0xFF000000)

/**
 * @see ColorScheme.error
 */
val errorDarkMediumContrast: Color = Color(0xFFFFB8BE)

/**
 * @see ColorScheme.onError
 */
val onErrorDarkMediumContrast: Color = Color(0xFF36000C)

/**
 * @see ColorScheme.errorContainer
 */
val errorContainerDarkMediumContrast: Color = Color(0xFFE5697A)

/**
 * @see ColorScheme.onErrorContainer
 */
val onErrorContainerDarkMediumContrast: Color = Color(0xFF000000)

/**
 * @see ColorScheme.background
 */
val backgroundDarkMediumContrast: Color = Color(0xFF1A120A)

/**
 * @see ColorScheme.onBackground
 */
val onBackgroundDarkMediumContrast: Color = Color(0xFFF1E0D1)

/**
 * @see ColorScheme.surface
 */
val surfaceDarkMediumContrast: Color = Color(0xFF1A120A)

/**
 * @see ColorScheme.onSurface
 */
val onSurfaceDarkMediumContrast: Color = Color(0xFFFFFAF8)

/**
 * @see ColorScheme.surfaceVariant
 */
val surfaceVariantDarkMediumContrast: Color = Color(0xFF544434)

/**
 * @see ColorScheme.onSurfaceVariant
 */
val onSurfaceVariantDarkMediumContrast: Color = Color(0xFFDEC7B1)

/**
 * @see ColorScheme.outline
 */
val outlineDarkMediumContrast: Color = Color(0xFFB59F8B)

/**
 * @see ColorScheme.outlineVariant
 */
val outlineVariantDarkMediumContrast: Color = Color(0xFF93806D)

/**
 * @see ColorScheme.scrim
 */
val scrimDarkMediumContrast: Color = Color(0xFF000000)

/**
 * @see ColorScheme.inverseSurface
 */
val inverseSurfaceDarkMediumContrast: Color = Color(0xFFF1E0D1)

/**
 * @see ColorScheme.inverseOnSurface
 */
val inverseOnSurfaceDarkMediumContrast: Color = Color(0xFF32281F)

/**
 * @see ColorScheme.inversePrimary
 */
val inversePrimaryDarkMediumContrast: Color = Color(0xFF693E00)

/**
 * @see ColorScheme.surfaceDim
 */
val surfaceDimDarkMediumContrast: Color = Color(0xFF1A120A)

/**
 * @see ColorScheme.surfaceBright
 */
val surfaceBrightDarkMediumContrast: Color = Color(0xFF42372D)

/**
 * @see ColorScheme.surfaceContainerLowest
 */
val surfaceContainerLowestDarkMediumContrast: Color = Color(0xFF140D06)

/**
 * @see ColorScheme.surfaceContainerLow
 */
val surfaceContainerLowDarkMediumContrast: Color = Color(0xFF221A11)

/**
 * @see ColorScheme.surfaceContainer
 */
val surfaceContainerDarkMediumContrast: Color = Color(0xFF271E15)

/**
 * @see ColorScheme.surfaceContainerHigh
 */
val surfaceContainerHighDarkMediumContrast: Color = Color(0xFF32281F)

/**
 * @see ColorScheme.surfaceContainerHighest
 */
val surfaceContainerHighestDarkMediumContrast: Color = Color(0xFF3D3329)

/**
 * @see ColorScheme.primary
 */
val primaryDarkHighContrast: Color = Color(0xFFFFFAF8)

/**
 * @see ColorScheme.onPrimary
 */
val onPrimaryDarkHighContrast: Color = Color(0xFF000000)

/**
 * @see ColorScheme.primaryContainer
 */
val primaryContainerDarkHighContrast: Color = Color(0xFFFFBE76)

/**
 * @see ColorScheme.onPrimaryContainer
 */
val onPrimaryContainerDarkHighContrast: Color = Color(0xFF000000)

/**
 * @see ColorScheme.secondary
 */
val secondaryDarkHighContrast: Color = Color(0xFFFFFEFF)

/**
 * @see ColorScheme.onSecondary
 */
val onSecondaryDarkHighContrast: Color = Color(0xFF000000)

/**
 * @see ColorScheme.secondaryContainer
 */
val secondaryContainerDarkHighContrast: Color = Color(0xFFFBCC80)

/**
 * @see ColorScheme.onSecondaryContainer
 */
val onSecondaryContainerDarkHighContrast: Color = Color(0xFF000000)

/**
 * @see ColorScheme.tertiary
 */
val tertiaryDarkHighContrast: Color = Color(0xFFFFFAF8)

/**
 * @see ColorScheme.onTertiary
 */
val onTertiaryDarkHighContrast: Color = Color(0xFF000000)

/**
 * @see ColorScheme.tertiaryContainer
 */
val tertiaryContainerDarkHighContrast: Color = Color(0xFFFFBC95)

/**
 * @see ColorScheme.onTertiaryContainer
 */
val onTertiaryContainerDarkHighContrast: Color = Color(0xFF000000)

/**
 * @see ColorScheme.error
 */
val errorDarkHighContrast: Color = Color(0xFFFFF9F9)

/**
 * @see ColorScheme.onError
 */
val onErrorDarkHighContrast: Color = Color(0xFF000000)

/**
 * @see ColorScheme.errorContainer
 */
val errorContainerDarkHighContrast: Color = Color(0xFFFFB8BE)

/**
 * @see ColorScheme.onErrorContainer
 */
val onErrorContainerDarkHighContrast: Color = Color(0xFF000000)

/**
 * @see ColorScheme.background
 */
val backgroundDarkHighContrast: Color = Color(0xFF1A120A)

/**
 * @see ColorScheme.onBackground
 */
val onBackgroundDarkHighContrast: Color = Color(0xFFF1E0D1)

/**
 * @see ColorScheme.surface
 */
val surfaceDarkHighContrast: Color = Color(0xFF1A120A)

/**
 * @see ColorScheme.onSurface
 */
val onSurfaceDarkHighContrast: Color = Color(0xFFFFFFFF)

/**
 * @see ColorScheme.surfaceVariant
 */
val surfaceVariantDarkHighContrast: Color = Color(0xFF544434)

/**
 * @see ColorScheme.onSurfaceVariant
 */
val onSurfaceVariantDarkHighContrast: Color = Color(0xFFFFFAF8)

/**
 * @see ColorScheme.outline
 */
val outlineDarkHighContrast: Color = Color(0xFFDEC7B1)

/**
 * @see ColorScheme.outlineVariant
 */
val outlineVariantDarkHighContrast: Color = Color(0xFFDEC7B1)

/**
 * @see ColorScheme.scrim
 */
val scrimDarkHighContrast: Color = Color(0xFF000000)

/**
 * @see ColorScheme.inverseSurface
 */
val inverseSurfaceDarkHighContrast: Color = Color(0xFFF1E0D1)

/**
 * @see ColorScheme.inverseOnSurface
 */
val inverseOnSurfaceDarkHighContrast: Color = Color(0xFF000000)

/**
 * @see ColorScheme.inversePrimary
 */
val inversePrimaryDarkHighContrast: Color = Color(0xFF3F2400)

/**
 * @see ColorScheme.surfaceDim
 */
val surfaceDimDarkHighContrast: Color = Color(0xFF1A120A)

/**
 * @see ColorScheme.surfaceBright
 */
val surfaceBrightDarkHighContrast: Color = Color(0xFF42372D)

/**
 * @see ColorScheme.surfaceContainerLowest
 */
val surfaceContainerLowestDarkHighContrast: Color = Color(0xFF140D06)

/**
 * @see ColorScheme.surfaceContainerLow
 */
val surfaceContainerLowDarkHighContrast: Color = Color(0xFF221A11)

/**
 * @see ColorScheme.surfaceContainer
 */
val surfaceContainerDarkHighContrast: Color = Color(0xFF271E15)

/**
 * @see ColorScheme.surfaceContainerHigh
 */
val surfaceContainerHighDarkHighContrast: Color = Color(0xFF32281F)

/**
 * @see ColorScheme.surfaceContainerHighest
 */
val surfaceContainerHighestDarkHighContrast: Color = Color(0xFF3D3329)
