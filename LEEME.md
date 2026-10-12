# Contador HUD (Forge 1.20.1)

## Comandos (solo operadores)
- `/contador 5m`  -> inicia (acepta 90, 30s, 5m, 1h30m, 2h15m10s)
- `/contador pausar` | `/contador reanudar` | `/contador detener`
- `/contador posicion <zona>` -> arriba-centro, arriba-izquierda, arriba-derecha,
  izquierda, centro, derecha, abajo-izquierda, abajo-centro, abajo-derecha

## Obtener el .jar sin instalar nada
1. Crea un repositorio en GitHub y sube TODO el contenido de esta carpeta (incluida `.github`).
2. Pestaña **Actions** -> "Compilar mod" -> espera ~5 min.
3. Entra a la ejecución terminada y descarga **contadorhud-jar** (dentro está el .jar).

## Obtener el .jar en tu PC
Con Java 17 y Gradle 8.1.1 instalados: `gradle build` -> `build/libs/contadorhud-1.0.0.jar`.

## Instalar
Copia el .jar a la carpeta `mods` del servidor y de cada jugador (Forge 1.20.1).
