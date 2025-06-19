# CozySpot

## 1. Versão do SDK Utilizado

- **compileSdk:** 35
- **minSdk:** 24
- **targetSdk:** 35
- **Java:** 11

## 2. Versão do Gradle

- **Plugin do Android:** 8.8.2
- **Gradle Wrapper:** 8.10.2  
  (`distributionUrl=https://services.gradle.org/distributions/gradle-8.10.2-bin.zip`)

## 3. Bibliotecas Utilizadas

| Biblioteca                                          | Versão   | Licença        | Link                                                                    |
|-----------------------------------------------------|----------|----------------|-------------------------------------------------------------------------|
| androidx.room:room-runtime                          | 2.6.1    | Apache-2.0     | https://developer.android.com/jetpack/androidx/releases/room            |
| androidx.room:room-compiler                         | 2.6.1    | Apache-2.0     | https://developer.android.com/jetpack/androidx/releases/room            |
| com.github.bumptech.glide:glide                     | 4.16.0   | BSD-2-Clause   | https://github.com/bumptech/glide                                       |
| com.github.bumptech.glide:compiler                  | 4.16.0   | BSD-2-Clause   | https://github.com/bumptech/glide                                       |
| org.maplibre.gl:android-sdk                         | 11.5.1   | BSD-2-Clause   | https://github.com/maplibre/maplibre-gl-native                          |
| org.maplibre.gl:android-plugin-annotation-v9        | 3.0.2    | BSD-2-Clause   | https://github.com/maplibre/maplibre-plugins-android                    |
| androidx.room:room-common-jvm                       | 2.7.1    | Apache-2.0     | https://developer.android.com/jetpack/androidx/releases/room            |
| com.google.android.gms:play-services-appsearch       | 16.0.1   | Apache-2.0     | https://developers.google.com/android/guides/setup                      |
| androidx.appcompat:appcompat                        | 1.7.0    | Apache-2.0     | https://developer.android.com/jetpack/androidx/releases/appcompat       |
| com.google.android.material:material                | 1.12.0   | Apache-2.0     | https://github.com/material-components/material-components-android      |
| androidx.activity:activity                          | 1.10.1   | Apache-2.0     | https://developer.android.com/jetpack/androidx/releases/activity        |
| androidx.constraintlayout:constraintlayout          | 2.2.1    | Apache-2.0     | https://developer.android.com/jetpack/androidx/releases/constraintlayout|
| junit:junit                                         | 4.13.2   | EPL-1.0        | https://junit.org                                                       |
| androidx.test.ext:junit                             | 1.2.1    | Apache-2.0     | https://developer.android.com/jetpack/androidx/releases/test            |
| androidx.test.espresso:espresso-core                | 3.6.1    | Apache-2.0     | https://developer.android.com/jetpack/androidx/releases/espresso        |

## 4. Processo de Importação

1. **Clone** o repositório:
   ```bash
   git clone <url-do-repositorio>
   ```
2. **Abra** o projeto no Android Studio (versão compatível com Java 11 e Gradle 8.10.2).
3. O Android Studio irá sincronizar as dependências automaticamente via Gradle.
4. **Compile** usando o botão "Build" ou via terminal:
   ```bash
   ./gradlew build
   ```
5. **Run/Debug:** Execute no emulador ou dispositivo físico.

> Não há etapas suplementares ao processo padrão de importação.

## 5. Observações

- Certifique-se de usar o Java 11 para compatibilidade.
- Consulte as licenças das bibliotecas externas nos links acima.
- Para dúvidas sobre versões de dependências, consulte `gradle/libs.versions.toml`.

---
