# 📱 LinguaPulse - Memofichas SRS, Notificaciones Inteligentes y Guía B2B Tech

> **Versión actual: 1.1.0-beta** · Cada push a `main` publica automáticamente una versión nueva que la app detecta e instala sola.

**LinguaPulse** es una aplicación nativa para Android desarrollada en **Kotlin** con **Jetpack Compose (Material 3)**. Su propósito es acelerar el aprendizaje y retención permanente de vocabulario, conectores, phrasal verbs y expresiones clave en inglés mediante el algoritmo de **Repetición Espaciada (SRS / SM-2)**, complementado con un sistema de **notificaciones periódicas interactivas**, soporte dinámico de **tema claro y oscuro**, y una **Guía Rápida Interactiva de Conversación y Fórmulas** basada en reglas gramaticales reales y técnicas de ventas consultivas de tecnología (Cloud, Datos e Inteligencia Artificial).

---

## ✨ Características Principales

### 1. 🎴 Memofichas Interactivas con Giro 3D y Audio (TTS)
- **Animación Fluida**: Giro 3D de 180° al tocar la tarjeta.
- **Pronunciación Nativa**: Botón de audio con síntesis de voz en inglés americano (*Text-to-Speech*).
- **Contenido Bilingüe Completo**:
  - Explicación del matiz y cuándo usarlo en **inglés**.
  - Explicación detallada en **español**.
  - Ejemplo en contexto real en inglés con su respectiva traducción.

### 2. 🧠 Motor de Repetición Espaciada (SRS / SuperMemo-2)
- Clasificación de retención en 3 niveles al voltear la tarjeta:
  - **Aún no / Repetir**: Reinicia el intervalo a 1 día y reduce el factor de facilidad.
  - **Bien / La sé**: Incrementa el intervalo progresivamente ($I_1 = 1\text{d}, I_2 = 4\text{d}, I_n = I_{n-1} \times \text{EF}$).
  - **Fácil / Dominada**: Otorga un multiplicador adicional y eleva el factor de facilidad.
- Cola diaria inteligente: Muestra únicamente las tarjetas que toca repasar hoy.

### 3. 🔔 Notificaciones Periódicas Directas & Deep Linking
- **Formato directo**: Título y cuerpo nítido: **`Palabra: Significado`** (ej. `"That being said: Dicho esto / No obstante"`).
- **Interacción inmediata**: Al pulsar la notificación, se abre un diálogo modal para marcar si **"Ya me la aprendí"** o **"Aún no me la sé"** sin necesidad de navegar manualmente.
- Frecuencia configurable: **cada 30 minutos, cada hora (recomendado), cada 2h, 4h, 8h o 1 vez al día**, con botón para disparar una **notificación de prueba inmediata**.
- La frecuencia elegida se **guarda en disco**, así que sobrevive al cierre de la app y al reinicio del teléfono.
- El mínimo real es de 15 minutos: es el periodo más corto que permite `WorkManager` en Android.
- Compatible con permisos de Android 13+ (`POST_NOTIFICATIONS`).

### 4. 🌗 Tema Claro y Oscuro Dinámico
- Respeta automáticamente el tema del sistema operativo del teléfono (`isSystemInDarkTheme()`).
- **Tema Claro**: Tonos neutros suaves (`#F8FAFC`), texto nítido, sin blancos deslumbrantes.
- **Tema Oscuro**: Fondos Slate/OLED (`#0F172A`), superficies cómodas que previenen el cansancio visual nocturno.

### 5. 📖 Guía Interactiva de Conversación & Fórmulas (Incluye PDF + Tips B2B)
Pestaña dedicada con acordeones interactivos y pronunciación:
1. **Tiempos Verbales en Inglés**:
   - Fórmulas de Presente (PS, PC, PP, PPC), Pasado y Futuro.
   - Tabla comparativa de traducción mental: "Que pasan", "Que están pasando", "Que habían pasado", etc.
2. **Verbos Modales y Auxiliares**:
   - Tabla completa con Can, Could, May, Might, Must, Shall, Should, Will, Would, Ought to, Need to, Have to, Used to, Be able to.
   - Auxiliares Be, Have y el tip del **"Do" enfático** (*"I DO recommend this architecture"*).
   - Tabla de cómo **NO mezclar modales** (*will can* ➔ *will be able to*, etc.).
3. **Much vs. Many**: Incontables vs Contables y el truco de *A lot of*.
4. **Preposiciones (IN - ON - AT)**: Pirámide de general a hiper-específico.
5. **Regla de Oro "To" + Verbo vs Verbo + "-ing"**:
   - Decisión / Deseo / Promesa ➔ `to + verbo`.
   - Emoción / Preferencia / Actividad ➔ `verbo + ing`.
   - Preposición antes del verbo ➔ siempre `verbo + ing`.
6. **Ventas B2B Tech & Tips de Conversación**:
   - **Estructura TED** para descubrimiento (*Tell me...*, *Explain to me...*, *Describe...*).
   - **Corporate Softeners**: Suavizadores para sonar diplomático y persuasivo.
   - **Falsos Amigos** comunes en negocios (*Actually*, *Compromise*, *Realize*, *Eventually*, *Resume*).
   - **Método Feel - Felt - Found** para resolver objeciones de seguridad, CISO y precios.

### 6. 🗂️ Dos Grandes Dominios de Vocabulario
- **Día a Día (Personal & Daily Life)**: Conectores cotidianos, phrasal verbs indispensables y oraciones comunes.
- **Ventas B2B de Tecnología (Cloud, Data & AI)**: Transiciones ejecutivas, términos de arquitectura (scale out, spin up, drill down), preguntas consultivas y manejo de objeciones.
- **Creador de Tarjetas Personalizadas**: Botón (+) para añadir tus propias frases cuando quieras.

---

## 🔄 Actualizaciones automáticas

LinguaPulse se actualiza sola, sin tiendas de aplicaciones:

| Paso | Qué ocurre |
| --- | --- |
| 1 | Haces `push` a `main`. |
| 2 | GitHub Actions compila el APK de release y publica un Release con `update.json`. |
| 3 | La app consulta `releases/latest/download/update.json` al abrirse (y cuando pulsas *Buscar actualizaciones*). |
| 4 | Si el `versionCode` remoto es mayor, muestra el aviso, descarga el APK y lanza el instalador del sistema. |

El `versionCode` se deriva del número de ejecución del workflow (`1000 + GITHUB_RUN_NUMBER`), así que **siempre crece** con cada cambio publicado.

La primera vez, Android pedirá el permiso de *"Instalar apps desconocidas"*; la app abre esa pantalla de ajustes automáticamente.

---

## ⚡ Rendimiento

Trabajo de optimización aplicado sobre la versión inicial:

- **Build de release con R8 y shrink de recursos** en lugar del APK debug. Un build debug lleva instrumentación de depuración y no optimiza nada: era la causa principal de la sensación de 30 fps.
- **Baseline Profile de Compose** (`profileinstaller`), que elimina el coste de compilación JIT en el primer arranque.
- **Animación de giro sin recomposición por frame**: el valor animado ya no se lee en la composición, solo dentro del bloque `graphicsLayer` (fase de dibujo), y la cara visible se calcula con `derivedStateOf`.
- **Arranque sin trabajo en el hilo principal**: la creación del canal de notificaciones y la apertura de Room se movieron a una corrutina de IO.
- Listas constantes elevadas a nivel de fichero para no reasignarlas en cada recomposición.

---

## 🏗️ Arquitectura Técnica

- **Lenguaje**: Kotlin 1.9.23
- **UI Toolkit**: Jetpack Compose (BOM 2024.04.01) + Material 3
- **Base de Datos**: Room Persistence Library (SQLite) 2.6.1 con KSP
- **Asincronía & Flujos**: Coroutines + Kotlin Flow + StateFlow
- **Tareas en Segundo Plano**: Android WorkManager 2.9.0
- **Audio**: Android TextToSpeech (TTS) nativo
- **CI/CD**: GitHub Actions para generación automática de APK

---

## 🚀 Cómo Generar el APK

### Opción 1: Automático mediante GitHub Actions (recomendado)
1. Haz `git push` de tus cambios a `main`.
2. El workflow `Build LinguaPulse APK` compila un **APK de release optimizado** (R8 + shrink de recursos) y publica un **GitHub Release** con:
   - `LinguaPulse-<versión>.apk` — el instalador firmado.
   - `update.json` — el manifiesto que la app consulta para detectar la actualización.
3. Desde el teléfono no tienes que hacer nada: al abrir LinguaPulse verás el aviso *"Nueva versión disponible"*. También puedes forzarlo en **Ajustes → Actualizaciones → Buscar actualizaciones**.

> La primera ejecución del workflow genera y versiona `signing/linguapulse-beta.jks`. Ese keystore mantiene la **misma firma** en todas las builds, que es lo que permite instalar una actualización encima de la anterior sin desinstalar. Al ser un keystore de beta en un repositorio público, no debe reutilizarse para una publicación en Google Play.

### Opción 2: Compilación Local en Android Studio
1. Abre la carpeta `LinguaPulse` en **Android Studio Hedgehog / Iguana / Jellyfish**.
2. Deja que Gradle sincronice las dependencias automáticamente.
3. Conecta tu teléfono Android (o usa un emulador) y pulsa **Run (Shift + F10)**.
4. O genera el APK desde el menú: **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
