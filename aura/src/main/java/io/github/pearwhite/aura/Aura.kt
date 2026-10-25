package io.github.pearwhite.aura

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.FloatState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.MotionDurationScale
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import io.github.pearwhite.aura.internal.AuraShaderRenderer
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.ceil
import kotlin.time.Duration.Companion.milliseconds

/*
 * Common period of the shader's time terms. Every time multiplier in the shader is a multiple of
 * 0.1, so 20*PI is a whole number of periods for all of them: wrapping time with this value is
 * seamless. If you add a pattern with other multipliers, keep this property or the animation
 * will jump once per period (about 63 seconds).
 * RU: Общий период временных слагаемых шейдера. Все множители времени в шейдере кратны 0.1,
 * RU: поэтому 20*PI содержит целое число периодов у каждого из них: зацикливание бесшовное.
 * RU: Если добавите паттерн с другими множителями, сохраните это свойство, иначе анимация будет
 * RU: скакать раз в период (около 63 секунд).
 */
private const val TIME_PERIOD = (20 * PI).toFloat()

/**
 * An animated "aura" (soft colored blob, sphere, or fill) drawn by an AGSL shader.
 * The shader needs Android 13+; on older versions [fallback] or a plain gradient is shown.
 *
 * @param palette Colors and their shares of the area. Empty palette draws nothing.
 * @param modifier Size and position. The aura is drawn behind the content of this node.
 * @param style Visual parameters, see [AuraStyle].
 * @param shape Outline of the aura, see [AuraShape].
 * @param animation Motion over time, see [AuraAnimation].
 * @param seed Selects a particular arrangement of spots; the same seed gives the same picture.
 * @param colorTransition Spec for smooth palette changes; null applies changes instantly.
 * @param fallback Content for devices below Android 13. Default: a gradient from the palette.
 *
 * RU: Анимированная «аура» (мягкое цветное пятно, шар или заливка), рисуется AGSL-шейдером.
 * RU: Шейдеру нужен Android 13+; на более старых версиях показывается [fallback] или градиент.
 * RU: palette — цвета и их доли площади (пустая палитра ничего не рисует).
 * RU: modifier — размер и положение; аура рисуется позади содержимого этого узла.
 * RU: style, shape, animation — стиль, контур и движение.
 * RU: seed — выбирает конкретное расположение пятен; одинаковый seed даёт одинаковую картинку.
 * RU: colorTransition — анимация смены палитры; null — менять мгновенно.
 * RU: fallback — содержимое для устройств ниже Android 13; по умолчанию градиент из палитры.
 */
@Composable
public fun Aura(
    palette: AuraPalette,
    modifier: Modifier = Modifier,
    style: AuraStyle = AuraDefaults.Style,
    shape: AuraShape = AuraDefaults.Shape,
    animation: AuraAnimation = AuraDefaults.Animation,
    seed: Float = 0f,
    colorTransition: AnimationSpec<Float>? = AuraDefaults.ColorTransition,
    fallback: (@Composable BoxScope.() -> Unit)? = null,
) {
    if (palette.isEmpty) return

    /* The API check lives here, not in AuraShader: AuraShader (and the RuntimeShader class) must
     * never be touched on devices below 33.
     * RU: Проверка версии API здесь, а не в AuraShader: AuraShader (и класс RuntimeShader) нельзя
     * RU: трогать на устройствах ниже 33. */
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        AuraShader(palette, modifier, style, shape, animation, seed, colorTransition)
    } else if (fallback != null) {
        Box(modifier, content = fallback)
    } else {
        Spacer(modifier.background(palette.toFallbackBrush(filled = shape !is AuraShape.Blob)))
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
private fun AuraShader(
    palette: AuraPalette, modifier: Modifier, style: AuraStyle, shape: AuraShape,
    animation: AuraAnimation, seed: Float, colorTransition: AnimationSpec<Float>?,
) {
    /* One renderer (and one RuntimeShader, which is expensive to create) per composition.
     * RU: Один рендерер (и один RuntimeShader, который дорого создавать) на композицию. */
    val renderer = remember { AuraShaderRenderer() }
    val time = rememberAuraTime(animation)
    val frame = rememberPaletteFrame(palette, colorTransition)

    /* IMPORTANT: `frame()` and `time.floatValue` are read inside the draw lambda, so state changes
     * invalidate only the draw phase. Reading them in the composable body would recompose on
     * every animation frame.
     * RU: ВАЖНО: `frame()` и `time.floatValue` читаются внутри draw-лямбды, поэтому изменения
     * RU: состояния инвалидируют только фазу отрисовки. Чтение в теле composable вызывало бы
     * RU: рекомпозицию на каждом кадре анимации. */
    Spacer(modifier.drawBehind {
        renderer.draw(
            this,
            frame(),
            style,
            shape,
            seed,
            time.floatValue
        )
    })
}

/*
 * Time source for the shader. Returns a state holder that is read only in the draw phase.
 * RU: Источник времени для шейдера. Возвращает состояние, которое читается только в фазе отрисовки.
 */
@Composable
private fun rememberAuraTime(animation: AuraAnimation): FloatState {
    /* Hoisted out of the coroutine: after a pause the pattern continues from the same place.
     * RU: Вынесено за пределы корутины: после паузы узор продолжается с того же места. */
    val time = remember { mutableFloatStateOf(0f) }
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val view = LocalView.current

    LaunchedEffect(animation, lifecycle) {
        if (animation !is AuraAnimation.Flow) return@LaunchedEffect

        /* How long to sleep before asking for the next frame (0 = no limit).
         * RU: Сколько спать перед запросом следующего кадра (0 = без лимита). */
        val sleepMs = frameSleepMs(animation.maxFps, view.display?.refreshRate ?: 60f)

        /* The block is cancelled when the lifecycle drops below STARTED (app in background) and
         * restarted on return, so no frame callbacks run while nobody can see the aura.
         * RU: Блок отменяется, когда жизненный цикл опускается ниже STARTED (приложение в фоне),
         * RU: и запускается заново при возврате: пока ауру никто не видит, колбэки кадров не работают. */
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            /* Fresh reference point on every start: otherwise the first dt after a pause would be
             * huge and the animation would jump.
             * RU: Новая точка отсчёта при каждом запуске: иначе первый dt после паузы огромный,
             * RU: и анимация скакнёт. */
            var last = withFrameNanos { it }
            while (true) {
                /* Respect the system "animation scale = off" setting, which hand-written
                 * frame loops do not honor automatically.
                 * RU: Уважаем системную настройку «масштаб анимации = выкл.», которую ручные
                 * RU: циклы по кадрам сами не учитывают. */
                if (currentCoroutineContext()[MotionDurationScale]?.scaleFactor == 0f) {
                    delay(500.milliseconds)
                    last = withFrameNanos { it }
                    continue
                }

                /* Sleeping instead of skipping frames: no frame is requested while we wait, so the
                 * system is free to keep the display at a lower refresh rate. The next vsync
                 * completes the interval.
                 * RU: Спим, а не пропускаем кадры: пока ждём, кадры не запрашиваются, и система
                 * RU: может держать экран на пониженной частоте. Интервал дополняет следующий vsync. */
                if (sleepMs > 0L) delay(sleepMs.milliseconds)

                withFrameNanos { now ->
                    val dt = (now - last) / 1_000_000_000f
                    last = now
                    /* Modulo keeps float precision on long sessions (see TIME_PERIOD).
                     * RU: Остаток от деления сохраняет точность float на длинных сессиях (см. TIME_PERIOD). */
                    time.floatValue = (time.floatValue + dt * animation.speed) % TIME_PERIOD
                }
            }
        }
    }
    return time
}

/*
* Sleep before asking for the next frame. The limit is expressed in display frames: one update every
* k frames, with k rounded UP so the limit is never exceeded (50 fps on 120 Hz becomes 40, not 60).
* We wake in the middle between two vsyncs ((k - 0.5) periods): the next vsync then completes exactly
* k periods, and timer jitter of up to half a period in either direction does not change the result.
* The tiny subtraction protects from float noise: displays report 120.0 as 119.99998 or 60.000004.
* RU: Сон перед запросом следующего кадра. Лимит выражаем в кадрах экрана: одно обновление на k кадров,
* RU: k округляется ВВЕРХ, чтобы лимит не превышался (50 fps на 120 Гц станет 40, а не 60).
* RU: Просыпаемся посередине между двумя vsync ((k - 0.5) периода): следующий vsync дополняет интервал
* RU: ровно до k периодов, и дрожание таймера до полпериода в любую сторону результат не меняет.
* RU: Малое вычитание защищает от шума float: экраны отдают 120.0 как 119.99998, а 60.0 как 60.000004.
*/
private fun frameSleepMs(maxFps: Int?, refreshHz: Float): Long {
    if (maxFps == null || maxFps <= 0) return 0L
    val hz = refreshHz.coerceAtLeast(1f)
    val k = ceil(hz / maxFps - 0.01f).toInt()
    if (k <= 1) return 0L                      // limit is not below the display rate: nothing to do
    return ((k - 0.5f) * 1000f / hz).toLong()
}

/*
 * Holds the "from" and "to" palettes of a running transition.
 * RU: Хранит палитры «из» и «в» текущего перехода.
 */
private class PaletteTransition(initial: AuraPalette) {
    var from = initial
    var to = initial
}

/*
 * Animates between palettes and exposes the current blend as a reusable frame.
 * RU: Анимирует переход между палитрами и отдаёт текущую смесь как переиспользуемый кадр.
 */
@Composable
private fun rememberPaletteFrame(
    target: AuraPalette,
    spec: AnimationSpec<Float>?,
): () -> PaletteFrame {
    val frame = remember { PaletteFrame() }
    val transition = remember { PaletteTransition(target) }
    val progress = remember { Animatable(1f) }

    /* `spec` is read through rememberUpdatedState and is NOT an effect key. If it were a key,
     * changing it mid-animation would cancel the effect, and the early return below would then
     * freeze the animation halfway.
     * RU: `spec` читается через rememberUpdatedState и НЕ является ключом эффекта. Будь он ключом,
     * RU: смена посреди анимации отменила бы эффект, а ранний return ниже заморозил бы анимацию
     * RU: на полпути. */
    val currentSpec by rememberUpdatedState(spec)

    LaunchedEffect(target) {
        if (target == transition.to) return@LaunchedEffect

        /* If a new palette arrives mid-transition, start from what is visible right now
         * (a snapshot), so there is no visual jump.
         * RU: Если новая палитра пришла посреди перехода, стартуем с того, что видно сейчас
         * RU: (снимок), поэтому визуального скачка нет. */
        transition.from = blend(transition.from, transition.to, progress.value)
        transition.to = target

        val s = currentSpec
        if (s == null) {
            progress.snapTo(1f)
        } else {
            progress.snapTo(0f)
            progress.animateTo(1f, s)
        }
    }

    /* Returned as a function so the caller reads `progress.value` in the draw phase.
     * RU: Возвращается функцией, чтобы вызывающий читал `progress.value` в фазе отрисовки. */
    return {
        blendInto(transition.from, transition.to, progress.value, frame)
        frame
    }
}

/*
 * Fallback for Android below 13: a gradient made of the palette, with the same shares.
 * `filled = false` (Blob) fades to transparent like a sphere; `filled = true` covers the whole area.
 * RU: Запасной вариант для Android ниже 13: градиент из палитры с теми же долями.
 * RU: `filled = false` (Blob) растворяется в прозрачность как шар; `filled = true` покрывает всю область.
 */
private fun AuraPalette.toFallbackBrush(filled: Boolean): Brush {
    /* A gradient needs at least two stops, so a single color is a plain fill.
     * RU: Градиенту нужно минимум две точки, поэтому один цвет — это обычная заливка. */
    if (filled && colors.size == 1) return SolidColor(colors[0])
    var acc = 0f
    val stops = buildList {
        for (i in colors.indices) {
            add((acc + weights[i] / 2f) * (if (filled) 1f else 0.8f) to colors[i])
            acc += weights[i]
        }
        if (!filled) add(1f to Color.Transparent)
    }.toTypedArray()
    return if (filled) Brush.linearGradient(colorStops = stops)
    else Brush.radialGradient(colorStops = stops)
}

