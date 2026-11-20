package org.serenityos.ladybirdwrapper

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Tab
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import java.net.URI

data class BrowserTab(val id: Int, val title: String, val url: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = lightColorScheme()) {
                BrowserShell()
            }
        }
    }
}

/**
 * The whole browser shell: a vertical tab rail on the left, an expressive
 * arc-shaped address bar on top, and the web content below.
 */
@Composable
fun BrowserShell() {
    val tabs = remember { mutableStateListOf(BrowserTab(1, "Ladybird", "https://ladybird.org")) }
    var selectedId by remember { mutableIntStateOf(1) }
    var nextId by remember { mutableIntStateOf(2) }
    var address by remember { mutableStateOf("https://ladybird.org") }
    val webView = remember { mutableStateOf<WebView?>(null) }

    BackHandler {
        val view = webView.value
        if (view != null && view.canGoBack()) view.goBack()
    }

    Row(modifier = Modifier.fillMaxSize()) {
        VerticalTabRail(
            tabs = tabs,
            selectedId = selectedId,
            onSelect = { id ->
                selectedId = id
                tabs.firstOrNull { it.id == id }?.let { address = it.url }
            },
            onAdd = {
                val tab = BrowserTab(nextId, "New tab", "https://ladybird.org")
                tabs.add(tab)
                selectedId = tab.id
                nextId += 1
                address = tab.url
            },
        )

        Column(modifier = Modifier.weight(1f)) {
            ArcAddressBar(
                address = address,
                onAddressChange = { address = it },
                onGo = {
                    val url = normalizeUrl(address)
                    val index = tabs.indexOfFirst { it.id == selectedId }
                    if (index >= 0) tabs[index] = tabs[index].copy(url = url, title = hostOf(url))
                    address = url
                },
            )

            val currentUrl = tabs.firstOrNull { it.id == selectedId }?.url ?: "about:blank"
            WebViewPane(
                url = currentUrl,
                onWebViewReady = { webView.value = it },
                onTitle = { title ->
                    val index = tabs.indexOfFirst { it.id == selectedId }
                    if (index >= 0 && title.isNotBlank()) tabs[index] = tabs[index].copy(title = title)
                },
            )
        }
    }
}

/** Vertical, expressive tab strip. Each entry is an open tab, with + for a new one. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VerticalTabRail(
    tabs: List<BrowserTab>,
    selectedId: Int,
    onSelect: (Int) -> Unit,
    onAdd: () -> Unit,
) {
    NavigationRail(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        modifier = Modifier.fillMaxHeight(),
    ) {
        tabs.forEach { tab ->
            NavigationRailItem(
                selected = tab.id == selectedId,
                onClick = { onSelect(tab.id) },
                icon = { Icon(Icons.Filled.Tab, contentDescription = null) },
                label = {
                    Text(
                        text = tab.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                alwaysShowLabel = true,
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        FloatingActionButton(
            onClick = onAdd,
            modifier = Modifier.padding(bottom = 16.dp),
            shape = RoundedCornerShape(50),
        ) {
            Icon(Icons.Filled.Add, contentDescription = "New tab")
        }
    }
}

/** Arc-style top bar: a curved primary surface with the URL field inside. */
@Composable
fun ArcAddressBar(
    address: String,
    onAddressChange: (String) -> Unit,
    onGo: () -> Unit,
) {
    Surface(
        shape = ArcShape(),
        color = MaterialTheme.colorScheme.primaryContainer,
        shadowElevation = 10.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
    ) {
        Row(
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 14.dp),
            verticalAlignment = Alignment.Top,
        ) {
            OutlinedTextField(
                value = address,
                onValueChange = onAddressChange,
                singleLine = true,
                label = { Text("Address") },
                shape = RoundedCornerShape(28.dp),
                colors = OutlinedTextFieldDefaults.colors(),
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 54.dp),
            )
            Spacer(modifier = Modifier.width(10.dp))
            FloatingActionButton(
                onClick = onGo,
                shape = RoundedCornerShape(50),
                containerColor = MaterialTheme.colorScheme.primary,
            ) {
                Icon(Icons.Filled.ArrowForward, contentDescription = "Go")
            }
        }
    }
}

/** A soft arc: flat on top, sweeping down into a curve at the bottom. */
private class ArcShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height * 0.60f)
            cubicTo(
                size.width * 0.78f, size.height * 1.20f,
                size.width * 0.22f, size.height * 1.20f,
                0f, size.height * 0.60f,
            )
            close()
        }
        return Outline.Generic(path)
    }
}

/** System WebView renderer (stand-in until Ladybird's engine library builds for Android). */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun WebViewPane(
    url: String,
    onWebViewReady: (WebView) -> Unit,
    onTitle: (String) -> Unit,
) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, loadedUrl: String?) {
                        view?.title?.let(onTitle)
                    }
                }
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.loadWithOverviewMode = true
                onWebViewReady(this)
                loadUrl(url)
            }
        },
        update = { view ->
            if (view.url != url) view.loadUrl(url)
        },
        modifier = Modifier.fillMaxSize(),
    )
}

private fun normalizeUrl(input: String): String {
    val trimmed = input.trim()
    return if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) trimmed else "https://$trimmed"
}

private fun hostOf(url: String): String =
    runCatching { URI(url).host ?: url }.getOrDefault(url)
