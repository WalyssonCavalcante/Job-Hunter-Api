package com.jobhunter.infrastructure.utils;

import com.microsoft.playwright.BrowserContext;

import java.util.Arrays;
import java.util.List;

/**
 * Utilitário para camuflar o Playwright e evitar detecção de bots.
 * Porta manual das estratégias do 'puppeteer-extra-plugin-stealth'.
 */
public class PlaywrightStealth {

    /**
     * Injeta scripts de evasão no contexto do navegador.
     */
    public static void inject(BrowserContext context) {

        // 1. Remove a flag 'navigator.webdriver' (A mais óbvia)
        context.addInitScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");

        // 2. Mock do objeto 'chrome' (O Chrome real tem isso, o Headless não tem por padrão)
        context.addInitScript("""
            window.chrome = {
                runtime: {},
                // Outras propriedades básicas para enganar verificações simples
                app: {
                    isInstalled: false,
                    InstallState: {
                        DISABLED: 'disabled',
                        INSTALLED: 'installed',
                        NOT_INSTALLED: 'not_installed'
                    },
                    RunningState: {
                        CANNOT_RUN: 'cannot_run',
                        READY_TO_RUN: 'ready_to_run',
                        RUNNING: 'running'
                    }
                }
            };
        """);

        // 3. Mock de Permissões (Notification API)
        // Bots geralmente falham aqui se não tiverem implementação
        context.addInitScript("""
            const originalQuery = window.navigator.permissions.query;
            window.navigator.permissions.query = (parameters) => (
                parameters.name === 'notifications' ?
                Promise.resolve({ state: Notification.permission }) :
                originalQuery(parameters)
            );
        """);

        // 4. Mock de Plugins (Muitos sites checam tamanho do array de plugins)
        context.addInitScript("""
            Object.defineProperty(navigator, 'plugins', {
                get: () => [1, 2, 3, 4, 5] // Array falso apenas para ter tamanho > 0
            });
            Object.defineProperty(navigator, 'languages', {
                get: () => ['en-US', 'en']
            });
        """);

        // 5. WebGL Vendor Spoofing (Esconde que é renderizado por software/headless)
        context.addInitScript("""
            const getParameter = WebGLRenderingContext.prototype.getParameter;
            WebGLRenderingContext.prototype.getParameter = function(parameter) {
                // 37445 = UNMASKED_VENDOR_WEBGL
                if (parameter === 37445) {
                    return 'Intel Inc.';
                }
                // 37446 = UNMASKED_RENDERER_WEBGL
                if (parameter === 37446) {
                    return 'Intel Iris OpenGL Engine';
                }
                return getParameter(parameter);
            };
        """);
    }
}