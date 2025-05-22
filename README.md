# MiniProyecto - SOS

Una aplicación Android desarrollada en Kotlin que convierte tu dispositivo en una linterna funcional y permite emitir señales de auxilio (SOS) con luz, sonido y vibración.

## Funcionalidades

- **Linterna activada por movimiento**: Sacude el teléfono para encender o apagar la linterna.
- **Modo SOS**: Emite una señal de auxilio en código Morse con destellos de luz, sonidos.
- **Interfaz moderna**: Utiliza Jetpack Compose para una interfaz clara y accesible.

## Tecnologías utilizadas

- Kotlin
- Android Studio
- Jetpack Compose
- Sensores del dispositivo (Acelerómetro)
- Camera2 API
- MediaPlayer y Vibrator

## Requisitos

minSdk = 21 -> La app puede instalarse en dispositivos con Android 5.0 Lollipop o superior.
targetSdk = 35 -> La app está optimizada para correr en Android 14 (API 35).

Compatibilidad mínima: cualquier dispositivo con Android 5.0 o más reciente podrá instalar y ejecutar la aplicación.


##  Cómo ejecutar el proyecto

1. Clona el repositorio o descomprime el archivo ZIP.
2. Abre el proyecto en Android Studio.
3. Conéctalo a un dispositivo físico (preferido) ya que el emulador no soporta linterna ni sensores.
4. Ejecuta la aplicación.

##  Estructura relevante

- MainActivity.kt: Lógica principal de la app.
- AndroidManifest.xml: Permisos y configuración.
- res/: Recursos visuales como imágenes, textos y temas.

##  Permisos requeridos

La app requiere los siguientes permisos declarados en `AndroidManifest.xml`:

- android.permission.CAMERA
- android.permission.VIBRATE


## 👨‍💻 Autor

Desarrollado por Sebastian Perez Bastidas.



