# Copper Golem Sorter — Forge 1.20.1

Backport inspirado en el Copper Golem moderno, adaptado a Minecraft Forge 1.20.1 y ampliado para cofres modded.

## v1.1.0

- Modelo propio de Copper Golem, ya no reutiliza el Iron Golem.
- 4 apariencias de oxidación: cobre, expuesto, desgastado y oxidado.
- Se crea con Carved Pumpkin o Jack o'Lantern sobre un bloque de cobre.
- Al invocarlo, el bloque de cobre se transforma en un Copper Chest.
- El Copper Chest tiene 4 etapas de oxidación y se puede encerar/raspar.
- El golem solo toma objetos desde Copper Chests.
- Lleva hasta 16 objetos.
- Radio por defecto: 32 bloques horizontal / 8 vertical.
- Revisa como máximo 10 cofres por ciclo.
- Si no encuentra destino espera 7 segundos y vuelve a intentar conservando el objeto.
- Ordena por tipo de item, incluso si nombre/durabilidad/NBT no coinciden.
- Mano vacía sobre el golem: suelta el objeto que lleva.
- Honeycomb: encera al golem.
- Hacha: quita cera o una capa de oxidación.
- Al oxidarse completamente puede convertirse en estatua.
- La estatua conserva el nombre del golem.
- 4 poses de estatua: standing, sitting, running y star.
- Señal de comparador: 1, 2, 3 y 4 respectivamente.
- Las estatuas también se oxidan y se pueden encerar/raspar.
- Una estatua sin oxidación vuelve a ser golem al usar un hacha.
- Iron Golems cercanos pueden colocar una flor decorativa.
- Tijeras retiran la flor.
- Al morir suelta 1–3 lingotes de cobre, además del objeto transportado.

## Destinos compatibles

Además de cofres vanilla/trapped chest, se aceptan cofres modded que expongan inventario estándar.

Compatibilidad contemplada con Iron Chests y los 4 cofres de Generations Core:

- `generations_core:pokeball_chest`
- `generations_core:greatball_chest`
- `generations_core:ultraball_chest`
- `generations_core:masterball_chest`

También se reconoce el alias `generation_core` para packs personalizados.

## Bloquear un cofre

Shift + clic derecho con un lingote de cobre alterna si el golem puede utilizar ese contenedor.

## Diagnóstico

```
/cgs scan
```

## Build

- Minecraft 1.20.1
- Forge 47.4.22
- Java 17
