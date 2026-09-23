# Copper Golem Sorter — Forge 1.20.1

Backport del Copper Golem moderno para Minecraft Forge 1.20.1, ampliado para ordenar cofres vanilla y modded.

## v1.1.1

- Se mantiene el modo actual: crear el gólem también deja un Copper Chest.
- El Copper Chest ahora usa una lógica de oxidación equivalente a la del cobre vanilla de 1.20.1:
  - misma probabilidad base por random tick;
  - misma penalización de la primera etapa;
  - tiene en cuenta cobre cercano más/menos oxidado;
  - un Copper Chest creado con el gólem y uno crafteado usan exactamente la misma lógica.
- Spawn Egg eliminado completamente del registro, creativo y JEI.
- Texturas del Copper Golem rehechas:
  - cobre fresco naranja;
  - cobre expuesto más apagado con pátina;
  - weathered predominantemente verde;
  - oxidized verde/azulado.
- Copper Chest rehecho con textura propia de cofre y cuatro apariencias de oxidación.
- Icono de Copper Chest rehecho para inventario/JEI.
- Icono de estatua rehecho para inventario.
- Traducciones revisadas en inglés, español de España y español de México.

## Comportamiento del clasificador

- Solo toma objetos de Copper Chests.
- Hasta 16 objetos por viaje.
- Radio: 32 bloques horizontal / 8 vertical.
- Máximo 10 cofres por búsqueda.
- Si no encuentra destino, conserva el objeto y espera 7 segundos.
- Prioriza cofres que ya contienen el mismo tipo de objeto.
- Compatible con cofres vanilla, Iron Chests/custom y los 4 cofres de Generations Core.

## Generations Core

- `generations_core:pokeball_chest`
- `generations_core:greatball_chest`
- `generations_core:ultraball_chest`
- `generations_core:masterball_chest`

## Diagnóstico

```
/cgs scan
```

## Build

- Minecraft 1.20.1
- Forge 47.4.22
- Java 17
