package ru.myocr.di;

import javax.inject.Singleton;

import dagger.Component;
import ru.myocr.App;
import ru.myocr.activity.ShopAddingActivity;

@Singleton
@Component(modules = {MainModule.class})
public interface DaggerGraphComponent {
    void inject(ShopAddingActivity activity);

    final class Initializer {
        private Initializer() {
        }

        public static DaggerGraphComponent init(App app) {
            return DaggerDaggerGraphComponent.builder()
                    .mainModule(new MainModule(app))
                    .build();
        }
    }
}
