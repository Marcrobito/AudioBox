# Audio Box – Secure Audio PTT Challenge

Este proyecto es una aplicación Android mínima construida como un ejercicio práctico para validar el manejo de audio nativo, con énfasis en corrección, separación de responsabilidades y claridad de diseño por encima de la complejidad visual.

El objetivo es capturar, transformar, almacenar y reproducir audio usando únicamente APIs nativas de Android.

---

## Alcance y restricciones

- Uso exclusivo de APIs nativas de Android
    - `AudioRecord` para captura
    - `AudioTrack` para reproducción
- No se utiliza `MediaRecorder`
- No se utilizan librerías de terceros para audio
- Configuración de audio:
    - Sample Rate: 48,000 Hz
    - Canal: Mono
    - Encoding: PCM 16-bit
- Transformación simple en tiempo real (XOR) aplicada durante la grabación
- UI mínima (Jetpack Compose)
- Enfoque en arquitectura y comportamiento correcto

---

## Arquitectura general

El sistema de audio está dividido de forma intencional en componentes pequeños y bien definidos:

AudioRecorder  →  AudioEncodeTransformer  →  AudioStorageWriter
↘
Archivo (.pcm)
↘
AudioPlayer   ←  AudioDecodeTransformer  ←  Lectura de archivo

Cada componente tiene una sola responsabilidad y se comunica a través de interfaces.

---

## Uso de interfaces

Aunque el proyecto es pequeño, las interfaces se utilizan de manera deliberada para:

- Aislar responsabilidades
- Evitar acoplamiento fuerte entre componentes
- Facilitar pruebas unitarias futuras
- Permitir cambios futuros sin reescribir todo el flujo

Ejemplos:
- El proceso de codificación y decodificación usa actualmente la misma lógica (XOR), pero se modelan como responsabilidades distintas.
- El almacenamiento está abstraído para permitir cambios futuros (por ejemplo, cifrado real, base de datos o streaming).

Esto es una decisión consciente de diseño, no sobre-ingeniería.

---

## Captura de audio

**Componente:** `AudioRecordRecorder`

- Usa `AudioRecord` con la configuración requerida
- Lee buffers PCM en un hilo en segundo plano
- Aplica la transformación durante la captura
- Escribe directamente los bytes transformados en almacenamiento
- No gestiona nombres de archivo ni catálogo

El objetivo es mantener la lógica de captura enfocada exclusivamente en la entrada de audio.

---

## Transformación de audio

**Componente:** `XorAudioTransformer`

- Implementa `AudioEncodeTransformer` y `AudioDecodeTransformer`
- Usa una clave XOR fija (por ejemplo `0x5A`)
- La transformación es simétrica

Aunque la implementación es la misma, se separan las interfaces para dejar claro el propósito de cada proceso y permitir futuras diferencias entre codificación y decodificación.

---

## Almacenamiento y catálogo

**Componentes:**
- `AudioStorageWriter`
- `RecordingWriterManager`
- `RecordingCatalog`

Responsabilidades:
- El manager crea los writers y define la propiedad de los archivos
- El catálogo expone la lista de grabaciones como un `StateFlow` caliente
- La UI y el ViewModel reaccionan automáticamente a cambios

El catálogo se maneja como singleton para garantizar una única fuente de verdad.

---

## Reproducción

**Componente:** `AudioTrackPlayer`

- Usa `AudioTrack` en modo `MODE_STREAM`
- Lee el archivo de forma incremental
- Aplica la decodificación antes de reproducir
- Notifica cuando la reproducción termina

La notificación de finalización es clave para mantener el estado visual y lógico consistente.

---

## ViewModel

**Componente:** `MainViewModel`

- Mantiene el estado de reproducción (`currentlyPlayingId`)
- Aplica reglas de exclusión:
    - No se puede reproducir mientras se graba
    - Iniciar una grabación detiene la reproducción activa
- Centraliza las decisiones de estado

La UI no toma decisiones de negocio; solo refleja el estado.

---

## Interfaz de usuario

- Construida con Jetpack Compose
- Un solo botón toggle para grabación (Start / Stop)
- Lista scrolleable de grabaciones
- Cada grabación se comporta como una burbuja de audio
- Indicador visual cuando un audio está reproduciéndose
- La reproducción se deshabilita mientras se graba

El diseño visual se mantiene simple a propósito.

---

## Fuera de alcance

- Eliminación de grabación