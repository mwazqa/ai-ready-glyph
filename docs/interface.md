# Interface

The English interface uses native Android Views. Version 0.13.4 retains the 0.9.0 dotted wordmark, device caption, hero text, rounded dark cards and red, black and white palette.

## Home screen

The status card shows readiness, available selected-source counts, notification access and playback status. Details expands the latest notification event and Glyph action. A prominent 64 dp power button at the top of this card controls automatic alerts. Its label stays Enable Glyph alerts in both states. The fill is red while alerts are enabled and gray while paused. Tapping toggles the setting; pausing stops current playback. The button exposes the current on/off state to accessibility services. While paused, secondary status controls and the Apps, Animations and Toy cards stay visible at 40% opacity with their controls disabled. The power button and pause explanation remain readable. Pausing stops on-screen previews as well as controller playback. Enabling alerts restores the controls and their existing choices. Notification access opens the Android explanation and settings. Action buttons retain their static fill or outline without a click ripple or elevation animation.

A compact Apps card shows how many available sources are enabled and installed. Manage apps opens source settings. During setup this card comes before Animations; after access and source setup, Animations comes first. The optional Glyph Toy card follows. Source switches no longer form long lists on the home screen.

## Animation library

The Animations card contains search, All / AI / Messages / Social text tabs and bounded pages. The animation total is not displayed. All shows every category; AI, Messages and Social narrow the results. Each tab measures its own single-line text and uses a red active underline provided by its selected-state background. The tab strip can scroll horizontally at large text sizes. It does not squeeze labels into equal-width pills.

Each animation has a circular black matrix face and a label, with no persistent rectangular border or background around the tile. Tiles and category tabs have no pressed flash or ripple. With alerts enabled, tapping plays the complete on-screen sequence and requests an immediate physical Glyph test on supported phones. Unsupported phones retain on-screen previews.

Swipe horizontally across the symbols to change pages. The page indicator says Swipe and shows the current page count. There are no arrow buttons. Paging is also exposed through keyboard and Android accessibility scroll actions. Horizontal motion cancels child tile touches before paging, while vertical scrolling is handed back to the outer screen. Intent calculation tests do not verify Android's native event routing.

The normal grid shows at most four symbols in two columns. Screens below 360 dp or above a 1.3 font scale use one column with at most two symbols per page. Matrix faces are 124 dp tall, or 192 dp in one column. Labels wrap. Changing category or search returns to the first result page; category, query and page survive Activity recreation.

Search and categories combine, matching every query word against name, stable ID or category. Page counts use the actual style list. Adding a style to an existing category does not require changing page layout or category-size assumptions.

## Apps screen

Manage apps shows only installed, Android-enabled supported sources. AI entries retain the model-provider catalog and Gemini-via-Google eligibility rule. Messaging and social entries retain their exact source identities; SMS uses the current resolved default messaging app.

Rows contain a small system app icon, name and native switch. Optional Google and SMS rows explain their broader notification scope. Tapping a row or switch changes its source immediately; there is no Save button or animation assignment control.

Search and category tabs combine with one app-state filter. Tapping that control cycles All apps, Enabled only and Disabled only. All apps is muted; either filtered mode is red. There is no checkmark. Accessibility descriptions name the current and next mode. Using filters never clears a source selection.

Enable all activates disabled apps in the current filtered results and saves immediately. Once all results are enabled, the same action becomes Disable all and turns those results off. Mixed results offer Enable all. Hidden sources are unchanged, and empty results disable the action. Disabling with Enabled only active, or enabling with Disabled only active, removes the affected rows from the current results. The action labels share a row and wrap at large text sizes. Counts and empty-state messages update immediately. Previously selected unavailable apps keep their preferences and reappear when eligible again.

Source choices keep the original AI package, messaging-source and social-source preference keys. SMS uses its logical source identity when its resolved package changes. The screen uses recycled ListView rows with reset switch listeners, plus an icon cache bounded to 48 entries. Category, query, app-state filter and list state use Android Activity state. Source settings are separate from preview selection.

## Playback and verification limits

Playing another symbol replaces the current manual test. Filtering or swiping while enabled stops previews removed from the screen without sending a Glyph command; the existing physical sequence continues until completion or replacement. On-screen previews complete their closing motion, remain blank for 250 ms and restore the static symbol. Physical duration remains six seconds. SDK binding can make physical playback start later than its on-screen preview.

Notification symbols remain fixed to their sources. Other AI providers use generic lettering. Automatic notifications retain the six-second wait followed by their full six-second animation. Preview choices only remember the optional Toy style.

The main screen and list respect system-bar insets. Apps reserves remaining height for a scrollable list; large text can still require scrolling. Native source switches retain 54 x 32 dp tracks and 28 dp thumbs. The Apps screen resizes for the keyboard.

Run python scripts/build_ui_preview.py with a JDK through JAVA_HOME or PATH. It exports actual renderer frames at 50 ms intervals and creates interactive layout reconstructions, including 320 px views with text at 200%. Browser app icons are illustrative monograms; Android uses installed system icons. Browser previews do not control a phone and are not Android screenshots. Native gestures, layout, row recycling, keyboard, state restoration, TalkBack and physical Glyph output require phone-side verification.
