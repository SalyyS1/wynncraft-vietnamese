package net.wynncraft.vi.translation.provider;

import java.util.concurrent.CompletableFuture;

public interface ITranslationProvider {
    String getName();
    CompletableFuture<String> translate(String text, String sourceLang, String targetLang);
}