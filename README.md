# Order Update Service

Este microservicio permite actualizar órdenes existentes, siempre y cuando no estén finalizadas o canceladas.

## Funcionalidades

- Actualización de productos y cantidades en órdenes existentes
- Recálculo automático del total del pedido
- Emisión de eventos `OrderUpdated` a AWS SNS
- Validación de autenticación mediante JWT
- Documentación API con Swagger/OpenAPI

## Requisitos

- Java 21
- Spring Boot 3.2.2
- PostgreSQL
- AWS SNS (para publicación de eventos)

## Configuración

Las principales configuraciones se encuentran en `application.properties`:

- Puerto: 8081 (por defecto)
- Contexto de la aplicación: `/api`
- Validación de tokens JWT mediante servicio externo
- Conexión a base de datos PostgreSQL
- Configuración de AWS SNS para eventos

## API

### Actualizar una orden

```
PUT /api/orders/{orderId}
```

**Headers:**
- Authorization: Bearer {token}

**Request Body:**
```json
{
  "items": [
    {
      "productName": "Hamburguesa",
      "quantity": 2,
      "unitPrice": 5.00
    },
    {
      "productName": "Papas fritas",
      "quantity": 1,
      "unitPrice": 2.50
    }
  ]
}
```

**Response:**
```json
{
  "orderId": "123e4567-e89b-12d3-a456-426614174000",
  "status": "PENDING",
  "message": "Order updated successfully.",
  "updatedAt": "2025-06-23T11:30:45.123"
}
```

## Seguridad

- Autenticación mediante JWT
- Validación de tokens con servicio externo
- Solo el propietario de la orden puede actualizarla
- Órdenes completadas o canceladas no pueden ser actualizadas

## Eventos

El servicio emite eventos `OrderUpdated` a AWS SNS cuando una orden es actualizada exitosamente. El evento contiene:

- ID de la orden
- ID del usuario
- Estado de la orden
- Monto total
- Fecha de actualización
- Lista de productos actualizados

## Documentación API

La documentación completa de la API está disponible en:

- Swagger UI: `/api/swagger-ui.html`
- OpenAPI JSON: `/api/api-docs`
