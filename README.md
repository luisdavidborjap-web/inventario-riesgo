# Inventario y Riesgo de Quiebre

Aplicación académica para una cadena de restaurantes.

## Arquitectura elegida
Monolito modular con Spring Boot. Mantiene separados los módulos de inventario, pronóstico, recomendaciones, aprobación, auditoría e integración con compras, sin introducir la complejidad operativa de microservicios.

## Funcionalidad
- Consulta existencias por bodega.
- Estima riesgo de quiebre.
- Recomienda transferencia entre bodegas si existe excedente.
- Recomienda compra si no existe una bodega con stock suficiente.
- Requiere aprobación humana antes de ejecutar.
- Las compras aprobadas se publican por RabbitMQ en la cola `compras.aprobadas`.
- Si el pronóstico no está disponible, usa un cálculo de respaldo.
- Registra auditoría.
- Muestra métricas: quiebres por producto, tiempo de aprobación, compras urgentes y porcentaje de recomendaciones aceptadas.

## Ejecutar
1. Copia `.env` si no existe.
2. Abre Docker Desktop.
3. Ejecuta:

```bash
docker compose up --build
```

4. Abre:
- Aplicación: http://localhost:8080
- RabbitMQ: http://localhost:15672

## Probar caída del pronóstico
En `.env` cambia:

```env
FORECAST_ENABLED=false
```

Luego:

```bash
docker compose down
docker compose up --build
```

Al generar recomendaciones, el sistema usará el fallback y lo registrará en auditoría.
