package com.Revolut.dependency;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class InjectorProvider {
    private static Injector injector;

    static {
        injector = Guice.createInjector(new AppInjector());
    }

    public static Injector provide() {
        return injector;
    }
}