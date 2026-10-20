package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
import kotlinx.coroutines.delay

private val Teal = Color(0xFF0891B2)
private val DeepTeal = Color(0xFF0E7490)
private val Green = Color(0xFF16A34A)
private val Aqua = Color(0xFF22D3EE)
private val Muted = Color(0xFF64748B)
private val Gradient = Brush.linearGradient(listOf(Teal, DeepTeal))
private val Shape = RoundedCornerShape(18.dp)
private val Outfit = FontFamily(Font(R.font.outfit))
private val AquaTypography =
    Typography().let { t ->
        t.copy(
            displayLarge = t.displayLarge.copy(fontFamily = Outfit),
            displayMedium = t.displayMedium.copy(fontFamily = Outfit),
            displaySmall = t.displaySmall.copy(fontFamily = Outfit),
            headlineLarge = t.headlineLarge.copy(fontFamily = Outfit),
            headlineMedium = t.headlineMedium.copy(fontFamily = Outfit),
            headlineSmall = t.headlineSmall.copy(fontFamily = Outfit),
            titleLarge = t.titleLarge.copy(fontFamily = Outfit),
            titleMedium = t.titleMedium.copy(fontFamily = Outfit),
            titleSmall = t.titleSmall.copy(fontFamily = Outfit),
            bodyLarge = t.bodyLarge.copy(fontFamily = Outfit),
            bodyMedium = t.bodyMedium.copy(fontFamily = Outfit),
            bodySmall = t.bodySmall.copy(fontFamily = Outfit),
            labelLarge = t.labelLarge.copy(fontFamily = Outfit),
            labelMedium = t.labelMedium.copy(fontFamily = Outfit),
            labelSmall = t.labelSmall.copy(fontFamily = Outfit),
        )
    }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { AquaSmartApp() }
    }
}

private data class Parameter(
    val name: String,
    val value: String,
    val range: String,
    val icon: String,
    val status: String,
    val trend: List<Float>,
)

private val parameters =
    listOf(
        Parameter(
            "Temperature",
            "27.4°C",
            "24–28°C",
            "🌡️",
            "Optimal",
            listOf(26.8f, 27f, 27.1f, 27.3f, 27.4f, 27.2f, 27.4f),
        ),
        Parameter(
            "pH Level",
            "7.2",
            "6.8–7.5",
            "⚗️",
            "Optimal",
            listOf(7.1f, 7f, 7.2f, 7.3f, 7.1f, 7.2f, 7.2f),
        ),
        Parameter(
            "Dissolved O₂",
            "6.8 mg/L",
            "5–8 mg/L",
            "💧",
            "Good",
            listOf(6.5f, 6.6f, 6.9f, 6.7f, 6.8f, 6.8f, 6.8f),
        ),
        Parameter(
            "Turbidity",
            "3.2 NTU",
            "0–5 NTU",
            "🔵",
            "Safe",
            listOf(3f, 3.1f, 3.3f, 3.2f, 3.4f, 3.1f, 3.2f),
        ),
        Parameter(
            "EC / TDS",
            "650 µS/cm",
            "400–1200 µS/cm",
            "⚡",
            "Optimal",
            listOf(620f, 630f, 640f, 650f, 645f, 655f, 650f),
        ),
    )

/** Preferences are local prototype settings. No hardware commands are sent. */
private class AppPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("aquasmart", Context.MODE_PRIVATE)

    fun bool(key: String, default: Boolean) = prefs.getBoolean(key, default)

    fun save(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    fun text(key: String, default: String) = prefs.getString(key, default) ?: default

    fun save(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }
}

@Composable
fun AquaSmartApp() {
    val context = LocalContext.current
    val prefs = remember { AppPreferences(context) }
    var dark by rememberSaveable { mutableStateOf(prefs.bool("dark", false)) }
    var fahrenheit by rememberSaveable { mutableStateOf(prefs.bool("fahrenheit", false)) }
    var name by rememberSaveable { mutableStateOf(prefs.text("name", "Juan dela Cruz")) }
    var email by rememberSaveable {
        mutableStateOf(prefs.text("email", "juandelacruz@aquasmart.ph"))
    }
    var screen by rememberSaveable { mutableStateOf("splash") }
    var dialog by rememberSaveable { mutableStateOf<String?>(null) }
    val colors =
        if (dark)
            darkColorScheme(
                primary = Aqua,
                secondary = Green,
                background = Color(0xFF081B27),
                surface = Color(0xFF122C3A),
                onSurface = Color(0xFFE2E8F0),
            )
        else
            lightColorScheme(
                primary = Teal,
                secondary = Green,
                background = Color(0xFFF0F9FF),
                surface = Color.White,
                onSurface = Color(0xFF0F172A),
            )
    MaterialTheme(colorScheme = colors, typography = AquaTypography) {
        val nav: (String) -> Unit = { screen = it }
        BackHandler(screen != "splash") {
            screen =
                if (screen in listOf("home", "monitoring", "feeding", "analytics", "more")) "splash"
                else "more"
        }
        Surface(Modifier.fillMaxSize(), color = colors.background) {
            Column(Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)) {
                Box(Modifier.weight(1f).fillMaxWidth()) {
                    key(screen) {
                        when (screen) {
                            "splash" -> Welcome { nav("home") }
                            "home" -> Home(name, fahrenheit, nav)
                            "monitoring" -> Monitoring(fahrenheit)
                            "feeding" -> ConnectedFeeding()
                            "scanner" -> Scanner(false)
                            "fish-detection" -> Scanner(true)
                            "analytics" -> Analytics(fahrenheit)
                            "alerts" -> Alerts()
                            "devices" -> Devices { dialog = "Add Device" }
                            "settings" ->
                                Settings(
                                    prefs,
                                    dark,
                                    {
                                        dark = it
                                        prefs.save("dark", it)
                                    },
                                    fahrenheit,
                                    {
                                        fahrenheit = it
                                        prefs.save("fahrenheit", it)
                                    },
                                ) {
                                    dialog = it
                                }
                            "profile" ->
                                Profile(
                                    name,
                                    email,
                                    nav,
                                    { dialog = it },
                                    {
                                        val csv =
                                            "AquaSmart demo data\nParameter,Value\n" +
                                                parameters.joinToString("\n") {
                                                    "${it.name},${it.value}"
                                                }
                                        context.startActivity(
                                            Intent.createChooser(
                                                Intent(Intent.ACTION_SEND).apply {
                                                    type = "text/plain"
                                                    putExtra(Intent.EXTRA_TEXT, csv)
                                                    putExtra(
                                                        Intent.EXTRA_SUBJECT,
                                                        "AquaSmart demo readings",
                                                    )
                                                },
                                                "Export demo readings",
                                            )
                                        )
                                    },
                                )
                            else -> More(nav)
                        }
                    }
                }
                if (screen != "splash") BottomNavigation(screen, nav)
            }
        }
        if (dialog == "Edit Profile") {
            var draftName by rememberSaveable { mutableStateOf(name) }
            var draftEmail by rememberSaveable { mutableStateOf(email) }
            AlertDialog(
                onDismissRequest = { dialog = null },
                title = { Text("Edit Profile") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            draftName,
                            { draftName = it },
                            label = { Text("Name") },
                            singleLine = true,
                        )
                        OutlinedTextField(
                            draftEmail,
                            { draftEmail = it },
                            label = { Text("Email") },
                            singleLine = true,
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        enabled = draftName.isNotBlank() && draftEmail.contains("@"),
                        onClick = {
                            name = draftName.trim()
                            email = draftEmail.trim()
                            prefs.save("name", name)
                            prefs.save("email", email)
                            dialog = null
                        },
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = { TextButton(onClick = { dialog = null }) { Text("Cancel") } },
            )
        } else
            dialog?.let { title ->
                val message =
                    when (title) {
                        "Add Device" ->
                            "This prototype shows sample devices. Connect your ESP32/device service to enable discovery and pairing. No device has been added."
                        "Privacy Policy" ->
                            "Prototype information: your profile and preferences are stored on this device. This app does not upload readings or camera images. A production privacy policy has not been configured."
                        "Terms of Service" ->
                            "AquaSmart is a UI demonstration. Sample readings and forecasts must not be used to operate a real aquaponics system. Production terms have not been configured."
                        "Language" -> "English is currently available."
                        "Feedback" ->
                            "Feedback submission will be available when your support service is connected."
                        else ->
                            "Explore monitoring, feeding schedules, demo scans, and analytics using the bottom navigation. All readings are sample data; hardware and AI services are not connected."
                    }
                AlertDialog(
                    onDismissRequest = { dialog = null },
                    title = { Text(title) },
                    text = { Text(message) },
                    confirmButton = { TextButton(onClick = { dialog = null }) { Text("Got it") } },
                )
            }
    }
}

@Composable
private fun Label(
    text: String,
    modifier: Modifier = Modifier,
    size: Int = 14,
    bold: Boolean = false,
    color: Color = MaterialTheme.colorScheme.onSurface,
) {
    Text(
        text,
        modifier,
        color,
        fontSize = size.sp,
        fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
    )
}

@Composable
private fun AquaCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier.fillMaxWidth(),
        shape = Shape,
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, Teal.copy(alpha = .12f)),
    ) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            content = content,
        )
    }
}

@Composable
private fun Page(
    title: String,
    subtitle: String = "",
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(Modifier.fillMaxSize()) {
        Column(
            Modifier.fillMaxWidth()
                .background(Gradient)
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Label(title, size = 23, bold = true, color = Color.White)
            if (subtitle.isNotEmpty())
                Label(subtitle, size = 12, color = Color.White.copy(alpha = .75f))
        }
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            content = content,
        )
    }
}

@Composable
private fun DemoNote() {
    Label("DEMO MODE · Sample data · No devices connected", size = 10, color = Muted)
}

@Composable
private fun Action(text: String, onClick: () -> Unit, enabled: Boolean = true) {
    Button(
        onClick,
        Modifier.fillMaxWidth().heightIn(min = 52.dp),
        enabled = enabled,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Teal),
    ) {
        Text(text, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Composable
private fun Badge(text: String) {
    val c =
        when (text) {
            "Safe" -> Teal
            "Offline",
            "Critical" -> Color(0xFFEF4444)
            "Warning" -> Color(0xFFD97706)
            else -> Green
        }
    Surface(color = c.copy(alpha = .13f), shape = RoundedCornerShape(50)) {
        Label(text, Modifier.padding(horizontal = 8.dp, vertical = 4.dp), 10, true, c)
    }
}

@Composable
private fun Chips(items: List<String>, selected: String, onSelect: (String) -> Unit) {
    Row(
        Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items.forEach { item ->
            FilterChip(
                selected == item,
                { onSelect(item) },
                label = { Text(item, fontSize = 12.sp) },
                colors =
                    FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Teal,
                        selectedLabelColor = Color.White,
                    ),
            )
        }
    }
}

@Composable
private fun Sparkline(
    values: List<Float>,
    modifier: Modifier = Modifier,
    color: Color = Teal,
    grid: Boolean = false,
) {
    Canvas(modifier.semantics { contentDescription = "Trend: ${values.joinToString()}" }) {
        val low = values.minOrNull() ?: 0f
        val span = ((values.maxOrNull() ?: 1f) - low).coerceAtLeast(.01f)
        if (grid)
            (0..4).forEach { i ->
                drawLine(
                    Teal.copy(alpha = .12f),
                    Offset(0f, size.height * i / 4),
                    Offset(size.width, size.height * i / 4),
                    1f,
                )
            }
        val path = Path()
        values.forEachIndexed { i, v ->
            val x = size.width * i / (values.size - 1).coerceAtLeast(1)
            val y = size.height * (.9f - .8f * (v - low) / span)
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
            if (grid) drawCircle(color, 3.dp.toPx(), Offset(x, y))
        }
        drawPath(path, color, style = Stroke(2.dp.toPx()))
    }
}

@Composable
private fun BottomNavigation(screen: String, nav: (String) -> Unit) {
    val tabs =
        listOf(
            "home" to "Home",
            "monitoring" to "Monitor",
            "feeding" to "Feed",
            "analytics" to "Analytics",
            "more" to "More",
        )
    val active = if (tabs.any { it.first == screen }) screen else "more"
    Surface(shadowElevation = 4.dp) {
        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
            tabs.forEach { (id, label) ->
                val c = if (active == id) Teal else Muted
                Column(
                    Modifier.weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { nav(id) }
                        .padding(vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    NavIcon(id, c)
                    Label(label, size = 10, bold = active == id, color = c)
                }
            }
        }
    }
}

@Composable
private fun NavIcon(id: String, color: Color) {
    Canvas(Modifier.size(22.dp)) {
        val s = size.width / 24f
        fun line(x: Float, y: Float, x2: Float, y2: Float) =
            drawLine(color, Offset(x * s, y * s), Offset(x2 * s, y2 * s), 2 * s)
        when (id) {
            "home" -> {
                val p =
                    Path().apply {
                        moveTo(3 * s, 10 * s)
                        lineTo(12 * s, 3 * s)
                        lineTo(21 * s, 10 * s)
                        lineTo(21 * s, 21 * s)
                        lineTo(15 * s, 21 * s)
                        lineTo(15 * s, 13 * s)
                        lineTo(9 * s, 13 * s)
                        lineTo(9 * s, 21 * s)
                        lineTo(3 * s, 21 * s)
                        close()
                    }
                drawPath(p, color, style = Stroke(2 * s))
            }
            "monitoring" -> {
                val p =
                    Path().apply {
                        moveTo(1 * s, 12 * s)
                        lineTo(6 * s, 12 * s)
                        lineTo(9 * s, 3 * s)
                        lineTo(15 * s, 21 * s)
                        lineTo(18 * s, 12 * s)
                        lineTo(23 * s, 12 * s)
                    }
                drawPath(p, color, style = Stroke(2 * s))
            }
            "feeding" -> {
                drawCircle(color, 10 * s, style = Stroke(2 * s))
                line(12f, 7f, 12f, 17f)
                line(7f, 12f, 17f, 12f)
            }
            "analytics" -> {
                line(5f, 21f, 5f, 14f)
                line(12f, 21f, 12f, 3f)
                line(19f, 21f, 19f, 9f)
            }
            else ->
                listOf(5f, 12f, 19f).forEach { drawCircle(color, 1.7f * s, Offset(it * s, 12 * s)) }
        }
    }
}

@Composable
private fun Welcome(start: () -> Unit) {
    Column(
        Modifier.fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFECFEFF), Color(0xFFCFFAFE), Color(0xFFA5F3FC))
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(65.dp))
        Image(
            painterResource(R.drawable.aquasmart_logo),
            "AquaSmart fish, water and plant logo",
            Modifier.size(144.dp).clip(RoundedCornerShape(26.dp)),
        )
        Spacer(Modifier.height(22.dp))
        Row {
            Label("Aqua", size = 38, bold = true, color = Teal)
            Label("Smart", size = 38, bold = true, color = Green)
        }
        Text(
            "Smart Aquaponics for a Sustainable Tomorrow",
            color = DeepTeal,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(26.dp))
        listOf("IoT-Enabled Monitoring", "Automated Feeding", "Smarter Insights").forEach {
            Label("•  $it", Modifier.padding(vertical = 4.dp), 12, color = Teal)
        }
        Spacer(Modifier.height(70.dp))
        Action("Get Started", start)
        OutlinedButton(
            start,
            Modifier.fillMaxWidth().heightIn(min = 50.dp),
            shape = RoundedCornerShape(14.dp),
        ) {
            Text("Sign In", color = Teal, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(14.dp))
        Label("AquaSmart · Thesis Project 2026", size = 11, color = Muted)
        Label(
            "UI prototype · Explore with a demo account",
            Modifier.padding(top = 6.dp),
            10,
            color = Muted,
        )
    }
}

private fun Parameter.display(f: Boolean) = if (name == "Temperature" && f) "81.3°F" else value

@Composable
private fun Home(name: String, f: Boolean, nav: (String) -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Column(
            Modifier.fillMaxWidth().background(Gradient).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Label("Good Morning! 👋", size = 14, color = Color.White.copy(alpha = .7f))
                    Label(name, size = 23, bold = true, color = Color.White)
                }
                TextButton(onClick = { nav("profile") }) { Text("👤", fontSize = 24.sp) }
            }
            Row(
                Modifier.fillMaxWidth()
                    .clip(Shape)
                    .background(Color.White.copy(alpha = .15f))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Label("System Status", size = 11, color = Color.White.copy(alpha = .7f))
                    Label("All Systems Operational", size = 16, bold = true, color = Color.White)
                }
                Label("✅", size = 24)
            }
        }
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            DemoNote()
            Label("Real-Time Parameters", bold = true)
            parameters.take(4).chunked(2).forEach { pair ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    pair.forEach { p ->
                        AquaCard(Modifier.weight(1f).clickable { nav("monitoring") }) {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Label(p.icon, size = 22)
                                Badge(p.status)
                            }
                            Label(p.name, size = 11, color = Muted)
                            Label(p.display(f), size = 22, bold = true)
                            Sparkline(
                                p.trend,
                                Modifier.size(70.dp, 26.dp),
                                if (p.status == "Safe") Teal else Green,
                            )
                        }
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                parameters.drop(4).forEach { p ->
                    AquaCard(Modifier.weight(1f).clickable { nav("monitoring") }) {
                        Label(p.icon, size = 20)
                        Label(p.name, size = 10, color = Muted)
                        Label(p.value, size = 13, bold = true)
                        Badge(p.status)
                    }
                }
            }
            AquaCard {
                Label("Ecosystem Health", bold = true)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Health(
                        "🐟 Fish Health",
                        "Excellent",
                        "Est. 48 fish · Active",
                        Teal,
                        Modifier.weight(1f),
                    )
                    Health(
                        "🌱 Plant Health",
                        "Very Good",
                        "Nutrient level: High",
                        Green,
                        Modifier.weight(1f),
                    )
                }
            }
            AquaCard {
                Label("Quick Actions", bold = true)
                Row {
                    listOf(
                            Triple("🍽️", "Feed Now", "feeding"),
                            Triple("🔬", "Scan Kit", "scanner"),
                            Triple("🐟", "Detect Fish", "fish-detection"),
                            Triple("🔔", "Alerts", "alerts"),
                        )
                        .forEach { (icon, label, dest) ->
                            Column(
                                Modifier.weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { nav(dest) }
                                    .padding(vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Label(icon, size = 23)
                                Label(label, size = 10, color = Teal)
                            }
                        }
                }
            }
            Row(
                Modifier.fillMaxWidth().clip(Shape).background(Gradient).padding(18.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Label("Next Feeding", size = 12, color = Color.White.copy(alpha = .7f))
                    Label("06:00 PM", size = 24, bold = true, color = Color.White)
                    Label(
                        "Sample schedule · 45g portion",
                        size = 10,
                        color = Color.White.copy(alpha = .7f),
                    )
                }
                TextButton(onClick = { nav("feeding") }) { Text("Manage →", color = Color.White) }
            }
        }
    }
}

@Composable
private fun Health(title: String, value: String, sub: String, color: Color, modifier: Modifier) {
    Column(
        modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = .08f))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Label(title, size = 11, bold = true, color = color)
        Label(value, size = 18, bold = true)
        Label(sub, size = 10, color = Muted)
    }
}

@Composable
private fun Monitoring(f: Boolean) {
    var tab by rememberSaveable { mutableStateOf("Live Data") }
    Page("Water Quality", "Real-time IoT monitoring") {
        DemoNote()
        Chips(listOf("Live Data", "History"), tab, { tab = it })
        if (tab == "Live Data")
            parameters.forEach { p ->
                AquaCard {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Label("${p.icon}  ${p.name}", bold = true)
                        Badge(p.status)
                    }
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column {
                            Label(p.display(f), size = 27, bold = true)
                            Label(
                                "Range: ${if(f && p.name == "Temperature") "75.2–82.4°F" else p.range}",
                                size = 11,
                                color = Muted,
                            )
                        }
                        Sparkline(p.trend, Modifier.size(80.dp, 36.dp), Green)
                    }
                }
            }
        else {
            Label("Sample readings · Last 7 days", size = 12, color = Muted)
            parameters.forEach { p ->
                AquaCard {
                    Label(p.name, bold = true)
                    val values =
                        if (f && p.name == "Temperature") p.trend.map { it * 1.8f + 32 }
                        else p.trend
                    Sparkline(values, Modifier.fillMaxWidth().height(85.dp), grid = true)
                    Label(
                        "Mon   ·   Tue   ·   Wed   ·   Thu   ·   Fri   ·   Sat   ·   Sun",
                        size = 10,
                        color = Muted,
                    )
                    Label(
                        "Min %.2f  ·  Max %.2f".format(values.minOrNull(), values.maxOrNull()),
                        size = 12,
                        color = Teal,
                    )
                }
            }
        }
    }
}

@Composable
private fun Scanner(fish: Boolean) {
    var phase by rememberSaveable { mutableStateOf(0) }
    LaunchedEffect(phase) {
        if (phase == 1) {
            delay(1500)
            phase = 2
        }
    }
    Page(
        if (fish) "Fish Detection" else "Tester Kit Scanner",
        if (fish) "YOLOv8 AI Object Detection" else "AI-powered colorimetric analysis",
    ) {
        DemoNote()
        Box(
            Modifier.fillMaxWidth().height(220.dp).clip(Shape).background(Color(0xFF0C1A2E)),
            contentAlignment = Alignment.Center,
        ) {
            if (phase == 1)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    CircularProgressIndicator(color = Aqua)
                    Label("Analyzing demo sample…", color = Color.White)
                }
            else if (phase == 2 && fish) {
                Row(Modifier.padding(18.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf(94, 88, 91).forEachIndexed { i, confidence ->
                        Column(
                            Modifier.weight(1f)
                                .padding(top = (i * 20).dp)
                                .border(2.dp, Aqua, RoundedCornerShape(6.dp))
                                .padding(6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Label("Fish · $confidence%", size = 10, color = Aqua)
                            Label("🐟", Modifier.padding(vertical = 12.dp), size = 30)
                        }
                    }
                }
            } else if (phase == 2)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Label("✅", size = 40)
                    Label("Demo Scan Complete!", size = 20, bold = true, color = Aqua)
                }
            else
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Box(
                        Modifier.size(220.dp, 120.dp).border(2.dp, Aqua, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Label(
                            if (fish) "Point at fish tank" else "Scan Water Test Kit",
                            size = 13,
                            color = Color.White.copy(alpha = .7f),
                        )
                    }
                    Label(
                        "Demo preview · Camera is not connected",
                        size = 10,
                        color = Color.White.copy(alpha = .5f),
                    )
                }
        }
        if (phase == 2 && fish)
            AquaCard {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Label("Estimated Fish Count", size = 12, color = Muted)
                        Label("48", size = 34, bold = true, color = Teal)
                    }
                    Column {
                        Label("Avg. Confidence", size = 12, color = Muted)
                        Label("91%", size = 28, bold = true, color = Green)
                    }
                }
                Label(
                    "Sample estimate · Three example bounding boxes shown",
                    size = 11,
                    color = Muted,
                )
            }
        if (!fish && phase != 2)
            AquaCard {
                Label("Colorimetric Test Kit Reference", bold = true)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(
                            "pH" to Teal,
                            "EC / TDS" to Color(0xFFF59E0B),
                        )
                        .forEach { (label, c) ->
                            Column(
                                Modifier.weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Row {
                                    (1..4).forEach {
                                        Box(
                                            Modifier.weight(1f)
                                                .height(32.dp)
                                                .padding(1.dp)
                                                .background(c.copy(alpha = .25f + it * .18f))
                                        )
                                    }
                                }
                                Label(label, size = 10, color = Muted)
                            }
                        }
                }
            }
        Action(
            if (phase == 1) "Analyzing…"
            else if (phase == 2) "↻ Scan Again"
            else if (fish) "📷 Start Demo Detection" else "📷 Scan Demo Sample",
            { phase = if (phase == 2) 0 else 1 },
            phase != 1,
        )
        if (phase == 2 && !fish)
            AquaCard {
                Label("Scan Results vs. Sensor Readings", bold = true)
                listOf(
                        Triple("pH", "7.0–7.5", "7.2"),
                        Triple("EC / TDS", "400–1200 µS/cm", "650 µS/cm"),
                    )
                    .forEach { (p, kit, sensor) ->
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(Modifier.weight(1f)) {
                                Label(p, bold = true)
                                Label("Kit: $kit  ·  Sensor: $sensor", size = 11, color = Muted)
                            }
                            Badge("Normal")
                        }
                    }
            }
        AquaCard {
            Label(if (fish) "🤖 YOLOv8 Fish Detection" else "How to Use", bold = true)
            if (fish)
                Label(
                    "The design previews fish population estimates and detection confidence. Connect a trained YOLOv8 model and camera pipeline to analyze real fish.",
                    size = 12,
                    color = Muted,
                )
            else
                listOf(
                        "1. Follow the test kit manufacturer's preparation instructions.",
                        "2. Wait for the specified color development time.",
                        "3. Hold the strip under good lighting.",
                        "4. Align the strip inside the camera frame.",
                    )
                    .forEach { Label(it, size = 12, color = Muted) }
        }
    }
}

@Composable
private fun Analytics(f: Boolean) {
    var range by rememberSaveable { mutableStateOf("7D") }
    var selected by rememberSaveable { mutableStateOf("pH Level") }
    val p = parameters.first { it.name == selected }
    val values = if (f && selected == "Temperature") p.trend.map { it * 1.8f + 32 } else p.trend
    Page("Predictive Analytics", "Water Quality Forecast") {
        DemoNote()
        Chips(listOf("1D", "3D", "7D", "30D"), range, { range = it })
        Chips(parameters.map { it.name }, selected, { selected = it })
        AquaCard {
            Label("${p.name} Forecast", bold = true)
            Label("Sample $range trend · Illustrative forecast", size = 11, color = Muted)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(Modifier.height(170.dp), verticalArrangement = Arrangement.SpaceBetween) {
                    Label("%.2f".format(values.maxOrNull()), size = 10, color = Muted)
                    Label("%.2f".format(values.minOrNull()), size = 10, color = Muted)
                }
                Sparkline(values, Modifier.weight(1f).height(170.dp), grid = true)
            }
            val labels =
                when (range) {
                    "1D" -> listOf("00:00", "08:00", "16:00", "24:00")
                    "3D" -> listOf("Day 1", "Day 2", "Day 3")
                    "30D" -> listOf("Day 1", "Day 10", "Day 20", "Day 30")
                    else -> listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                labels.forEach { Label(it, size = 10, color = Muted) }
            }
        }
        AquaCard {
            Label("🤖 Insights", bold = true)
            if (selected == "EC / TDS") {
                Badge("Safe")
                Label("EC / TDS levels are optimal for nutrient uptake", bold = true)
                Label(
                    "Current conductivity of 650 µS/cm is ideal for fish and plant growth.",
                    size = 12,
                    color = Muted,
                )
            } else {
                Badge("Safe")
                Label(
                    "Water parameters are expected to remain within safe levels.",
                    size = 14,
                    bold = true,
                )
                Label(
                    "Maintain regular monitoring and your scheduled water quality checks.",
                    size = 12,
                    color = Muted,
                )
            }
            Label(
                "Illustrative guidance from the supplied design, not a live prediction.",
                size = 10,
                color = Muted,
            )
        }
    }
}

private data class DemoAlert(
    val category: String,
    val title: String,
    val message: String,
    val time: String,
    val status: String,
)

private val demoAlerts =
    listOf(
        DemoAlert(
            "Water Quality",
            "High pH Level",
            "pH reading of 7.9 detected — above safe range.",
            "Today 10:32 AM",
            "Warning",
        ),
        DemoAlert(
            "Water Quality",
            "Low Dissolved Oxygen",
            "DO dropped to 4.8 mg/L. Aeration recommended.",
            "Today 08:15 AM",
            "Critical",
        ),
        DemoAlert(
            "Water Quality",
            "EC / TDS Optimal",
            "Nutrient concentration stable at 650 µS/cm.",
            "Yesterday 11:40 PM",
            "Normal",
        ),
        DemoAlert(
            "Feeding",
            "Feeding Completed",
            "Automated feeding of 45g completed successfully.",
            "Today 06:00 AM",
            "Normal",
        ),
        DemoAlert(
            "Feeding",
            "Manual Feed Logged",
            "Manual feed of 30g recorded.",
            "Today 03:02 PM",
            "Safe",
        ),
        DemoAlert(
            "Water Quality",
            "Water Quality Normal",
            "All parameters within acceptable ranges.",
            "Yesterday 06:00 AM",
            "Normal",
        ),
        DemoAlert(
            "System",
            "IoT Sensor Online",
            "Water quality sensor reconnected to Wi-Fi.",
            "Yesterday 09:12 AM",
            "Safe",
        ),
    )

@Composable
private fun Alerts() {
    var tab by rememberSaveable { mutableStateOf("All") }
    Page("Alerts", "7 sample notifications") {
        DemoNote()
        Chips(listOf("All", "Water Quality", "Feeding", "System"), tab, { tab = it })
        demoAlerts
            .filter { tab == "All" || it.category == tab }
            .forEach { a ->
                AquaCard {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Label(a.category, size = 10, color = Muted)
                        Badge(a.status)
                    }
                    Label(a.title, bold = true)
                    Label(a.message, size = 12, color = Muted)
                    Label(a.time, size = 10, color = Muted)
                }
            }
    }
}

@Composable
private fun Devices(add: () -> Unit) {
    Page("Devices", "5 of 6 online in demo") {
        DemoNote()
        Action("+ Add Device", add)
        listOf(
                Triple("🔬", "Water Quality Sensors", "Sensor Array"),
                Triple("🍽️", "Automated Feeder", "Actuator"),
                Triple("💧", "Water Pump", "Actuator"),
                Triple("🌬️", "Aerator", "Actuator"),
                Triple("📷", "Camera", "Imaging"),
                Triple("🖥️", "ESP32 Controller", "Microcontroller"),
            )
            .forEach { (icon, name, type) ->
                AquaCard {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Label(icon, size = 28)
                        Column(Modifier.weight(1f)) {
                            Label(name, bold = true)
                            Label(type, size = 10, color = Muted)
                            Label(
                                if (name == "Camera") "Not connected" else "Wi-Fi · AquaSmart-IoT",
                                size = 10,
                                color = Muted,
                            )
                        }
                        Badge(if (name == "Camera") "Offline" else "Online")
                    }
                }
            }
    }
}

@Composable
private fun ToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onChange: (Boolean) -> Unit,
) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f).padding(end = 8.dp)) {
            Label(title, bold = true)
            Label(subtitle, size = 11, color = Muted)
        }
        Switch(checked, onChange)
    }
}

@Composable
private fun MenuRow(title: String, value: String = "›", onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Label(title, Modifier.weight(1f), bold = true)
        Label(value, size = 12, color = Muted)
    }
}

@Composable
private fun Settings(
    prefs: AppPreferences,
    dark: Boolean,
    setDark: (Boolean) -> Unit,
    f: Boolean,
    setF: (Boolean) -> Unit,
    info: (String) -> Unit,
) {
    Page("Settings") {
        AquaCard {
            Label("NOTIFICATIONS", size = 11, color = Muted, bold = true)
            listOf(
                    Triple("waterAlerts", "Water Quality Alerts", "pH, EC/TDS, dissolved oxygen"),
                    Triple("feedAlerts", "Feeding Notifications", "Scheduled and manual feeds"),
                    Triple("systemAlerts", "System Alerts", "Device status, connectivity"),
                )
                .forEach { (key, title, sub) ->
                    var checked by rememberSaveable {
                        mutableStateOf(prefs.bool(key, key != "systemAlerts"))
                    }
                    ToggleRow(
                        title,
                        sub,
                        checked,
                        {
                            checked = it
                            prefs.save(key, it)
                        },
                    )
                }
            Label(
                "Notification preferences are saved locally. Push delivery is not connected.",
                size = 10,
                color = Muted,
            )
        }
        AquaCard {
            Label("PREFERENCES", size = 11, color = Muted, bold = true)
            ToggleRow("Dark Mode", "Switch to dark theme", dark, setDark)
            ToggleRow("Fahrenheit", "Temperature unit: ${if(f) "°F" else "°C"}", f, setF)
            Label("Water Quality Units: mg/L · ppm · NTU", size = 12, color = Muted)
            MenuRow("Language", "English") { info("Language") }
        }
        AquaCard {
            Label("ABOUT", size = 11, color = Muted, bold = true)
            Label("App Version · 1.0 UI prototype", size = 13)
            MenuRow("Terms of Service") { info("Terms of Service") }
            MenuRow("Privacy Policy") { info("Privacy Policy") }
        }
    }
}

@Composable
private fun Profile(
    name: String,
    email: String,
    nav: (String) -> Unit,
    info: (String) -> Unit,
    export: () -> Unit,
) {
    Page("Profile", "Account & system information") {
        AquaCard {
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Label("👨‍🌾", size = 50)
                Label(name, size = 23, bold = true)
                Label(email, size = 13, color = Muted)
                Badge("Aquaponics Researcher")
            }
        }
        AquaCard {
            Label("MY SYSTEM", size = 11, color = Muted, bold = true)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                listOf("48" to "Fish Count", "500 L" to "Tank Size", "8 mo." to "System Age")
                    .forEach { (v, t) ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Label(v, size = 22, bold = true, color = Teal)
                            Label(t, size = 10, color = Muted)
                        }
                    }
            }
        }
        DemoNote()
        AquaCard {
            MenuRow("👤  Edit Profile") { info("Edit Profile") }
            MenuRow("⚙️  Account Settings") { nav("settings") }
            MenuRow("📊  Data Export", onClick = export)
            MenuRow("❓  Help & Support") { info("Help & Support") }
            MenuRow("💬  Feedback") { info("Feedback") }
        }
        OutlinedButton(
            onClick = { nav("splash") },
            modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
            shape = Shape,
        ) {
            Text("Sign Out", color = Color(0xFFEF4444))
        }
        Label("AquaSmart · Thesis Project 2026", Modifier.fillMaxWidth(), 11, color = Muted)
    }
}

@Composable
private fun More(nav: (String) -> Unit) {
    val items =
        listOf(
            Triple("🔬", "Tester Kit Scanner", "scanner"),
            Triple("🐟", "Fish Detection", "fish-detection"),
            Triple("🔔", "Alerts & Notifications", "alerts"),
            Triple("📡", "Devices", "devices"),
            Triple("⚙️", "Settings", "settings"),
            Triple("👤", "Profile", "profile"),
        )
    val subtitles =
        listOf(
            "AI colorimetric scan",
            "YOLOv8 fish count",
            "View all system alerts",
            "Connected IoT devices",
            "App preferences",
            "Account & system info",
        )
    Page("More", "Additional features") {
        items.chunked(2).forEachIndexed { row, pair ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                pair.forEachIndexed { col, (icon, label, dest) ->
                    AquaCard(Modifier.weight(1f).clickable { nav(dest) }) {
                        Label(icon, size = 32)
                        Label(label, bold = true)
                        Label(subtitles[row * 2 + col], size = 11, color = Muted)
                    }
                }
            }
        }
        DemoNote()
    }
}

@Preview(showBackground = true, device = "spec:width=412dp,height=915dp,dpi=420")
@Composable
private fun AquaSmartPreview() {
    AquaSmartApp()
}
