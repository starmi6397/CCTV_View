package com.cctv_view.ui.components

import android.annotation.SuppressLint
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.cctv_view.data.Channel

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun TVWebView(
    channel: Channel?,
    onPageFinished: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val webView = remember {
        WebView(context).apply {
            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                loadsImagesAutomatically = false
                blockNetworkImage = true
                mediaPlaybackRequiresUserGesture = false
                userAgentString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36"

                // 缓存设置 - 使用新版 API
                cacheMode = android.webkit.WebSettings.LOAD_DEFAULT

                javaScriptCanOpenWindowsAutomatically = true
                setSupportZoom(false)
                builtInZoomControls = false
                displayZoomControls = false
                useWideViewPort = true
                loadWithOverviewMode = true
            }

            // 混合内容模式（Android 5.0+）
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                settings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            }

            webViewClient = object : WebViewClient() {
                override fun onReceivedError(
                    view: WebView?,
                    errorCode: Int,
                    description: String?,
                    failingUrl: String?
                ) {
                    super.onReceivedError(view, errorCode, description, failingUrl)
                    // ERR_CACHE_MISS 错误时重试
                    if (errorCode == -1) {
                        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                            failingUrl?.let { view?.loadUrl(it) }
                        }, 500)
                    }
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    if (url == "about:blank") return

                    // 获取节目信息的 JavaScript
                    val jsCode = """
                        (function() {
                            var now = document.querySelector('#jiemu > li.cur.act');
                            var next = document.querySelector('#jiemu > li:nth-child(4)');
                            var nowText = now ? now.innerText : '';
                            var nextText = next ? next.innerText : '';
                            return nowText + (nextText ? '\n' + nextText : '');
                        })();
                    """

                    view?.evaluateJavascript(jsCode) { result ->
                        val info = result.replace("\"", "")
                        onPageFinished(info)
                    }

                    // 自动全屏
                    val fullscreenJs = """
                        (function() {
                            function autoFullscreen() {
                                var btn = document.querySelector('#player_pagefullscreen_yes_player') || 
                                          document.querySelector('.videoFull');
                                if (btn) {
                                    btn.click();
                                    var video = document.querySelector('video');
                                    if (video) video.volume = 1;
                                } else {
                                    setTimeout(autoFullscreen, 500);
                                }
                            }
                            autoFullscreen();
                        })();
                    """.trimIndent()

                    view?.evaluateJavascript(fullscreenJs, null)
                }
            }

            webChromeClient = WebChromeClient()
            isFocusable = false
        }
    }

    LaunchedEffect(channel) {
        channel?.let {
            webView.loadUrl(it.url)
        }
    }

    AndroidView(
        factory = { webView },
        update = { },
        modifier = modifier
    )
}