# Smart Assistant — Design Specification (from Jetpack Compose source)

> Source of truth: `app/src/main/java/com/smartassistant/app/ui/**` and `app/src/main/java/com/smartassistant/app/ui/theme/**`.
>
> Notes:
> - Where the code provides separate Light/Dark values, both are listed.
> - Some “tokens” are implicit (e.g., commonly repeated `dp` paddings); those are derived from usage across components.

---

## 1) Color Palette

### 1.1 Core Material 3 scheme

**Primary**
- Light: `#006B5A` (`PrimaryLight`)
- Dark: `#5BDBC0` (`PrimaryDark`)

**Background**
- Light: `#F5FBF8` (`BackgroundLight`)
- Dark: `#0F1513` (`BackgroundDark`)

**Surface**
- Light: `#F5FBF8` (`SurfaceLight`)
- Dark: `#0F1513` (`SurfaceDark`)

**Text**
- Primary text (on background/surface)
  - Light: `#171D1B` (`OnBackgroundLight` / `OnSurfaceLight`)
  - Dark: `#DFE4E0` (`OnBackgroundDark` / `OnSurfaceDark`)
- Secondary text (on surface variant)
  - Light: `#3F4945` (`OnSurfaceVariantLight`)
  - Dark: `#BEC9C4` (`OnSurfaceVariantDark`)

**Error**
- Light: `#BA1A1A` (`ErrorLight`)
- Dark: `#FFB4AB` (`ErrorDark`)

### 1.2 Supporting palette (commonly referenced)

**Surface Variant (used for “container” / subtle areas)**
- Light: `#DAE5E0` (`SurfaceVariantLight`)
- Dark: `#3F4945` (`SurfaceVariantDark`)

**Outline / Dividers**
- Outline Light: `#6F7975` (`OutlineLight`)
- Outline Dark: `#89938F` (`OutlineDark`)
- Outline Variant Light: `#BEC9C4` (`OutlineVariantLight`)
- Outline Variant Dark: `#3F4945` (`OutlineVariantDark`)

**Tertiary (status dot when generating)**
- Light: `#416277` (`TertiaryLight`)
- Dark: `#A8CBE2` (`TertiaryDark`)

### 1.3 Extended / “App-specific” colors

Defined in `Theme.kt` (via `SmartAssistantTheme.extendedColors`).

**Message bubble colors**
- User bubble
  - Light: `#006B5A` (same as PrimaryLight)
  - Dark: `#5BDBC0` (same as PrimaryDark)
- AI bubble
  - Light: `#E8F5F1` (`AiBubbleColorLight`)
  - Dark: `#1E2D28` (`AiBubbleColorDark`)

**Input bar background**
- Light: `#FFFFFF` (`InputBarBackgroundLight`)
- Dark: `#1A2420` (`InputBarBackgroundDark`)

**Icon tint**
- Light: `#006B5A` (`IconTintLight`)
- Dark: `#5BDBC0` (`IconTintDark`)

**Divider color (non-primary divider token, not always used)**
- Light: `#E0E6E3` (`DividerColorLight`)
- Dark: `#2A3530` (`DividerColorDark`)

### 1.4 Scrim / overlay colors

**Drawer scrim**
- Color: `#000000` with animated alpha to **0.50** when visible (`AnimatedDrawerOverlay`)

**Voice listening overlay**
- Color: `#000000` with alpha **0.35** (`VoiceListeningOverlay`)

---

## 2) Typography

### 2.1 Font family

**Barlow** is the app’s typeface and is bundled in `app/src/main/res/font/`.

Included weights:
- Thin (100)
- ExtraLight (200)
- Light (300)
- Regular/Normal (400)
- Medium (500)
- SemiBold (600)
- Bold (700)
- ExtraBold (800)
- Black (900)

Italic variants are also present for the same weights.

### 2.2 Type scale mapping (requested levels)

The app defines Material 3 typography in `Type.kt`. The following table maps your requested levels to the closest Material styles in the code.

| Level (Spec) | Compose style used | Font family | Weight | Size | Line height |
|---|---|---:|---:|---:|---:|
| **H1** | `displaySmall` | Barlow | 400 (Normal) | 36sp | 44sp |
| **H2** | `headlineSmall` | Barlow | 400 (Normal) | 24sp | 32sp |
| **Body1** | `bodyLarge` | Barlow | 400 (Normal) | 16sp | 24sp |
| **Body2** | `bodyMedium` | Barlow | 400 (Normal) | 14sp | 20sp |
| **Caption** | `bodySmall` *(or `labelSmall` where “label” semantics apply)* | Barlow | 400 (Normal) *(labelSmall is 500)* | 12sp *(labelSmall is 11sp)* | 16sp |

### 2.3 Component-specific overrides seen in UI

These are explicit overrides on top of Material styles:

- **Message bubble text**: `bodyMedium` but overridden to **15sp** size and **22sp** line height (`MessageBubble` / `MessageBubbleNew`).
- **Top bar title**: 18sp, SemiBold.
- **Top bar status**: `typography.bodySmall` (12sp / 16sp from theme).
- **Welcome title**: 28sp, Bold.
- **Welcome description**: 16sp, lineHeight 24sp.
- **Suggestion chip text**: 13sp, Medium.
- **History/Settings drawer header**: 18sp, SemiBold, letter spacing not specified.

---

## 3) Spacing & Sizing

### 3.1 Spacing tokens observed (dp)

Most layouts reuse a small set of dp values. These show up repeatedly across `ChatScreen`, top bars, bubbles, drawers, and chips:

- **2dp**: small vertical gap (e.g., subtitle spacing in top bar)
- **3dp**: micro spacing in history preview separation
- **4dp**: small bottom padding; small corner “flatness” in bubble shapes
- **6dp**: small horizontal gap (status dot → text)
- **8dp**: common vertical spacing (toolbars, chip container, list padding, etc.)
- **12dp**: primary baseline padding for many horizontal edges (chat content, toolbars, input)
- **14dp**: history item vertical padding
- **16dp**: standard “content padding” token (message inner padding, drawer header horizontal padding)
- **20dp**: dialog horizontal padding; dialog corner radius; divider inset
- **24dp**: welcome bottom spacing; large spacing
- **32dp**: welcome container padding

### 3.2 Key component sizes

**Toolbar / Top bars**
- **ChatTopBar height**: **64dp** (`ChatTopBar`)
- **Drawer header row height**: **56dp** (`HistoryDrawer`, `SettingsDrawer`)

**Buttons / icon buttons (touch targets)**
- Standard icon button touch box: **40dp** square (History/NewChat/Delete/Settings/Add)
- Common icon size inside: **20–24dp**
- Chat input trailing action button container: **36dp** (mic/send/stop)

**Input bar**
- In `ChatScreen`, input bar base height is treated as **80dp** for inset math (`inputBarBaseHeight = 80.dp`).
- Actual visible field: Surface has vertical padding 8dp outside + TextField inside; the value is best treated as **≈80dp overall footprint**.

**Message bubble**
- Max bubble width: **300dp** (`widthIn(max = 300.dp)`).
- Outer message row padding: **horizontal 12dp**, **vertical 6dp**.
- Bubble content padding: **horizontal 16dp**, **vertical 12dp**.
- Bubble “height” is content-dependent (no fixed height). Baseline minimum can be inferred from padding:
  - Single-line text bubble min content height ≈ `12dp + text lineHeight (22sp) + 12dp`.

**Suggestion chips**
- Chip padding: **12dp horizontal / 8dp vertical**, icon 16dp.
- Chip corner radius: 18dp (see Shapes section).

**Dividers**
- App divider thickness: **2dp** (`AppHorizontalDivider`).

---

## 4) Shapes (Corner Radius)

### 4.1 Material shape scale

From `Shape.kt` (`Shapes`):
- extraSmall: **4dp**
- small: **8dp**
- medium: **12dp**
- large: **16dp**
- extraLarge: **24dp**

### 4.2 Message bubbles (asymmetrical)

Used in `MessageBubble` / `MessageBubbleNew`.

- **User bubble** (`UserBubbleShape`):
  - topStart 20dp
  - topEnd 20dp
  - bottomStart 20dp
  - bottomEnd **4dp** *(flat corner points toward sender/right)*

- **AI bubble** (`AiBubbleShape`):
  - topStart 20dp
  - topEnd 20dp
  - bottomStart **4dp** *(flat corner points toward sender/left)*
  - bottomEnd 20dp

### 4.3 Other common shapes

- **Input field** (`InputFieldShape`): **28dp** (pill-like)
- **Card / container** (`CardShape`): **16dp**
- **Chip** (`ChipShape`): **18dp**
- **Dialog (AddActionDialog)**: **20dp** rounded corners
- **TopBarShape**: explicitly all **0dp** (rect)

> Drawer corner radius: not explicitly set (drawers are `Surface` filling their allocated width).

---

## 5) Component Structure

### 5.1 Chat screen layout (`ChatScreen`)

Hierarchy:
1. Root `Box` (fills screen, background = `colorScheme.background`, padded by status bars)
2. Main `Column`
   - `ChatTopBar` (fixed 64dp)
   - `ChatToolbar` (divider + 8dp spacer + row of 2 icon buttons)
   - `LazyColumn` messages (weight 1f)
3. Bottom-aligned `Column` (input region)
   - Suggestion chips (animated)
   - Pending attachments row (if any)
   - `ChatInput`
4. Overlays
   - Add dialog
   - Voice listening overlay
   - History drawer overlay (slides from left)
   - Settings drawer overlay (slides from right)

### 5.2 Message item / bubble (`MessageBubble` / `MessageBubbleNew`)

**Alignment rules**
- Container: `Row(fillMaxWidth)` with `horizontalArrangement`:
  - User message → `Arrangement.End`
  - AI message → `Arrangement.Start`

**Visual container**
- `Surface`
  - shape: `UserBubbleShape` or `AiBubbleShape`
  - color:
    - User → `extendedColors.userBubble`
    - AI → `extendedColors.aiBubble`
  - elevation:
    - User: tonal 0dp, shadow 2dp
    - AI: tonal 1dp, shadow 1dp
  - width: `widthIn(max = 300.dp)`

**Inner content**
- `Column(padding horizontal 16dp, vertical 12dp)`
- Optional attachments section (only in `MessageBubbleNew`)
- Message body:
  - if state = `TYPING` → `AiTypingIndicator()` (animated dots)
  - else → `Text` with 15sp size / 22sp line height

### 5.3 ChatTopBar

- `Surface` (height 64dp, elevation/shadow)
- `Row(padding horizontal 12dp)`
  - App icon (40dp)
  - Spacer 12dp
  - `Column(weight 1f)`
    - Title (18sp SemiBold, primary color)
    - Subtitle row:
      - Status dot (8dp circle, color changes)
      - Spacer 6dp
      - Status text (`bodySmall`, onSurfaceVariant)
  - Optional spinner when generating (20dp)

### 5.4 ChatToolbar

- `Column`
  - `AppHorizontalDivider` (2dp primary)
  - Spacer 8dp
  - `Row(padding horizontal 12dp, spaceBetween)`
    - History icon button (40dp, icon 24dp)
    - Settings icon button (40dp, icon 24dp)

### 5.5 Drawers

**History drawer**
- Drawer container: `Surface(fillMaxHeight, fillMaxWidth)` inside a parent that already constrains width.
- Header row: 56dp height, horizontal padding 16dp.
- List items:
  - Row padding: 16dp horizontal, 14dp vertical.
  - Press/click feedback: scale down + background color tint.
  - Delete button: IconButton 40dp.

**Settings drawer**
- Similar structure: header 56dp, divider, then settings list.
- Settings item:
  - Row vertical padding 12dp.
  - Title 16sp Medium; description 13sp Normal.

---

## 6) Behavior (Drawer + scrim/blur)

### 6.1 Drawer width and placement

Implemented in `AnimatedDrawerOverlay`:

- Drawer occupies **80% of screen width**: `fillMaxWidth(0.80f)`.
- Drawer fills height.
- Alignment:
  - History drawer: aligned to **start** (left) (`slideFromRight = false`).
  - Settings drawer: aligned to **end** (right) (`slideFromRight = true`).

### 6.2 Animations

- **Scrim alpha animation**: 0 → 0.5 when visible (`animateFloatAsState`, `tween(300ms)`).
- **Drawer slide animation**: `animateIntOffsetAsState` tween 300ms, `FastOutSlowInEasing`.
  - Hidden state offset: ±800 px on X (positive for right drawer, negative for left drawer).
  - Visible state offset: `IntOffset.Zero`.

### 6.3 Scrim interaction

- Scrim is a full-screen `Box` painted with `Color.Black.copy(alpha = overlayAlpha)`.
- It’s clickable with `indication = null` (no ripple) and dismisses the drawer on tap.

### 6.4 Background blur

- **No blur effect is implemented**.
- Current behavior is a **simple dimming scrim** (black overlay) without `graphicsLayer` blur or RenderEffect.

---

## Quick copy/paste tokens for Figma

### Colors (Light mode focus)
- Primary: `#006B5A`
- Background: `#F5FBF8`
- Surface: `#F5FBF8`
- Text / OnSurface: `#171D1B`
- Text Secondary / OnSurfaceVariant: `#3F4945`
- Error: `#BA1A1A`
- AI Bubble: `#E8F5F1`
- Input Surface: `#FFFFFF`
- Scrim: `#000000` @ 50%

### Radii
- Card: 16dp
- Input pill: 28dp
- Chip: 18dp
- Message bubble: 20dp with one corner 4dp (sender side)
- Dialog: 20dp

### Key heights
- TopBar: 64dp
- Drawer header: 56dp
- IconButton touch target: 40dp
- Input bar footprint: ~80dp (used for inset/padding math)
