# Copper Golem Sorter — Forge 1.20.1

Copper Golem Sorter añade un Copper Golem enfocado en organización automática de almacenes para Minecraft Forge 1.20.1.

## Funcionamiento

- Un **Copper Chest** funciona como cofre de entrada.
- El Copper Golem recoge hasta **16 objetos por viaje**.
- Primero busca un cofre que **ya contenga exactamente el mismo objeto**.
- Si no existe uno, puede utilizar un **cofre completamente vacío** como nuevo destino.
- Nunca descarga objetos en otro Copper Chest.
- **Shift + clic derecho + Copper Ingot** bloquea/desbloquea cualquier contenedor compatible.
- La búsqueda revisa block entities de **chunks ya cargados**: no fuerza generación de chunks.

## Compatibilidad

El sistema usa Forge Item Handler y, como respaldo, la interfaz vanilla Container. Por eso funciona de forma genérica con cofres modded, incluyendo Iron Chests y las variantes personalizadas que expongan inventario.

Los cuatro cofres de Generations Core están reconocidos explícitamente:

- `generations_core:pokeball_chest`
- `generations_core:greatball_chest`
- `generations_core:ultraball_chest`
- `generations_core:masterball_chest`

Generations Core e Iron Chests son opcionales: no son dependencias obligatorias.

## Creación y oxidación

Coloca una **Carved Pumpkin** encima de un bloque de cobre para crear el golem. Las variantes de cobre expuesto, weathered, oxidized y enceradas también se reconocen.

- Honeycomb: encera al golem y detiene la oxidación.
- Hacha: quita la cera o retrocede una etapa.
- Al oxidarse completamente se convierte en **Copper Golem Statue**, sin IA ni coste de tick.
- Usa un hacha sobre la estatua para reactivarlo en la etapa Weathered.

## Diagnóstico

```
/cgs scan
```

Informa cuántos contenedores compatibles, Copper Chests, cofres Generations Core y contenedores bloqueados hay cerca.

## Configuración

El archivo common config permite ajustar radio horizontal/vertical, máximo de contenedores inspeccionados, objetos por viaje, intervalos de búsqueda, timeout de pathfinding y velocidad de oxidación.

## Build

- Minecraft 1.20.1
- Forge 47.4.22
- Java 17

GitHub Actions genera automáticamente el JAR en cada push a `main`.
