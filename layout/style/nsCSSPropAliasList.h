/* vim: set shiftwidth=2 tabstop=8 autoindent cindent expandtab: */
/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

/*
 * a list of all CSS property aliases with data about them, for preprocessing
 */

/******

  This file contains the list of all CSS properties that are just
  aliases for other properties (e.g., for when we temporarily continue
  to support a prefixed property after adding support for its unprefixed
  form).  It is designed to be used as inline input through the magic of
  C preprocessing.  All entries must be enclosed in the appropriate
  CSS_PROP_ALIAS macro which will have cruel and unusual things done to
  it.

  The arguments to CSS_PROP_ALIAS are:

  -. 'aliasname' entries represent a CSS property name and *must* use
  only lowercase characters.

  -. 'id' should be the same as the 'id' field in nsCSSPropList.h for
  the property that 'aliasname' is being aliased to.

  -. 'method' is the CSS2Properties property name.  Unlike
  nsCSSPropList.h, prefixes should just be included in this file (rather
  than needing the CSS_PROP_DOMPROP_PREFIXED(prop) macro).

  -. 'pref' is the name of a pref that controls whether the property
  is enabled.  The property is enabled if 'pref' is an empty string,
  or if the boolean property whose name is 'pref' is set to true.

 ******/

CSS_PROP_ALIAS(-moz-transform-origin, transform_origin, MozTransformOrigin, "layout.css.prefixes.transforms")
CSS_PROP_ALIAS(-moz-perspective-origin, perspective_origin, MozPerspectiveOrigin, "layout.css.prefixes.transforms")
CSS_PROP_ALIAS(-moz-perspective, perspective, MozPerspective, "layout.css.prefixes.transforms")
CSS_PROP_ALIAS(-moz-transform-style, transform_style, MozTransformStyle, "layout.css.prefixes.transforms")
CSS_PROP_ALIAS(-moz-backface-visibility, backface_visibility, MozBackfaceVisibility, "layout.css.prefixes.transforms")
CSS_PROP_ALIAS(-moz-border-image, border_image, MozBorderImage, "layout.css.prefixes.border-image")
CSS_PROP_ALIAS(-moz-transition, transition, MozTransition, "layout.css.prefixes.transitions")
CSS_PROP_ALIAS(-moz-transition-delay, transition_delay, MozTransitionDelay, "layout.css.prefixes.transitions")
CSS_PROP_ALIAS(-moz-transition-duration, transition_duration, MozTransitionDuration, "layout.css.prefixes.transitions")
CSS_PROP_ALIAS(-moz-transition-property, transition_property, MozTransitionProperty, "layout.css.prefixes.transitions")
CSS_PROP_ALIAS(-moz-transition-timing-function, transition_timing_function, MozTransitionTimingFunction, "layout.css.prefixes.transitions")
CSS_PROP_ALIAS(-moz-animation, animation, MozAnimation, "layout.css.prefixes.animations")
CSS_PROP_ALIAS(-moz-animation-delay, animation_delay, MozAnimationDelay, "layout.css.prefixes.animations")
CSS_PROP_ALIAS(-moz-animation-direction, animation_direction, MozAnimationDirection, "layout.css.prefixes.animations")
CSS_PROP_ALIAS(-moz-animation-duration, animation_duration, MozAnimationDuration, "layout.css.prefixes.animations")
CSS_PROP_ALIAS(-moz-animation-fill-mode, animation_fill_mode, MozAnimationFillMode, "layout.css.prefixes.animations")
CSS_PROP_ALIAS(-moz-animation-iteration-count, animation_iteration_count, MozAnimationIterationCount, "layout.css.prefixes.animations")
CSS_PROP_ALIAS(-moz-animation-name, animation_name, MozAnimationName, "layout.css.prefixes.animations")
CSS_PROP_ALIAS(-moz-animation-play-state, animation_play_state, MozAnimationPlayState, "layout.css.prefixes.animations")
CSS_PROP_ALIAS(-moz-animation-timing-function, animation_timing_function, MozAnimationTimingFunction, "layout.css.prefixes.animations")
CSS_PROP_ALIAS(font-feature-settings, font_feature_settings, FontFeatureSettings, "layout.css.font-features.enabled")
CSS_PROP_ALIAS(font-language-override, font_language_override, FontLanguageOverride, "layout.css.font-features.enabled")
CSS_PROP_ALIAS(-webkit-box-sizing, box_sizing, WebkitBoxSizing, "")
CSS_PROP_ALIAS(-webkit-user-select, user_select, WebkitUserSelect, "")
CSS_PROP_ALIAS(-webkit-align-items, align_items, WebkitAlignItems, "")
CSS_PROP_ALIAS(-webkit-align-self, align_self, WebkitAlignSelf, "")
CSS_PROP_ALIAS(-webkit-animation, animation, WebkitAnimation, "")
CSS_PROP_ALIAS(-webkit-animation-delay, animation_delay, WebkitAnimationDelay, "")
CSS_PROP_ALIAS(-webkit-animation-direction, animation_direction, WebkitAnimationDirection, "")
CSS_PROP_ALIAS(-webkit-animation-duration, animation_duration, WebkitAnimationDuration, "")
CSS_PROP_ALIAS(-webkit-animation-fill-mode, animation_fill_mode, WebkitAnimationFillMode, "")
CSS_PROP_ALIAS(-webkit-animation-iteration-count, animation_iteration_count, WebkitAnimationIterationCount, "")
CSS_PROP_ALIAS(-webkit-animation-name, animation_name, WebkitAnimationName, "")
CSS_PROP_ALIAS(-webkit-animation-play-state, animation_play_state, WebkitAnimationPlayState, "")
CSS_PROP_ALIAS(-webkit-animation-timing-function, animation_timing_function, WebkitAnimationTimingFunction, "")
CSS_PROP_ALIAS(-webkit-appearance, appearance, WebkitAppearance, "")
CSS_PROP_ALIAS(-webkit-backface-visibility, backface_visibility, WebkitBackfaceVisibility, "")
CSS_PROP_ALIAS(-webkit-background-clip, background_clip, WebkitBackgroundClip, "")
CSS_PROP_ALIAS(-webkit-background-origin, background_origin, WebkitBackgroundOrigin, "")
CSS_PROP_ALIAS(-webkit-background-size, background_size, WebkitBackgroundSize, "")
CSS_PROP_ALIAS(-webkit-border, border, WebkitBorder, "")
CSS_PROP_ALIAS(-webkit-border-end, border_end, WebkitBorderEnd, "")
CSS_PROP_ALIAS(-webkit-border-end-color, border_end_color, WebkitBorderEndColor, "")
CSS_PROP_ALIAS(-webkit-border-end-style, border_end_style, WebkitBorderEndStyle, "")
CSS_PROP_ALIAS(-webkit-border-end-width, border_end_width, WebkitBorderEndWidth, "")
CSS_PROP_ALIAS(-webkit-border-start, border_start, WebkitBorderStart, "")
CSS_PROP_ALIAS(-webkit-border-start-color, border_start_color, WebkitBorderStartColor, "")
CSS_PROP_ALIAS(-webkit-border-start-style, border_start_style, WebkitBorderStartStyle, "")
CSS_PROP_ALIAS(-webkit-border-start-width, border_start_width, WebkitBorderStartWidth, "")
CSS_PROP_ALIAS(-webkit-border-image, border_image, WebkitBorderImage, "")
CSS_PROP_ALIAS(-webkit-border-radius, border_radius, WebkitBorderRadius, "")
CSS_PROP_ALIAS(-webkit-box-align, box_align, WebkitBoxAlign, "")
CSS_PROP_ALIAS(-webkit-box-direction, box_direction, WebkitBoxDirection, "")
CSS_PROP_ALIAS(-webkit-box-flex, box_flex, WebkitBoxFlex, "")
CSS_PROP_ALIAS(-webkit-box-ordinal-group, box_ordinal_group, WebkitBoxOrdinalGroup, "")
CSS_PROP_ALIAS(-webkit-box-orient, box_orient, WebkitBoxOrient, "")
CSS_PROP_ALIAS(-webkit-box-pack, box_pack, WebkitBoxPack, "")
CSS_PROP_ALIAS(-webkit-box-shadow, box_shadow, WebkitBoxShadow, "")
CSS_PROP_ALIAS(-webkit-clip-path, clip_path, WebkitClipPath, "")
CSS_PROP_ALIAS(-webkit-column-count, _moz_column_count, WebkitColumnCount, "")
CSS_PROP_ALIAS(-webkit-column-width, _moz_column_width, WebkitColumnWidth, "")
CSS_PROP_ALIAS(-webkit-column-gap, _moz_column_gap, WebkitColumnGap, "")
CSS_PROP_ALIAS(-webkit-column-rule, _moz_column_rule, WebkitColumnRule, "")
CSS_PROP_ALIAS(-webkit-column-rule-color, _moz_column_rule_color, WebkitColumnRuleColor, "")
CSS_PROP_ALIAS(-webkit-column-rule-style, _moz_column_rule_style, WebkitColumnRuleStyle, "")
CSS_PROP_ALIAS(-webkit-column-rule-width, _moz_column_rule_width, WebkitColumnRuleWidth, "")
CSS_PROP_ALIAS(-webkit-columns, _moz_columns, WebkitColumns, "")
CSS_PROP_ALIAS(-webkit-filter, filter, WebkitFilter, "")
CSS_PROP_ALIAS(-webkit-flex, flex, WebkitFlex, "")
CSS_PROP_ALIAS(-webkit-flex-basis, flex_basis, WebkitFlexBasis, "")
CSS_PROP_ALIAS(-webkit-flex-direction, flex_direction, WebkitFlexDirection, "")
CSS_PROP_ALIAS(-webkit-flex-grow, flex_grow, WebkitFlexGrow, "")
CSS_PROP_ALIAS(-webkit-flex-shrink, flex_shrink, WebkitFlexShrink, "")
CSS_PROP_ALIAS(-webkit-justify-content, justify_content, WebkitJustifyContent, "")
CSS_PROP_ALIAS(-webkit-order, order, WebkitOrder, "")
CSS_PROP_ALIAS(-webkit-padding-start, padding_start, WebkitPaddingStart, "")
CSS_PROP_ALIAS(-webkit-padding-end, padding_end, WebkitPaddingEnd, "")
CSS_PROP_ALIAS(-webkit-perspective, perspective, WebkitPerspective, "")
CSS_PROP_ALIAS(-webkit-perspective-origin, perspective_origin, WebkitPerspectiveOrigin, "")
CSS_PROP_ALIAS(-webkit-text-size-adjust, text_size_adjust, WebkittextSizeAdjust, "")
CSS_PROP_ALIAS(-webkit-stroke, stroke, WebkitStroke, "")
CSS_PROP_ALIAS(-webkit-stroke-width, stroke_width, WebkitstrokeWidth, "")
CSS_PROP_ALIAS(-webkit-transform, transform, WebkitTransform, "")
CSS_PROP_ALIAS(-webkit-transform-origin, transform_origin, WebkitOrigin, "")
CSS_PROP_ALIAS(-webkit-transform-style, transform_style, WebkitStyle, "")
CSS_PROP_ALIAS(-webkit-transition, transition, WebkitTransition, "")
CSS_PROP_ALIAS(-webkit-transition-delay, transition_delay, WebkitTransitionDelay, "")
CSS_PROP_ALIAS(-webkit-transition-duration, transition_duration, WebkitTransitionDuration, "")
CSS_PROP_ALIAS(-webkit-transition-property, transition_property, WebkitTransitionProperty, "")
CSS_PROP_ALIAS(-webkit-transition-timing-function, transition_timing_function, WebkitTransitionTimingFunction, "")
CSS_PROP_ALIAS(-webkit-user-modify, user_modify, WebkitUserModify, "")
